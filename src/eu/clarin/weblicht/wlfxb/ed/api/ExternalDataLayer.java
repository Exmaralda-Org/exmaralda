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
package eu.clarin.weblicht.wlfxb.ed.api;

/**
 * The <tt>ExternalDataLayer</tt> interface represents linguistic annotations 
 * layer to be used with TCF annotations but to be stored outside of TCF. 
 * 
 * The <tt>ExternalDataLayer</tt> has a mime type of its data file and a URL 
 * link to its data file.
 * 
 * @author Yana Panchenko
 */
public interface ExternalDataLayer {

    /**
     * Gets the MIME type of the data file of this layer.
     * 
     * @return the MIME type
     */
    public String getDataMimeType();

    /**
     * Gets link (URL) to the data file of this layer.
     * 
     * @return link to the data file.
     */
    public String getLink();

    /**
     * Adds link (URL) to the location of the data file of this layer.
     * 
     * @param dataURL the URL of the data file.
     */
    public void addLink(String dataURL);
}
