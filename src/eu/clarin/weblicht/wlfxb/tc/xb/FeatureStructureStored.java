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

import eu.clarin.weblicht.wlfxb.tc.api.Feature;
import eu.clarin.weblicht.wlfxb.tc.api.FeatureStructure;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = FeatureStructureStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class FeatureStructureStored implements FeatureStructure {

    public static final String XML_NAME = "fs";
    @XmlElement(name = FeatureStored.XML_NAME, type = FeatureStored.class)
    protected List<FeatureStored> features = new ArrayList<FeatureStored>();

    @Override
    public Feature[] getFeatures() {
        return features.toArray(new Feature[features.size()]);
    }

    @Override
    public String toString() {
        return features.toString();
    }
}
