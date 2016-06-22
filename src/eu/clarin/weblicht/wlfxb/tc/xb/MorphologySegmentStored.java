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

import eu.clarin.weblicht.wlfxb.tc.api.MorphologySegment;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = MorphologySegmentStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class MorphologySegmentStored implements MorphologySegment {

    public static final String XML_NAME = "segment";
    @XmlAttribute(name = CommonAttributes.TYPE)
    protected String type;
    @XmlAttribute(name = CommonAttributes.FUNCTION)
    protected String function;
    @XmlAttribute(name = CommonAttributes.CATEGORY)
    protected String category;
    @XmlAttribute(name = CommonAttributes.START_CHAR_OFFSET)
    protected Integer start;
    @XmlAttribute(name = CommonAttributes.END_CHAR_OFFSET)
    protected Integer end;
    // temporary to hold unmarshalled objects before I can transfer them to fs or value
    private List<Object> content = new ArrayList<Object>();
    protected String value;
    protected List<MorphologySegmentStored> subsegments;

    @XmlMixed
    @XmlElementRefs({
        @XmlElementRef(name = MorphologySegmentStored.XML_NAME, type = MorphologySegmentStored.class)})
    protected List<Object> getContent() {
        List<Object> contentToMarshal = new ArrayList<Object>();
        if (subsegments != null) {
            contentToMarshal.addAll(subsegments);
        } else if (value != null) {
            contentToMarshal.add(value);
        } else {
            return null;
        }
        return contentToMarshal;
    }

    void setContent(List<Object> content) {
        this.content = content;
    }

    protected void afterUnmarshal(Unmarshaller u, Object parent) {
        for (Object obj : content) {
            if (obj instanceof String) {
                String v = ((String) obj).trim();
                if (subsegments == null && v.length() > 0) {
                    value = v;
                    return;
                }
            } else if (obj instanceof MorphologySegmentStored) {
                if (subsegments == null) {
                    subsegments = new ArrayList<MorphologySegmentStored>();
                }
                subsegments.add((MorphologySegmentStored) obj);
            }
        }
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getFunction() {
        return function;
    }

    @Override
    public boolean hasCharoffsets() {
        return (start != null && end != null);
    }

    @Override
    public Integer getStart() {
        return start;
    }

    @Override
    public Integer getEnd() {
        return end;
    }

    @Override
    public boolean isTerminal() {
        return (subsegments == null || subsegments.isEmpty());
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public MorphologySegment[] getSubsegments() {
        if (subsegments == null) {
            return null;
        }
        return subsegments.toArray(new MorphologySegment[subsegments.size()]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (type != null) {
            sb.append(type);
            sb.append(" ");
        }
        if (function != null) {
            sb.append(function);
            sb.append(" ");
        }
        if (category != null) {
            sb.append(category);
            sb.append(" ");
        }
        if (hasCharoffsets()) {
            sb.append(start);
            sb.append(" ");
            sb.append(end);
            sb.append(" ");
        }
        if (isTerminal()) {
            sb.append(value);
        } else if (subsegments != null && !subsegments.isEmpty()) {
            sb.append(subsegments.toString());
        }
        return sb.toString();
    }
}
