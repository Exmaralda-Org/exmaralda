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

import eu.clarin.weblicht.wlfxb.lx.api.LexiconLayer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yana Panchenko
 *
 */
public enum LexiconLayerTag {

    /*
     * New layers should be added in such an order that they are enumerated
     * after the layers they reference and before the layers that are
     * referencing them. The order here will also be the order in TCF output.
     */
    //LEMMAS("lemmas", LemmasLayerStored.class),
    ENTRIES("entries", EntriesLayerStored.class),
    //MORPHOLOGY("morphology"),
    POSTAGS("POStags", PosTagsLayerStored.class),
    FREQUENCIES("frequencies", FrequenciesLayerStored.class),
    SYLLABIFICATIONS("syllabifications", SyllabificationsLayerStored.class),
    COOCCURRENCES("cooccurrences", CooccurrencesLayerStored.class),
    SYNONYMS("synonyms", SynonymsLayerStored.class)
    //@Deprecated
    //RELATIONS("word-relations", RelationsLayerStored.class)
    ;
    
    private static final Map<String, LexiconLayerTag> xmlNameToLayerTagMap =
            new HashMap<String, LexiconLayerTag>() {

                {
                    for (LexiconLayerTag layerTag : LexiconLayerTag.values()) {
                        put(layerTag.xmlName, layerTag);
                    }
                }
            };
    private static final Map<Class<? extends LexiconLayer>, LexiconLayerTag> classToLayerTagMap =
            new HashMap<Class<? extends LexiconLayer>, LexiconLayerTag>() {

                {
                    for (LexiconLayerTag layerTag : LexiconLayerTag.values()) {
                        put(layerTag.getLayerClass(), layerTag);
                    }
                }
            };
    private static final LexiconLayerTag[] layersOrder;

    static {
        layersOrder = new LexiconLayerTag[LexiconLayerTag.values().length];
        for (LexiconLayerTag layerTag : LexiconLayerTag.values()) {
            layersOrder[layerTag.ordinal()] = layerTag;
        }
    }
    private final String xmlName;
    private final Class<? extends LexiconLayer> layerClass;

    LexiconLayerTag(String name, Class<? extends LexiconLayer> layerClass) {
        this.xmlName = name;
        this.layerClass = layerClass;
    }

    public final String getXmlName() {
        return xmlName;
    }

    public final Class<? extends LexiconLayer> getLayerClass() {
        return layerClass;
    }

    public static LexiconLayerTag getFromXmlName(String xmlName) {
        return xmlNameToLayerTagMap.get(xmlName);
    }

    public static List<LexiconLayerTag> orderedLayerTags() {
        return Arrays.asList(layersOrder);
    }

    public static LexiconLayerTag getFromClass(Class<? extends LexiconLayer> cl) {
        return classToLayerTagMap.get(cl);
    }
}
