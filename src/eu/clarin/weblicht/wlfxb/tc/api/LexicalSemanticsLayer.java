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
 * The annotations in <tt>LexicalSemanticsLayer</tt> are represented by synonymy, 
 * antonymy, hyponymy, hyperonymy. The layers specify the resource from which 
 * the annotations were extracted, and enumerate the orthform elements. Each 
 * orthform element has an orthform string (can be a word, a phrase, or a list 
 * of both) and references lemmas that are in synonymy/antonymy/hyponymy/hyperonymy 
 * relations with this orthform.
 * 
 * @author Yana Panchenko
 */
public interface LexicalSemanticsLayer extends TextCorpusLayer {

    //public String getSource();

    public Orthform getOrthform(int index);

    public Orthform getOrthform(Lemma lemma);

    public Lemma[] getLemmas(Orthform orthform);

    public Orthform addOrthform(String orthformValues, Lemma lemma);

    public Orthform addOrthform(String[] orthformValues, Lemma lemma);
}
