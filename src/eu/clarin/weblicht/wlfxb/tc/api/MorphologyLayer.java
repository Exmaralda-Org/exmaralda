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
 * The <tt>MorphologyLayer</tt> layer specifies the morphological features of 
 * tokens. Each morphology annotation (analysis) contains a list of morphological 
 * features with either a feature name - feature value pair, or a feature name - 
 * feature sub-features pair. Additionally, the morphology layer specifies 
 * whether it contains optional segmentation annotations. Segmentation 
 * annotations specify morphological features of segments of a given token.
 * 
 * @author Yana Panchenko
 */
public interface MorphologyLayer extends TextCorpusLayer {

    public boolean hasSegmentation();

    public boolean hasCharoffsets();

    public MorphologyAnalysis getAnalysis(int index);

    public MorphologyAnalysis getAnalysis(Token token);

    public Token[] getTokens(MorphologyAnalysis analysis);

    public MorphologyAnalysis addAnalysis(Token analysedToken, List<Feature> morphologyFeatures);

    public MorphologyAnalysis addAnalysis(Token analysedToken, List<Feature> morphologyFeatures,
            List<MorphologySegment> segments);

    public MorphologyAnalysis addAnalysis(List<Token> analysedTokens, List<Feature> morphologyFeatures);

    public MorphologyAnalysis addAnalysis(List<Token> analysedTokens, List<Feature> morphologyFeatures,
            List<MorphologySegment> segments);

    public Feature createFeature(String name, String value);

    public Feature createFeature(String name, List<Feature> subfeatures);

    public MorphologySegment createSegment(String type, String category,
            String function, Integer start, Integer end, String value);

    public MorphologySegment createSegment(String type, String category,
            String function, Integer start, Integer end, List<MorphologySegment> subsegments);
}
