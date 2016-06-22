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
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = TokenStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"end", "start", "tokenId"})
public class TokenStored implements Token {

    public static final String XML_NAME = "token";
    public static final String ID_PREFIX = "t_";
    @XmlValue
    protected String tokenString;
    @XmlAttribute(name = CommonAttributes.ID, required = true)
    protected String tokenId; //TODO: see if it makes sense to store order instead of tokenID, 
    //and in connector to store tokenIDs and lists where index correspond to order instead of maps... to ... instead of Token to ... 
    @XmlAttribute(name = CommonAttributes.START_CHAR_OFFSET)
    protected Long start;
    //@XmlAttribute(name=CommonAttributes.END_CHAR_OFFSET)
    //Long end;
    protected int order;

    @Override
    public String getString() {
        return tokenString;
    }

    @Override
    public String getID() {
        return tokenId;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public Long getStart() {
        return start;
    }

    @Override
    @XmlAttribute(name = CommonAttributes.END_CHAR_OFFSET)
    public Long getEnd() {
        if (start != null) {
            return start + tokenString.length();
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(order + ": " + tokenId);
        sb.append(" -> ");
        sb.append(tokenString);
        if (start != null) {
            sb.append(" (");
            sb.append(start);
            sb.append("-");
            sb.append(getEnd());
            sb.append(")");
        }
        return sb.toString();
    }
}
