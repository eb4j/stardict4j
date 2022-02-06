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
public class DictionaryEntry {
    private final String word;
    private final String article;

    public DictionaryEntry(final String word, final String article) {
        this.word = word;
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
     * return article.
     * @return article.
     */
    public String getArticle() {
        return article;
    }
}
