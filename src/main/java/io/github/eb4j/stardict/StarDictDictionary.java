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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class to represent StarDict dictionary data.
 */
public abstract class StarDictDictionary implements AutoCloseable {

    protected final DictionaryData<IndexEntry> data;

    protected final StarDictInfo info;

    /**
     * @param data collection of <code>Entry</code>s loaded from file
     */
    StarDictDictionary(final DictionaryData<IndexEntry> data, final StarDictInfo info) {
        this.data = data;
        this.info = info;
    }

    public String getDictionaryName() {
        return info.getBookName();
    }

    public String getDictionaryVersion() {
        return info.getVersion();
    }

    private final Map<IndexEntry, String> cache = new HashMap<>();

    public List<DictionaryEntry> readArticles(final String word) {
        List<DictionaryEntry> list = new ArrayList<>();
        for (Map.Entry<String, IndexEntry> e : data.lookUp(word)) {
            DictionaryEntry dictionaryEntry = new DictionaryEntry(e.getKey(), getArticle(e.getValue()));
            list.add(dictionaryEntry);
        }
        return list;
    }

    public List<DictionaryEntry> readArticlesPredictive(final String word) {
        List<DictionaryEntry> list = new ArrayList<>();
        for (Map.Entry<String, IndexEntry> e : data.lookUpPredictive(word)) {
            DictionaryEntry dictionaryEntry = new DictionaryEntry(e.getKey(), getArticle(e.getValue()));
            list.add(dictionaryEntry);
        }
        return list;
    }

    private synchronized String getArticle(final IndexEntry starDictEntry) {
        return cache.computeIfAbsent(starDictEntry, (e) -> readArticle(e.getStart(), e.getLen()));
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
}
