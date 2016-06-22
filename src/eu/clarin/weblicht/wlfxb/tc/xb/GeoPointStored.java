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

import eu.clarin.weblicht.wlfxb.tc.api.GeoPoint;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import java.util.Arrays;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = GeoPointStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class GeoPointStored implements GeoPoint {

    public static final String XML_NAME = "gpoint";
    @XmlAttribute(name = CommonAttributes.ID)
    private String id;
    @XmlAttribute(name = "lon", required = true)
    protected String longitude;
    @XmlAttribute(name = "lat", required = true)
    protected String latitude;
    @XmlAttribute(name = "alt")
    protected Double altitude;
    @XmlAttribute(name = "continent")
    protected String continent;
    @XmlAttribute(name = "country")
    protected String country;
    @XmlAttribute(name = "capital")
    protected String capital;
    @XmlAttribute(name = CommonAttributes.TOKEN_SEQUENCE_REFERENCE, required = true)
    protected String[] tokRefs;

    @Override
    public String getLongitude() {
        return longitude;
    }

    @Override
    public String getLatitude() {
        return latitude;
    }

    @Override
    public Double getAltitude() {
        return altitude;
    }

    @Override
    public String getContinent() {
        return continent;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public String getCapital() {
        return capital;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (id != null) {
            sb.append(id);
            sb.append(" -> ");
        }
        sb.append(" ").append(longitude);
        sb.append(" ").append(latitude);
        if (altitude != null) {
            sb.append(" ").append(altitude);
        }
        if (continent != null) {
            sb.append(" ").append(continent);
        }
        if (country != null) {
            sb.append(" ").append(country);
        }
        if (capital != null) {
            sb.append(" ").append(capital);
        }

        sb.append(Arrays.toString(tokRefs));
        return sb.toString();
    }
}
