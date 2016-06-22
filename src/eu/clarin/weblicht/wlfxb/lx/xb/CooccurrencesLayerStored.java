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

import eu.clarin.weblicht.wlfxb.lx.api.*;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = CooccurrencesLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class CooccurrencesLayerStored extends TermsContainerStored implements CooccurrencesLayer {

    public static final String XML_NAME = "cooccurrences";
    @XmlElement(name = CooccurrenceStored.XML_NAME)
    private List<CooccurrenceStored> coocs = new ArrayList<CooccurrenceStored>();
    //private LexiconLayersConnector connector;

    protected CooccurrencesLayerStored() {
    }

    protected CooccurrencesLayerStored(LexiconLayersConnector connector) {
        super(connector);
    }

    @Override
    protected void setLayersConnector(LexiconLayersConnector connector) {
        super.setLayersConnector(connector);
        for (CooccurrenceStored cooc : coocs) {
            connect(cooc);
        }
    }

    @Override
    public boolean isEmpty() {
        return coocs.isEmpty();
    }

    @Override
    public int size() {
        return coocs.size();
    }

    @Override
    public Cooccurrence getCooccurrence(int index) {
        return coocs.get(index);
    }
    
    @Override
    public Cooccurrence[] getCooccurrences(Entry entry) {
        List<Cooccurrence> coocList = connector.entry2ItsCoocs.get(entry);
        if (coocList != null) {
            return coocList.toArray(new Cooccurrence[coocList.size()]);
        } else {
            return new Cooccurrence[]{};
        }
    }
    
    @Override
    public Entry[] getEntries(Cooccurrence cooccurrence) {
        if (cooccurrence instanceof CooccurrenceStored) {
            CooccurrenceStored cooc = (CooccurrenceStored) cooccurrence;
            List<Entry> entries = new ArrayList<Entry>();
            for (TermStored t : cooc.terms) {
                if (t.entryId != null) {
                    entries.add(connector.entryId2ItsEntry.get(t.entryId));
                }
            }
            return entries.toArray(new Entry[entries.size()]);
        } else {
            return null;
        }
    }

    @Override
    public String[] getTermsAsStrings(Cooccurrence cooccurrence, boolean includeAnnotatedTerm) {
        if (cooccurrence instanceof CooccurrenceStored) {
            CooccurrenceStored cooc = (CooccurrenceStored) cooccurrence;
            return super.getTermsAsStrings(cooc.terms, includeAnnotatedTerm);
        } else {
            return null;
        }
    }

    @Override
    public Cooccurrence addCooccurrence(CooccurrenceFunction function, List<Term> terms) {
        CooccurrenceStored cooc = new CooccurrenceStored();
        cooc.function = function;
        for (Term term : terms) {
            if (term instanceof TermStored) {
                cooc.terms.add((TermStored) term);
            }
        }
        connect(cooc);
        coocs.add(cooc);
        return cooc;
    }

    private void connect(CooccurrenceStored cooccurrence) {
        for (TermStored term : cooccurrence.terms) {
            if (term.entryId != null) {
                Entry entry = connector.entryId2ItsEntry.get(term.entryId);
                if (!connector.entry2ItsCoocs.containsKey(entry)) {
                    connector.entry2ItsCoocs.put(entry, new ArrayList<Cooccurrence>());
                }
                connector.entry2ItsCoocs.get(entry).add(cooccurrence);
            }
        }
    }

    @Override
    public Cooccurrence addCooccurrence(CooccurrenceFunction function, Sig sig, List<Term> terms) {
        CooccurrenceStored cooc = (CooccurrenceStored) addCooccurrence(function, terms);
        if (sig instanceof SigStored) {
            cooc.sig = (SigStored) sig;
        }
        return cooc;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" : ");
        sb.append(coocs.toString());
        return sb.toString();
    }
}
