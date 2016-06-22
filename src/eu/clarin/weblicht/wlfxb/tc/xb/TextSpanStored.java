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

import eu.clarin.weblicht.wlfxb.tc.api.TextSpan;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yana Panchenko
 *
 */
//@XmlRootElement(name = TextSpanStored.XML_NAME)
//@XmlAccessorType(XmlAccessType.NONE)
//public class TextSpanStored implements TextSpan {
//
//    public static final String XML_NAME = "textspan";
//    @XmlAttribute(name = CommonAttributes.START_TOKEN)
//    protected String startToken;
//    @XmlAttribute(name = CommonAttributes.END_TOKEN)
//    protected String endToken;
//    @XmlAttribute(name = CommonAttributes.TYPE)
//    protected TextSpanType type;
//
//    @Override
//    public TextSpanType getType() {
//        return type;
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder(type.name());
//        if (startToken != null && endToken != null) {
//            sb.append(" ");
//            sb.append(startToken);
//            sb.append(" - ");
//            sb.append(endToken);
//        }
//        return sb.toString();
//    }
//}

@XmlRootElement(name = TextSpanStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class TextSpanStored implements TextSpan {

    public static final String XML_NAME = "textspan";
    @XmlAttribute(name = "startChar")
    protected Integer startChar;
    @XmlAttribute(name = "endChar")
    protected Integer endChar;
    @XmlAttribute(name = CommonAttributes.START_TOKEN)
    protected String startToken;
    @XmlAttribute(name = CommonAttributes.END_TOKEN)
    protected String endToken;
    @XmlAttribute(name = CommonAttributes.TYPE)
    protected String type;
    
    protected String value;
    protected List<TextSpanStored> subspans;
    
    // temporary to hold unmarshalled objects before I can transfer them to fs or value
    private List<Object> content = new ArrayList<Object>();
    
    @XmlMixed
    @XmlElementRefs({
    @XmlElementRef(name = TextSpanStored.XML_NAME, type = TextSpanStored.class)})
    protected List<Object> getContent() {
        List<Object> contentToMarshal = new ArrayList<Object>();
        if (subspans != null) {
            contentToMarshal.addAll(subspans);
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
                if (subspans == null && v.length() > 0) {
                    value = v;
                    return;
                }
            } else if (obj instanceof TextSpanStored) {
                if (subspans == null) {
                    subspans = new ArrayList<TextSpanStored>();
                }
                subspans.add((TextSpanStored) obj);
            }
        }
    }


    @Override
    public String getType() {
        return type;
    }


    @Override
    public boolean isTerminal() {
        return (subspans == null || subspans.isEmpty());
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public TextSpanStored[] getSubspans() {
        if (subspans == null) {
            return null;
        }
        return subspans.toArray(new TextSpanStored[subspans.size()]);
    }

    @Override
    public Integer getStartChar() {
        return startChar;
    }

    @Override
    public Integer getEndChar() {
        return endChar;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (type != null) {
            sb.append(type);
            sb.append(" ");
        }
        if (startToken != null && endToken != null) {
            sb.append(" ");
            sb.append(startToken);
            sb.append(" - ");
            sb.append(endToken);
        }
        if (startChar != null && endChar != null) {
            sb.append(" ");
            sb.append(startChar);
            sb.append(" - ");
            sb.append(endChar);
        }
        if (isTerminal()) {
            sb.append(value);
        } else if (subspans != null && !subspans.isEmpty()) {
            sb.append(subspans.toString());
        }
        return sb.toString();
    }
    
}
