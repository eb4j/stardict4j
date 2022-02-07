package io.github.eb4j.stardict;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * Dictionary driver for StarDict format.
 * <p>
 * StarDict format described on https://github.com/huzheng001/stardict-3/blob/master/dict/doc/StarDictFileFormat
 * <h1>Files</h1>
 * Every dictionary consists of these files:
 * <ol><li>somedict.ifo
 * <li>somedict.idx or somedict.idx.gz
 * <li>somedict.dict or somedict.dict.dz
 * <li>somedict.syn (optional)
 * </ol>
 *
 * @author Alex Buloichik
 * @author Hiroshi Miura
 * @author Aaron Madlon-Kay
 * @author Suguru Oho
 */
public class StarDictLoader {

    private StarDictLoader() {
    }

    public static StarDictDictionary load(final File ifoFile) throws Exception {
        Map<String, String> header = readIFO(ifoFile);
        StarDictInfo info = new StarDictInfo(header);
        String version = info.getVersion();
        if (!"2.4.2".equals(version) && !"3.0.0".equals(version)) {
            throw new Exception("Invalid version of dictionary: " + version);
        }
        String sametypesequence = header.get("sametypesequence");
        DictionaryEntry.EntryType[] types = new DictionaryEntry.EntryType[sametypesequence.length()];
        for (int i = 0; i < sametypesequence.length(); i++) {
            types[i] = DictionaryEntry.EntryType.getTypeByValue(sametypesequence.charAt(i));
            if (types[i] == null) {
                throw new Exception("Invalid dictionary type: " + sametypesequence);
            }
        }

        int idxoffsetbits = 32;
        if ("3.0.0".equals(version)) {
            String bitsString = header.get("idxoffsetbits");
            if (bitsString != null) {
                idxoffsetbits = Integer.parseInt(bitsString);
            }
        }

        if (idxoffsetbits != 32 && idxoffsetbits != 64) {
            throw new Exception("StarDict dictionaries other than idxoffsetbits=64 or 32 are not supported.");
        }

        String f = ifoFile.getPath();
        if (f.endsWith(".ifo")) {
            f = f.substring(0, f.length() - ".ifo".length());
        }
        String dictName = f;

        File idxFile = getFile(dictName, ".idx.gz", ".idx")
                .orElseThrow(() -> new FileNotFoundException("No .idx file could be found"));
        File synFile = getFile(dictName, ".syn.gz", ".syn")
                .orElse(null);
        DictionaryData<IndexEntry> data = loadData(idxFile, synFile, idxoffsetbits == 64, types);

        File dictFile = getFile(dictName, ".dict.dz", ".dict")
                .orElseThrow(() -> new FileNotFoundException("No .dict.dz or .dict files were found for " + dictName));

        try {
            if (dictFile.getName().endsWith(".dz")) {
                return new StarDictZipDict(info, dictFile, data);
            } else {
                return new StarDictFileDict(info, dictFile, data);
            }
        } catch (IOException ex) {
            throw new FileNotFoundException("No .dict.dz or .dict files were found for " + dictName);
        }
    }

    /**
     * Read header.
     */
    private static Map<String, String> readIFO(final File ifoFile) throws Exception {
        Map<String, String> result = new TreeMap<>();
        try (BufferedReader rd = Files.newBufferedReader(ifoFile.toPath(), StandardCharsets.UTF_8)) {
            String line;
            String first = rd.readLine();
            if (!"StarDict's dict ifo file".equals(first)) {
                throw new Exception("Invalid header of .ifo file: " + first);
            }
            while ((line = rd.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                int pos = line.indexOf('=');
                if (pos < 0) {
                    throw new Exception("Invalid format of .ifo file: " + line);
                }
                result.put(line.substring(0, pos), line.substring(pos + 1));
            }
        }
        return result;
    }

    private static Optional<File> getFile(final String basename, final String... suffixes) {
        return Stream.of(suffixes).map(suff -> new File(basename + suff)).filter(File::isFile)
                .findFirst();
    }

    private static DictionaryData<IndexEntry> loadData(final File idxFile, final File synFile, final boolean off64,
                                                       final DictionaryEntry.EntryType[] types) throws IOException {
        DictionaryDataBuilder<IndexEntry> builder = new DictionaryDataBuilder<>();
        InputStream is = new FileInputStream(idxFile);
        try {
            if (idxFile.getName().endsWith(".gz")) {
                is = new GZIPInputStream(is, 8192);
            }
            try (DataInputStream idx = new DataInputStream(new BufferedInputStream(is));
                 ByteArrayOutputStream mem = new ByteArrayOutputStream()) {
                int c = 0;
                while (true) {
                    c = c % types.length;
                    int b = idx.read();
                    if (b == -1) {
                        break;
                    }
                    if (b == 0) {
                        String key = new String(mem.toByteArray(), 0, mem.size(), StandardCharsets.UTF_8);
                        mem.reset();
                        long bodyOffset;
                        if (off64) {
                            bodyOffset = idx.readLong();
                        } else {
                            bodyOffset = idx.readInt();
                        }
                        int bodyLength = idx.readInt();
                        builder.add(key, new IndexEntry(bodyOffset, bodyLength, types[c]));
                        c++;
                    } else {
                        mem.write(b);
                    }
                }
            }
        } finally {
            is.close();
        }
        // when there is no syn file.
        if (synFile == null) {
            return builder.build();

        }
        //
        is = new FileInputStream(synFile);
        try {
            if (synFile.getName().endsWith(".gz")) {
                is = new GZIPInputStream(is, 8192);
            }
            try (DataInputStream syn = new DataInputStream(new BufferedInputStream(is));
                 ByteArrayOutputStream mem = new ByteArrayOutputStream()) {
                while (true) {
                    int b = syn.read();
                    if (b == -1) {
                        break;
                    }
                    if (b == 0) {
                        String key = new String(mem.toByteArray(), 0, mem.size(), StandardCharsets.UTF_8);
                        mem.reset();
                        int index = syn.readInt();
                        builder.addSynonym(key, index);
                    } else {
                        mem.write(b);
                    }
                }
            }
        } finally {
            is.close();
        }
        return builder.build();
    }

}
