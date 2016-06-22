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

/**
 * @author Yana Panchenko
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract  class TermsContainerStored extends LexiconLayerStoredAbstract implements TermsContainer {

    protected LexiconLayersConnector connector;

    protected TermsContainerStored() {
    }

    protected TermsContainerStored(LexiconLayersConnector connector) {
        this.connector = connector;
    }

    @Override
    protected void setLayersConnector(LexiconLayersConnector connector) {
        this.connector = connector;
    }

    @Override
    public String[] getTermsAsStrings(List<? extends Term> terms, boolean includeAnnotatedTerm) {
            List<String> termsAsStrings = new ArrayList<String>();
            for (Term term : terms) {
                if (term instanceof TermStored) {
                    TermStored t = (TermStored) term;
                    if (t.entryId != null) {
                        if (includeAnnotatedTerm) {
                            termsAsStrings.add(connector.entryId2ItsEntry.get(t.entryId).getString());
                        }
                    } else if (t.word != null) {
                        termsAsStrings.add(t.word);
                    }
                }
            }
            return termsAsStrings.toArray(new String[termsAsStrings.size()]);
    }


//    @Override
//    public Term createTerm(Lemma lemma) {
//        TermStored term = new TermStored();
//        term.eId = lemma.getID();
//        return term;
//    }
    
    @Override
    public Term createTerm(Entry entry) {
        TermStored term = new TermStored();
        term.entryId = entry.getID();
        return term;
    }

    @Override
    public Term createTerm(String word) {
        TermStored term = new TermStored();
        term.word = word;
        return term;
    }

    @Override
    public Sig createSig(String measure, float value) {
        SigStored sig = new SigStored(measure, value);
        return sig;
    }


}
