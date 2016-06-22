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
package eu.clarin.weblicht.wlfxb.lx.xb;

import eu.clarin.weblicht.wlfxb.lx.api.Cooccurrence;
import eu.clarin.weblicht.wlfxb.lx.api.Frequency;
import eu.clarin.weblicht.wlfxb.lx.api.PosTag;
import eu.clarin.weblicht.wlfxb.lx.api.Entry;
import eu.clarin.weblicht.wlfxb.lx.api.Syllabification;
import eu.clarin.weblicht.wlfxb.lx.api.Synonym;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yana Panchenko
 *
 */
public class LexiconLayersConnector {

    // maps for connecting elements of different layers that reference one another
    protected Map<String, Entry> entryId2ItsEntry = new HashMap<String, Entry>();
    
    //protected Map<String, Lemma> lemmaId2ItsLemma = new HashMap<String, Lemma>();
    //protected Map<String, Entry> typeId2ItsType = new HashMap<String, Entry>();
    //protected Map<Lemma, List<PosTag>> lemma2ItsTags = new HashMap<Lemma, List<PosTag>>();
    protected Map<Entry, List<PosTag>> entry2ItsTags = new HashMap<Entry, List<PosTag>>();
    //protected Map<Lemma, Frequency> lemma2ItsFreq = new HashMap<Lemma, Frequency>();
    protected Map<Entry, Frequency> entry2ItsFreq = new HashMap<Entry, Frequency>();
    //protected Map<Lemma, List<Relation>> lemma2ItsRels = new HashMap<Lemma, List<Relation>>();
    //protected Map<Lemma, List<Cooccurrence>> lemma2ItsCoocs = new HashMap<Lemma, List<Cooccurrence>>();
    //protected Map<Entry, List<Cooccurrence>> type2ItsCoocs = new HashMap<Entry, List<Cooccurrence>>();
    protected Map<Entry, List<Cooccurrence>> entry2ItsCoocs = new HashMap<Entry, List<Cooccurrence>>();
    protected Map<Entry, List<Synonym>> entry2ItsSyns = new HashMap<Entry, List<Synonym>>();
    protected Map<Entry, Syllabification> entry2ItsSyllab = new HashMap<Entry, Syllabification>();

    LexiconLayersConnector() {
        super();
    }
}
