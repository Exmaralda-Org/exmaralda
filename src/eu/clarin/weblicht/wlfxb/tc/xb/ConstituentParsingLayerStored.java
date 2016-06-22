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
package eu.clarin.weblicht.wlfxb.tc.xb;

import eu.clarin.weblicht.wlfxb.tc.api.*;
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
@XmlRootElement(name = ConstituentParsingLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class ConstituentParsingLayerStored extends TextCorpusLayerStoredAbstract implements ConstituentParsingLayer {

    public static final String XML_NAME = "parsing";
    @XmlAttribute(name = CommonAttributes.TAGSET)
    private String tagset;
    @XmlElement(name = ConstituentParseStored.XML_NAME)
    private List<ConstituentParseStored> parses = new ArrayList<ConstituentParseStored>();
    private TextCorpusLayersConnector connector;

    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        this.connector = connector;
        for (ConstituentParseStored parse : parses) {
            Constituent constituent = parse.getRoot();
            connect(constituent);
        }
    }

    private void connect(Constituent constituent) {
        if (constituent instanceof ConstituentStored) {
            ConstituentStored constitStored = (ConstituentStored) constituent;
            if (constitStored.constituentId == null) {
                constitStored.constituentId = ConstituentStored.ID_PREFIX + this.connector.constitId2ItsConstit.size();
            }
            this.connector.constitId2ItsConstit.put(constitStored.constituentId, constitStored);
        }
        if (!constituent.isTerminal()) {
            for (Constituent child : constituent.getChildren()) {
                connect(child);
            }
        }
    }

    protected ConstituentParsingLayerStored() {
    }

    protected ConstituentParsingLayerStored(String tagset) {
        this.tagset = tagset;
    }

    protected ConstituentParsingLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    @Override
    public boolean isEmpty() {
        return parses.isEmpty();
    }

    @Override
    public int size() {
        return parses.size();
    }

    @Override
    public String getTagset() {
        return tagset;
    }

    @Override
    public ConstituentParse getParse(int index) {
        ConstituentParseStored parse = parses.get(index);
        return parse;
    }

    @Override
    public Constituent getParseRoot(int index) {
        ConstituentStored root = parses.get(index).constituentParseRoot;
        return root;
    }

    @Override
    public Token[] getTokens(ConstituentParse parse) {
        if (parse instanceof ConstituentParseStored) {
            ConstituentParseStored pStored = (ConstituentParseStored) parse;
            return getTokens(pStored.constituentParseRoot);
        } else {
            return null;
        }
    }

    @Override
    public Token[] getTokens(Constituent constituent) {
        if (constituent instanceof ConstituentStored) {
            ConstituentStored cStored = (ConstituentStored) constituent;
            //if (cStored.isTerminal()) {
            //	return WlfUtilities.tokenIdsToTokens(cStored.tokRefs, connector.tokenId2ItsToken);
            //}
            List<Token> terminalsTokens = new ArrayList<Token>();
            collectTerminalsTokens(terminalsTokens, cStored);
            Token[] tokens = new Token[terminalsTokens.size()];
            return terminalsTokens.toArray(tokens);
        } else {
            return null;
        }
    }

    @Override
    public Constituent getConstituent(ConstituentReference cref) {
        if (cref instanceof ConstituentReferenceStored) {
            return this.connector.constitId2ItsConstit.get(
                    ((ConstituentReferenceStored) cref).constId);
        } else {
            return null;
        }
    }

    private void collectTerminalsTokens(List<Token> terminalsTokens,
            ConstituentStored c) {
        if (c.isTerminal()) {
            if (!c.isEmptyTerminal()) {
                for (int i = 0; i < c.tokRefs.length; i++) {
                    terminalsTokens.add(connector.tokenId2ItsToken.get(c.tokRefs[i]));
                }
            }
        } else {
            for (ConstituentStored child : c.children) {
                collectTerminalsTokens(terminalsTokens, child);
            }
        }
    }

    @Override
    public Constituent createConstituent(String category, List<Constituent> children) {
        return createConstituent(category, null, children, null);
    }

    @Override
    public Constituent createConstituent(String category, String edge, List<Constituent> children) {
        return createConstituent(category, edge, children, null);
    }

    @Override
    public Constituent createConstituent(String category, List<Constituent> children, String id) {
        return createConstituent(category, null, children, id);
    }

    @Override
    public Constituent createConstituent(String category) {
        return createConstituent(category, null, new ArrayList<Constituent>(), null);
    }

    @Override
    public Constituent createConstituent(String category, String edge) {
        return createConstituent(category, edge, new ArrayList<Constituent>(), null);
    }

    @Override
    public Constituent createConstituent(String category, String edge, String id) {
        return createConstituent(category, edge, new ArrayList<Constituent>(), id);
    }

    @Override
    public Constituent createConstituent(String category, String edge, List<Constituent> children, String id) {
        ConstituentStored c = new ConstituentStored();
        c.category = category;
        c.edge = edge;
        if (id == null) {
            id = ConstituentStored.ID_PREFIX + connector.constitId2ItsConstit.size();
        }
        c.constituentId = id;
        connector.constitId2ItsConstit.put(id, c);
        for (Constituent child : children) {
            if (child instanceof ConstituentStored) {
                c.children.add((ConstituentStored) child);
            } else {
                return null;
            }
        }
        return c;
    }

    @Override
    public Constituent addChild(Constituent parent, Constituent child) {
        if (parent instanceof ConstituentStored
                && child instanceof ConstituentStored) {
            ((ConstituentStored) parent).children.add((ConstituentStored) child);
        } else {
            return null;
        }
        return parent;
    }

    @Override
    public Constituent addSecondaryEdgeChild(Constituent parent, Constituent child, String edgeLabel) {
        if (parent instanceof ConstituentStored
                && child instanceof ConstituentStored) {
            ConstituentStored parentStored = (ConstituentStored) parent;
            ConstituentStored childStored = (ConstituentStored) child;
            ConstituentReferenceStored crefStored =
                    new ConstituentReferenceStored(childStored, edgeLabel);
            parentStored.crefs.add(crefStored);
        } else {
            return null;
        }
        return parent;
    }

    @Override
    public Constituent createTerminalConstituent(String category, List<Token> tokens) {
        return createTerminalConstituent(category, null, tokens, null);
    }

    @Override
    public Constituent createTerminalConstituent(String category, String edge, List<Token> tokens) {
        return createTerminalConstituent(category, edge, tokens, null);
    }

    @Override
    public Constituent createTerminalConstituent(String category, List<Token> tokens, String id) {
        return createTerminalConstituent(category, null, tokens, id);
    }

    @Override
    public Constituent createTerminalConstituent(String category, Token token) {
        return createTerminalConstituent(category, null, Arrays.asList(new Token[]{token}), null);
    }

    @Override
    public Constituent createTerminalConstituent(String category, String edge, Token token) {
        return createTerminalConstituent(category, edge, Arrays.asList(new Token[]{token}), null);
    }

    @Override
    public Constituent createTerminalConstituent(String category, String edge, Token token, String id) {
        return createTerminalConstituent(category, edge, Arrays.asList(new Token[]{token}), id);
    }

    @Override
    public Constituent createTerminalConstituent(String category, String edge, List<Token> tokens, String id) {
        ConstituentStored c = new ConstituentStored();
        String[] tokRefs = WlfUtilities.tokens2TokenIds(tokens);
        c.category = category;
        c.edge = edge;
        c.tokRefs = tokRefs;
        if (id == null) {
            id = ConstituentStored.ID_PREFIX + connector.constitId2ItsConstit.size();
        }
        c.constituentId = id;
        connector.constitId2ItsConstit.put(id, c);
        return c;
    }

    @Override
    public ConstituentParse addParse(Constituent root) {
        ConstituentParseStored parse = new ConstituentParseStored();
        if (root instanceof ConstituentStored) {
            parse.constituentParseRoot = (ConstituentStored) root;
            parses.add(parse);
            return parse;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" {");
        sb.append(CommonAttributes.TAGSET).append(" ").append(getTagset());
        sb.append("}: ");
        sb.append(parses.toString());
        return sb.toString();
    }
}
