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
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import eu.clarin.weblicht.wlfxb.utils.WlfUtilities;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = MorphologyLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class MorphologyLayerStored extends TextCorpusLayerStoredAbstract implements MorphologyLayer {

    public static final String XML_NAME = "morphology";
    @XmlAttribute(name = CommonAttributes.CHAR_OFFSETS)
    private Boolean hasCharoffsets;
    @XmlAttribute(name = "segmentation")
    private Boolean hasSegmentation;
    @XmlElement(name = MorphologyAnalysisStored.XML_NAME)
    private List<MorphologyAnalysisStored> moans = new ArrayList<MorphologyAnalysisStored>();
    private TextCorpusLayersConnector connector;

    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        this.connector = connector;
        for (MorphologyAnalysisStored a : moans) {
            for (String tokRef : a.tokRefs) {
                connector.token2ItsAnalysis.put(connector.tokenId2ItsToken.get(tokRef), a);
            }
        }
    }

    protected MorphologyLayerStored() {
    }

    protected MorphologyLayerStored(Boolean hasSegmentation) {
        this.hasSegmentation = hasSegmentation;
    }

    protected MorphologyLayerStored(Boolean hasSegmentation, Boolean hasCharOffsets) {
        this(hasSegmentation);
        this.hasCharoffsets = hasCharOffsets;
    }

    protected MorphologyLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    @Override
    public boolean isEmpty() {
        return moans.isEmpty();
    }

    @Override
    public int size() {
        return moans.size();
    }

    @Override
    public boolean hasSegmentation() {
        if (this.hasSegmentation == Boolean.TRUE) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasCharoffsets() {
        if (this.hasCharoffsets == Boolean.TRUE) {
            return true;
        }
        return false;
    }

    @Override
    public MorphologyAnalysis getAnalysis(int index) {
        return moans.get(index);
    }

    @Override
    public MorphologyAnalysis getAnalysis(Token token) {
        MorphologyAnalysis a = connector.token2ItsAnalysis.get(token);
        return a;
    }

    @Override
    public Token[] getTokens(MorphologyAnalysis analysis) {
        if (analysis instanceof MorphologyAnalysisStored) {
            MorphologyAnalysisStored a = (MorphologyAnalysisStored) analysis;
            return WlfUtilities.tokenIdsToTokens(a.tokRefs, connector.tokenId2ItsToken);
        }
        return null;
    }

    @Override
    public MorphologyAnalysis addAnalysis(Token analysedToken,
            List<Feature> morphologyFeatures) {
        List<Token> toks = new ArrayList<Token>();
        toks.add(analysedToken);
        return addAnalysis(toks, morphologyFeatures);
    }

    @Override
    public MorphologyAnalysis addAnalysis(Token analysedToken,
            List<Feature> morphologyFeatures, List<MorphologySegment> segments) {
        List<Token> toks = new ArrayList<Token>();
        toks.add(analysedToken);
        return addAnalysis(toks, morphologyFeatures, segments);
    }

    @Override
    public MorphologyAnalysis addAnalysis(List<Token> analysedTokens,
            List<Feature> morphologyFeatures) {
        MorphologyAnalysisStored a = new MorphologyAnalysisStored();
        FeatureStructureStored fs = new FeatureStructureStored();
        for (Feature f : morphologyFeatures) {
            if (f instanceof FeatureStored) {
                fs.features.add((FeatureStored) f);
            }
        }
        if (!fs.features.isEmpty()) {
            a.tag = new MorphologyTagStored();
            a.tag.fs = fs;
        }
        a.tokRefs = new String[analysedTokens.size()];
        for (int i = 0; i < analysedTokens.size(); i++) {
            Token token = analysedTokens.get(i);
            a.tokRefs[i] = token.getID();
            connector.token2ItsAnalysis.put(token, a);
        }
        moans.add(a);
        return a;
    }

    @Override
    public MorphologyAnalysis addAnalysis(List<Token> analysedTokens,
            List<Feature> morphologyFeatures, List<MorphologySegment> segments) {
        MorphologyAnalysisStored a = (MorphologyAnalysisStored) addAnalysis(analysedTokens, morphologyFeatures);
        for (MorphologySegment segment : segments) {
            if (segment instanceof MorphologySegmentStored) {
                MorphologySegmentStored s = (MorphologySegmentStored) segment;
                this.hasSegmentation = true;
                if (s.hasCharoffsets()) {
                    this.hasCharoffsets = true;
                }
                if (a.segments == null) {
                    a.segments = new ArrayList<MorphologySegmentStored>();
                }
                a.segments.add(s);
            }
        }
        return a;
    }

    @Override
    public Feature createFeature(String name, String value) {
        FeatureStored f = new FeatureStored();
        f.name = name;
        f.value = value;
        return f;
    }

    @Override
    public Feature createFeature(String name, List<Feature> subfeatures) {
        FeatureStored f = new FeatureStored();
        f.name = name;
        f.fs = new FeatureStructureStored();
        for (Feature sf : subfeatures) {
            if (sf instanceof FeatureStored) {
                f.fs.features.add((FeatureStored) sf);
            }
        }
        return f;
    }

    @Override
    public MorphologySegment createSegment(String type, String category,
            String function, Integer start, Integer end, String value) {
        MorphologySegmentStored ms = new MorphologySegmentStored();
        ms.type = type;
        ms.function = function;
        ms.category = category;
        ms.start = start;
        ms.end = end;
        ms.value = value;
        return ms;
    }

    @Override
    public MorphologySegment createSegment(String type, String category,
            String function, Integer start, Integer end,
            List<MorphologySegment> subsegments) {
        MorphologySegmentStored ms = new MorphologySegmentStored();
        ms.type = type;
        ms.function = function;
        ms.category = category;
        ms.start = start;
        ms.end = end;
        ms.subsegments = new ArrayList<MorphologySegmentStored>();
        for (MorphologySegment s : subsegments) {
            if (s instanceof MorphologySegmentStored) {
                ms.subsegments.add((MorphologySegmentStored) s);
            }
        }
        return ms;
    }

    protected void beforeMarshal(Marshaller m) {
        setFalseAttrToNull();
    }

    private void setFalseAttrToNull() {
        if (this.hasSegmentation == Boolean.FALSE) {
            this.hasSegmentation = null;
        }
        if (this.hasCharoffsets == Boolean.FALSE) {
            this.hasCharoffsets = null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" {");
        if (this.hasCharoffsets != null) {
            sb.append(CommonAttributes.CHAR_OFFSETS).append(" ").append(this.hasCharoffsets);
        }
        if (this.hasSegmentation != null) {
            sb.append(" segmentation ").append(this.hasSegmentation);
        }
        sb.append("}: ");
        sb.append(moans.toString());
        return sb.toString();
    }
}
