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
import eu.clarin.weblicht.wlfxb.tc.api.DependencyParse;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * @author Yana Panchenko
 *
 */
public class DependencyParseStored implements DependencyParse {

    public static final String XML_NAME = "parse";
    public static final String ID_PREFIX = "pd_";
    @XmlAttribute(name = CommonAttributes.ID)
    private String parseId;
    @XmlElement(name = DependencyStored.XML_NAME, required = true)
    protected List<DependencyStored> dependencies;
    @XmlElement(name = EmptyTokenStored.XML_NAME)
    @XmlElementWrapper(name = "emptytoks")
    protected List<EmptyTokenStored> emptytoks;

    @Override
    public Dependency[] getDependencies() {
        if (dependencies == null) {
            return new Dependency[0];
        }
        return dependencies.toArray(new Dependency[dependencies.size()]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (parseId != null) {
            sb.append(parseId);
            sb.append(" -> ");
        }
        sb.append(dependencies.toString());
//		if (emptytoks != null) {
//			sb.append(emptytoks.toString());
//		}
        return sb.toString();
    }
}
