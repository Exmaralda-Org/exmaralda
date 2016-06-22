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
package eu.clarin.weblicht.wlfxb.tc.xb;

import eu.clarin.weblicht.wlfxb.tc.api.TextLayer;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = TextLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class TextLayerStored extends TextCorpusLayerStoredAbstract implements TextLayer {

    @XmlValue
    private String text;
    public static final String XML_NAME = "text";

    protected TextLayerStored() {
        this.text = "";
    }

    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        // doesn't need connector
    }

    @Override
    public boolean isEmpty() {
        return (text == null || text.length() == 0);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void addText(String text) {
        if (this.text == null) {
            this.text = text;
        } else {
            this.text += text;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" : ");
        sb.append(text);
        return sb.toString();
    }

    @Override
    public int size() {
        return text.length();
    }
//	@XmlElement(name=TextSegmentStored.XML_NAME, type=TextSegmentStored.class)
//	List<TextSegment> textSegments = new ArrayList<TextSegment>(); 
//
//	
//	public List<TextSegment> getTextSegments() {
//		return textSegments;
//	}
//
//
//	public LayerProfile getProfile() {
//		LayerProfile profile = new LayerProfile();
//		return profile;
//	}
//	
//	@Override
//	public String toString() {
//		StringBuilder sb = new StringBuilder(XML_NAME);
//		sb.append(" ");
//		sb.append(getProfile().toString());
//		sb.append(": ");
//		sb.append(textSegments.toString());
//		return sb.toString();
//	}
}
