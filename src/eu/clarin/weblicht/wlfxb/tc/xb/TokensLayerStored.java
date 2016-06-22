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

import eu.clarin.weblicht.wlfxb.tc.api.Token;
import eu.clarin.weblicht.wlfxb.tc.api.TokensLayer;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = TokensLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class TokensLayerStored extends TextCorpusLayerStoredAbstract implements TokensLayer {

    public static final String XML_NAME = "tokens";
    private TextCorpusLayersConnector connector;
    @XmlElement(name = TokenStored.XML_NAME)
    private List<TokenStored> tokens = new ArrayList<TokenStored>();
    @XmlAttribute(name = CommonAttributes.CHAR_OFFSETS)
    private Boolean charOffsets;

    protected TokensLayerStored() {
    }

    protected TokensLayerStored(Boolean hasCharOffsets) {
        this.charOffsets = hasCharOffsets;
    }

    protected TokensLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        this.connector = connector;
        for (TokenStored token : tokens) {
            token.order = this.connector.tokenId2ItsToken.size();
            this.connector.tokenId2ItsToken.put(token.tokenId, token);
        }
        this.connector.tokens = tokens;
    }

    @Override
    public Token getToken(int index) {
        Token token = tokens.get(index);
        return token;
    }

    @Override
    public Token getToken(String tokenId) {
        Token token = connector.tokenId2ItsToken.get(tokenId);
        return token;
    }

    @Override
    public Token addToken(String tokenString) {
        int tokenCount = tokens.size();
        String tokenId = TokenStored.ID_PREFIX + tokenCount;
        return addToken(tokenString, null, null, tokenId);
    }

    @Override
    public Token addToken(String tokenString, String tokenId) {
        return addToken(tokenString, null, null, tokenId);
    }

    @Override
    public Token addToken(String tokenString, long start, long end) {
        int tokenCount = tokens.size();
        String tokenId = TokenStored.ID_PREFIX + tokenCount;
        return addToken(tokenString, (Long) start, (Long) end, tokenId);
    }

    @Override
    public Token addToken(String tokenString, long start, long end, String tokenId) {
        return addToken(tokenString, (Long) start, (Long) end, tokenId);
    }

    private Token addToken(String tokenString, Long start, Long end, String tokenId) {
        TokenStored token = new TokenStored();
        token.tokenId = tokenId;
        token.tokenString = tokenString;
        if (start != null && end != null) {
            token.start = start;
            this.charOffsets = true;
        }
        token.order = tokens.size();
        connector.tokenId2ItsToken.put(token.tokenId, token);
        tokens.add(token);
        return token;
    }

    @Override
    public boolean isEmpty() {
        return tokens.isEmpty();
    }

    @Override
    public int size() {
        return tokens.size();
    }

    @Override
    public boolean hasCharOffsets() {
        if (charOffsets == null) {
            return false;
        }
        return charOffsets;
    }

    protected void beforeMarshal(Marshaller m) {
        setFalseAttrToNull();
    }

    private void setFalseAttrToNull() {
        if (this.charOffsets == Boolean.FALSE) {
            this.charOffsets = null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" {");
        if (hasCharOffsets()) {
            sb.append(CommonAttributes.CHAR_OFFSETS).append(" ").append(Boolean.toString(charOffsets));
        }
        sb.append("}: ");
        sb.append(tokens.toString());
        return sb.toString();
    }
}
