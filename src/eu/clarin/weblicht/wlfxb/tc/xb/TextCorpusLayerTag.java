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

import eu.clarin.weblicht.wlfxb.tc.api.TextCorpusLayer;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TextCorpusLayerTag {

    /*
     * New layers should be added in such an order that they are enumerated
     * after the layers they reference and before the layers that are
     * referencing them. The order here will also be the order in TCF output.
     */
    TEXT(TextLayerStored.XML_NAME, TextLayerStored.class),
    TOKENS(TokensLayerStored.XML_NAME, TokensLayerStored.class),
    SENTENCES(SentencesLayerStored.XML_NAME, SentencesLayerStored.class),
    LEMMAS(LemmasLayerStored.XML_NAME, LemmasLayerStored.class),
    POSTAGS(PosTagsLayerStored.XML_NAME, PosTagsLayerStored.class),
    MORPHOLOGY(MorphologyLayerStored.XML_NAME, MorphologyLayerStored.class),
    PARSING_CONSTITUENT(ConstituentParsingLayerStored.XML_NAME, ConstituentParsingLayerStored.class),
    PARSING_DEPENDENCY(DependencyParsingLayerStored.XML_NAME, DependencyParsingLayerStored.class),
    @Deprecated
    RELATIONS(RelationsLayerStored.XML_NAME, RelationsLayerStored.class),
    NAMED_ENTITIES(NamedEntitiesLayerStored.XML_NAME, NamedEntitiesLayerStored.class),
    REFERENCES(ReferencesLayerStored.XML_NAME, ReferencesLayerStored.class),
    SYNONYMY(SynonymyLayerStored.XML_NAME, SynonymyLayerStored.class),
    ANTONYMY(AntonymyLayerStored.XML_NAME, AntonymyLayerStored.class),
    HYPONYMY(HyponymyLayerStored.XML_NAME, HyponymyLayerStored.class),
    HYPERONYMY(HyperonymyLayerStored.XML_NAME, HyperonymyLayerStored.class),
    WORD_SPLITTINGS(WordSplittingLayerStored.XML_NAME, WordSplittingLayerStored.class),
    PHONETICS(PhoneticsLayerStored.XML_NAME, PhoneticsLayerStored.class),
    GEO(GeoLayerStored.XML_NAME, GeoLayerStored.class),
    ORTHOGRAPHY(OrthographyLayerStored.XML_NAME, OrthographyLayerStored.class),
    TEXT_STRUCTURE(TextStructureLayerStored.XML_NAME, TextStructureLayerStored.class),
    DISCOURSE_CONNECTIVES(DiscourseConnectivesLayerStored.XML_NAME, DiscourseConnectivesLayerStored.class),
    WORD_SENSES(WordSensesLayerStored.XML_NAME, WordSensesLayerStored.class),
    CORPUS_MATCHES(MatchesLayerStored.XML_NAME, MatchesLayerStored.class),
    TEXT_SOURCE(TextSourceLayerStored.XML_NAME, TextSourceLayerStored.class);
    
    private static final Map<String, TextCorpusLayerTag> xmlNameToLayerTagMap =
            new HashMap<String, TextCorpusLayerTag>() {

                {
                    for (TextCorpusLayerTag layerTag : TextCorpusLayerTag.values()) {
                        put(layerTag.xmlName, layerTag);
                    }
                }
            };
    
    private static final Map<Class<? extends TextCorpusLayer>, TextCorpusLayerTag> classToLayerTagMap =
            new HashMap<Class<? extends TextCorpusLayer>, TextCorpusLayerTag>() {

                {
                    for (TextCorpusLayerTag layerTag : TextCorpusLayerTag.values()) {
                        put(layerTag.getLayerClass(), layerTag);
                    }
                }
            };
    
    private static final TextCorpusLayerTag[] layersOrder;

    static {
        layersOrder = new TextCorpusLayerTag[TextCorpusLayerTag.values().length];
        for (TextCorpusLayerTag layerTag : TextCorpusLayerTag.values()) {
            layersOrder[layerTag.ordinal()] = layerTag;
        }
    }
    
    private static final EnumMap<TextCorpusLayerTag, EnumSet<TextCorpusLayerTag>> layerDependencies;
    static {
        layerDependencies = new  EnumMap<TextCorpusLayerTag, EnumSet<TextCorpusLayerTag>>(TextCorpusLayerTag.class);
        layerDependencies.put(TextCorpusLayerTag.TEXT, EnumSet.noneOf(TextCorpusLayerTag.class));
        layerDependencies.put(TextCorpusLayerTag.TOKENS, EnumSet.noneOf(TextCorpusLayerTag.class));
        layerDependencies.put(TextCorpusLayerTag.LEMMAS, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.POSTAGS, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.SENTENCES, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.NAMED_ENTITIES, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.PHONETICS, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.PARSING_CONSTITUENT, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.PARSING_DEPENDENCY, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.MORPHOLOGY, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.ORTHOGRAPHY, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.REFERENCES, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.CORPUS_MATCHES, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.DISCOURSE_CONNECTIVES, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.GEO, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.TEXT_STRUCTURE, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.WORD_SPLITTINGS, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.SYNONYMY, EnumSet.of(TextCorpusLayerTag.LEMMAS));
        layerDependencies.put(TextCorpusLayerTag.ANTONYMY, EnumSet.of(TextCorpusLayerTag.LEMMAS));
        layerDependencies.put(TextCorpusLayerTag.HYPONYMY, EnumSet.of(TextCorpusLayerTag.LEMMAS));
        layerDependencies.put(TextCorpusLayerTag.HYPERONYMY, EnumSet.of(TextCorpusLayerTag.LEMMAS));
        layerDependencies.put(TextCorpusLayerTag.RELATIONS, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.WORD_SENSES, EnumSet.of(TextCorpusLayerTag.TOKENS));
        layerDependencies.put(TextCorpusLayerTag.TEXT_SOURCE, EnumSet.noneOf(TextCorpusLayerTag.class));
    }
    
    private final String xmlName;
    private final Class<? extends TextCorpusLayer> layerClass;

    TextCorpusLayerTag(String name, Class<? extends TextCorpusLayer> layerClass) {
        this.xmlName = name;
        this.layerClass = layerClass;
    }

    public final String getXmlName() {
        return xmlName;
    }

    public final Class<? extends TextCorpusLayer> getLayerClass() {
        return layerClass;
    }

    public static TextCorpusLayerTag getFromXmlName(String xmlName) {
        return xmlNameToLayerTagMap.get(xmlName);
    }

    public static List<TextCorpusLayerTag> orderedLayerTags() {
        return Arrays.asList(layersOrder);
    }

    public static TextCorpusLayerTag getFromClass(Class<? extends TextCorpusLayer> cl) {
        return classToLayerTagMap.get(cl);
    }
    
    public EnumSet<TextCorpusLayerTag> withDependentLayers() {
        EnumSet<TextCorpusLayerTag> layerAndDependentLayers = EnumSet.of(this);
        addDependentLayers(this, layerAndDependentLayers);
        return layerAndDependentLayers;
    }
        
    private static void addDependentLayers(TextCorpusLayerTag tag, EnumSet<TextCorpusLayerTag> layers) {
        EnumSet<TextCorpusLayerTag> dependentLayers = layerDependencies.get(tag);
        layers.addAll(dependentLayers);
        for (TextCorpusLayerTag dependentLayer : dependentLayers) {
                addDependentLayers(dependentLayer, layers);
        }
    }
}
