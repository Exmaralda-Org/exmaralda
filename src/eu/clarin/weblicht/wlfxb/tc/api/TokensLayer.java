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
 * The <tt>TokensLayer</tt> is composed of token elements, each having a unique 
 * identifier and token string value. Optionally, each token can reference its 
 * start and end character offset position in relation to the character string 
 * in the text layer. The <tt>TokensLayer</tt> is the main anchor layer 
 * among layers of the {@link TextCorpus}, i.e. all other layers (with the 
 * exception of the text layer) directly or indirectly (via other layers) 
 * reference tokens.
 * 
 * @author Yana Panchenko
 */
public interface TokensLayer extends TextCorpusLayer {

    public Token getToken(int index);

    public Token getToken(String tokenId);

    public boolean hasCharOffsets();

    public Token addToken(String tokenString);

    public Token addToken(String tokenString, String tokenId);

    public Token addToken(String tokenString, long start, long end);

    public Token addToken(String tokenString, long start, long end, String tokenId);
}
