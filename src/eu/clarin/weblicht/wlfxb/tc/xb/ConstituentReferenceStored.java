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

import eu.clarin.weblicht.wlfxb.tc.api.ConstituentReference;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = ConstituentReferenceStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class ConstituentReferenceStored implements ConstituentReference {

    public static final String XML_NAME = "cref";
    public static final String XML_ATTRIBUTE_EDGE_LABEL = "edge";
    public static final String XML_ATTRIBUTE_CREF = "constID";
    @XmlAttribute(name = XML_ATTRIBUTE_EDGE_LABEL, required = true)
    private String edge;
    @XmlAttribute(name = XML_ATTRIBUTE_CREF, required = true)
    protected String constId;

    ConstituentReferenceStored() {
    }

    ConstituentReferenceStored(ConstituentStored cref, String edgeLabel) {
        this.edge = edgeLabel;
        this.constId = cref.constituentId;
    }

    @Override
    public String getEdgeLabel() {
        return edge;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(edge);
        sb.append(" -> ");
        sb.append(constId);
        return sb.toString();
    }
}
