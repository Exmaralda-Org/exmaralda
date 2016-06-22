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
package eu.clarin.weblicht.wlfxb.md.xb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.*;


@XmlRootElement(name = MetaData.XML_NAME, namespace = MetaData.XML_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
public class MetaData {

    public static final String XML_NAME = "MetaData";
    public static final String XML_NAMESPACE = "http://www.dspin.de/data/metadata";
    
    @XmlElement(name = Services.XML_NAME)
    private Services services;
    
    @XmlElement(name = Source.XML_NAME)
    private Source source;
    @XmlElements({
        @XmlElement(name = "md", type = MetaDataItem.class)})
    private List<MetaDataItem> metaDataItems = new ArrayList<MetaDataItem>();

    public void addMetaDataItem(String name, String value) {
        metaDataItems.add(new MetaDataItem(name, value));
    }

    public List<MetaDataItem> getMetaDataItems() {
        return Collections.unmodifiableList(metaDataItems);
    }

    public Services getServices() {
        return services;
    }

    public Source getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "MetaData{" + "services=" + services + ", "
                + "source=" + source + ", "
                + "metaDataItems=" + metaDataItems + '}';
    }

}
