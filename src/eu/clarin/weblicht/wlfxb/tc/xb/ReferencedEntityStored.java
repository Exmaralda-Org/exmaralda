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
import eu.clarin.weblicht.wlfxb.tc.api.ReferencedEntity;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = ReferencedEntityStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class ReferencedEntityStored implements ReferencedEntity {

    public static final String XML_NAME = "entity";
    @XmlAttribute(name = CommonAttributes.ID)
    protected String id;
    @XmlElement(name = "extref")
    protected ExternalReferenceStored externalRef;
    @XmlElement(name = ReferenceStored.XML_NAME)
    protected List<ReferenceStored> references = new ArrayList<ReferenceStored>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (id != null) {
            sb.append(id);
            sb.append(" -> ");
        }
        if (externalRef != null) {
            sb.append(" ");
            sb.append(externalRef.toString());
        }
        sb.append(references.toString());
        return sb.toString();
    }

    @Override
    public String getExternalId() {
        if (this.externalRef != null) {
            return this.externalRef.refid;
        }
        return null;
    }

    @Override
    public Reference[] getReferences() {
        if (!references.isEmpty()) {
            return references.toArray(new Reference[references.size()]);
        }
        return null;
    }
}
