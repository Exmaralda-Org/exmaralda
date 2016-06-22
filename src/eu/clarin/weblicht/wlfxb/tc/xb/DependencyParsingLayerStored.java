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

import eu.clarin.weblicht.wlfxb.tc.api.Dependency;
import eu.clarin.weblicht.wlfxb.tc.api.DependencyParse;
import eu.clarin.weblicht.wlfxb.tc.api.DependencyParsingLayer;
import eu.clarin.weblicht.wlfxb.tc.api.Token;
import eu.clarin.weblicht.wlfxb.utils.CommonAttributes;
import eu.clarin.weblicht.wlfxb.utils.WlfUtilities;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = DependencyParsingLayerStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class DependencyParsingLayerStored extends TextCorpusLayerStoredAbstract implements DependencyParsingLayer {

    public static final String XML_NAME = "depparsing";
    public static final String XML_ATTR_MULTIPLE_GOVERNORS = "multigovs";
    public static final String XML_ATTR_EMPTY_TOKENS = "emptytoks";
    @XmlAttribute(name = CommonAttributes.TAGSET)
    private String tagset;
    @XmlAttribute(name = XML_ATTR_MULTIPLE_GOVERNORS, required = true)
    private boolean multigovs;
    @XmlAttribute(name = XML_ATTR_EMPTY_TOKENS, required = true)
    private boolean emptytoks;
    @XmlElement(name = DependencyParseStored.XML_NAME)
    private List<DependencyParseStored> parses = new ArrayList<DependencyParseStored>();
    private TextCorpusLayersConnector connector;

    protected void setLayersConnector(TextCorpusLayersConnector connector) {
        this.connector = connector;
        for (DependencyParseStored parse : parses) {
            if (parse.emptytoks != null) {
                for (EmptyTokenStored etok : parse.emptytoks) {
                    etok.order = this.connector.emptyTokId2EmptyTok.size();
                    this.connector.emptyTokId2EmptyTok.put(etok.getID(), etok);
                }
            }
        }
    }

    protected DependencyParsingLayerStored() {
    }

    protected DependencyParsingLayerStored(String tagset, Boolean hasMultipleGovernors, Boolean hasEmptyTokens) {
        this.tagset = tagset;
        this.multigovs = hasMultipleGovernors;
        this.emptytoks = hasEmptyTokens;
    }

    protected DependencyParsingLayerStored(Boolean hasMultipleGovernors, Boolean hasEmptyTokens) {
        this.multigovs = hasMultipleGovernors;
        this.emptytoks = hasEmptyTokens;
    }

    protected DependencyParsingLayerStored(TextCorpusLayersConnector connector) {
        this.connector = connector;
    }

    @Override
    public boolean isEmpty() {
        if (parses.isEmpty()) {
            return true;
        }
        return false;
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
    public boolean hasEmptyTokens() {
        return this.emptytoks;
    }

    @Override
    public boolean hasMultipleGovernors() {
        return this.multigovs;
    }

    @Override
    public DependencyParse getParse(int index) {
        DependencyParseStored parse = parses.get(index);
        return parse;
    }

    @Override
    public Token[] getGovernorTokens(Dependency dependency) {
        if (dependency instanceof DependencyStored) {
            DependencyStored dep = (DependencyStored) dependency;
            if (dep.govIds != null) {
                return getTokens(dep.govIds);
            }
        }
        return null;
    }

    @Override
    public Token[] getDependentTokens(Dependency dependency) {
        if (dependency instanceof DependencyStored) {
            DependencyStored dep = (DependencyStored) dependency;
            return getTokens(dep.depIds);
        } else {
            return null;
        }
    }

    private Token[] getTokens(String[] tokRefs) {
        Token[] tokens = new Token[tokRefs.length];
        for (int i = 0; i < tokens.length; i++) {
            if (connector.emptyTokId2EmptyTok.containsKey(tokRefs[i])) {
                tokens[i] = connector.emptyTokId2EmptyTok.get(tokRefs[i]);
            } else {
                tokens[i] = connector.tokenId2ItsToken.get(tokRefs[i]);
            }
        }
        return tokens;
    }

    public Token createEmptyToken(String tokenString) {
        if (tokenString == null) {
            tokenString = "";
        }
        EmptyTokenStored token = new EmptyTokenStored();
        token.tokenString = tokenString;
        int tokenCount = connector.emptyTokId2EmptyTok.size();
        token.order = tokenCount;
        token.id = EmptyTokenStored.ID_PREFIX + tokenCount;
        //add to connector map
        connector.emptyTokId2EmptyTok.put(token.id, token);
        return token;
    }

    public Dependency createDependency(String function, List<Token> dependent, List<Token> governor) {
        DependencyStored dep = new DependencyStored();
        dep.function = function;
        dep.depIds = WlfUtilities.tokens2TokenIds(dependent);
        dep.govIds = WlfUtilities.tokens2TokenIds(governor);
        return dep;
    }

    public Dependency createDependency(String function, List<Token> dependent) {
        DependencyStored dep = new DependencyStored();
        dep.function = function;
        dep.depIds = WlfUtilities.tokens2TokenIds(dependent);
        return dep;
    }

    public Dependency createDependency(List<Token> dependent, List<Token> governor) {
        return createDependency(null, dependent, governor);
    }

    public Dependency createDependency(List<Token> dependent) {
        DependencyStored dep = new DependencyStored();
        dep.depIds = WlfUtilities.tokens2TokenIds(dependent);
        return dep;
    }

    public Dependency createDependency(String function, Token dependent, Token governor) {
        DependencyStored dep = new DependencyStored();
        dep.function = function;
        dep.depIds = new String[]{dependent.getID()};
        dep.govIds = new String[]{governor.getID()};
        return dep;
    }

    public Dependency createDependency(String function, Token dependent) {
        DependencyStored dep = new DependencyStored();
        dep.function = function;
        dep.depIds = new String[]{dependent.getID()};
        return dep;
    }

    public Dependency createDependency(Token dependent, Token governor) {
        return createDependency(null, dependent, governor);
    }

    public Dependency createDependency(Token dependent) {
        DependencyStored dep = new DependencyStored();
        dep.depIds = new String[]{dependent.getID()};
        return dep;
    }

    public DependencyParse addParse(List<Dependency> dependencies) {

        DependencyParseStored parse = new DependencyParseStored();
        List<DependencyStored> deps = new ArrayList<DependencyStored>(dependencies.size());
        List<EmptyTokenStored> emptyTokens = new ArrayList<EmptyTokenStored>();
        for (Dependency dep : dependencies) {
            if (dep instanceof DependencyStored) {
                DependencyStored depS = (DependencyStored) dep;
                deps.add(depS);
                for (String ref : depS.depIds) {
                    EmptyTokenStored emptyToken = connector.emptyTokId2EmptyTok.get(ref);
                    if (emptyToken != null && !emptyTokens.contains(emptyToken)) {
                        emptyTokens.add(connector.emptyTokId2EmptyTok.get(ref));
                    }
                }
                if (depS.govIds != null) {
                    for (String ref : depS.govIds) {
                        EmptyTokenStored emptyToken = connector.emptyTokId2EmptyTok.get(ref);
                        if (emptyToken != null && !emptyTokens.contains(emptyToken)) {
                            emptyTokens.add(connector.emptyTokId2EmptyTok.get(ref));
                        }
                    }
                }
            }
        }
        if (!emptyTokens.isEmpty()) {
            parse.emptytoks = emptyTokens;
        }
        parse.dependencies = deps;
        parses.add(parse);
        return parse;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(" {");
        sb.append(CommonAttributes.TAGSET).append(" ").append(getTagset()).append(" ");
        sb.append(XML_ATTR_MULTIPLE_GOVERNORS).append(" ").append(this.multigovs).append(" ");
        sb.append(XML_ATTR_EMPTY_TOKENS).append(" ").append(this.emptytoks);
        sb.append("}: ");
        sb.append(parses.toString());
        return sb.toString();
    }
}
