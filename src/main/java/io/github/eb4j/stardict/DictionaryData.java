/*
 * Stardict4j - access library for stardict format.
 * Copyright (C) 2022 Hiroshi Miura.
 *               2016 Aaron Madlon-Kay
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

import org.trie4j.MapTrie;
import org.trie4j.doublearray.MapDoubleArray;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

/**
 * A class that encapsulates the storage and retrieval of string-keyed data.
 *
 * @author Aaron Madlon-Kay
 * @author Hiroshi Miura
 * @param <T>
 *            The type of data stored
 */
class DictionaryData<T> {

    private final MapDoubleArray<Object> data;

    /**
     * POJO class to hold dictionary data.
     */
    DictionaryData(final MapTrie<Object> mapTrie) {
        data = new MapDoubleArray<>(mapTrie);
    }

    /**
     * Look up the given word.
     *
     * @param word
     *            The word to look up
     * @return A list of stored objects matching the given word
     */
    public List<Entry<String, T>> lookUp(final String word) {
        List<Entry<String, T>> result = new ArrayList<>();
        get(word, data.get(word), result);
        return result;
    }

    /**
     * Look up the given word using predictive completion; e.g. "term" will
     * match "terminology" (and "terminal", etc.).
     *
     * @param word
     *            The word to look up
     * @return A list of stored objects matching the given word
     */
    public List<Entry<String, T>> lookUpPredictive(final String word) {
        List<Entry<String, T>> result = new ArrayList<>();
        data.predictiveSearch(word).forEach(w -> get(w, data.get(w), result));
        return result;
    }

    /**
     * Unpack the given stored object (singular, or array) into the given
     * collection.
     *
     * @param key
     * @param value
     * @param into
     */
    @SuppressWarnings("unchecked")
    private <U> void get(final U key, final Object value, final Collection<Entry<U, T>> into) {
        if (value == null) {
            return;
        }
        if (value instanceof Object[]) {
            for (Object o : (Object[]) value) {
                if (o != null) {
                    into.add(new AbstractMap.SimpleImmutableEntry<>(key, (T) o));
                }
            }
        } else {
            into.add(new AbstractMap.SimpleImmutableEntry<>(key, (T) value));
        }
    }

    /**
     * Get the number of stored keys.
     *
     * @return The number of stored keys
     */
    public int size() {
        return data.size();
    }
}
