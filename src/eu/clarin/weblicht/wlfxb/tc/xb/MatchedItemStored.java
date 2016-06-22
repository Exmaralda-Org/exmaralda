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

import eu.clarin.weblicht.wlfxb.tc.api.MatchedItem;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = MatchedItemStored.XML_NAME)
@XmlType(propOrder = {"targets", "categories", "srcIds", "tokIds"})
public class MatchedItemStored implements MatchedItem {

    public static final String XML_NAME = "item";
    public static final String XML_ATTRIBUTE_SOURCE_IDs = "srcIDs";
    public static final String XML_ELEMENT_TARGET = "target";
    public static final String XML_ELEMENT_CATEGORY = "category";
    @XmlAttribute(name = CommonAttributes.TOKEN_SEQUENCE_REFERENCE, required = true)
    protected String[] tokIds;
    @XmlAttribute(name = XML_ATTRIBUTE_SOURCE_IDs)
    protected String[] srcIds;
    @XmlElement(name = XML_ELEMENT_TARGET)
    protected List<MatchedItemTarget> targets = new ArrayList<MatchedItemTarget>();
    @XmlElement(name = XML_ELEMENT_CATEGORY)
    protected List<MatchedItemCategory> categories = new ArrayList<MatchedItemCategory>();

    MatchedItemStored() {
    }

    MatchedItemStored(String[] tokIds, String[] srcIds, Map<String, String> targetsMap, Map<String, String> categoriesMap) {
        this.tokIds = tokIds;
        if (srcIds.length > 0) {
            this.srcIds = srcIds;
        }
        for (String name : targetsMap.keySet()) {
            targets.add(new MatchedItemTarget(name, targetsMap.get(name)));
        }
        for (String name : categoriesMap.keySet()) {
            categories.add(new MatchedItemCategory(name, categoriesMap.get(name)));
        }
    }

    @Override
    public String[] getOriginCorpusTokenIds() {
        return srcIds;
    }

    @Override
    public Set<String> getTargetNames() {
        Set<String> names = new HashSet<String>();
        for (MatchedItemTarget target : this.targets) {
            names.add(target.name);
        }
        return names;
    }

    @Override
    public String getTargetValue(String targetName) {
        for (MatchedItemTarget target : this.targets) {
            if (targetName.equals(target.name)) {
                return target.value;
            }
        }
        return null;
    }

    @Override
    public Set<String> getCategoriesNames() {
        Set<String> names = new HashSet<String>();
        for (MatchedItemCategory cat : this.categories) {
            names.add(cat.name);
        }
        return names;
    }

    @Override
    public String getCategoryValue(String categoryName) {
        for (MatchedItemCategory cat : this.categories) {
            if (categoryName.equals(cat.name)) {
                return cat.value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Arrays.toString(tokIds));
        if (srcIds != null) {
            sb.append(" ");
            sb.append(Arrays.toString(srcIds));
        }
        if (!targets.isEmpty()) {
            sb.append(" ");
            sb.append(targets);
        }
        if (!categories.isEmpty()) {
            sb.append(" ");
            sb.append(categories);
        }
        return sb.toString();
    }
}
