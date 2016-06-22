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
 * The <tt>ReferencesLayer</tt> layer represents annotations on tokens, or 
 * sequences of tokens, that refer to the same entities. The layer consists of a
 * list of entity elements. Each entity element consists of all the mentions 
 * (reference elements) that refer to this entity. Each reference element 
 * enumerates token identifiers, pointing to a sequence of tokens that 
 * represents this reference. Optionally, the minimum sequence of tokens of the 
 * reference can be specified (e.g. when reference is a long noun phrase, 
 * minimum representation is the head of the phrase). Linguistic type of the 
 * reference (pronoun/nominal/name/demonstrative/zero pronoun, other/finer 
 * distinctions are possible) type can be specified. Relation to another 
 * reference (to target reference) can be specified as well (anaphoric, 
 * cataphoric, coreferential, etc.). Additionally, an external identifier of the
 * entity can be provided (url of a Wikipedia article for the entity, id of the
 * entity in a database, etc.) References layer has optional attributes to
 * specify type tagset for the case when linguistic types of references are 
 * specified, relation tagset for the case relations between references are 
 * specified, name of external reference source in case external references of 
 * entities are provided.
 * 
 * @author Yana Panchenko
 */
public interface ReferencesLayer extends TextCorpusLayer {

    public String getTypetagset();

    public String getReltagset();

    public boolean hasExternalReferences();

    public String getExternalReferenceSource();

    public ReferencedEntity getReferencedEntity(int index);

    public List<ReferencedEntity> getReferencedEntities(Token token);

    public Token[] getTokens(Reference reference);

    public Token[] getMinimumTokens(Reference reference);

    public Reference[] getTarget(Reference reference);

    public ReferencedEntity addReferent(List<Reference> references);

    public ReferencedEntity addReferent(List<Reference> references, String externalId);

    public Reference createReference(List<Token> referenceTokens);

    public Reference createReference(List<Token> referenceTokens, List<Token> minReferenceTokens);

    public Reference createReference(String type, List<Token> referenceTokens, List<Token> minReferenceTokens);

    public void addRelation(Reference reference, String relation, Reference... target);
}
