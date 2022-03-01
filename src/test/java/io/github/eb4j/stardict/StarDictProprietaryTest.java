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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StarDictProprietaryTest {

    private static final String TESTDATA = "proprietary/en-he.ifo";

    /**
     * Test with proprietary english-hebrew dictionary that have optional syn file.
     * @throws Exception
     */
    @Test
    @EnabledIf("fileExist")
    public void testReadDict() throws Exception {
        StarDictDictionary dict = StarDictDictionary.loadDictionary(new File("src/test/resources/" + TESTDATA));
        assertEquals("3.0.0", dict.getDictionaryVersion());
        //  search main entries
        String word = "abandon";
        List<StarDictDictionary.Entry> result = dict.readArticles(word);
        assertEquals(4, result.size());
        // 3rd entry is adjunct in this data
        assertEquals(word, result.get(2).getWord());
        assertEquals(StarDictDictionary.EntryType.HTML, result.get(2).getType());
        String article = result.get(2).getArticle();
        // search adjunct synonyms
        word = "abandoned";
        result = dict.readArticles(word);
        assertEquals(1, result.size());
        // should be same result.
        assertEquals(article, result.get(0).getArticle());
    }

    static boolean fileExist() {
        return StarDictProprietaryTest.class.getResource("/" + TESTDATA) != null;
    }

}
