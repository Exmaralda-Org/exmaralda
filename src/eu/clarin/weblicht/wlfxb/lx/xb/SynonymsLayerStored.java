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
@XmlRootElement(name = SynonymsLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class SynonymsLayerStored extends TermsContainerStored implements SynonymsLayer {

    public static final String XML_NAME = "synonyms";
    @XmlElement(name = SynonymStored.XML_NAME)
    private List<SynonymStored> syns = new ArrayList<SynonymStored>();

    protected SynonymsLayerStored() {
    }

    protected SynonymsLayerStored(LexiconLayersConnector connector) {
        super(connector);
    }

    @Override
    protected void setLayersConnector(LexiconLayersConnector connector) {
        super.setLayersConnector(connector);
        for (SynonymStored syn : syns) {
            connect(syn);
        }
    }

    @Override
    public boolean isEmpty() {
        return syns.isEmpty();
    }

    @Override
    public int size() {
        return syns.size();
    }

    @Override
    public Synonym getSynonym(int index) {
        return syns.get(index);
    }
    
    @Override
    public Synonym[] getSynonyms(Entry entry) {
        List<Synonym> list = connector.entry2ItsSyns.get(entry);
        if (list != null) {
            return list.toArray(new Synonym[list.size()]);
        } else {
            return new Synonym[]{};
        }
    }
    
    @Override
    public Entry[] getEntries(Synonym synonym) {
        if (synonym instanceof SynonymStored) {
            SynonymStored syn = (SynonymStored) synonym;
            List<Entry> entries = new ArrayList<Entry>();
            for (TermStored t : syn.terms) {
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
    public String[] getTermsAsStrings(Synonym synonym, boolean includeAnnotatedTerm) {
        if (synonym instanceof SynonymStored) {
            SynonymStored syn = (SynonymStored) synonym;
            return super.getTermsAsStrings(syn.terms, includeAnnotatedTerm);
        } else {
            return null;
        }
    }

    @Override
    public Synonym addSynonym(List<Term> terms) {
        SynonymStored syn = new SynonymStored();
        for (Term term : terms) {
            if (term instanceof TermStored) {
                syn.terms.add((TermStored) term);
            }
        }
        connect(syn);
        syns.add(syn);
        return syn;
    }

    private void connect(SynonymStored syn) {
        for (TermStored term : syn.terms) {
            if (term.entryId != null) {
                Entry entry = connector.entryId2ItsEntry.get(term.entryId);
                if (!connector.entry2ItsSyns.containsKey(entry)) {
                    connector.entry2ItsSyns.put(entry, new ArrayList<Synonym>());
                }
                connector.entry2ItsSyns.get(entry).add(syn);
            }
        }
    }

    @Override
    public Synonym addSynonym(Sig sig, List<Term> terms) {
        SynonymStored syn = (SynonymStored) addSynonym(terms);
        if (sig instanceof SigStored) {
            syn.sig = (SigStored) sig;
        }
        return syn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" : ");
        sb.append(syns.toString());
        return sb.toString();
    }
}
