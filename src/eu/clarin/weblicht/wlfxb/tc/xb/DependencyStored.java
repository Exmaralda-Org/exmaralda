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

import eu.clarin.weblicht.wlfxb.tc.api.Dependency;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import java.util.Arrays;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author Yana Panchenko
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class DependencyStored implements Dependency {
	
	public static final String XML_NAME = "dependency";
	public static final String XML_ATTR_DEPENDENT_REFERENCE = "depIDs";
	public static final String XML_ATTR_GOVERNOR_REFERENCE = "govIDs";
	
	
	@XmlAttribute(name=CommonAttributes.FUNCTION)
	protected String function;
	@XmlAttribute(name=XML_ATTR_DEPENDENT_REFERENCE, required = true)
	protected String[] depIds;
	@XmlAttribute(name=XML_ATTR_GOVERNOR_REFERENCE)
	protected String[] govIds;
	
	@Override
	public String getFunction() {
		return function;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (function != null) {
			sb.append(function);
			sb.append(" ");
		}
		sb.append(Arrays.toString(depIds)).append(" <- ");
		if (govIds == null) {
			sb.append("[ ]");
		} else {
			sb.append(Arrays.toString(govIds));
		}
		return sb.toString();
	}

}
