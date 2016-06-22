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
package eu.clarin.weblicht.wlfxb.lx.xb;

import eu.clarin.weblicht.wlfxb.lx.api.Sig;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = SigStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class SigStored implements Sig {

    public static final String XML_NAME = "sig";
    @XmlAttribute(name = "measure")
    private String measure;
    @XmlValue
    private float value;

    SigStored() {
    }

    SigStored(String measure, float value) {
        this.measure = measure;
        this.value = value;
    }

    @Override
    public String getMeasure() {
        return measure;
    }

    @Override
    public float getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (measure != null) {
            sb.append(measure);
            sb.append(" ");
        }
        sb.append("").append(value);
        return sb.toString();
    }
}
