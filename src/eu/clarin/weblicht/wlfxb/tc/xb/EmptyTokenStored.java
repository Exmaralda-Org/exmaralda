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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author Yana Panchenko
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class EmptyTokenStored implements Token {
	
	
	public static final String XML_NAME = "emptytok";
	public static final String ID_PREFIX = "et_";
	
	@XmlValue
	protected String tokenString;
	@XmlAttribute(name=CommonAttributes.ID, required = true)
	protected String id;
	protected int order;
	
	
	@Override
	public String getString() {
            return tokenString;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(order).append(": ").append(id).append(" ").append(tokenString);
		return sb.toString();
	}



	@Override
	public String getID() {
		return id;
	}


	@Override
	public Long getStart() {
		return null;
	}


	@Override
	public Long getEnd() {
		return null;
	}
	
	public int getOrder() {
		return order;
	}

}
