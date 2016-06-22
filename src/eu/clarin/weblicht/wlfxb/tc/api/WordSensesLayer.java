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
 * The annotations in <tt>WordSensesLayer</tt> are represented by lexical units
 * from a given word senses source. The layer annotates tokens with the 
 * disambiguated sense by specifying lexicon units representing the sense of the
 * token. The lexical units can be represented by their ids in the source data base.
 * 
 * @author Yana Panchenko
 */
public interface WordSensesLayer extends TextCorpusLayer {

    public String getSource();

    public WordSense getWordSense(int index);

    public WordSense getWordSense(Token token);

    public Token[] getTokens(WordSense ws);

    public WordSense addWordSense(Token token, String ... lexicalUnits);
    
    public WordSense addWordSense(List<Token> tokens, String ... lexicalUnits);
    
    public WordSense addWordSense(Token token, String comment, String ... lexicalUnits);
    
    public WordSense addWordSense(List<Token> tokens, String comment, String ... lexicalUnits);

}
