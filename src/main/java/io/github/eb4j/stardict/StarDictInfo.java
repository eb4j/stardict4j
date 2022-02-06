/*
 * Stardict4j - access library for stardict format.
 * Copyright (C) 2022 Hiroshi Miura.
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

import java.util.Map;

public class StarDictInfo {
    private final String bookName;
    private final String version;
    private final int wordCount;
    private final boolean hasSynonym;
    private final int synWordCount;
    private final boolean oftindexbits64;

    private final String sametypesequence;

    public StarDictInfo(final Map<String, String> header) throws Exception {
        this.bookName = header.get("bookname");
        this.version = header.get("version");
        String swc = header.getOrDefault("synwordcount", null);
        hasSynonym = (swc != null);
        if (hasSynonym) {
            synWordCount = Integer.parseUnsignedInt(swc);
        } else {
            synWordCount = -1;
        }
        String wc = header.get("wordcount");
        wordCount = Integer.parseUnsignedInt(wc);
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
        this.oftindexbits64 = (idxoffsetbits == 64);
        this.sametypesequence = header.getOrDefault("sametypesequence", null);
    }

    public String getBookName() {
        return bookName;
    }

    public String getVersion() {
        return version;
    }

    public int getWordCount() {
        return wordCount;
    }

    public boolean isHasSynonym() {
        return hasSynonym;
    }

    public int getSynWordCount() {
        return synWordCount;
    }

    public boolean isOftindexbits64() {
        return oftindexbits64;
    }

    public String getSametypesequence() {
        return sametypesequence;
    }
}
