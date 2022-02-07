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
    private final String author;
    private final String website;
    private final String description;
    private final String date;
    private final String sametypesequence;

    public StarDictInfo(final Map<String, String> header) throws Exception {
        this.bookName = header.get("bookname");
        this.version = header.get("version");
        String swc = header.getOrDefault("synwordcount", null);
        hasSynonym = (swc != null);
        if (hasSynonym) {
            synWordCount = Integer.parseUnsignedInt(swc);
        } else {
            synWordCount = 0;
        }
        String wc = header.getOrDefault("wordcount", "0");
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
        this.author = header.getOrDefault("author", "");
        this.website = header.getOrDefault("website", "");
        this.description = header.getOrDefault("description", "");
        this.date = header.getOrDefault("date", null);
    }

    /**
     * Return book name.
     * @return book name.
     */
    public String getBookName() {
        return bookName;
    }

    /**
     * Return version string.
     * @return "2.4.2" or "3.0.0".
     */
    public String getVersion() {
        return version;
    }

    /**
     * Return word count.
     * @return word count.
     */
    public int getWordCount() {
        return wordCount;
    }

    /**
     * Does dictionary has synonym?
     * @return true when .syn file exist, otherwise false.
     */
    public boolean isHasSynonym() {
        return hasSynonym;
    }

    /**
     * Return synonym word count.
     * @return synonym word count when specified, otherwise 0.
     */
    public int getSynWordCount() {
        return synWordCount;
    }

    /**
     * Is dictionary index is 64bit?
     * @return true when index field is 64 bit, otherwise false.
     */
    public boolean isOftindexbits64() {
        return oftindexbits64;
    }

    /**
     * Return author string.
     * @return author when specified, otherwise empty string.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Return website url string.
     * @return website url when exist, otherwise empty string.
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Return description.
     * @return description when exist, otherwise empty string "".
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return date field string.
     * @return date field string.
     */
    public String getDate() {
        return date;
    }

    /**
     * Return sametypesequence field value string.
     * @return string
     */
    public String getSametypesequence() {
        return sametypesequence;
    }
}
