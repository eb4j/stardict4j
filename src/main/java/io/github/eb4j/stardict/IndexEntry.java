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

import java.util.Objects;

/**
 * Simple container for offsets+lengths of entries in StarDict dictionary.
 * Subclasses of StarDictDict know how to read this from the underlying data
 * file.
 */
public class IndexEntry {
    private final long start;
    private final int len;
    private final StarDictEntry.EntryType type;

    public IndexEntry(final long start, final int len, final StarDictEntry.EntryType type) {
        this.start = start;
        this.len = len;
        this.type = type;
    }

    public long getStart() {
        return start;
    }

    public int getLen() {
        return len;
    }

    public StarDictEntry.EntryType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(len, start);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IndexEntry other = (IndexEntry) obj;
        return len == other.len && start == other.start;
    }
}
