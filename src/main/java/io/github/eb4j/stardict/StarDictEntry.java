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

/**
 * Dictionary article data class.
 */
public class StarDictEntry {

    /**
     * Entry types.
     */
    public enum EntryType {
        /** Word's pure text meaning. */
        MEAN('m'),
        /** English phonetic string.  */
        PHONETIC('t'),
        /** A string which is marked up with the Pango text markup language. */
        PANGO('g'),
        /** A string which is marked up with the xdxf language. */
        XDXF('x'),
        /** Chinese YinBiao or Japanese KANA. */
        YINBAO('y'),
        /** KingSoft PowerWord's XML data. */
        KINGSOFT('k'),
        /** MediaWiki markup language. */
        MEDIAWIKI('w'),
        /** html codes. */
        HTML('h'),
        /** WordNet data. */
        WORDNET('n'),
        /** Resource file list. */
        RESOURCE('r'),
        /** WAVE file. */
        WAV('W'),
        /** Picture image. */
        PICTURE('P'),
        /** Reserved for experimental extension. */
        EXPERIMENTAL('X');

        private final char typeValue;

        EntryType(final char type) {
            typeValue = type;
        }

        public char getTypeValue() {
            return typeValue;
        }

        public static EntryType getTypeByValue(char c) {
            for (EntryType t: values()) {
                if (t.getTypeValue() == c) {
                    return t;
                }
            }
            return null;
        }
    }

    private final String word;
    private final EntryType type;
    private final String article;

    public StarDictEntry(final String word, final EntryType type, final String article) {
        this.word = word;
        this.type = type;
        this.article = article;
    }

    /**
     * return entry word.
     * @return entry word.
     */
    public String getWord() {
        return word;
    }

    /**
     * Return entry type.
     * @return type enum value.
     */
    public EntryType getType() {
        return type;
    }

    /**
     * return article.
     * @return article.
     */
    public String getArticle() {
        return article;
    }
}
