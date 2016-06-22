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

import eu.clarin.weblicht.wlfxb.ed.api.ExternalDataLayer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ExternalDataLayerTag {

    /*
     * New layers should be added in such an order that they are enumerated
     * after the layers they reference and before the layers that are
     * referencing them. The order here will also be the order in TCF output.
     */
    SPEECH_SIGNAL(SpeechSignalLayerStored.XML_NAME, SpeechSignalLayerStored.class),
    TOKEN_SEGMENTATION(TokenSegmentationLayerStored.XML_NAME, TokenSegmentationLayerStored.class),
    PHONETIC_SEGMENTATION(PhoneticSegmentationLayerStored.XML_NAME, PhoneticSegmentationLayerStored.class),
    CANONICAL_SEGMENTATION(CanonicalSegmentationLayerStored.XML_NAME, CanonicalSegmentationLayerStored.class),
    NAMEDENTITY_MODEL(NamedEntityModelLayerStored.XML_NAME, NamedEntityModelLayerStored.class);
    private static final Map<String, ExternalDataLayerTag> xmlNameToLayerTagMap =
            new HashMap<String, ExternalDataLayerTag>() {

                {

                    for (ExternalDataLayerTag layerTag : ExternalDataLayerTag.values()) {
                        put(layerTag.xmlName, layerTag);
                    }
                }
            };
    private static final Map<Class<? extends ExternalDataLayer>, ExternalDataLayerTag> classToLayerTagMap =
            new HashMap<Class<? extends ExternalDataLayer>, ExternalDataLayerTag>() {

                {

                    for (ExternalDataLayerTag layerTag : ExternalDataLayerTag.values()) {
                        put(layerTag.getLayerClass(), layerTag);
                    }
                }
            };
    private static final ExternalDataLayerTag[] layersOrder;

    static {
        layersOrder = new ExternalDataLayerTag[ExternalDataLayerTag.values().length];
        for (ExternalDataLayerTag layerTag : ExternalDataLayerTag.values()) {
            layersOrder[layerTag.ordinal()] = layerTag;
        }
    }
    private final String xmlName;
    private final Class<? extends ExternalDataLayer> layerClass;

    ExternalDataLayerTag(String name, Class<? extends ExternalDataLayer> layerClass) {
        this.xmlName = name;
        this.layerClass = layerClass;
    }

    public final String getXmlName() {
        return xmlName;
    }

    public final Class<? extends ExternalDataLayer> getLayerClass() {
        return layerClass;
    }

    public static ExternalDataLayerTag getFromXmlName(String xmlName) {
        return xmlNameToLayerTagMap.get(xmlName);
    }

    public static List<ExternalDataLayerTag> orderedLayerTags() {
        return Arrays.asList(layersOrder);
    }

    public static ExternalDataLayerTag getFromClass(Class<? extends ExternalDataLayer> cl) {
        return classToLayerTagMap.get(cl);
    }
}
