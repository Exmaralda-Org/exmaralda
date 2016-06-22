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

import java.util.List;

/**
 * The <tt>ExternalData</tt> interface represents layers of linguistic 
 * annotations to be used with TCF annotations but to be stored outside of TCF.
 * Corresponds to TCF <a href="http://clarin-d.de/images/weblicht-tutorials/resources/tcf-04/schemas/latest/extdata_0_4.rnc">ExternalData specification</a>.
 * 
 * Each layer of annotations in <tt>ExternalData</tt> has a URL link to its 
 * annotations data file. 
 * 
 * @author Yana Panchenko
 */
public interface ExternalData {

    public List<ExternalDataLayer> getLayers();

    public SpeechSignalLayer getSpeechSignalLayer();

    public SpeechSignalLayer createSpeechSignalLayer(String mimeType);

    public SpeechSignalLayer createSpeechSignalLayer(String mimeType, int numberOfChannels);

    public TokenSegmentationLayer getTokenSegmentationLayer();

    public TokenSegmentationLayer createTokenSegmentationLayer(String mimeType);

    public PhoneticSegmentationLayer getPhoneticSegmentationLayer();

    public PhoneticSegmentationLayer createPhoneticSegmentationLayer(String mimeType);

    public CanonicalSegmentationLayer getCanonicalSegmentationLayer();

    public CanonicalSegmentationLayer createCanonicalSegmentationLayer(String mimeType);
    
    public NamedEntityModelLayer createNamedEntityModelLayer(String mimeType, String neType, String modelType);
    
    public NamedEntityModelLayer getNamedEntityModelLayer();
}
