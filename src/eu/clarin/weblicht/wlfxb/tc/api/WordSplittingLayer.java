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
package eu.clarin.weblicht.wlfxb.tc.api;

/**
 * The <tt>WordSplittingLayer</tt> annotates tokens with regard to character 
 * intervals the token can be split into. The type of the splitting is specified 
 * at the layer level. Each split references the token it annotates and character 
 * offsets of the splits (count of characters should start at 0).
 * 
 * @author Yana Panchenko
 */
public interface WordSplittingLayer extends TextCorpusLayer {

    public String getType();

    public WordSplit getSplit(int index);

    public WordSplit getSplit(Token token);

    public Token getToken(WordSplit split);

    public WordSplit addSplit(Token token, int[] splitIndices);
}
