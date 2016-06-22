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
 * The <tt>DependencyParsingLayer</tt> annotates dependency relations between 
 * tokens. Each dependency annotation contains a reference to a token, or 
 * sequence of tokens, that is in a dependent role in the given relation, as 
 * well as a reference to a token, or sequence of tokens, that is in the 
 * governor role in the given relation. Optionally, the function of the 
 * dependent-governor relation is specified. In some cases, such as in the case 
 * of root dependency, the governor can be omitted. Additionally, the dependency 
 * layer specifies: a tagset for dependency function tags, whether empty tokens
 * can be inserted into the dependency parse, whether a dependent can have more 
 * than one governor in the parse.
 * 
 * @author Yana Panchenko
 */
public interface DependencyParsingLayer extends TextCorpusLayer {

    public String getTagset();

    public DependencyParse getParse(int index);

    public Token[] getGovernorTokens(Dependency dependency);

    public Token[] getDependentTokens(Dependency dependency);

    public Dependency createDependency(String function, List<Token> dependent, List<Token> governor);

    public Dependency createDependency(String function, List<Token> dependent);

    public Dependency createDependency(List<Token> dependent, List<Token> governor);

    public Dependency createDependency(List<Token> dependent);

    public Dependency createDependency(String function, Token dependent, Token governor);

    public Dependency createDependency(String function, Token dependent);

    public Dependency createDependency(Token dependent, Token governor);

    public Dependency createDependency(Token dependent);

    public Token createEmptyToken(String tokenString);

    public DependencyParse addParse(List<Dependency> dependencies);

    public boolean hasEmptyTokens();

    public boolean hasMultipleGovernors();
}
