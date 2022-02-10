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

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class to represent StarDict dictionary data.
 */
public abstract class StarDictDictionary implements AutoCloseable {

    private final LoadingCache<IndexEntry, String> cache;

    /** dictionary index data. */
    protected final DictionaryData<IndexEntry> data;
    /** dictionary metadata. */
    protected final StarDictInfo info;

    /**
     * Default constructor.
     * @param data collection of <code>IndexEntry</code>s loaded from file
     * @param maxsize max size of cache.
     * @param duration duration to keep in cache.
     * @param info metadata info.
     */
    StarDictDictionary(final DictionaryData<IndexEntry> data, final StarDictInfo info, final int maxsize,
                              final Duration duration) {
        this.data = data;
        this.info = info;
        cache = Caffeine.newBuilder()
                .maximumSize(maxsize)
                .expireAfterAccess(duration)
                .build(e -> readArticle(e.getStart(), e.getLen()));
    }

    /**
     * Builder utility method for StarDictDictionary.
     * @param ifoFile .ifo file.
     * @return StarDictDicitonary object.
     * @throws Exception when i/o error or parse error occurred.
     */
    public static StarDictDictionary loadDictionary(final File ifoFile)
            throws Exception {
        String f = ifoFile.getPath();
        if (f.endsWith(".ifo")) {
            f = f.substring(0, f.length() - ".ifo".length());
        }
        Path dictPath = Paths.get(f + ".dict.dz");
        if (dictPath.toString().endsWith(".dz")) {
            return StarDictLoader.load(ifoFile, 1_000, Duration.ofMinutes(15));
        } else {
            return StarDictLoader.load(ifoFile, 500, Duration.ofMinutes(5));
        }
    }

    /**
     * Builder utility method for StarDictDictionary.
     * @param ifoFile .ifo file.
     * @param cacheSize cache size of article.
     * @param duration cache expiry time.
     * @return StarDictDicitonary object.
     * @throws Exception when i/o error or parse error occurred.
     */
    public static StarDictDictionary loadDictionary(final File ifoFile, final int cacheSize, final Duration duration)
            throws Exception {
        return StarDictLoader.load(ifoFile, cacheSize, duration);
    }

    /**
     * get human readable name.
     * @return name
     */
    public String getDictionaryName() {
        return info.getBookName();
    }

    /**
     * Get dictionary version.
     * @return version whether "2.4.2" or "3.0.0"
     */
    public String getDictionaryVersion() {
        return info.getVersion();
    }

    /**
     * return dicitionary information class.
     * @return StarDictInfo object.
     */
    public StarDictInfo getInformation() {
        return info;
    }

    public List<Entry> readArticles(final String word) {
        List<Entry> list = new ArrayList<>();
        for (Map.Entry<String, IndexEntry> e : data.lookUp(word)) {
            Entry entry = new Entry(e.getKey(), getType(e.getValue()), cache.get(e.getValue()));
            list.add(entry);
        }
        return list;
    }

    public List<Entry> readArticlesPredictive(final String word) {
        List<Entry> list = new ArrayList<>();
        for (Map.Entry<String, IndexEntry> e : data.lookUpPredictive(word)) {
            Entry entry = new Entry(e.getKey(), getType(e.getValue()), cache.get(e.getValue()));
            list.add(entry);
        }
        return list;
    }

    private synchronized EntryType getType(final IndexEntry starDictEntry) {
        return starDictEntry.getType();
    }

    /**
     * Read data from the underlying file.
     *
     * @param start Start offset in data file
     * @param len   Length of article data
     * @return Raw article text
     */
    protected abstract String readArticle(long start, int len);

    public abstract void close() throws IOException;

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

        public static EntryType getTypeByValue(final char c) {
            for (EntryType t: EntryType.values()) {
                if (t.getTypeValue() == c) {
                    return t;
                }
            }
            return null;
        }
    }

    /**
     * Dictionary article data class.
     */
    public static class Entry {

        private final String word;
        private final EntryType type;
        private final String article;

        public Entry(final String word, final EntryType type, final String article) {
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
}
