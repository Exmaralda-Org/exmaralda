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

import eu.clarin.weblicht.wlfxb.tc.api.CorrectionOperation;
import eu.clarin.weblicht.wlfxb.tc.api.OrthCorrection;
import eu.clarin.weblicht.wlfxb.tc.api.OrthographyLayer;
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
@XmlRootElement(name = OrthographyLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class OrthographyLayerStored extends TextCorpusLayerStoredAbstract implements OrthographyLayer {

    public static final String XML_NAME = "orthography";
    @XmlElement(name = OrthCorrectionStored.XML_NAME)
    private List<OrthCorrectionStored> corrections = new ArrayList<OrthCorrectionStored>();
    private TextCorpusLayersConnector connector;

    protected OrthographyLayerStored() {
    }

    protected OrthographyLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        this.connector = connector;
        for (int i = 0; i < corrections.size(); i++) {
            OrthCorrectionStored corr = corrections.get(i);
            for (String tokRef : corr.tokRefs) {
                connector.token2ItsCorrection.put(connector.tokenId2ItsToken.get(tokRef), corr);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return corrections.isEmpty();
    }

    @Override
    public int size() {
        return corrections.size();
    }

    @Override
    public OrthCorrection getCorrection(int index) {
        OrthCorrection corr = corrections.get(index);
        return corr;
    }

    @Override
    public OrthCorrection getCorrection(Token token) {
        OrthCorrection corr = connector.token2ItsCorrection.get(token);
        return corr;
    }

    @Override
    public Token[] getTokens(OrthCorrection correction) {
        if (correction instanceof OrthCorrectionStored) {
            OrthCorrectionStored corrStored = (OrthCorrectionStored) correction;
            return WlfUtilities.tokenIdsToTokens(corrStored.tokRefs, connector.tokenId2ItsToken);
        } else {
            return null;
        }
    }

    @Override
    public OrthCorrection addCorrection(String correctionString, Token correctedToken, CorrectionOperation operation) {
        List<Token> corrTokens = Arrays.asList(new Token[]{correctedToken});
        return addCorrection(correctionString, corrTokens, operation);
    }

    @Override
    public OrthCorrection addCorrection(String correctionString, List<Token> correctedTokens, CorrectionOperation operation) {
        OrthCorrectionStored corr = new OrthCorrectionStored();
        corr.corrString = correctionString;
        corr.tokRefs = new String[correctedTokens.size()];
        corr.operation = operation;
        for (int i = 0; i < correctedTokens.size(); i++) {
            Token corrToken = correctedTokens.get(i);
            corr.tokRefs[i] = corrToken.getID();
            connector.token2ItsCorrection.put(corrToken, corr);
        }
        corrections.add(corr);
        return corr;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" : ");
        sb.append(corrections.toString());
        return sb.toString();
    }
}
