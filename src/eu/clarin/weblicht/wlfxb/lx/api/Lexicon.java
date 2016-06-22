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
package eu.clarin.weblicht.wlfxb.lx.api;


/**
 * @author Yana Panchenko
 *
 */
public interface Lexicon {

    public EntriesLayer getEntriesLayer();

    public EntriesLayer createEntriesLayer(EntryType entryType);

    public PosTagsLayer getPosTagsLayer();

    public PosTagsLayer createPosTagsLayer(String tagset);

    public FrequenciesLayer getFrequenciesLayer();

    public FrequenciesLayer createFrequenciesLayer(FrequencyType type);
    
    public CooccurrencesLayer getCooccurrencesLayer();
    
    public CooccurrencesLayer createCooccurrencesLayer();
    
    public SynonymsLayer getSynonymsLayer();
    
    public SynonymsLayer createSynonymsLayer();
    
    public SyllabificationsLayer getSyllabificationsLayer();
    
    public SyllabificationsLayer createSyllabificationsLayer();
}
