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

import eu.clarin.weblicht.wlfxb.tc.api.Relation;
import eu.clarin.weblicht.wlfxb.tc.api.RelationsLayer;
import eu.clarin.weblicht.wlfxb.tc.api.Token;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import eu.clarin.weblicht.wlfxb.utils.WlfUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@SuppressWarnings("deprecation")
@XmlRootElement(name = RelationsLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@Deprecated
public class RelationsLayerStored extends TextCorpusLayerStoredAbstract implements RelationsLayer {

    public static final String XML_NAME = "relations";
    @XmlAttribute(name = CommonAttributes.TYPE, required = true)
    private String type;
    @XmlElement(name = RelationStored.XML_NAME)
    private List<RelationStored> relations = new ArrayList<RelationStored>();
    private TextCorpusLayersConnector connector;

    protected RelationsLayerStored() {
    }

    protected RelationsLayerStored(String type) {
        this.type = type;
    }

    protected RelationsLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        this.connector = connector;
        for (RelationStored rel : relations) {
            for (String tokRef : rel.tokRefs) {
                connector.token2ItsRelation.put(connector.tokenId2ItsToken.get(tokRef), rel);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return relations.isEmpty();
    }

    @Override
    public int size() {
        return relations.size();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Relation getRelation(int index) {
        return relations.get(index);
    }

    @Override
    public Relation getRelation(Token token) {
        return connector.token2ItsRelation.get(token);
    }

    @Override
    public Token[] getTokens(Relation relation) {
        if (relation instanceof RelationStored) {
            RelationStored r = (RelationStored) relation;
            return WlfUtilities.tokenIdsToTokens(r.tokRefs, connector.tokenId2ItsToken);
        } else {
            throw new UnsupportedOperationException(WlfUtilities.layersErrorMessage(Relation.class, RelationsLayer.class));
        }
    }

    @Override
    public Relation addRelation(String function, Token relationToken) {
        List<Token> relTokens = Arrays.asList(new Token[]{relationToken});
        return addRelation(function, relTokens);
    }

    @Override
    public Relation addRelation(Token relationToken) {
        return addRelation(null, relationToken);
    }

    @Override
    public Relation addRelation(String function, List<Token> relationTokens) {
        RelationStored rel = new RelationStored();
        rel.function = function;
        rel.tokRefs = new String[relationTokens.size()];
        for (int i = 0; i < relationTokens.size(); i++) {
            Token token = relationTokens.get(i);
            rel.tokRefs[i] = token.getID();
            connector.token2ItsRelation.put(token, rel);
        }
        relations.add(rel);
        return rel;
    }

    @Override
    public Relation addRelation(List<Token> relationTokens) {
        return addRelation(null, relationTokens);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" ");
        sb.append("{");
        sb.append(type);
        sb.append("} :");
        sb.append(relations.toString());
        return sb.toString();
    }
}
