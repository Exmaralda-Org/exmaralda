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

import eu.clarin.weblicht.wlfxb.tc.api.DiscourseConnective;
import eu.clarin.weblicht.wlfxb.tc.api.DiscourseConnectivesLayer;
import eu.clarin.weblicht.wlfxb.tc.api.Token;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import eu.clarin.weblicht.wlfxb.utils.WlfUtilities;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = DiscourseConnectivesLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class DiscourseConnectivesLayerStored extends TextCorpusLayerStoredAbstract implements DiscourseConnectivesLayer {

    public static final String XML_NAME = "discourseconnectives";
    @XmlElement(name = DiscourseConnectiveStored.XML_NAME, type = DiscourseConnectiveStored.class)
    private List<DiscourseConnectiveStored> connectives = new ArrayList<DiscourseConnectiveStored>();
    @XmlAttribute(name = CommonAttributes.TAGSET)
    private String typesTagset;
    private TextCorpusLayersConnector connector;

    protected DiscourseConnectivesLayerStored() {
    }

    protected DiscourseConnectivesLayerStored(String typesTagset) {
        this.typesTagset = typesTagset;
    }

    protected DiscourseConnectivesLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        this.connector = connector;
        for (DiscourseConnectiveStored connective : connectives) {
            for (String tokRef : connective.tokRefs) {
                connector.token2ItsDConnective.put(connector.tokenId2ItsToken.get(tokRef), connective);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return connectives.isEmpty();
    }

    @Override
    public int size() {
        return connectives.size();
    }

    @Override
    public String getTypesTagset() {
        return typesTagset;
    }

    @Override
    public DiscourseConnective getConnective(int index) {
        return connectives.get(index);
    }

    @Override
    public DiscourseConnective getConnective(Token token) {
        DiscourseConnective connective = connector.token2ItsDConnective.get(token);
        return connective;
    }

    @Override
    public Token[] getTokens(DiscourseConnective connective) {
        if (connective instanceof DiscourseConnectiveStored) {
            DiscourseConnectiveStored cStored = (DiscourseConnectiveStored) connective;
            return WlfUtilities.tokenIdsToTokens(cStored.tokRefs, connector.tokenId2ItsToken);
        } else {
            return null;
        }
    }

    @Override
    public DiscourseConnective addConnective(List<Token> connectiveTokens) {
        return addConnective(connectiveTokens, null);
    }

    @Override
    public DiscourseConnective addConnective(List<Token> connectiveTokens, String semanticType) {
        DiscourseConnectiveStored connective = new DiscourseConnectiveStored();
        connective.tokRefs = new String[connectiveTokens.size()];
        connective.type = semanticType;
        for (int i = 0; i < connectiveTokens.size(); i++) {
            Token cToken = connectiveTokens.get(i);
            connective.tokRefs[i] = cToken.getID();
            connector.token2ItsDConnective.put(cToken, connective);
        }
        connectives.add(connective);
        return connective;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" {");
        if (typesTagset != null) {
            sb.append(CommonAttributes.TAGSET).append(" ").append(typesTagset);
        }
        sb.append("}: ");
        sb.append(connectives.toString());
        return sb.toString();
    }
}
