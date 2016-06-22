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
package eu.clarin.weblicht.wlfxb.tc.api;

/**
 * @author Yana Panchenko
 *
 */
public enum GeoLongLatFormat {

    /**
     * Coordinate containing only degrees (positive or negative real number).
     * Example: 40.446195, -79.948862
     */
    DegDec,
    /**
     * Coordinate containing degrees (integer) and minutes (real number).
     * Example: 40°26.7717, -79°56.93172
     */
    MinDec,
    /**
     * Coordinate containing degrees (integer), minutes (integer), and seconds
     * (integer, or real number). Example: Latitude 40:26:46N, Longitude
     * 79:56:55W
     */
    DMS;
}
