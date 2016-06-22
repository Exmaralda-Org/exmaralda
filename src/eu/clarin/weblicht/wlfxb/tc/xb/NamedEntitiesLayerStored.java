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

import eu.clarin.weblicht.wlfxb.tc.api.NamedEntitiesLayer;
import eu.clarin.weblicht.wlfxb.tc.api.NamedEntity;
import eu.clarin.weblicht.wlfxb.tc.api.Token;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import eu.clarin.weblicht.wlfxb.utils.WlfUtilities;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = NamedEntitiesLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class NamedEntitiesLayerStored extends TextCorpusLayerStoredAbstract implements NamedEntitiesLayer {

    public static final String XML_NAME = "namedEntities";
    @XmlAttribute(name = CommonAttributes.TYPE, required = true)
    private String type;
    @XmlElement(name = NamedEntityStored.XML_NAME)
    private List<NamedEntityStored> entities = new ArrayList<NamedEntityStored>();
    private Set<String> foundTypes = new HashSet<String>();
    private TextCorpusLayersConnector connector;

    protected NamedEntitiesLayerStored() {
    }

    protected NamedEntitiesLayerStored(String type) {
        this.type = type;
    }

    protected NamedEntitiesLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        this.connector = connector;
        for (NamedEntityStored ne : entities) {
            for (String tokRef : ne.tokRefs) {
                //connector.token2ItsNE.put(connector.tokenId2ItsToken.get(tokRef), ne);
                Token tok = connector.tokenId2ItsToken.get(tokRef);
                if (!connector.token2ItsNE.containsKey(tok)) {
                    connector.token2ItsNE.put(tok, new ArrayList<NamedEntity>());
                }
                connector.token2ItsNE.get(tok).add(ne);
            }
            foundTypes.add(ne.getType());
        }
    }

    @Override
    public boolean isEmpty() {
        return entities.isEmpty();
    }

    @Override
    public int size() {
        return entities.size();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Set<String> getFoundTypes() {
        return foundTypes;
    }

    @Override
    public NamedEntity getEntity(int index) {
        return entities.get(index);
    }

    @Override
    public NamedEntity getEntity(Token token) {
        List<NamedEntity> nes = connector.token2ItsNE.get(token);
        if (nes != null && !nes.isEmpty()) {
            return nes.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<NamedEntity> getEntities(Token token) {
        List<NamedEntity> nes = connector.token2ItsNE.get(token);
        return nes;
    }

    @Override
    public Token[] getTokens(NamedEntity entity) {
        if (entity instanceof NamedEntityStored) {
            NamedEntityStored ne = (NamedEntityStored) entity;
            return WlfUtilities.tokenIdsToTokens(ne.tokRefs, connector.tokenId2ItsToken);
        } else {
            return null;
        }
    }

    @Override
    public NamedEntity addEntity(String entityType, Token tagToken) {
        List<Token> tagTokens = Arrays.asList(new Token[]{tagToken});
        return addEntity(entityType, tagTokens);
    }

    @Override
    public NamedEntity addEntity(String entityType, List<Token> entityTokens) {
        NamedEntityStored ne = new NamedEntityStored();
        ne.type = entityType;
        ne.tokRefs = new String[entityTokens.size()];
        for (int i = 0; i < entityTokens.size(); i++) {
            Token token = entityTokens.get(i);
            ne.tokRefs[i] = token.getID();
            //connector.token2ItsNE.put(token, ne);
            Token tok = connector.tokenId2ItsToken.get(token.getID());
            if (!connector.token2ItsNE.containsKey(tok)) {
                connector.token2ItsNE.put(tok, new ArrayList<NamedEntity>());
            }
            connector.token2ItsNE.get(tok).add(ne);
        }
        entities.add(ne);
        this.foundTypes.add(ne.getType());
        return ne;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" ");
        sb.append("{");
        sb.append(type);
        sb.append("} :");
        sb.append(entities.toString());
        return sb.toString();
    }
}
