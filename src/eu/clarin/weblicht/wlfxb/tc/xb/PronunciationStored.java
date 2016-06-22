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

import eu.clarin.weblicht.wlfxb.tc.api.Pronunciation;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = PronunciationStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class PronunciationStored implements Pronunciation {

    public static final String XML_NAME = "pron";
    @XmlAttribute(name = CommonAttributes.TYPE, required = true)
    protected PronunciationType type;
    @XmlAttribute(name = "onset")
    protected Float onset;
    @XmlAttribute(name = "offset")
    protected Float offset;
    @XmlAttribute(name = "cp")
    protected String cp;
    @XmlAttribute(name = "rp")
    protected String rp;
    @XmlElement(name = PronunciationStored.XML_NAME)
    protected List<PronunciationStored> children = new ArrayList<PronunciationStored>();

    @Override
    public PronunciationType getType() {
        return type;
    }

    @Override
    public String getCanonical() {
        return cp;
    }

    @Override
    public String getRealized() {
        return rp;
    }

    @Override
    public Float getOnsetInSeconds() {
        return onset;
    }

    @Override
    public Float getOffsetInSeconds() {
        return offset;
    }

    @Override
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    @Override
    public boolean hasOnOffsets() {
        return (onset != null && offset != null);
    }

    @Override
    public Pronunciation[] getChildren() {
        return children.toArray(new Pronunciation[children.size()]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        if (cp != null) {
            sb.append(" ");
            sb.append(cp);
        }
        if (rp != null) {
            sb.append(" ");
            sb.append(rp);
        }
        if (hasOnOffsets()) {
            sb.append(" ").append(onset);
            sb.append(" ").append(offset);
        }
        if (hasChildren()) {
            sb.append(" ( ");
            sb.append(children.toString());
            sb.append(" )");
        }
        return sb.toString();
    }
}
