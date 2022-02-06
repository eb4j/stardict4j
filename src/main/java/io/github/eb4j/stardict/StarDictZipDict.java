/*
 * Stardict4j - access library for stardict format.
 * Copyright (C) 2022 Hiroshi Miura.
 * Copyright (C) 2009 Alex Buloichik
 *               2015-2016 Hiroshi Miura, Aaron Madlon-Kay
 *               2020 Suguru Oho, Aaron Madlon-Kay
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.eb4j.stardict;

import org.dict.zip.DictZipInputStream;
import org.dict.zip.RandomAccessInputStream;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class StarDictZipDict extends StarDictDictionary {

    private final DictZipInputStream dataFile;

    StarDictZipDict(final String bookName, final File dictFile, final DictionaryData<IndexEntry> data)
            throws IOException {
        super(data, bookName, dictFile.getPath());
        dataFile = new DictZipInputStream(new RandomAccessInputStream(new RandomAccessFile(dictFile, "r")));
    }

    @Override
    protected String readArticle(final long start, final int len) {
        String result = null;
        try {
            dataFile.seek(start);
            byte[] data = new byte[len];
            dataFile.readFully(data);
            result = new String(data, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        dataFile.close();
    }
}
