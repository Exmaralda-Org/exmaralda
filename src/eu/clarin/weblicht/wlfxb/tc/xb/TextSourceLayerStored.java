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

import eu.clarin.weblicht.wlfxb.tc.api.TextSourceLayer;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author Çağrı Çöltekin
 *
 */
@XmlRootElement(name = TextSourceLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class TextSourceLayerStored extends TextCorpusLayerStoredAbstract
        implements TextSourceLayer {

    @XmlValue
    private String text;
    @XmlAttribute(name = CommonAttributes.TYPE, required = true)
    private String type;
    @XmlAttribute(name = CommonAttributes.EXTREF, required = false)
    private String extRef;
    public static final String XML_NAME = "textSource";

    public TextSourceLayerStored() {
        this.text = "";
        this.text = null;
        this.extRef = null;
    }

    @Override
    public boolean isEmpty() {
        return (text == null || text.length() == 0);
    }

    @Override
    public int size() {
        return text.length();
    }

    @Override
    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        // we do not need one
    }
    
    @Override
    public String getText() {
        return this.text;
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
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
    
    @Override
    public String getExtRef() {
        return this.extRef;
    }
    
    @Override
    public void setExtRef(String extRef) {
        this.extRef = extRef;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" : ");
        sb.append(text);
        return sb.toString();
    }

}
