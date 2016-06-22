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
import eu.clarin.weblicht.wlfxb.tc.api.MorphologyAnalysis;
import eu.clarin.weblicht.wlfxb.tc.api.MorphologySegment;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = MorphologyAnalysisStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class MorphologyAnalysisStored implements MorphologyAnalysis {

    public static final String XML_NAME = "analysis";
    @XmlAttribute(name = CommonAttributes.TOKEN_SEQUENCE_REFERENCE)
    protected String[] tokRefs;
    @XmlElement(name = MorphologyTagStored.XML_NAME)
    protected MorphologyTagStored tag;
    @XmlElement(name = MorphologySegmentStored.XML_NAME)
    @XmlElementWrapper(name = "segmentation")
    protected List<MorphologySegmentStored> segments;

    @Override
    public Feature[] getFeatures() {
        return tag.fs.getFeatures();
    }

    @Override
    public MorphologySegment[] getSegmentation() {
        if (segments == null) {
            return null;
        } else {
            return segments.toArray(new MorphologySegment[segments.size()]);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Arrays.toString(tokRefs));
        sb.append("( ");
        sb.append(tag.toString());
        sb.append(" )");
        if (segments != null) {
            sb.append("[ ");
            sb.append(segments.toString());
            sb.append(" ]");
        }
        return sb.toString();
    }
}
