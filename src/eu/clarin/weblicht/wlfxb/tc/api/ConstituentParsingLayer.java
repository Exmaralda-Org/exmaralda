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
 * The <tt>ConstituentParsingLayer</tt> layer represents phrase structure 
 * parsing annotations on sentence tokens. The layer specifies the tagset used 
 * for phrase structure categories. The parsed structure is a tree, where the 
 * terminal nodes reference tokens, and non-terminal nodes are composed of other
 * nodes. Optionally, the nodes can include incoming edge labels. Additionally, 
 * secondary edges can be specified by referencing target nodes.
 * 
 * @author Yana Panchenko
 */
public interface ConstituentParsingLayer extends TextCorpusLayer {

    public String getTagset();

    public ConstituentParse getParse(int index);

    public Constituent getParseRoot(int index);

    public Token[] getTokens(ConstituentParse parse);

    public Token[] getTokens(Constituent constituent);

    /**
     * Creates non-terminal constituent with the given category and constituent
     * children. Children should have been created by the same
     * ConstituentParsingLayer object before
     */
    public Constituent createConstituent(String category, List<Constituent> children);

    public Constituent createConstituent(String category, String edge, List<Constituent> children);

    public Constituent createConstituent(String category, List<Constituent> children, String id);

    public Constituent createConstituent(String category, String edge, List<Constituent> children, String id);

    /**
     * Creates non-terminal constituent with the given category, children should
     * be added later
     */
    public Constituent createConstituent(String category);

    public Constituent createConstituent(String category, String edge);

    public Constituent createConstituent(String category, String edge, String id);

    /**
     * Adds constituent child to a parent constituent. Both child and parent
     * constituent should have been created by the same ConstituentParsingLayer
     * object before
     */
    public Constituent addChild(Constituent parent, Constituent child);

    /**
     * Adds secondary edge child to a constituent. Both child and parent
     * constituent should have been created by the same ConstituentParsingLayer
     * object before
     */
    public Constituent addSecondaryEdgeChild(Constituent parent, Constituent child, String edgeLabel);

    /**
     * Gets the Constituent reference by this ConstituentReference object
     */
    public Constituent getConstituent(ConstituentReference cref);

    /**
     * Creates terminal constituent with the given category and tokens
     */
    public Constituent createTerminalConstituent(String category, List<Token> tokens);

    public Constituent createTerminalConstituent(String category, String edge, List<Token> tokens);

    public Constituent createTerminalConstituent(String category, List<Token> tokens, String id);

    public Constituent createTerminalConstituent(String category, String edge, List<Token> tokens, String id);

    /**
     * Creates terminal constituent with the given category and token
     */
    public Constituent createTerminalConstituent(String category, Token token);

    public Constituent createTerminalConstituent(String category, String edge, Token token);

    public Constituent createTerminalConstituent(String category, String edge, Token token, String id);

    /**
     * Creates sentence parse with the given constituent root. Root should have
     * been created by the same ConstituentParsingLayer object before
     */
    public ConstituentParse addParse(Constituent root);
}
