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
import eu.clarin.weblicht.wlfxb.lx.api.EntriesLayer;
import eu.clarin.weblicht.wlfxb.lx.api.EntryType;
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
@XmlRootElement(name = EntriesLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class EntriesLayerStored extends LexiconLayerStoredAbstract implements EntriesLayer {

    public static final String XML_NAME = "entries";
    private LexiconLayersConnector connector;
    @XmlAttribute(name = "type")
    private EntryType entryType;
    @XmlElement(name = EntryStored.XML_NAME)
    private List<EntryStored> entries = new ArrayList<EntryStored>();

    protected EntriesLayerStored() {
    }
    
    protected EntriesLayerStored(EntryType type) {
        this.entryType = type;
    }

    protected EntriesLayerStored(LexiconLayersConnector connector) {
        this.connector = connector;
    }

    @Override
    public void setLayersConnector(LexiconLayersConnector connector) {
        this.connector = connector;
        for (EntryStored entry : entries) {
            this.connector.entryId2ItsEntry.put(entry.entryId, entry);
        }
    }
    
    @Override
    public EntryType getType() {
        return entryType;
    }

    @Override
    public Entry getEntry(int index) {
        return entries.get(index);
    }

    @Override
    public Entry getEntry(String entryId) {
        Entry entry = connector.entryId2ItsEntry.get(entryId);
        return entry;
    }

    @Override
    public Entry addEntry(String entryString) {
        EntryStored entry = new EntryStored();
        int entryCount = entries.size();
        entry.entryId = EntryStored.ID_PREFIX + entryCount;
        entry.entryString = entryString;
        connector.entryId2ItsEntry.put(entry.entryId, entry);
        entries.add(entry);
        return entry;
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" ");
        sb.append(entryType);
        sb.append(" : ");
        sb.append(entries.toString());
        return sb.toString();
    }
}
