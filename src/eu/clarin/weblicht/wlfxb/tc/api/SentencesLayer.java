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
 * The <tt>SentencesLayer</tt> represents sentence boundary annotations. Each sentence 
 * element enumerates token identifiers of tokens that belong to this sentence. 
 * Optionally, a sentence can have start and end character offset positions in 
 * relation to the character string in the text layer.
 * 
 * @author Yana Panchenko
 */
public interface SentencesLayer extends TextCorpusLayer {

    public boolean hasCharOffsets();

    public Sentence getSentence(int index);

    public Sentence getSentence(Token token);

    public Token[] getTokens(Sentence sentence);

    public Sentence addSentence(List<Token> tokens);

    public Sentence addSentence(List<Token> tokens, int start, int end);
}
