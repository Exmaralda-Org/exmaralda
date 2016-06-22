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
/**
 *
 */
package eu.clarin.weblicht.wlfxb.tc.xb;

import eu.clarin.weblicht.wlfxb.tc.api.Lemma;
import eu.clarin.weblicht.wlfxb.tc.api.LexicalSemanticsLayer;
import eu.clarin.weblicht.wlfxb.tc.api.Orthform;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import eu.clarin.weblicht.wlfxb.utils.WlfUtilities;
import java.util.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Yana Panchenko
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class LexicalSemanticsLayerStored extends TextCorpusLayerStoredAbstract implements LexicalSemanticsLayer {

//   @XmlAttribute(name = CommonAttributes.SOURCE)
//    protected String source;
    @XmlElement(name = OrthformStored.XML_NAME)
    protected List<OrthformStored> orthforms = new ArrayList<OrthformStored>();
    protected TextCorpusLayersConnector connector;
    private Map<String, OrthformStored> orthformValue2Orthform = new HashMap<String, OrthformStored>();

    protected LexicalSemanticsLayerStored() {
    }

//    protected LexicalSemanticsLayerStored(String source) {
//        this.source = source;
//    }

    protected LexicalSemanticsLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    @Override
    public boolean isEmpty() {
        return orthforms.isEmpty();
    }

//    @Override
//    public String getSource() {
//        return source;
//    }

    @Override
    public int size() {
        return orthforms.size();
    }

    @Override
    public Orthform getOrthform(int index) {
        return orthforms.get(index);
    }

    @Override
    public Lemma[] getLemmas(Orthform orthform) {
        if (orthform instanceof OrthformStored) {
            OrthformStored orth = (OrthformStored) orthform;
            Lemma[] lemmas = new Lemma[orth.lemmaRefs.length];
            for (int i = 0; i < orth.lemmaRefs.length; i++) {
                lemmas[i] = this.connector.lemmaId2ItsLemma.get((orth).lemmaRefs[i]);
            }
            return lemmas;
        } else {
            throw new UnsupportedOperationException(WlfUtilities.layersErrorMessage(Orthform.class, LexicalSemanticsLayerStored.class));
        }
    }

    @Override
    public Orthform addOrthform(String orthformValues, Lemma lemma) {
        LemmaStored lem = null;
        if (lemma instanceof LemmaStored) {
            lem = (LemmaStored) lemma;
        } else {
            throw new UnsupportedOperationException(WlfUtilities.layersErrorMessage(Lemma.class, LemmasLayerStored.class));
        }
        if (!this.orthformValue2Orthform.containsKey(orthformValues)) {
            OrthformStored orthform = new OrthformStored();
            orthform.values = orthformValues;
            this.orthformValue2Orthform.put(orthformValues, orthform);
            orthforms.add(orthform);
        }
        OrthformStored orthform = this.orthformValue2Orthform.get(orthformValues);
        addLemma(orthform, lem);
        return orthform;
    }

    @Override
    public Orthform addOrthform(String[] orthformValues, Lemma lemma) {
        String values = Arrays.toString(orthformValues);
        return addOrthform(values.substring(1, values.length() - 1), lemma);
    }

    private void addLemma(OrthformStored orthform, LemmaStored lemma) {
        if (orthform.lemmaRefs == null) {
            orthform.lemmaRefs = new String[]{lemma.lemmaId};
        } else {
            orthform.lemmaRefs = Arrays.copyOf(orthform.lemmaRefs, orthform.lemmaRefs.length + 1);
            orthform.lemmaRefs[orthform.lemmaRefs.length - 1] = lemma.lemmaId;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
//        sb.append("{");
//        sb.append(source);
//        sb.append("}");
        sb.append(" : ");
        sb.append(orthforms.toString());
        return sb.toString();
    }
}
