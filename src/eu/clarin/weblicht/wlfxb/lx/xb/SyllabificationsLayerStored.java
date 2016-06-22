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

import eu.clarin.weblicht.wlfxb.lx.api.Entry;
import eu.clarin.weblicht.wlfxb.lx.api.Syllabification;
import eu.clarin.weblicht.wlfxb.lx.api.SyllabificationsLayer;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = SyllabificationsLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class SyllabificationsLayerStored extends LexiconLayerStoredAbstract implements SyllabificationsLayer {

    public static final String XML_NAME = "syllabifications";
    @XmlElement(name = SyllabificationStored.XML_NAME)
    private List<SyllabificationStored> syls = new ArrayList<SyllabificationStored>();
    private LexiconLayersConnector connector;

    protected SyllabificationsLayerStored() {
    }

    protected SyllabificationsLayerStored(LexiconLayersConnector connector) {
        this.connector = connector;
    }

    @Override
    protected void setLayersConnector(LexiconLayersConnector connector) {
        this.connector = connector;
        for (SyllabificationStored syl : syls) {
            connector.entry2ItsSyllab.put(connector.entryId2ItsEntry.get(syl.entryId), syl);
        }
    }

    @Override
    public boolean isEmpty() {
        return syls.isEmpty();
    }

    @Override
    public int size() {
        return syls.size();
    }

    @Override
    public Syllabification getSyllabification(int index) {
        Syllabification syl = syls.get(index);
        return syl;
    }

    @Override
    public Syllabification getSyllabification(Entry entry) {
        Syllabification syl = connector.entry2ItsSyllab.get(entry);
        return syl;
    }

    @Override
    public Entry getEntry(Syllabification syllabification) {
        if (syllabification instanceof SyllabificationStored) {
            SyllabificationStored sylStored = (SyllabificationStored) syllabification;
            return connector.entryId2ItsEntry.get(sylStored.entryId);
        } else {
            return null;
        }
    }

    @Override
    public Syllabification addSyllabification(String syllabificationString, Entry entry) {
        SyllabificationStored syl = new SyllabificationStored();
        syl.syllabString = syllabificationString;
        syl.entryId = entry.getID();
        connector.entry2ItsSyllab.put(entry, syl);
        syls.add(syl);
        return syl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" : ");
        sb.append(syls.toString());
        return sb.toString();
    }

}
