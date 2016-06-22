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

import eu.clarin.weblicht.wlfxb.tc.api.PhoneticsLayer;
import eu.clarin.weblicht.wlfxb.tc.api.PhoneticsSegment;
import eu.clarin.weblicht.wlfxb.tc.api.Pronunciation;
import eu.clarin.weblicht.wlfxb.tc.api.Token;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import eu.clarin.weblicht.wlfxb.utils.WlfUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = PhoneticsLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class PhoneticsLayerStored extends TextCorpusLayerStoredAbstract implements PhoneticsLayer {

    public static final String XML_NAME = "phonetics";
    @XmlAttribute(name = CommonAttributes.ALPHABET)
    private String alphabet;
    @XmlElement(name = PhoneticsSegmentStored.XML_NAME)
    private List<PhoneticsSegmentStored> phsegs = new ArrayList<PhoneticsSegmentStored>();
    private TextCorpusLayersConnector connector;

    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        this.connector = connector;
        for (int i = 0; i < phsegs.size(); i++) {
            PhoneticsSegmentStored phseg = phsegs.get(i);
            connector.token2ItsPhseg.put(connector.tokenId2ItsToken.get(phseg.tokRef), phseg);
        }
    }

    protected PhoneticsLayerStored() {
    }

    protected PhoneticsLayerStored(String alphabet) {
        this.alphabet = alphabet;
    }

    protected PhoneticsLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    @Override
    public boolean isEmpty() {
        return phsegs.isEmpty();
    }

    @Override
    public int size() {
        return phsegs.size();
    }

    @Override
    public String getAlphabet() {
        return alphabet;
    }

    @Override
    public PhoneticsSegment getSegment(int index) {
        return phsegs.get(index);
    }

    @Override
    public PhoneticsSegment getSegment(Token token) {
        return connector.token2ItsPhseg.get(token);
    }

    @Override
    public Token getToken(PhoneticsSegment segment) {
        if (segment instanceof PhoneticsSegmentStored) {
            return this.connector.tokenId2ItsToken.get(((PhoneticsSegmentStored) segment).tokRef);
        } else {
            throw new UnsupportedOperationException(WlfUtilities.layersErrorMessage(PhoneticsSegment.class, PhoneticsLayer.class));
        }
    }

    @Override
    public Pronunciation createPronunciation(PronunciationType type,
            String canonicalPronunciation, String realizedPronunciation,
            float onsetInSeconds, float offsetInSeconds,
            List<Pronunciation> children) {
        PronunciationStored pron = new PronunciationStored();
        pron.type = type;
        pron.cp = canonicalPronunciation;
        pron.rp = realizedPronunciation;
        pron.onset = onsetInSeconds;
        pron.offset = offsetInSeconds;
        for (Pronunciation child : children) {
            if (child instanceof PronunciationStored) {
                pron.children.add((PronunciationStored) child);
            } else {
                throw new UnsupportedOperationException(WlfUtilities.layersErrorMessage(Pronunciation.class, PhoneticsLayer.class));
            }
        }
        return pron;
    }

    @Override
    public Pronunciation createPronunciation(PronunciationType type,
            String canonicalPronunciation, String realizedPronunciation,
            List<Pronunciation> children) {
        PronunciationStored pron = new PronunciationStored();
        pron.type = type;
        pron.cp = canonicalPronunciation;
        pron.rp = realizedPronunciation;
        for (Pronunciation child : children) {
            if (child instanceof PronunciationStored) {
                pron.children.add((PronunciationStored) child);
            } else {
                throw new UnsupportedOperationException(WlfUtilities.layersErrorMessage(Pronunciation.class, PhoneticsLayer.class));
            }
        }
        return pron;
    }

    @Override
    public Pronunciation createPronunciation(PronunciationType type,
            String realizedPronunciation, float onsetInSeconds,
            float offsetInSeconds, List<Pronunciation> children) {
        PronunciationStored pron = new PronunciationStored();
        pron.type = type;
        pron.rp = realizedPronunciation;
        pron.onset = onsetInSeconds;
        pron.offset = offsetInSeconds;
        for (Pronunciation child : children) {
            if (child instanceof PronunciationStored) {
                pron.children.add((PronunciationStored) child);
            } else {
                throw new UnsupportedOperationException(WlfUtilities.layersErrorMessage(Pronunciation.class, PhoneticsLayer.class));
            }
        }
        return pron;
    }

    @Override
    public Pronunciation createPronunciation(PronunciationType type,
            String realizedPronunciation, float onsetInSeconds,
            float offsetInSeconds) {
        PronunciationStored pron = new PronunciationStored();
        pron.type = type;
        pron.rp = realizedPronunciation;
        pron.onset = onsetInSeconds;
        pron.offset = offsetInSeconds;
        return pron;
    }

    @Override
    public Pronunciation createPronunciation(PronunciationType type,
            String canonicalPronunciation) {
        PronunciationStored pron = new PronunciationStored();
        pron.type = type;
        pron.cp = canonicalPronunciation;
        return pron;
    }

    @Override
    public Pronunciation addChild(Pronunciation parent, Pronunciation child) {
        PronunciationStored parentS = null;
        PronunciationStored childS = null;
        if (parent instanceof PronunciationStored) {
            parentS = (PronunciationStored) parent;
        } else {
            throw new UnsupportedOperationException(WlfUtilities.layersErrorMessage(Pronunciation.class, PhoneticsLayer.class));
        }
        if (child instanceof PronunciationStored) {
            childS = (PronunciationStored) child;
        } else {
            throw new UnsupportedOperationException(WlfUtilities.layersErrorMessage(Pronunciation.class, PhoneticsLayer.class));
        }
        parentS.children.add(childS);
        return parentS;
    }

    @Override
    public PhoneticsSegment addSegment(Pronunciation pronunciation, Token token) {
        return addSegment(Arrays.asList(new Pronunciation[]{pronunciation}), token);
    }

    @Override
    public PhoneticsSegment addSegment(List<Pronunciation> pronunciations, Token token) {
        PhoneticsSegmentStored phseg = new PhoneticsSegmentStored();
        phseg.tokRef = token.getID();
        for (Pronunciation pron : pronunciations) {
            if (pron instanceof PronunciationStored) {
                phseg.prons.add((PronunciationStored) pron);
            } else {
                throw new UnsupportedOperationException(WlfUtilities.layersErrorMessage(Pronunciation.class, PhoneticsLayer.class));
            }
        }
        phsegs.add(phseg);
        return phseg;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" {");
        sb.append(CommonAttributes.ALPHABET).append(" ").append(alphabet);
        sb.append("}: ");
        sb.append(phsegs.toString());
        return sb.toString();
    }
}
