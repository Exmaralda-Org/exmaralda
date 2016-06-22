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

import eu.clarin.weblicht.wlfxb.tc.api.Orthform;
import eu.clarin.weblicht.wlfxb.tc.api.Token;
import eu.clarin.weblicht.wlfxb.tc.api.WordSense;
import eu.clarin.weblicht.wlfxb.tc.api.WordSensesLayer;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import eu.clarin.weblicht.wlfxb.utils.WlfUtilities;
import java.util.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = WordSensesLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class WordSensesLayerStored extends TextCorpusLayerStoredAbstract implements WordSensesLayer {

    public static final String XML_NAME = "wsd";
    @XmlAttribute(name = CommonAttributes.SOURCE)
    private String source;
    @XmlElement(name = WordSenseStored.XML_NAME)
    private List<WordSenseStored> senses = new ArrayList<WordSenseStored>();
    private TextCorpusLayersConnector connector;

    protected WordSensesLayerStored() {
    }

    protected WordSensesLayerStored(String source) {
        this.source = source;
    }

    protected WordSensesLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    @Override
    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        this.connector = connector;
        for (WordSenseStored ws : senses) {
            for (String tokRef : ws.tokRefs) {
                connector.token2ItsWordSense.put(connector.tokenId2ItsToken.get(tokRef), ws);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return senses.isEmpty();
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public int size() {
        return senses.size();
    }

    @Override
    public WordSense getWordSense(int index) {
        return senses.get(index);
    }
    
    @Override
    public WordSense getWordSense(Token token) {
        WordSense ws = connector.token2ItsWordSense.get(token);
        return ws;
    }

    @Override
    public Token[] getTokens(WordSense ws) {
        if (ws instanceof WordSenseStored) {
            WordSenseStored sense = (WordSenseStored) ws;
            Token[] tokens = new Token[sense.tokRefs.length];
            for (int i = 0; i < sense.tokRefs.length; i++) {
                tokens[i] = this.connector.tokenId2ItsToken.get(sense.tokRefs[i]);
            }
            return tokens;
        } else {
            throw new UnsupportedOperationException(WlfUtilities.layersErrorMessage(Orthform.class, LexicalSemanticsLayerStored.class));
        }
    }

    
    @Override
    public WordSense addWordSense(Token token, String ... lexicalUnits) {
        List<Token> tokens = Arrays.asList(new Token[]{token});
        return addWordSense(tokens, null, lexicalUnits);
    }
    
    @Override
    public WordSense addWordSense(List<Token> tokens, String ... lexicalUnits) {
        return addWordSense(tokens, null, lexicalUnits);
    }
    
    @Override
    public WordSense addWordSense(Token token, String comment, String ... lexicalUnits) {
        List<Token> tokens = Arrays.asList(new Token[]{token});
        return addWordSense(tokens, comment, lexicalUnits);
    }
    
    @Override
    public WordSense addWordSense(List<Token> tokens, String comment, String ... lexicalUnits) {
        WordSenseStored ws = new WordSenseStored();
        ws.comment = comment;
        ws.lexunits = new String[lexicalUnits.length];
        System.arraycopy(lexicalUnits, 0, ws.lexunits, 0, lexicalUnits.length);
        ws.tokRefs = new String[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            ws.tokRefs[i] = token.getID();
            connector.token2ItsWordSense.put(token, ws);
        }
        senses.add(ws);
        return ws;
    }
    

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append("{");
        sb.append(source);
        sb.append("}");
        sb.append(" : ");
        sb.append(senses.toString());
        return sb.toString();
    }

}
