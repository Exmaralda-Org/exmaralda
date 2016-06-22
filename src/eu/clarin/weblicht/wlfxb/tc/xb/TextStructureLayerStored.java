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

import eu.clarin.weblicht.wlfxb.io.WLFormatException;
import eu.clarin.weblicht.wlfxb.tc.api.*;
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
@XmlRootElement(name = TextStructureLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class TextStructureLayerStored extends TextCorpusLayerStoredAbstract implements TextStructureLayer {

    public static final String XML_NAME = "textstructure";
    @XmlElement(name = TextSpanStored.XML_NAME)
    private List<TextSpanStored> tspans = new ArrayList<TextSpanStored>();
    private TextCorpusLayersConnector connector;

//    protected void setLayersConnector(TextCorpusLayersConnector connector) {
//        this.connector = connector;
//        for (int i = 0; i < tspans.size(); i++) {
//            TextSpanStored tspan = tspans.get(i);
//            TextSpanType type = tspan.getType();
//            if (tspan.startToken != null && tspan.endToken != null) {
//                int start = connector.tokenId2ItsToken.get(tspan.startToken).getOrder();
//                int end = connector.tokenId2ItsToken.get(tspan.endToken).getOrder() + 1;
//                for (int j = start; j < end; j++) {
//                    connector.token2ItsTextSpans.get(type).put(connector.tokens.get(j), tspan);
//                }
//            }
//        }
//    }
    
    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        this.connector = connector;
        for (int i = 0; i < tspans.size(); i++) {
            addToConnector(tspans.get(i), connector);
        }
    }
    
    private void addToConnector(TextSpanStored tspan, TextCorpusLayersConnector connector) {
            String type = tspan.getType();
            if (!connector.token2ItsTextSpans.containsKey(type)) {
                    connector.token2ItsTextSpans.put(type, new HashMap<Token, TextSpan>());
            }
            if (tspan.startToken != null && tspan.endToken != null) {
                int start = connector.tokenId2ItsToken.get(tspan.startToken).getOrder();
                int end = connector.tokenId2ItsToken.get(tspan.endToken).getOrder() + 1;
                for (int j = start; j < end; j++) {
                    connector.token2ItsTextSpans.get(type).put(connector.tokens.get(j), tspan);
                }
                if (tspan.getSubspans() != null) {
                    for (TextSpanStored subspan : tspan.getSubspans()) {
                        addToConnector(subspan, connector);
                    }
                }
            }
    }

    protected TextStructureLayerStored() {
    }

    protected TextStructureLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    @Override
    public boolean isEmpty() {
        return tspans.isEmpty();
    }

    @Override
    public int size() {
        return tspans.size();
    }

    @Override
    public TextSpan getSpan(int index) {
        return tspans.get(index);
    }

//    @Override
//    public List<TextSpan> getSpans(Token token) {
//        List<TextSpan> spans = new ArrayList<TextSpan>();
//        for (TextSpanType type : connector.token2ItsTextSpans.keySet()) {
//            Map<Token, TextSpan> tokToSpan = connector.token2ItsTextSpans.get(type);
//            TextSpan span = tokToSpan.get(token);
//            if (span != null) {
//                spans.add(span);
//            }
//        }
//        return spans;
//    }
    
    @Override
    public List<TextSpan> getSpans(Token token) {
        List<TextSpan> spans = new ArrayList<TextSpan>();
        for (String type : connector.token2ItsTextSpans.keySet()) {
            Map<Token, TextSpan> tokToSpan = connector.token2ItsTextSpans.get(type);
            TextSpan span = tokToSpan.get(token);
            if (span != null) {
                spans.add(span);
            }
        }
        return spans;
    }

//    @Override
//    public TextSpan getSpan(Token token, TextSpanType type) {
//        Map<Token, TextSpan> tokToSpan = connector.token2ItsTextSpans.get(type);
//        TextSpan span = tokToSpan.get(token);
//        return span;
//    }

    @Override
    public TextSpan getSpan(Token token, String type) {
        TextSpan span = null;
        Map<Token, TextSpan> tokToSpan = connector.token2ItsTextSpans.get(type);
        if (tokToSpan != null) {
            span = tokToSpan.get(token);
        }
        return span;
    }
    
//    @Override
//    public List<TextSpan> getSpans(TextSpanType type) {
//        List<TextSpan> spans = new ArrayList<TextSpan>();
//        Map<Token, TextSpan> tokToSpan = connector.token2ItsTextSpans.get(type);
//        spans.addAll(tokToSpan.values());
//        return spans;
//    }
    
    @Override
    public List<TextSpan> getSpans(String type) {
        List<TextSpan> spans = new ArrayList<TextSpan>();
        Map<Token, TextSpan> tokToSpan = connector.token2ItsTextSpans.get(type);
        if (tokToSpan != null) {
            spans.addAll(tokToSpan.values());
        }
        return spans;
    }

    @Override
    public Token[] getTokens(TextSpan span) {
        if (span instanceof TextSpanStored) {
            TextSpanStored tspan = (TextSpanStored) span;
            if (tspan.startToken != null && tspan.startToken != null) {
                int start = connector.tokenId2ItsToken.get(tspan.startToken).getOrder();
                int end = connector.tokenId2ItsToken.get(tspan.endToken).getOrder() + 1;
                Token[] tokens = new Token[end - start];
                for (int i = 0, j = start; j < end; i++, j++) {
                    tokens[i] = connector.tokens.get(j);
                }
                return tokens;
            } else {
                return new Token[0];
            }
        } else {
            throw new UnsupportedOperationException(WlfUtilities.layersErrorMessage(TextSpan.class, OrthographyLayer.class));
        }

    }

//    @Override
//    public TextSpan addSpan(Token spanStart, Token spanEnd, TextSpanType type) {
//        TextSpanStored tspan = new TextSpanStored();
//        tspan.type = type;
//        if (spanStart != null && spanEnd != null) {
//            tspan.startToken = spanStart.getID();
//            tspan.endToken = spanEnd.getID();
//            int start = connector.tokenId2ItsToken.get(tspan.startToken).getOrder();
//            int end = connector.tokenId2ItsToken.get(tspan.endToken).getOrder() + 1;
//            for (int j = start; j < end; j++) {
//                connector.token2ItsTextSpans.get(type).put(connector.tokens.get(j), tspan);
//            }
//        }
//        tspans.add(tspan);
//        return tspan;
//    }
    
    @Override
    public TextSpan addSpan(Token spanStart, Token spanEnd, String type) {
        return addSpan(spanStart, spanEnd, type, null, null, null);
    }
    
    @Override
    public TextSpan addSpan(Token spanStart, Token spanEnd, String type, int startChar, int endChar) {
        return addSpan(spanStart, spanEnd, type, null, startChar, endChar);
    }
    
    @Override
    public TextSpan addSpan(Token spanStart, Token spanEnd, String type, String value) {
        return addSpan(spanStart, spanEnd, type, value, null, null);
    }
    
    private TextSpan addSpan(Token spanStart, Token spanEnd, String type, String value, Integer startChar, Integer endChar) {
        TextSpanStored tspan = createTextSpan(spanStart, spanEnd, type, value, startChar, endChar);
        tspans.add(tspan);
        return tspan;
    }
    
    @Override
    public TextSpan addSpan(TextSpan parentSpan, Token spanStart, Token spanEnd, String type) throws WLFormatException {
        return addSpan(parentSpan, spanStart, spanEnd, type, null);
    }
    
    @Override
    public TextSpan addSpan(TextSpan parentSpan, Token spanStart, Token spanEnd, String type, int startChar, int endChar) throws WLFormatException {
        return addSpan(parentSpan, spanStart, spanEnd, type, null, startChar, endChar);
    }
    
    @Override
    public TextSpan addSpan(TextSpan parentSpan, Token spanStart, Token spanEnd, String type, String value) throws WLFormatException {
        return addSpan(parentSpan, spanStart, spanEnd, type, value, null, null);
    }
    
    private TextSpan addSpan(TextSpan parentSpan, Token spanStart, Token spanEnd, 
            String type, String value,
            Integer startChar, Integer endChar) throws WLFormatException {
        
        TextSpanStored tspan = createTextSpan(spanStart, spanEnd, type, value, startChar, endChar);
        
        if (parentSpan == null) {
            tspans.add(tspan);
        } else {
            if (parentSpan.getValue() != null) {
                throw new WLFormatException("The parent " + 
                        TextSpan.class.getSimpleName() + " already has value: " 
                        + parentSpan.getValue() + ". The " + TextSpan.class.getSimpleName() + 
                        "that has value cannot additionally have " + TextSpan.class.getSimpleName() + " subelements");
            }
            
            if (parentSpan instanceof TextSpanStored) {
                TextSpanStored parentSpanStored = (TextSpanStored) parentSpan;
                if (parentSpanStored.subspans == null) {
                    parentSpanStored.subspans = new ArrayList<TextSpanStored>();
                }
                parentSpanStored.subspans.add(tspan);
            }
        }
        return tspan;
    }
    
    private TextSpanStored createTextSpan(Token spanStart, Token spanEnd, 
            String type, String value,
            Integer startChar, Integer endChar) {
         
        TextSpanStored tspan = new TextSpanStored();
        tspan.type = type;
        tspan.value = value;
        if (spanStart != null && spanEnd != null) {
            tspan.startToken = spanStart.getID();
            tspan.endToken = spanEnd.getID();
            int start = connector.tokenId2ItsToken.get(tspan.startToken).getOrder();
            int end = connector.tokenId2ItsToken.get(tspan.endToken).getOrder() + 1;
            for (int j = start; j < end; j++) {
                if (!connector.token2ItsTextSpans.containsKey(type)) {
                    connector.token2ItsTextSpans.put(type, new HashMap<Token, TextSpan>());
                }
                connector.token2ItsTextSpans.get(type).put(connector.tokens.get(j), tspan);
            }
        }
        if (startChar != null && endChar != null) {
            tspan.startChar = startChar;
            tspan.endChar = endChar;
        }
        return tspan;
    }
    


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(": ");
        sb.append(tspans.toString());
        return sb.toString();
    }



}
