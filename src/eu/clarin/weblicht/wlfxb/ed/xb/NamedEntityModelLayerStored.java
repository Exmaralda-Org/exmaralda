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
package eu.clarin.weblicht.wlfxb.ed.xb;

import eu.clarin.weblicht.wlfxb.ed.api.NamedEntityModelLayer;
import eu.clarin.weblicht.wlfxb.ed.api.PhoneticSegmentationLayer;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = NamedEntityModelLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class NamedEntityModelLayerStored extends ExternalDataLayerStored implements NamedEntityModelLayer {

    public static final String XML_NAME = "namedentitymodel";
    
    @XmlAttribute(name = "netype", required = true)
    private String neType;
    @XmlAttribute(name = "modeltype", required = true)
    private String modelType;

    protected NamedEntityModelLayerStored() {
        super();
    }

    protected NamedEntityModelLayerStored(String mimeType, String neType, String modelType) {
        super(mimeType);
        this.neType = neType;
        this.modelType = modelType;
    }
    
    
    public String getNamedEntitiesType() {
        return neType;
    }

    public String getModelType() {
        return modelType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" : ");
        sb.append(super.getDataMimeType());
        sb.append(" ").append(neType);
        sb.append(" ").append(modelType);
        sb.append(" ");
        sb.append(super.getLink());
        return sb.toString();
    }

}
