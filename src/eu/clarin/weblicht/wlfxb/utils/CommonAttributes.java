/**
 * wlfxb - a library for creating and processing of TCF data streams.
 *
 * Copyright (C) University of Tübingen.
 *
 * This file is part of wlfxb.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 *
 */
package eu.clarin.weblicht.wlfxb.utils;

/**
 * @author Yana Panchenko
 *
 */
public abstract class CommonAttributes {

    public static final String ID = "ID";
    public static final String TOKEN_SEQUENCE_REFERENCE = "tokenIDs";
    public static final String TOKEN_REFERENCE = "tokID";
    public static final String LEMMA_REFERENCE = "lemID";
    public static final String ENTRY_REFERENCE = "entryID";
    public static final String NONCONSECUTIVE_LEMMAS_REFERENCE = "lemmaRefs";
    public static final String CHAR_OFFSETS = "charOffsets";
    public static final String START_CHAR_OFFSET = "start";
    public static final String END_CHAR_OFFSET = "end";
    public static final String START_TOKEN = "start";
    public static final String END_TOKEN = "end";
    public static final String TAGSET = "tagset";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String PID = "pid";
    public static final String TYPE = "type";
    public static final String FUNCTION = "func";
    public static final String CATEGORY = "cat";
    public static final String SOURCE = "src";
    public static final String ALPHABET = "alphabet"; //alphabet 
    public static final String EXTREF = "extref";
}
