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

import eu.clarin.weblicht.wlfxb.tc.api.Constituent;
import eu.clarin.weblicht.wlfxb.tc.api.ConstituentParse;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = ConstituentParseStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class ConstituentParseStored implements ConstituentParse {

    public static final String XML_NAME = "parse";
    public static final String ID_PREFIX = "pc_";
    @XmlAttribute(name = CommonAttributes.ID)
    private String constituentParseId;
    @XmlElement(name = ConstituentStored.XML_NAME, required = true)
    protected ConstituentStored constituentParseRoot;

    @Override
    public Constituent getRoot() {
        return constituentParseRoot;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (constituentParseId != null) {
            sb.append(constituentParseId);
            sb.append(" -> ");
        }
        sb.append(constituentParseRoot.toString());
        return sb.toString();
    }
}
