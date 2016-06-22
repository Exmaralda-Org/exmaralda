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

import eu.clarin.weblicht.wlfxb.tc.api.MatchedCorpus;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = MatchedCorpusStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class MatchedCorpusStored implements MatchedCorpus {

    public static final String XML_NAME = "corpus";
    @XmlAttribute(name = CommonAttributes.NAME, required = true)
    private String name;
    @XmlAttribute(name = CommonAttributes.PID, required = true)
    protected String pid;
    @XmlElement(name = MatchedItemStored.XML_NAME)
    protected List<MatchedItemStored> matchedItems = new ArrayList<MatchedItemStored>();

    MatchedCorpusStored() {
    }

    MatchedCorpusStored(String name, String pid) {
        this.name = name;
        this.pid = pid;
    }

    @Override
    public MatchedItemStored[] getMatchedItems() {
        // return array in order not to let user add new things to the items list
        MatchedItemStored[] items = new MatchedItemStored[matchedItems.size()];
        return matchedItems.toArray(items);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPID() {
        return pid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" ");
        sb.append(name);
        sb.append(" ");
        sb.append(pid);
        sb.append(" ");
        sb.append(matchedItems.toString());
        return sb.toString().trim();
    }
}
