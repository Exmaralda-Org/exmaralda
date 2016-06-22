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

import eu.clarin.weblicht.wlfxb.tc.api.Reference;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import java.util.Arrays;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = ReferenceStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class ReferenceStored implements Reference {

    public static final String XML_NAME = "reference";
    public static final String ID_PREFIX = "rc_";
    @XmlAttribute(name = CommonAttributes.ID)
    protected String id;
    @XmlAttribute(name = CommonAttributes.TYPE)
    protected String type;
    @XmlAttribute(name = "rel")
    protected String relation;
    @XmlAttribute(name = "target")
    protected String[] targetIds;
    @XmlAttribute(name = CommonAttributes.TOKEN_SEQUENCE_REFERENCE, required = true)
    protected String[] tokRefs;
    @XmlAttribute(name = "mintokIDs", required = true)
    protected String[] minTokRefs;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getRelation() {
        return relation;
    }

    protected void beforeMarshal(Marshaller m) {
        setEmptyTargetToNull();
    }

    private void setEmptyTargetToNull() {
        if (this.targetIds != null && this.targetIds.length == 0) {
            this.targetIds = null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (id != null) {
            sb.append(id);
            sb.append(" ");
        }
        if (type != null) {
            sb.append(type);
            sb.append(" ");
        }
        if (relation != null) {
            sb.append(relation);
            sb.append(" ");
        }
        if (targetIds != null) {
            sb.append("->");
            sb.append(Arrays.toString(targetIds));
        }
        sb.append(" ");
        sb.append(Arrays.toString(tokRefs));
        if (minTokRefs != null) {
            sb.append(" ");
            sb.append(Arrays.toString(minTokRefs));
        }
        return sb.toString();
    }
}
