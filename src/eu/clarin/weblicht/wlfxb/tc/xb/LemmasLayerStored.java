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
import eu.clarin.weblicht.wlfxb.tc.api.LemmasLayer;
import eu.clarin.weblicht.wlfxb.tc.api.Token;
import eu.clarin.weblicht.wlfxb.utils.WlfUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = LemmasLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class LemmasLayerStored extends TextCorpusLayerStoredAbstract implements LemmasLayer {

    public static final String XML_NAME = "lemmas";
    @XmlElement(name = LemmaStored.XML_NAME)
    private List<LemmaStored> lemmas = new ArrayList<LemmaStored>();
    private TextCorpusLayersConnector connector;

    protected LemmasLayerStored() {
    }

    protected LemmasLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    @Override
    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        this.connector = connector;
        for (int i = 0; i < lemmas.size(); i++) {
            LemmaStored lemma = lemmas.get(i);
            for (String tokRef : lemma.tokRefs) {
                connector.token2ItsLemma.put(connector.tokenId2ItsToken.get(tokRef), lemma);
            }
            if (lemma.lemmaId == null) {
                lemma.lemmaId = LemmaStored.ID_PREFIX + "_" + i;
            }
            connector.lemmaId2ItsLemma.put(lemma.lemmaId, lemma);
        }
    }

    @Override
    public boolean isEmpty() {
        return lemmas.isEmpty();
    }

    @Override
    public int size() {
        return lemmas.size();
    }

    @Override
    public Lemma getLemma(int index) {
        Lemma lemma = lemmas.get(index);
        return lemma;
    }

    @Override
    public Lemma getLemma(Token token) {
        Lemma lemma = connector.token2ItsLemma.get(token);
        return lemma;
    }

    @Override
    public Token[] getTokens(Lemma lemma) {
        if (lemma instanceof LemmaStored) {
            LemmaStored lemmaStored = (LemmaStored) lemma;
            return WlfUtilities.tokenIdsToTokens(lemmaStored.tokRefs, connector.tokenId2ItsToken);
        } else {
            return null;
        }
    }

    @Override
    public Lemma addLemma(String lemmaString, Token lemmaToken) {
        List<Token> lemmaTokens = Arrays.asList(new Token[]{lemmaToken});
        return addLemma(lemmaString, lemmaTokens);
    }

    @Override
    public Lemma addLemma(String lemmaString, List<Token> lemmaTokens) {
        LemmaStored lemma = new LemmaStored();
        int lemmaCount = lemmas.size();
        lemma.lemmaId = LemmaStored.ID_PREFIX + lemmaCount;
        lemma.lemmaString = lemmaString;
        lemma.tokRefs = new String[lemmaTokens.size()];
        for (int i = 0; i < lemmaTokens.size(); i++) {
            Token lemmaToken = lemmaTokens.get(i);
            lemma.tokRefs[i] = lemmaToken.getID();
            connector.token2ItsLemma.put(lemmaToken, lemma);
        }
        lemmas.add(lemma);
        return lemma;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" : ");
        sb.append(lemmas.toString());
        return sb.toString();
    }
}
