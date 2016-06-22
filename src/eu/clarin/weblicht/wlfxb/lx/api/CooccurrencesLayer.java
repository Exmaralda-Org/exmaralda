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
package eu.clarin.weblicht.wlfxb.lx.api;

import java.util.List;

/**
 * @author Yana Panchenko
 *
 */
public interface CooccurrencesLayer extends LexiconLayer, TermsContainer {

    public Cooccurrence getCooccurrence(int index);
    
    public Cooccurrence[] getCooccurrences(Entry entry);
    
    public Entry[] getEntries(Cooccurrence cooccurrence);

    public String[] getTermsAsStrings(Cooccurrence cooccurrence, boolean includeAnnotatedTerm);

    public Cooccurrence addCooccurrence(CooccurrenceFunction function, List<Term> terms);

    public Cooccurrence addCooccurrence(CooccurrenceFunction function, Sig sig, List<Term> terms);

}
