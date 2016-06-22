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
package eu.clarin.weblicht.wlfxb.utils;

import eu.clarin.weblicht.wlfxb.tc.api.Token;
import java.util.List;
import java.util.Map;

/**
 * @author Yana Panchenko
 *
 */
public class WlfUtilities {

    public static Token[] tokenIdsToTokens(String[] tokIds,
            Map<String, Token> tokenId2ItsToken) {
        Token[] tokens = new Token[tokIds.length];
        for (int i = 0; i < tokIds.length; i++) {
            tokens[i] = tokenId2ItsToken.get(tokIds[i]);
        }
        return tokens;
    }

    public static String[] tokens2TokenIds(List<Token> tokens) {
        String[] tokenIds = new String[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            tokenIds[i] = tokens.get(i).getID();
        }
        return tokenIds;
    }

    public static String layersErrorMessage(Class<?> interfaceOfLayerComponent,
            Class<?> interfaceOfLayer) {
        return "Applicable only to " + interfaceOfLayerComponent.getSimpleName()
                + " created-by/extracted-from the same " + interfaceOfLayer.getSimpleName();
    }
}
