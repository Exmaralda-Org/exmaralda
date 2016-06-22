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
import eu.clarin.weblicht.wlfxb.tc.api.Orthform;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = SynonymyLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class SynonymyLayerStored extends LexicalSemanticsLayerStored {

    public static final String XML_NAME = "synonymy";

    protected SynonymyLayerStored() {
    }

    protected SynonymyLayerStored(TextCorpusLayersConnector connector) {
        super(connector);
    }

//    protected SynonymyLayerStored(String source) {
//        super(source);
//    }

    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        super.connector = connector;
        for (OrthformStored orthform : super.orthforms) {
            for (String lemRef : orthform.lemmaRefs) {
                connector.lemma2ItsSynonyms.put(connector.lemmaId2ItsLemma.get(lemRef), orthform);
            }
        }
    }

    @Override
    public Orthform getOrthform(Lemma lemma) {
        return super.connector.lemma2ItsSynonyms.get(lemma);
    }

    @Override
    public String toString() {
        return XML_NAME + " " + super.toString();
    }
}
