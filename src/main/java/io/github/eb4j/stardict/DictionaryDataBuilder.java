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

import org.trie4j.MapTrie;
import org.trie4j.patricia.MapPatriciaTrie;

import java.util.ArrayList;
import java.util.List;

final class DictionaryDataBuilder<T> {

    private MapTrie<Object> mapPatriciaTrie = new MapPatriciaTrie<>();
    private List<String> keys = new ArrayList<>();

    /**
     * Builder factory for POJO class DictionaryData.
     */
    DictionaryDataBuilder() {
    }

    /**
     * build DictionaryData POJO.
     * @return DictionaryData immutable object.
     */
    public DictionaryData<T> build() {
        return new DictionaryData<T>(mapPatriciaTrie);
    }

    /**
     * Do the actual storing of the value. Most values are going to be singular,
     * but dictionaries may store multiple definitions for the same key, so in
     * that case we store the values in an array.
     *
     * @param key
     */
    public void add(final String key, final IndexEntry entry) {
        keys.add(key);
        Object stored = mapPatriciaTrie.get(key);
        if (stored == null) {
            mapPatriciaTrie.insert(key, entry);
        } else {
            if (stored instanceof Object[]) {
                stored = extendArray((Object[]) stored, entry);
            } else {
                stored = new Object[] {stored, entry};
            }
            mapPatriciaTrie.put(key, stored);
        }
    }

    public void addSynonym(final String key, final int index) {
        String ref = keys.get(index);
        Object stored = mapPatriciaTrie.get(key);
        IndexEntry entry;
        if (stored instanceof Object[]) {
            entry = (IndexEntry) ((Object[]) stored)[0];
        } else {
            entry = (IndexEntry) stored;
        }
        add(ref, entry);
    }

    /**
     * Return the given array with the given value appended to it.
     *
     * @param array
     * @param value
     * @return
     */
    Object[] extendArray(final Object[] array, final Object value) {
        Object[] newArray = new Object[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[newArray.length - 1] = value;
        return newArray;
    }
}
