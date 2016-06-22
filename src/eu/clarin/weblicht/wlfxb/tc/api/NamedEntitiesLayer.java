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
import java.util.Set;

/**
 * The <tt>NamedEntitiesLayer</tt> layer specifies named entity annotations on 
 * tokens. Each entity annotation references a token, or sequence of tokens, 
 * that represents this entity and specifies a named entity class (e.g. person, 
 * location, etc.). The layer specifies a tagset (as the value of the type 
 * attribute) used for named entity type tags. 
 * 
 * @author Yana Panchenko
 */
public interface NamedEntitiesLayer extends TextCorpusLayer {

    public String getType();

    public NamedEntity getEntity(int index);

    public NamedEntity getEntity(Token token);

    public List<NamedEntity> getEntities(Token token);

    public Token[] getTokens(NamedEntity entity);

    public NamedEntity addEntity(String entityType, Token entityToken);

    public NamedEntity addEntity(String entityType, List<Token> entityTokens);

    public Set<String> getFoundTypes();
}
