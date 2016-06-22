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

import java.util.List;

/**
 * The <tt>OrthographyLayer</tt> layer annotates tokens with their correct 
 * orthographic transcription. For each correction a correction operation is 
 * specified. 
 * 
 * @author Yana Panchenko
 */
public interface OrthographyLayer extends TextCorpusLayer {

    public OrthCorrection getCorrection(int index);

    public OrthCorrection getCorrection(Token token);

    public Token[] getTokens(OrthCorrection correction);

    public OrthCorrection addCorrection(String correctionString, Token correctedToken, CorrectionOperation operation);

    public OrthCorrection addCorrection(String correctionString, List<Token> correctedTokens, CorrectionOperation operation);
}
