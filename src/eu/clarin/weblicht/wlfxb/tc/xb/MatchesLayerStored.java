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

import eu.clarin.weblicht.wlfxb.tc.api.MatchedCorpus;
import eu.clarin.weblicht.wlfxb.tc.api.MatchedItem;
import eu.clarin.weblicht.wlfxb.tc.api.MatchesLayer;
import eu.clarin.weblicht.wlfxb.tc.api.Token;
import eu.clarin.weblicht.wlfxb.utils.WlfUtilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = MatchesLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class MatchesLayerStored extends TextCorpusLayerStoredAbstract implements MatchesLayer {

    public static final String XML_NAME = "matches";
    //public static final String XML_ATTRIBUTE_QUERY_TYPE = "type";
    //public static final String XML_ATTRIBUTE_QUERY_STRING = "query";
    //@XmlAttribute(name=XML_ATTRIBUTE_QUERY_TYPE, required=true)
    //private String queryType;
    //@XmlAttribute(name=XML_ATTRIBUTE_QUERY_STRING, required=true)
    //private String queryString;
    @XmlElement(name = "query")
    private MatchesQuery query;
    @XmlElement(name = MatchedCorpusStored.XML_NAME)
    private List<MatchedCorpusStored> corpora = new ArrayList<MatchedCorpusStored>();
    private TextCorpusLayersConnector connector;

    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        this.connector = connector;
        for (MatchedCorpusStored corpus : corpora) {
            for (MatchedItemStored item : corpus.matchedItems) {
                for (String tokRef : item.tokIds) {
                    connector.token2ItsMatchedItem.put(connector.tokenId2ItsToken.get(tokRef), item);
                }
            }
        }
    }

    protected MatchesLayerStored() {
    }

    protected MatchesLayerStored(String queryLanguage, String queryString) {
        this.query = new MatchesQuery(queryLanguage, queryString);
    }

    protected MatchesLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    @Override
    public boolean isEmpty() {
        return corpora.isEmpty();
    }

    @Override
    public int size() {
        return corpora.size();
    }

    @Override
    public String getQueryType() {
        return query.type;
    }

    @Override
    public String getQueryString() {
        return query.value;
    }

    @Override
    public MatchedCorpus getCorpus(int index) {
        return corpora.get(index);
    }

    @Override
    public MatchedItem getMatchedItem(Token token) {
        MatchedItem item = connector.token2ItsMatchedItem.get(token);
        return item;
    }

    @Override
    public MatchedCorpus addCorpus(String corpusName, String corpusPID) {
        MatchedCorpusStored corpus = new MatchedCorpusStored(corpusName, corpusPID);
        this.corpora.add(corpus);
        return corpus;
    }

    @Override
    public MatchedItem addItem(MatchedCorpus corpusToAddItem,
            List<Token> itemTokens, List<String> itemOriginCorpusTokenIds) {
        return addItem(corpusToAddItem, itemTokens, itemOriginCorpusTokenIds,
                new HashMap<String, String>(0), new HashMap<String, String>(0));
    }

    @Override
    public MatchedItem addItem(MatchedCorpus corpusToAddItem,
            List<Token> itemTokens) {
        return addItem(corpusToAddItem, itemTokens, new ArrayList<String>(0),
                new HashMap<String, String>(0), new HashMap<String, String>(0));
    }

    @Override
    public MatchedItem addItem(MatchedCorpus corpusToAddItem,
            List<Token> itemTokens,
            Map<String, String> itemTargets, Map<String, String> itemCategories) {

        return addItem(corpusToAddItem, itemTokens, new ArrayList<String>(0),
                itemTargets, itemCategories);
    }

    @Override
    public MatchedItem addItem(MatchedCorpus corpusToAddItem,
            List<Token> itemTokens, List<String> itemOriginCorpusTokenIds,
            Map<String, String> itemTargets, Map<String, String> itemCategories) {

        if (!(corpusToAddItem instanceof MatchedCorpusStored)) {
            //TODO log warning? how to let user know?
            return null;
        }

        String[] srcIds = new String[itemOriginCorpusTokenIds.size()];
        String[] tokIds = WlfUtilities.tokens2TokenIds(itemTokens);
        srcIds = itemOriginCorpusTokenIds.toArray(srcIds);
        MatchedItemStored item = new MatchedItemStored(
                tokIds, srcIds, itemTargets, itemCategories);
        ((MatchedCorpusStored) corpusToAddItem).matchedItems.add(item);

        for (int i = 0; i < itemTokens.size(); i++) {
            Token token = itemTokens.get(i);
            connector.token2ItsMatchedItem.put(token, item);
        }

        return item;
    }

    @Override
    public Token[] getTokens(MatchedItem item) {
        if (item instanceof MatchedItemStored) {
            MatchedItemStored iStored = (MatchedItemStored) item;
            Token[] tokens = new Token[iStored.tokIds.length];
            for (int i = 0; i < iStored.tokIds.length; i++) {
                tokens[i] = connector.tokenId2ItsToken.get(iStored.tokIds[i]);
            }
            return tokens;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" {");
        sb.append(query.type).append(" ").append(query.value);
        sb.append("}: ");
        sb.append(corpora.toString());
        return sb.toString();
    }
}
