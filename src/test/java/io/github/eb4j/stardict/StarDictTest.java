/*
 * Stardict4j - access library for stardict format.
 * Copyright (C) 2022 Hiroshi Miura.
 * Copyright (C) 2010 Alex Buloichik
 *               2015 Hiroshi Miura, Aaron Madlon-Kay
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

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Dictionary test.
 *
 * @author Alex Buloichik
 * @author Hiroshi Miura
 * @author Aaron Madlon-Kay
 */
public class StarDictTest {

    @Test
    public void testReadFileDict() throws Exception {
        StarDictDictionary dict = StarDictDictionary.loadDictionary(
                new File("src/test/resources/dicts/latin-francais.ifo"));
        assertEquals("latin-français", dict.getDictionaryName());
        assertEquals("2.4.2", dict.getDictionaryVersion());
        StarDictInfo info = dict.getInformation();
        assertEquals("m", info.getSametypesequence());
        assertEquals("Yves Ouvrard", info.getAuthor());
        assertEquals("http://www.collatinus.org/", info.getWebsite());
        assertTrue(info.getDescription().startsWith("Dictionnaire"));
        assertEquals("1er Novembre 2007", info.getDate());
        assertEquals(10451, info.getWordCount());
        assertEquals(10451, dict.data.size());

        String word = "testudo";
        List<Map.Entry<String, IndexEntry>> data = dict.data.lookUp(word);
        assertEquals(1, data.size());

        List<StarDictDictionary.Entry> result = dict.readArticles(word);
        assertEquals(1, result.size());
        assertEquals(word, result.get(0).getWord());
        assertEquals("dinis, f. : tortue", result.get(0).getArticle());

        // Test prediction
        word = "testu";
        result = dict.readArticles(word);
        assertTrue(result.isEmpty());
        result = dict.readArticlesPredictive(word);
        assertEquals(1, result.size());
        assertEquals("testudo", result.get(0).getWord());
        assertEquals("dinis, f. : tortue", result.get(0).getArticle());
        assertEquals(StarDictDictionary.EntryType.MEAN, result.get(0).getType());
    }

    @Test
    public void testReadZipDict() throws Exception {
        StarDictDictionary dict = StarDictDictionary.loadDictionary(
                new File("src/test/resources/dicts-zipped/latin-francais.ifo"));
        assertEquals("latin-français", dict.getDictionaryName());
        assertEquals("2.4.2", dict.getDictionaryVersion());
        assertEquals(10451, dict.data.size());

        String word = "testudo";
        List<Map.Entry<String, IndexEntry>> data = dict.data.lookUp(word);
        assertEquals(1, data.size());
        List<StarDictDictionary.Entry> result = dict.readArticles(word);
        assertEquals(1, result.size());
        assertFalse(result.isEmpty());
        assertEquals(word, result.get(0).getWord());
        assertEquals("dinis, f. : tortue", result.get(0).getArticle());
        assertEquals(StarDictDictionary.EntryType.MEAN, result.get(0).getType());
    }
}
