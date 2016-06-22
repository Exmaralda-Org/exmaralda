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

import eu.clarin.weblicht.wlfxb.tc.api.Token;
import eu.clarin.weblicht.wlfxb.tc.api.WordSplit;
import eu.clarin.weblicht.wlfxb.tc.api.WordSplittingLayer;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import eu.clarin.weblicht.wlfxb.utils.WlfUtilities;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = WordSplittingLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class WordSplittingLayerStored extends TextCorpusLayerStoredAbstract implements WordSplittingLayer {

    public static final String XML_NAME = "WordSplittings";
    @XmlAttribute(name = CommonAttributes.TYPE)
    private String type;
    @XmlElement(name = WordSplitStored.XML_NAME)
    private List<WordSplitStored> splits = new ArrayList<WordSplitStored>();
    private TextCorpusLayersConnector connector;

    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        this.connector = connector;
        for (WordSplitStored split : splits) {
            connector.token2ItsSplit.put(connector.tokenId2ItsToken.get(split.tokRef), split);
        }
    }

    protected WordSplittingLayerStored() {
    }

    protected WordSplittingLayerStored(String type) {
        this.type = type;
    }

    protected WordSplittingLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    @Override
    public boolean isEmpty() {
        return splits.isEmpty();
    }

    @Override
    public int size() {
        return splits.size();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public WordSplit getSplit(int index) {
        return splits.get(index);
    }

    @Override
    public WordSplit getSplit(Token token) {
        return this.connector.token2ItsSplit.get(token);
    }

    @Override
    public Token getToken(WordSplit split) {
        if (split instanceof WordSplitStored) {
            return this.connector.tokenId2ItsToken.get(((WordSplitStored) split).tokRef);
        } else {
            throw new UnsupportedOperationException(WlfUtilities.layersErrorMessage(WordSplit.class, WordSplittingLayer.class));
        }

    }

    @Override
    public WordSplit addSplit(Token token, int[] splitIndices) {
        WordSplitStored wordSplit = new WordSplitStored();
        wordSplit.splitIndices = splitIndices;
        wordSplit.tokRef = token.getID();
        splits.add(wordSplit);
        return wordSplit;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" {");
        sb.append(type);
        sb.append("}: ");
        sb.append(splits.toString());
        return sb.toString();
    }
}
