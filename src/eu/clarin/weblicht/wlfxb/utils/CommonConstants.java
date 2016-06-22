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

/**
 * @author Yana Panchenko
 *
 */
public class CommonConstants {

    public static final String XML_WL1_MODEL_PI_CONTENT =
            "href=\"http://de.clarin.eu/images/weblicht-tutorials/resources/tcf-04/schemas/"
            + "latest/d-spin_0_4.rnc\" type=\"application/relax-ng-compact-syntax\"";
    public static final String XML_MODEL_DECLARATION_WITH_WL1_PI_CONTENT = "\n<?xml-model "
            + "href=\"http://de.clarin.eu/images/weblicht-tutorials/resources/tcf-04/schemas/"
            + "latest/d-spin_0_4.rnc\" "
            + "type=\"application/relax-ng-compact-syntax\"?>\n";
    
    public static final String CMD_SCHEMA_LOCATION = "http://www.clarin.eu/cmd/ http://catalog.clarin.eu/ds/ComponentRegistry/rest/registry/profiles/clarin.eu:cr1:p_1320657629623/xsd";
    
}
