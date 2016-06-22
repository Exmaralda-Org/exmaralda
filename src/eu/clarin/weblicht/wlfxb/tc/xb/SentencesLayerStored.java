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

import eu.clarin.weblicht.wlfxb.tc.api.Sentence;
import eu.clarin.weblicht.wlfxb.tc.api.SentencesLayer;
import eu.clarin.weblicht.wlfxb.tc.api.Token;
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
@XmlRootElement(name = SentencesLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class SentencesLayerStored extends TextCorpusLayerStoredAbstract implements SentencesLayer {

    public static final String XML_NAME = "sentences";
    @XmlElement(name = SentenceStored.XML_NAME, type = SentenceStored.class)
    protected List<SentenceStored> sentences = new ArrayList<SentenceStored>();
    @XmlAttribute(name = CommonAttributes.CHAR_OFFSETS)
    protected Boolean charOffsets;
    private TextCorpusLayersConnector connector;

    protected SentencesLayerStored() {
    }

    protected SentencesLayerStored(Boolean hasCharOffsets) {
        this.charOffsets = hasCharOffsets;
    }

    protected SentencesLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        this.connector = connector;
        for (SentenceStored sentence : sentences) {
            for (String tokRef : sentence.tokIds) {
                connector.token2ItsSentence.put(connector.tokenId2ItsToken.get(tokRef), sentence);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return sentences.isEmpty();
    }

    @Override
    public int size() {
        return sentences.size();
    }

    @Override
    public boolean hasCharOffsets() {
        if (charOffsets == null) {
            return false;
        }
        return charOffsets;
    }

    @Override
    public Sentence getSentence(int index) {
        return sentences.get(index);
    }

    @Override
    public Sentence getSentence(Token token) {
        Sentence sentence = connector.token2ItsSentence.get(token);
        return sentence;
    }

    @Override
    public Token[] getTokens(Sentence sentence) {
        if (sentence instanceof SentenceStored) {
            SentenceStored sStored = (SentenceStored) sentence;
            return WlfUtilities.tokenIdsToTokens(sStored.tokIds, connector.tokenId2ItsToken);
        } else {
            return null;
        }
    }

    @Override
    public Sentence addSentence(List<Token> sentenceTokens) {
        return addSentence(sentenceTokens, null, null);
    }

    @Override
    public Sentence addSentence(List<Token> sentenceTokens, int start, int end) {
        return addSentence(sentenceTokens, (Integer) start, (Integer) end);
    }

    public Sentence addSentence(List<Token> sentenceTokens, Integer start, Integer end) {
        SentenceStored sentence = new SentenceStored();
        sentence.tokIds = new String[sentenceTokens.size()];
        if (start != null && end != null) {
            sentence.start = start;
            sentence.end = end;
            this.charOffsets = true;
        }
        for (int i = 0; i < sentenceTokens.size(); i++) {
            Token sentenceToken = sentenceTokens.get(i);
            sentence.tokIds[i] = sentenceToken.getID();
            connector.token2ItsSentence.put(sentenceToken, sentence);
        }
        sentences.add(sentence);
        return sentence;
    }

    protected void beforeMarshal(Marshaller m) {
        setFalseAttrToNull();
    }

    private void setFalseAttrToNull() {
        if (this.charOffsets == Boolean.FALSE) {
            this.charOffsets = null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" {");
        if (hasCharOffsets()) {
            sb.append(CommonAttributes.CHAR_OFFSETS).append(" ").append(Boolean.toString(charOffsets));
        }
        sb.append("}: ");
        sb.append(sentences.toString());
        return sb.toString();
    }
}
