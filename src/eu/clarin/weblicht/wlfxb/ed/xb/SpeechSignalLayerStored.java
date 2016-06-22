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

import eu.clarin.weblicht.wlfxb.ed.api.SpeechSignalLayer;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = SpeechSignalLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class SpeechSignalLayerStored extends ExternalDataLayerStored implements SpeechSignalLayer {

    @XmlAttribute(name = "numberchannels")
    private Integer numberOfChannels;
    public static final String XML_NAME = "speechsignal";

    protected SpeechSignalLayerStored() {
        super();
    }

    protected SpeechSignalLayerStored(String mimeType, Integer numberOfChannels) {
        super(mimeType);
        this.numberOfChannels = numberOfChannels;
    }

    protected SpeechSignalLayerStored(int numberOfChannels) {
        super();
        this.numberOfChannels = numberOfChannels;
    }

    @Override
    public Integer getNumberOfChannels() {
        return numberOfChannels;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" : ");
        sb.append(super.getDataMimeType());
        if (numberOfChannels != null) {
            sb.append(" ").append(numberOfChannels);
        }
        sb.append(" ");
        sb.append(super.getLink());
        return sb.toString();
    }
}
