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
package eu.clarin.weblicht.wlfxb.lx.xb;

import eu.clarin.weblicht.wlfxb.lx.api.*;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = LexiconStored.XML_NAME, namespace = LexiconStored.XML_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {
    "entriesLayer",
    "posTagsLayer",
    "frequenciesLayer",
    "cooccurrencesLayer",
    "synonymsLayer",
    "syllabificationsLayer"})
public class LexiconStored implements Lexicon {

    public static final String XML_NAME = "Lexicon";
    public static final String XML_NAMESPACE = "http://www.dspin.de/data/lexicon";
    @XmlAttribute
    protected String lang;
    protected LexiconLayerStoredAbstract[] layersInOrder;
    private LexiconLayersConnector connector;

    LexiconStored() {
        connector = new LexiconLayersConnector();
        layersInOrder = new LexiconLayerStoredAbstract[LexiconLayerTag.orderedLayerTags().size()];
    }

    public LexiconStored(String language) {
        this();
        this.lang = language;
    }

    public String getLanguage() {
        return lang;
    }

    @Override
    public EntriesLayer createEntriesLayer(EntryType entryType) {
        return initializeLayer(EntriesLayerStored.class, entryType);
    }

    @Override
    public PosTagsLayer createPosTagsLayer(String tagset) {
        return initializeLayer(PosTagsLayerStored.class, tagset);
    }

    @Override
    public FrequenciesLayer createFrequenciesLayer(FrequencyType freqType) {
        return initializeLayer(FrequenciesLayerStored.class, freqType);
    }
    
    @Override
    public CooccurrencesLayer createCooccurrencesLayer() {
        return initializeLayer(CooccurrencesLayerStored.class);
    }

    @Override
    public SynonymsLayer createSynonymsLayer() {
        return initializeLayer(SynonymsLayerStored.class);
    }
    
    @Override
    public SyllabificationsLayer createSyllabificationsLayer() {
        return initializeLayer(SyllabificationsLayerStored.class);
    }

    @SuppressWarnings("unchecked")
    private <T extends LexiconLayerStoredAbstract> T initializeLayer(Class<T> layerClass, Object... params) {

        Class<?>[] paramsClass = null;
        if (params != null) {
            paramsClass = new Class<?>[params.length];
            for (int i = 0; i < params.length; i++) {
                paramsClass[i] = params[i].getClass();
            }
        }

        LexiconLayerTag layerTag = LexiconLayerTag.getFromClass(layerClass);
        try {
            Constructor<?> ct;
            T instance;
            if (params == null) {
                ct = layerClass.getDeclaredConstructor();
                instance = (T) ct.newInstance();
            } else {
                ct = layerClass.getDeclaredConstructor(paramsClass);
                instance = (T) ct.newInstance(params);
            }
            layersInOrder[layerTag.ordinal()] = instance;
            instance.setLayersConnector(connector);
        } catch (Exception e) {
            //e.printStackTrace();
            Logger.getLogger(LexiconStored.class.getName()).log(Level.SEVERE, null, e);
        }
        return (T) layersInOrder[layerTag.ordinal()];
    }

    @XmlElement(name = EntriesLayerStored.XML_NAME)
    protected void setEntriesLayer(EntriesLayerStored layer) {
        layersInOrder[LexiconLayerTag.ENTRIES.ordinal()] = layer;
    }

    @Override
    public EntriesLayerStored getEntriesLayer() {
        return ((EntriesLayerStored) layersInOrder[LexiconLayerTag.ENTRIES.ordinal()]);
    }

    @XmlElement(name = PosTagsLayerStored.XML_NAME)
    protected void setPosTagsLayer(PosTagsLayerStored layer) {
        layersInOrder[LexiconLayerTag.POSTAGS.ordinal()] = layer;
    }

    @Override
    public PosTagsLayerStored getPosTagsLayer() {
        return ((PosTagsLayerStored) layersInOrder[LexiconLayerTag.POSTAGS.ordinal()]);
    }

    @XmlElement(name = FrequenciesLayerStored.XML_NAME)
    protected void setFrequenciesLayer(FrequenciesLayerStored layer) {
        layersInOrder[LexiconLayerTag.FREQUENCIES.ordinal()] = layer;
    }

    @Override
    public FrequenciesLayerStored getFrequenciesLayer() {
        return ((FrequenciesLayerStored) layersInOrder[LexiconLayerTag.FREQUENCIES.ordinal()]);
    }
    
    @XmlElement(name = CooccurrencesLayerStored.XML_NAME)
    protected void setCooccurrencesLayer(CooccurrencesLayerStored layer) {
        layersInOrder[LexiconLayerTag.COOCCURRENCES.ordinal()] = layer;
    }

    @Override
    public CooccurrencesLayerStored getCooccurrencesLayer() {
        return ((CooccurrencesLayerStored) layersInOrder[LexiconLayerTag.COOCCURRENCES.ordinal()]);
    }

    @XmlElement(name = SynonymsLayerStored.XML_NAME)
    protected void setSynonymsLayer(SynonymsLayerStored layer) {
        layersInOrder[LexiconLayerTag.SYNONYMS.ordinal()] = layer;
    }

    @Override
    public SynonymsLayerStored getSynonymsLayer() {
        return ((SynonymsLayerStored) layersInOrder[LexiconLayerTag.SYNONYMS.ordinal()]);
    }
    
    @XmlElement(name = SyllabificationsLayerStored.XML_NAME)
    protected void setSyllabificationsLayer(SyllabificationsLayerStored layer) {
        layersInOrder[LexiconLayerTag.SYLLABIFICATIONS.ordinal()] = layer;
    }

    @Override
    public SyllabificationsLayerStored getSyllabificationsLayer() {
        return ((SyllabificationsLayerStored) layersInOrder[LexiconLayerTag.SYLLABIFICATIONS.ordinal()]);
    }

    protected void afterUnmarshal(Unmarshaller u, Object parent) {
        connectLayers();
    }

//		protected void beforeMarshal(Marshaller m) {
//			setEmptyLayersToNull();
//		}
//		
//
//		/**
//		 * 
//		 */
//		private void setEmptyLayersToNull() {
//			
//			for (int i = 0; i < this.layersInOrder.length; i++) {
//				if (layersInOrder[i] != null && layersInOrder[i].isEmpty()) {
//					layersInOrder[i] = null;
//				}
//			}
//		}
    protected void connectLayers() {
        for (int i = 0; i < this.layersInOrder.length; i++) {
            if (layersInOrder[i] != null) {
                layersInOrder[i].setLayersConnector(connector);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(XML_NAME);
        sb.append(":\n");

        for (LexiconLayer layer : this.layersInOrder) {
            if (layer != null) {
                sb.append(layer);
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
