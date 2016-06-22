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
 * The <tt>GeoLayer</tt> layer represents annotations for geographical locations. A token, 
 * or sequence of tokens, for which a geographical location can be identified, 
 * is annotated with longitude and latitude coordinates, optionally altitude, 
 * continent, country and capital. At the layer level, the attributes specify 
 * which format is used as longitude, latitude, continent, country and capital 
 * values of a geographical point.
 * 
 * @author Yana Panchenko
 */
public interface GeoLayer extends TextCorpusLayer {

    public GeoLongLatFormat getCoordinatesFormat();

    public GeoContinentFormat getContinentFormat();

    public GeoCountryFormat getCountryFormat();

    public GeoCapitalFormat getCapitalFormat();

    public String getSource();

    public GeoPoint getPoint(int index);

    public GeoPoint getPoint(Token token);

    public Token[] getTokens(GeoPoint point);

    public GeoPoint addPoint(String longitude, String latitude, Double altitude,
            String continent, String country, String capital,
            Token entityToken);

    public GeoPoint addPoint(String longitude, String latitude, Double altitude,
            String continent, String country, String capital,
            List<Token> entityTokens);
}
