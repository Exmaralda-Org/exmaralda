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
/**
 *
 */
package eu.clarin.weblicht.wlfxb.tc.xb;

import eu.clarin.weblicht.wlfxb.tc.api.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@SuppressWarnings("deprecation")
@XmlRootElement(name = TextCorpusStored.XML_NAME, namespace = TextCorpusStored.XML_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {
    "textLayer",
    "tokensLayer",
    "sentencesLayer",
    "lemmasLayer",
    "posTagsLayer",
    "constituentParsingLayer",
    "dependencyParsingLayer",
    "morphologyLayer",
    "namedEntitiesLayer",
    "referencesLayer",
    "relationsLayer",
    "matchesLayer",
    "wordSplittingLayer",
    "phoneticsLayer",
    "synonymyLayer",
    "antonymyLayer",
    "hyponymyLayer",
    "hyperonymyLayer",
    "geoLayer",
    "textStructureLayer",
    "orthographyLayer",
    "discourseConnectivesLayer",
    "wordSensesLayer",
    "textSourceLayer"})
public class TextCorpusStored implements TextCorpus {

    public static final String XML_NAME = "TextCorpus";
    public static final String XML_NAMESPACE = "http://www.dspin.de/data/textcorpus";
    @XmlAttribute
    protected String lang;
    protected TextCorpusLayerStoredAbstract[] layersInOrder;
    private TextCorpusLayersConnector connector;

    TextCorpusStored() {
        connector = new TextCorpusLayersConnector();
        layersInOrder = new TextCorpusLayerStoredAbstract[TextCorpusLayerTag.orderedLayerTags().size()];
    }

    public TextCorpusStored(String language) {
        this();
        this.lang = language;
    }

    @Override
    public String getLanguage() {
        return lang;
    }

    @Override
    public List<TextCorpusLayer> getLayers() {
        List<TextCorpusLayer> allLayers = new ArrayList<TextCorpusLayer>(this.layersInOrder.length);
        for (TextCorpusLayer layer : this.layersInOrder) {
            if (layer != null) {
                allLayers.add(layer);
            }
        }
        return allLayers;
    }

    @Override
    public TextLayer createTextLayer() {
        TextLayer layer = initializeLayer(TextLayerStored.class);
        return layer;
    }

    @Override
    public TokensLayer createTokensLayer() {
        return initializeLayer(TokensLayerStored.class);
    }

    @Override
    public TokensLayer createTokensLayer(boolean hasCharOffsets) {
        return initializeLayer(TokensLayerStored.class, Boolean.valueOf(hasCharOffsets));
    }

    @Override
    public LemmasLayer createLemmasLayer() {
        return initializeLayer(LemmasLayerStored.class);
    }

    @Override
    public PosTagsLayer createPosTagsLayer(String tagset) {
        return initializeLayer(PosTagsLayerStored.class, tagset);
    }

    @Override
    public SentencesLayer createSentencesLayer() {
        return initializeLayer(SentencesLayerStored.class);
    }

    @Override
    public SentencesLayer createSentencesLayer(boolean hasCharOffsets) {
        return initializeLayer(SentencesLayerStored.class, Boolean.valueOf(hasCharOffsets));
    }

    @Override
    public ConstituentParsingLayer createConstituentParsingLayer(String tagset) {
        return initializeLayer(ConstituentParsingLayerStored.class, tagset);
    }

    @Override
    public DependencyParsingLayer createDependencyParsingLayer(String tagset,
            boolean multipleGovernorsPossible, boolean emptyTokensPossible) {
        return initializeLayer(DependencyParsingLayerStored.class, tagset,
                Boolean.valueOf(multipleGovernorsPossible), Boolean.valueOf(emptyTokensPossible));
    }

    @Override
    public DependencyParsingLayer createDependencyParsingLayer(
            boolean multipleGovernorsPossible, boolean emptyTokensPossible) {
        return initializeLayer(DependencyParsingLayerStored.class,
                Boolean.valueOf(multipleGovernorsPossible), Boolean.valueOf(emptyTokensPossible));
    }

    @Override
    public MorphologyLayer createMorphologyLayer() {
        return initializeLayer(MorphologyLayerStored.class);
    }

    @Override
    public MorphologyLayer createMorphologyLayer(boolean hasSegmentation) {
        return initializeLayer(MorphologyLayerStored.class, Boolean.valueOf(hasSegmentation));
    }

    @Override
    public MorphologyLayer createMorphologyLayer(boolean hasSegmentation, boolean hasCharOffsets) {
        return initializeLayer(MorphologyLayerStored.class, Boolean.valueOf(hasSegmentation), Boolean.valueOf(hasCharOffsets));
    }

    @Override
    public NamedEntitiesLayer createNamedEntitiesLayer(String entitiesType) {
        return initializeLayer(NamedEntitiesLayerStored.class, entitiesType);
    }

    @Override
    public ReferencesLayer createReferencesLayer(String typetagset, String reltagset, String externalReferencesSource) {
        ReferencesLayerStored layer = initializeLayer(ReferencesLayerStored.class);
        layer.typetagset = typetagset;
        layer.reltagset = reltagset;
        layer.externalReferenceSource = externalReferencesSource;
        return layer;
    }

    @Override
    public RelationsLayer createRelationsLayer(String type) {
        return initializeLayer(RelationsLayerStored.class, type);
    }

    @Override
    public MatchesLayer createMatchesLayer(String queryLanguage, String queryString) {
        return initializeLayer(MatchesLayerStored.class, queryLanguage, queryString);
    }

    @Override
    public WordSplittingLayer createWordSplittingLayer(String type) {
        return initializeLayer(WordSplittingLayerStored.class, type);
    }

    @Override
    public PhoneticsLayer createPhotenicsLayer(String alphabet) {
        return initializeLayer(PhoneticsLayerStored.class, alphabet);
    }

    @Override
    public GeoLayer createGeoLayer(String source, GeoLongLatFormat coordFormat) {
        return initializeLayer(GeoLayerStored.class, source, coordFormat);
    }

    public GeoLayer createGeoLayer(String source, GeoLongLatFormat coordFormat,
            GeoContinentFormat continentFormat) {
        return initializeLayer(GeoLayerStored.class, source, coordFormat,
                continentFormat);
    }

    public GeoLayer createGeoLayer(String source, GeoLongLatFormat coordFormat,
            GeoContinentFormat continentFormat, GeoCountryFormat countryFormat) {
        return initializeLayer(GeoLayerStored.class, source, coordFormat,
                continentFormat, countryFormat);
    }

    public GeoLayer createGeoLayer(String source, GeoLongLatFormat coordFormat,
            GeoContinentFormat continentFormat, GeoCapitalFormat capitalFormat) {
        return initializeLayer(GeoLayerStored.class, source, coordFormat,
                continentFormat, capitalFormat);
    }

    public GeoLayer createGeoLayer(String source, GeoLongLatFormat coordFormat,
            GeoCountryFormat countryFormat) {
        return initializeLayer(GeoLayerStored.class, source, coordFormat,
                countryFormat);
    }

    public GeoLayer createGeoLayer(String source, GeoLongLatFormat coordFormat,
            GeoCountryFormat countryFormat, GeoCapitalFormat capitalFormat) {
        return initializeLayer(GeoLayerStored.class, source, coordFormat,
                countryFormat, capitalFormat);
    }

    public GeoLayer createGeoLayer(String source, GeoLongLatFormat coordFormat,
            GeoCapitalFormat capitalFormat) {
        return initializeLayer(GeoLayerStored.class, source, coordFormat,
                capitalFormat);
    }

    @Override
    public GeoLayer createGeoLayer(String source, GeoLongLatFormat coordFormat,
            GeoContinentFormat continentFormat, GeoCountryFormat countryFormat, GeoCapitalFormat capitalFormat) {
        return initializeLayer(GeoLayerStored.class, source, coordFormat,
                continentFormat, countryFormat, capitalFormat);
    }

    @Override
    public LexicalSemanticsLayer createSynonymyLayer() {
        return initializeLayer(SynonymyLayerStored.class);
    }

    @Override
    public LexicalSemanticsLayer createAntonymyLayer() {
        return initializeLayer(AntonymyLayerStored.class);
    }

    @Override
    public LexicalSemanticsLayer createHyponymyLayer() {
        return initializeLayer(HyponymyLayerStored.class);
    }

    @Override
    public LexicalSemanticsLayer createHyperonymyLayer() {
        return initializeLayer(HyperonymyLayerStored.class);
    }

    @Override
    public OrthographyLayer createOrthographyLayer() {
        return initializeLayer(OrthographyLayerStored.class);
    }

    @Override
    public TextStructureLayer createTextStructureLayer() {
        return initializeLayer(TextStructureLayerStored.class);
    }

    @Override
    public DiscourseConnectivesLayer createDiscourseConnectivesLayer() {
        return initializeLayer(DiscourseConnectivesLayerStored.class);
    }

    @Override
    public DiscourseConnectivesLayer createDiscourseConnectivesLayer(String typesTagset) {
        return initializeLayer(DiscourseConnectivesLayerStored.class, typesTagset);
    }
    
    @Override
    public WordSensesLayer createWordSensesLayer(String source) {
        return initializeLayer(WordSensesLayerStored.class, source);
    }

    @Override
    public TextSourceLayer createTextSourceLayer() {
        return initializeLayer(TextSourceLayerStored.class);
    }

    @SuppressWarnings("unchecked")
    private <T extends TextCorpusLayerStoredAbstract> T initializeLayer(Class<T> layerClass, Object... params) {

        Class<?>[] paramsClass = null;
        if (params != null) {
            paramsClass = new Class<?>[params.length];
            for (int i = 0; i < params.length; i++) {
                paramsClass[i] = params[i].getClass();
            }
        }
        TextCorpusLayerTag layerTag = TextCorpusLayerTag.getFromClass(layerClass);
        try {
            Constructor<?> ct;
            T instance;
            if (params == null) {
                ct = layerClass.getDeclaredConstructor();
                instance = (T) ct.newInstance();
            } else {
                ct = layerClass.getDeclaredConstructor(paramsClass);
                instance = (T) ct.newInstance(params);
            }
            layersInOrder[layerTag.ordinal()] = instance;
            instance.setLayersConnector(connector);
        } catch (Exception e) {
            //e.printStackTrace();
            Logger.getLogger(TextCorpusStored.class.getName()).log(Level.SEVERE, null, e);
        }
        return (T) layersInOrder[layerTag.ordinal()];
    }

    @XmlElement(name = TextLayerStored.XML_NAME)
    protected void setTextLayer(TextLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.TEXT.ordinal()] = layer;
    }

    @Override
    public TextLayerStored getTextLayer() {
        return ((TextLayerStored) layersInOrder[TextCorpusLayerTag.TEXT.ordinal()]);
    }

    @XmlElement(name = TokensLayerStored.XML_NAME)
    protected void setTokensLayer(TokensLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.TOKENS.ordinal()] = layer;
    }

    @Override
    public TokensLayerStored getTokensLayer() {
        return ((TokensLayerStored) layersInOrder[TextCorpusLayerTag.TOKENS.ordinal()]);
    }

    @XmlElement(name = SentencesLayerStored.XML_NAME)
    protected void setSentencesLayer(SentencesLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.SENTENCES.ordinal()] = layer;
    }

    @Override
    public SentencesLayerStored getSentencesLayer() {
        return ((SentencesLayerStored) layersInOrder[TextCorpusLayerTag.SENTENCES.ordinal()]);
    }

    @XmlElement(name = LemmasLayerStored.XML_NAME)
    protected void setLemmasLayer(LemmasLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.LEMMAS.ordinal()] = layer;
    }

    @Override
    public LemmasLayerStored getLemmasLayer() {
        return ((LemmasLayerStored) layersInOrder[TextCorpusLayerTag.LEMMAS.ordinal()]);
    }

    @XmlElement(name = PosTagsLayerStored.XML_NAME)
    protected void setPosTagsLayer(PosTagsLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.POSTAGS.ordinal()] = layer;
    }

    @Override
    public PosTagsLayerStored getPosTagsLayer() {
        return ((PosTagsLayerStored) layersInOrder[TextCorpusLayerTag.POSTAGS.ordinal()]);
    }

    @XmlElement(name = ConstituentParsingLayerStored.XML_NAME)
    protected void setConstituentParsingLayer(ConstituentParsingLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.PARSING_CONSTITUENT.ordinal()] = layer;
    }

    @Override
    public ConstituentParsingLayerStored getConstituentParsingLayer() {
        return ((ConstituentParsingLayerStored) layersInOrder[TextCorpusLayerTag.PARSING_CONSTITUENT.ordinal()]);
    }

    @XmlElement(name = DependencyParsingLayerStored.XML_NAME)
    protected void setDependencyParsingLayer(DependencyParsingLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.PARSING_DEPENDENCY.ordinal()] = layer;
    }

    @Override
    public DependencyParsingLayerStored getDependencyParsingLayer() {
        return ((DependencyParsingLayerStored) layersInOrder[TextCorpusLayerTag.PARSING_DEPENDENCY.ordinal()]);
    }

    @XmlElement(name = MorphologyLayerStored.XML_NAME)
    protected void setMorphologyLayer(MorphologyLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.MORPHOLOGY.ordinal()] = layer;
    }

    @Override
    public MorphologyLayerStored getMorphologyLayer() {
        return ((MorphologyLayerStored) layersInOrder[TextCorpusLayerTag.MORPHOLOGY.ordinal()]);
    }

    @XmlElement(name = NamedEntitiesLayerStored.XML_NAME)
    protected void setNamedEntitiesLayer(NamedEntitiesLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.NAMED_ENTITIES.ordinal()] = layer;
    }

    @Override
    public NamedEntitiesLayerStored getNamedEntitiesLayer() {
        return ((NamedEntitiesLayerStored) layersInOrder[TextCorpusLayerTag.NAMED_ENTITIES.ordinal()]);
    }

    @XmlElement(name = ReferencesLayerStored.XML_NAME)
    protected void setReferencesLayer(ReferencesLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.REFERENCES.ordinal()] = layer;
    }

    @Override
    public ReferencesLayerStored getReferencesLayer() {
        return ((ReferencesLayerStored) layersInOrder[TextCorpusLayerTag.REFERENCES.ordinal()]);
    }

    @XmlElement(name = RelationsLayerStored.XML_NAME)
    protected void setRelationsLayer(RelationsLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.RELATIONS.ordinal()] = layer;
    }

    @Override
    public RelationsLayerStored getRelationsLayer() {
        return ((RelationsLayerStored) layersInOrder[TextCorpusLayerTag.RELATIONS.ordinal()]);
    }

    @XmlElement(name = MatchesLayerStored.XML_NAME)
    protected void setMatchesLayer(MatchesLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.CORPUS_MATCHES.ordinal()] = layer;
    }

    @Override
    public MatchesLayerStored getMatchesLayer() {
        return ((MatchesLayerStored) layersInOrder[TextCorpusLayerTag.CORPUS_MATCHES.ordinal()]);
    }

    @XmlElement(name = WordSplittingLayerStored.XML_NAME)
    protected void setWordSplittingLayer(WordSplittingLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.WORD_SPLITTINGS.ordinal()] = layer;
    }

    @Override
    public WordSplittingLayerStored getWordSplittingLayer() {
        return ((WordSplittingLayerStored) layersInOrder[TextCorpusLayerTag.WORD_SPLITTINGS.ordinal()]);
    }

    @XmlElement(name = PhoneticsLayerStored.XML_NAME)
    protected void setPhoneticsLayer(PhoneticsLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.PHONETICS.ordinal()] = layer;
    }

    @Override
    public PhoneticsLayerStored getPhoneticsLayer() {
        return ((PhoneticsLayerStored) layersInOrder[TextCorpusLayerTag.PHONETICS.ordinal()]);
    }

    @XmlElement(name = GeoLayerStored.XML_NAME)
    protected void setGeoLayer(GeoLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.GEO.ordinal()] = layer;
    }

    @Override
    public GeoLayerStored getGeoLayer() {
        return ((GeoLayerStored) layersInOrder[TextCorpusLayerTag.GEO.ordinal()]);
    }

    @XmlElement(name = SynonymyLayerStored.XML_NAME)
    protected void setSynonymyLayer(SynonymyLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.SYNONYMY.ordinal()] = layer;
    }

    @Override
    public SynonymyLayerStored getSynonymyLayer() {
        return ((SynonymyLayerStored) layersInOrder[TextCorpusLayerTag.SYNONYMY.ordinal()]);
    }

    @XmlElement(name = AntonymyLayerStored.XML_NAME)
    protected void setAntonymyLayer(AntonymyLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.ANTONYMY.ordinal()] = layer;
    }

    @Override
    public AntonymyLayerStored getAntonymyLayer() {
        return ((AntonymyLayerStored) layersInOrder[TextCorpusLayerTag.ANTONYMY.ordinal()]);
    }

    @XmlElement(name = HyponymyLayerStored.XML_NAME)
    protected void setHyponymyLayer(HyponymyLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.HYPONYMY.ordinal()] = layer;
    }

    @Override
    public HyponymyLayerStored getHyponymyLayer() {
        return ((HyponymyLayerStored) layersInOrder[TextCorpusLayerTag.HYPONYMY.ordinal()]);
    }

    @XmlElement(name = HyperonymyLayerStored.XML_NAME)
    protected void setHyperonymyLayer(HyperonymyLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.HYPERONYMY.ordinal()] = layer;
    }

    @Override
    public HyperonymyLayerStored getHyperonymyLayer() {
        return ((HyperonymyLayerStored) layersInOrder[TextCorpusLayerTag.HYPERONYMY.ordinal()]);
    }

    @XmlElement(name = TextStructureLayerStored.XML_NAME)
    protected void setTextStructureLayer(TextStructureLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.TEXT_STRUCTURE.ordinal()] = layer;
    }

    @Override
    public TextStructureLayerStored getTextStructureLayer() {
        return ((TextStructureLayerStored) layersInOrder[TextCorpusLayerTag.TEXT_STRUCTURE.ordinal()]);
    }

    @XmlElement(name = OrthographyLayerStored.XML_NAME)
    protected void setOrthographyLayer(OrthographyLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.ORTHOGRAPHY.ordinal()] = layer;
    }

    @Override
    public OrthographyLayerStored getOrthographyLayer() {
        return ((OrthographyLayerStored) layersInOrder[TextCorpusLayerTag.ORTHOGRAPHY.ordinal()]);
    }

    @XmlElement(name = DiscourseConnectivesLayerStored.XML_NAME)
    protected void setDiscourseConnectivesLayer(DiscourseConnectivesLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.DISCOURSE_CONNECTIVES.ordinal()] = layer;
    }

    @XmlElement(name = TextSourceLayerStored.XML_NAME)
    protected void setTextSourceLayer(TextSourceLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.TEXT_SOURCE.ordinal()] = layer;
    }


    @Override
    public DiscourseConnectivesLayerStored getDiscourseConnectivesLayer() {
        return ((DiscourseConnectivesLayerStored) layersInOrder[TextCorpusLayerTag.DISCOURSE_CONNECTIVES.ordinal()]);
    }
    
    @XmlElement(name = WordSensesLayerStored.XML_NAME)
    protected void setWordSensesLayer(WordSensesLayerStored layer) {
        layersInOrder[TextCorpusLayerTag.WORD_SENSES.ordinal()] = layer;
    }

    @Override
    public WordSensesLayerStored getWordSensesLayer() {
        return ((WordSensesLayerStored) layersInOrder[TextCorpusLayerTag.WORD_SENSES.ordinal()]);
    }

    @Override
    public TextSourceLayerStored getTextSourceLayer() {
        return ((TextSourceLayerStored) layersInOrder[TextCorpusLayerTag.TEXT_SOURCE.ordinal()]);
    }

    protected void afterUnmarshal(Unmarshaller u, Object parent) {
        connectLayers();
    }

    protected void connectLayers() {
        for (int i = 0; i < this.layersInOrder.length; i++) {
            if (layersInOrder[i] != null) {
                layersInOrder[i].setLayersConnector(connector);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(":\n");

        for (TextCorpusLayer layer : this.layersInOrder) {
            if (layer != null) {
                sb.append(layer);
                sb.append("\n");
            }
        }

        return sb.toString().trim();
    }

    /**
     * Composes the layers into one document. Normally, you should not use this
     * method, unless you want to manually compose document from the layer
     * pieces.The method composes correctly only in case referencing between
     * layers (IDs <- IDREFs) is correct
     *
     * @param lang
     * @param layers
     * @return text corpus data composed of the provided layers
     */
    public static TextCorpusStored compose(String lang, TextCorpusLayerStoredAbstract... layers) {
        TextCorpusStored tc = new TextCorpusStored(lang);
        for (TextCorpusLayerStoredAbstract layer : layers) {
            tc.layersInOrder[TextCorpusLayerTag.getFromClass(layer.getClass()).ordinal()] = layer;
        }
        tc.connectLayers();
        return tc;
    }
}
