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

import eu.clarin.weblicht.wlfxb.lx.api.FrequenciesLayer;
import eu.clarin.weblicht.wlfxb.lx.api.Frequency;
import eu.clarin.weblicht.wlfxb.lx.api.Entry;
import eu.clarin.weblicht.wlfxb.lx.api.FrequencyType;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = FrequenciesLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class FrequenciesLayerStored extends LexiconLayerStoredAbstract implements FrequenciesLayer {

    public static final String XML_NAME = "frequencies";
    @XmlAttribute(name = CommonAttributes.TYPE)
    private FrequencyType freqType;
    @XmlElement(name = FrequencyStored.XML_NAME)
    private List<FrequencyStored> frequencies = new ArrayList<FrequencyStored>();
    private LexiconLayersConnector connector;

    protected FrequenciesLayerStored() {
    }
    
    protected FrequenciesLayerStored(FrequencyType freqType) {
        this.freqType = freqType;
    }

    protected FrequenciesLayerStored(LexiconLayersConnector connector) {
        this.connector = connector;
    }

    @Override
    protected void setLayersConnector(LexiconLayersConnector connector) {
        this.connector = connector;
        for (FrequencyStored freq : frequencies) {
            connector.entry2ItsFreq.put(connector.entryId2ItsEntry.get(freq.entryId), freq);
        }
    }

    @Override
    public boolean isEmpty() {
        return frequencies.isEmpty();
    }

    @Override
    public int size() {
        return frequencies.size();
    }
    
    
    @Override
    public FrequencyType getType() {
        return freqType;
    }

    @Override
    public Frequency getFrequency(int index) {
        Frequency freq = frequencies.get(index);
        return freq;
    }

    @Override
    public Frequency getFrequency(Entry entry) {
        Frequency freq = connector.entry2ItsFreq.get(entry);
        return freq;
    }

    @Override
    public Entry getEntry(Frequency freq) {
        if (freq instanceof FrequencyStored) {
            FrequencyStored freqStored = (FrequencyStored) freq;
            return connector.entryId2ItsEntry.get(freqStored.entryId);
        } else {
            return null;
        }
    }

    @Override
    public Frequency addFrequency(Entry entry, double frequency) {
        FrequencyStored freq = new FrequencyStored();
        //int count = tags.size();
        //tagStored.tagId = PosTagStored.ID_PREFIX + count;
        freq.value = frequency;
        freq.entryId = entry.getID();
        connector.entry2ItsFreq.put(entry, freq);
        frequencies.add(freq);
        return freq;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" : ");
        sb.append(frequencies.toString());
        return sb.toString();
    }

}
