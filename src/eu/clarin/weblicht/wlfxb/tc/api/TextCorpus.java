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
package eu.clarin.weblicht.wlfxb.tc.api;

import java.util.List;

/**
 * Interface <tt>TextCorpus</tt> represents TCF TextCorpus annotations.
 * Corresponds to TCF <a href="http://clarin-d.de/images/weblicht-tutorials/resources/tcf-04/schemas/latest/textcorpus_0_4.rnc">TextCorpus specification</a>.
 * 
 * These annotations represent linguistic annotations on written connected text.
 * The annotations are divided into the annotation layers, were each layer
 * represents specific linguistic aspect. For example, <tt>TextCorpus</tt> can
 * contain {@link TokensLayer}, {@link PosTagsLayer},
 * {@link ConstituentParsingLayer}, etc. In TextCorpus, annotations from any
 * layer usually annotate (directly or indirectly) {@link Token} annotations
 * from {@link TokensLayer}. An exception is {@link TextLayer} which is
 * independent from any other layer.
 * See also:
 * <a href="http://weblicht.sfs.uni-tuebingen.de/weblichtwiki/index.php/The_TCF_Format">TCF Format description</a>.
 *
 * @author Yana Panchenko
 */
public interface TextCorpus {

    /**
     * Gets the language of the text/tokens in this <tt>TextCorpus</tt>.
     *
     * @return language of <tt>TextCorpus</tt>.
     */
    public String getLanguage();

    /**
     * Gets all annotation layers of this <tt>TextCorpus</tt>.
     *
     * @return annotations layers.
     */
    public List<TextCorpusLayer> getLayers();

    /**
     * Gets text layer of this <tt>TextCorpus</tt>.
     *
     * @return annotation layer containing text.
     */
    public TextLayer getTextLayer();

    /**
     * Creates empty {@link TextLayer} in this <tt>TextCorpus</tt>.
     *
     * @return annotation layer that has been created.
     */
    public TextLayer createTextLayer();

    /**
     * Gets tokens layer of this <tt>TextCorpus</tt>.
     *
     * @return annotation layer containing tokens.
     */
    public TokensLayer getTokensLayer();

    /**
     * Creates empty {@link TokensLayer} in this <tt>TextCorpus</tt>.
     *
     * @return annotation layer that has been created.
     */
    public TokensLayer createTokensLayer();

    /**
     * Creates empty {@link TokensLayer} in this <tt>TextCorpus</tt>.
     *
     * @param hasCharOffsets true if the {@link Token} objects in this
     * <tt>TokensLayer</tt> will contain character offset in text information,
     * false otherwise.
     * @return annotation layer that has been created.
     */
    public TokensLayer createTokensLayer(boolean hasCharOffsets);

    /**
     * Gets lemmas layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing lemma annotations on {@link Token} objects from
     * {@link TokensLayer}.
     */
    public LemmasLayer getLemmasLayer();

    /**
     * Creates empty {@link LemmasLayer} in this <tt>TextCorpus</tt>.
     *
     * @return annotation layer that has been created.
     */
    public LemmasLayer createLemmasLayer();

    /**
     * Gets part-of-speech layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing part-of-speech annotations on {@link Token}
     * objects from {@link TokensLayer}.
     */
    public PosTagsLayer getPosTagsLayer();

    /**
     * Creates empty {@link PosTagsLayer} with the given tagset in this
     * <tt>TextCorpus</tt>.
     *
     * @param tagset of the part-of-speech annotations.
     * @return annotation layer that has been created.
     */
    public PosTagsLayer createPosTagsLayer(String tagset);

    /**
     * Gets sentences layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing sentence boundary annotations on {@link Token}
     * objects from {@link TokensLayer}.
     */
    public SentencesLayer getSentencesLayer();

    /**
     * Creates empty {@link SentencesLayer} in this <tt>TextCorpus</tt>.
     *
     * @return annotation layer that has been created.
     */
    public SentencesLayer createSentencesLayer();

    /**
     * Creates empty {@link SentencesLayer} in this <tt>TextCorpus</tt>.
     *
     * @param hasCharOffsets true if the {@link Sentence} objects in this
     * <tt>SentencesLayer</tt> will contain character offset in text
     * information, false otherwise.
     * @return annotation layer that has been created.
     */
    public SentencesLayer createSentencesLayer(boolean hasCharOffsets);

    /**
     * Gets constituent parsing layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing constituent parsing annotations on {@link Token}
     * objects from {@link TokensLayer}.
     */
    public ConstituentParsingLayer getConstituentParsingLayer();

    /**
     * Creates empty {@link ConstituentParsingLayer} with the given tagset in
     * this <tt>TextCorpus</tt>.
     *
     * @param tagset of the parsing annotations.
     * @return annotation layer that has been created.
     */
    public ConstituentParsingLayer createConstituentParsingLayer(String tagset);

    /**
     * Gets dependency parsing layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing dependency parsing annotations on {@link Token}
     * objects from {@link TokensLayer}.
     */
    public DependencyParsingLayer getDependencyParsingLayer();

    /**
     * Creates empty {@link DependencyParsingLayer} in this <tt>TextCorpus</tt>.
     *
     * @param multipleGovernorsPossible true if a dependent can be governed by
     * more than 1 governor, false otherwise.
     * @param emptyTokensPossible true if dependency annotations can contain
     * empty tokens.
     * @return annotation layer that has been created.
     */
    public DependencyParsingLayer createDependencyParsingLayer(
            boolean multipleGovernorsPossible, boolean emptyTokensPossible);

    /**
     * Creates empty {@link DependencyParsingLayer} with the given tagset in
     * this <tt>TextCorpus</tt>.
     *
     * @param tagset of the functions between dependent and governor.
     * @param multipleGovernorsPossible true if a dependent can be governed by
     * more than 1 governor, false otherwise.
     * @param emptyTokensPossible true if dependency annotations can contain
     * empty tokens.
     * @return annotation layer that has been created.
     */
    public DependencyParsingLayer createDependencyParsingLayer(String tagset,
            boolean multipleGovernorsPossible, boolean emptyTokensPossible);

    /**
     * Gets morphology layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing morphological analysis annotations on
     * {@link Token} objects from {@link TokensLayer}.
     */
    public MorphologyLayer getMorphologyLayer();
    
    /**
     * Creates empty {@link MorphologyLayer} in this <tt>TextCorpus</tt>.
     *
     * @return annotation layer that has been created.
     */
    public MorphologyLayer createMorphologyLayer();

    /**
     * Creates empty {@link MorphologyLayer} in this <tt>TextCorpus</tt>.
     *
     * @param hasSegmentation true if morphology annotations contain
     * segmentation analysis.
     * @return annotation layer that has been created.
     */
    public MorphologyLayer createMorphologyLayer(boolean hasSegmentation);

    /**
     * Creates empty {@link MorphologyLayer} in this <tt>TextCorpus</tt>.
     *
     * @param hasSegmentation true if morphology annotations contain
     * segmentation analysis.
     * @param hasCharOffsets true if the {@link MorphologyAnalysis} objects in
     * this layer will contain character offset for segmentation within the
     * token information, false otherwise.
     * @return annotation layer that has been created.
     */
    public MorphologyLayer createMorphologyLayer(boolean hasSegmentation, boolean hasCharOffsets);

    /**
     * Gets named entities layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing named entity annotations on {@link Token}
     * objects from {@link TokensLayer}.
     */
    public NamedEntitiesLayer getNamedEntitiesLayer();

    /**
     * Creates empty {@link NamedEntitiesLayer} with the given tagset for named
     * entity types in this <tt>TextCorpus</tt>.
     *
     * @param entitiesType tagset of the named entity annotations.
     * @return annotation layer that has been created.
     */
    public NamedEntitiesLayer createNamedEntitiesLayer(String entitiesType);

    /**
     * Gets references layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing reference/coreference annotations on
     * {@link Token} objects from {@link TokensLayer}.
     */
    public ReferencesLayer getReferencesLayer();

    /**
     * Creates empty references layers of this <tt>TextCorpus</tt>, ready to be
     * filled in with the references data.
     *
     * @param typetagset tagset for the mention type values of the references
     * (should be null if no types are defined)
     * @param reltagset tagset for relation values between the references
     * (should be null if no relations are defined)
     * @param externalReferencesSource name of external source (should be null
     * if entities from the external source are not referenced)
     * @return annotation layer that has been created.
     */
    public ReferencesLayer createReferencesLayer(String typetagset, String reltagset, String externalReferencesSource);

    @SuppressWarnings("deprecation")
    public RelationsLayer getRelationsLayer();

    @SuppressWarnings("deprecation")
    public RelationsLayer createRelationsLayer(String type);

    /**
     * Gets matches layer of this <tt>TextCorpus</tt>.
     *
     * @return layer matches annotations on {@link Token} objects from
     * {@link TokensLayer}.
     */
    public MatchesLayer getMatchesLayer();

    /**
     * Creates empty MatchesLayer layers of this <tt>TextCorpus</tt>, ready to
     * be filled in with the corpus match annotations.
     *
     * @param queryLanguage language of the query used to extract corpus matches
     * from a corpus.
     * @param queryString the query used to extract corpus matches from a
     * corpus.
     * @return annotation layer that has been created.
     */
    public MatchesLayer createMatchesLayer(String queryLanguage, String queryString);

    /**
     * Gets word splitting layer of this <tt>TextCorpus</tt>.
     *
     * @return layer split annotations (e.g. hyphenation) on {@link Token}
     * objects from {@link TokensLayer}.
     */
    public WordSplittingLayer getWordSplittingLayer();

    /**
     * Creates empty {@link WordSplittingLayer} with the given type of the
     * splitting in this <tt>TextCorpus</tt>.
     *
     * @param type of the splitting, e.g. hyphenation.
     * @return annotation layer that has been created.
     */
    public WordSplittingLayer createWordSplittingLayer(String type);

    /**
     * Gets phonetics layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing phonetic transcriptions of {@link Token} objects
     * from {@link TokensLayer}.
     */
    public PhoneticsLayer getPhoneticsLayer();

    /**
     * Creates empty {@link PhoneticsLayer} with the given alphabet for phonetic
     * transcriptions in this <tt>TextCorpus</tt>.
     *
     * @param alphabet of the phonetic transcription annotations.
     * @return annotation layer that has been created.
     */
    public PhoneticsLayer createPhotenicsLayer(String alphabet);

    /**
     * Gets geo layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing geographical location annotations on
     * {@link Token} objects from {@link TokensLayer}.
     */
    public GeoLayer getGeoLayer();

    /**
     * Creates empty {@link GeoLayer} in this <tt>TextCorpus</tt>.
     *
     * @param source of the geographical coordinates.
     * @param coordFormat format of the geographical coordinates.
     * @return annotation layer that has been created.
     */
    public GeoLayer createGeoLayer(String source, GeoLongLatFormat coordFormat);

    /**
     * Creates empty {@link GeoLayer} in this <tt>TextCorpus</tt>.
     *
     * @param source of the geographical coordinates.
     * @param coordFormat format of the geographical coordinates.
     * @param conitentFormat format of the continent (in case no continent is
     * specified should be null).
     * @param countryFormat format of the country (in case no country is
     * specified should be null).
     * @param capitalFormat format of the capital (in case no capital is
     * specified should be null).
     * @return annotation layer that has been created.
     */
    public GeoLayer createGeoLayer(String source, GeoLongLatFormat coordFormat,
            GeoContinentFormat conitentFormat, GeoCountryFormat countryFormat, GeoCapitalFormat capitalFormat);

    /**
     * Gets orthography layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing correct orthographic spellings of misspelled
     * {@link Token} objects from {@link TokensLayer}.
     */
    public OrthographyLayer getOrthographyLayer();

    /**
     * Creates empty {@link OrthographyLayer} in this <tt>TextCorpus</tt>.
     *
     * @return annotation layer that has been created.
     */
    public OrthographyLayer createOrthographyLayer();

    /**
     * Gets text structure layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing original text structure (such as paragraphs,
     * lines, pages, etc.), anchored on {@link Token} objects from
     * {@link TokensLayer}.
     */
    public TextStructureLayer getTextStructureLayer();

    /**
     * Creates empty {@link TextStructureLayer} in this <tt>TextCorpus</tt>.
     *
     * @return annotation layer that has been created.
     */
    public TextStructureLayer createTextStructureLayer();

    /**
     * Gets synonymy layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing synonyms of {@link Lemma} objects from
     * {@link LemmasLayer}.
     */
    public LexicalSemanticsLayer getSynonymyLayer();

    /**
     * Creates empty synonymy layer in this <tt>TextCorpus</tt>.
     *
     * @return annotation layer that has been created.
     */
    public LexicalSemanticsLayer createSynonymyLayer();

    /**
     * Gets antonymy layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing antonyms of {@link Lemma} objects from
     * {@link LemmasLayer}.
     */
    public LexicalSemanticsLayer getAntonymyLayer();

    /**
     * Creates empty antonymy layer in this <tt>TextCorpus</tt>.
     *
     * @return annotation layer that has been created.
     */
    public LexicalSemanticsLayer createAntonymyLayer();

    /**
     * Gets hyponymy layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing hyponyms of {@link Lemma} objects from
     * {@link LemmasLayer}.
     */
    public LexicalSemanticsLayer getHyponymyLayer();

    /**
     * Creates empty hyponymy layer in this <tt>TextCorpus</tt>.
     *
     * @return annotation layer that has been created.
     */
    public LexicalSemanticsLayer createHyponymyLayer();

    /**
     * Gets hyperonymy layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing hyperonyms of {@link Lemma} objects from
     * {@link LemmasLayer}.
     */
    public LexicalSemanticsLayer getHyperonymyLayer();

    /**
     * Creates empty hyperonymy layer in this <tt>TextCorpus</tt>.
     *
     * @return annotation layer that has been created.
     */
    public LexicalSemanticsLayer createHyperonymyLayer();

    /**
     * Gets discourse connectives layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing discourse connectives annotations on
     * {@link Token} objects from {@link TokensLayer}.
     */
    public DiscourseConnectivesLayer getDiscourseConnectivesLayer();

    /**
     * Creates empty {@link DiscourseConnectivesLayer} in this
     * <tt>TextCorpus</tt>.
     *
     * @return annotation layer that has been created.
     */
    public DiscourseConnectivesLayer createDiscourseConnectivesLayer();

    /**
     * Creates empty {@link DiscourseConnectivesLayer} in this
     * <tt>TextCorpus</tt>.
     *
     * @param typeTagset tagset used to label semantic types of the connectives
     * @return annotation layer that has been created.
     */
    public DiscourseConnectivesLayer createDiscourseConnectivesLayer(String typeTagset);
    
    /**
     * Gets word senses layer of this <tt>TextCorpus</tt>.
     *
     * @return layer containing word sense annotations on
     * {@link Token} objects from {@link TokensLayer}.
     */
    public WordSensesLayer getWordSensesLayer();
    
    /**
     * Creates empty {@link WordSensesLayer} in this
     * <tt>TextCorpus</tt>.
     *
     * @param source from where the word senses are taken
     * @return annotation layer that has been created.
     */
    public WordSensesLayer createWordSensesLayer(String source);

    /**
     * Gets textSource layer of this <tt>TextSource</tt>.
     *
     * @return annotation layer containing text.
     */
    public TextSourceLayer getTextSourceLayer();

    /**
     * Creates empty {@link TextSourceLayer} in this <tt>TextCorpus</tt>.
     *
     * @return annotation layer that has been created.
     */
    public TextSourceLayer createTextSourceLayer();
}
