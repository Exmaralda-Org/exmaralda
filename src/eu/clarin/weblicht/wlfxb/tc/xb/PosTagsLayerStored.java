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

import eu.clarin.weblicht.wlfxb.tc.api.PosTag;
import eu.clarin.weblicht.wlfxb.tc.api.PosTagsLayer;
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
@XmlRootElement(name = PosTagsLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class PosTagsLayerStored extends TextCorpusLayerStoredAbstract implements PosTagsLayer {

    public static final String XML_NAME = "POStags";
    @XmlAttribute(name = CommonAttributes.TAGSET, required = true)
    private String tagset;
    @XmlElement(name = PosTagStored.XML_NAME)
    private List<PosTagStored> tags = new ArrayList<PosTagStored>();
    private TextCorpusLayersConnector connector;

    protected PosTagsLayerStored() {
    }

    protected PosTagsLayerStored(String tagset) {
        this.tagset = tagset;
    }

    protected PosTagsLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        this.connector = connector;
        for (PosTagStored tag : tags) {
            for (String tokRef : tag.tokRefs) {
                connector.token2ItsPosTag.put(connector.tokenId2ItsToken.get(tokRef), tag);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return tags.isEmpty();
    }

    @Override
    public int size() {
        return tags.size();
    }

    @Override
    public String getTagset() {
        return tagset;
    }

    @Override
    public PosTag getTag(int index) {
        PosTag tag = tags.get(index);
        return tag;
    }

    @Override
    public PosTag getTag(Token token) {
        PosTag tag = connector.token2ItsPosTag.get(token);
        return tag;
    }

    @Override
    public Token[] getTokens(PosTag tag) {
        if (tag instanceof PosTagStored) {
            PosTagStored tagStored = (PosTagStored) tag;
            return WlfUtilities.tokenIdsToTokens(tagStored.tokRefs, connector.tokenId2ItsToken);
        } else {
            return null;
        }
    }

    @Override
    public PosTag addTag(String tagString, Token tagToken) {
        List<Token> tagTokens = Arrays.asList(new Token[]{tagToken});
        return addTag(tagString, tagTokens);
    }

    @Override
    public PosTag addTag(String tagString, List<Token> tagTokens) {
        PosTagStored tag = new PosTagStored();
        //int count = tags.size();
        //tagStored.tagId = PosTagStored.ID_PREFIX + count;
        tag.tagString = tagString;
        tag.tokRefs = new String[tagTokens.size()];
        for (int i = 0; i < tagTokens.size(); i++) {
            Token token = tagTokens.get(i);
            tag.tokRefs[i] = token.getID();
            connector.token2ItsPosTag.put(token, tag);
        }
        tags.add(tag);
        return tag;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" ");
        sb.append("{");
        sb.append(tagset);
        sb.append("} :");
        sb.append(tags.toString());
        return sb.toString();
    }
}
