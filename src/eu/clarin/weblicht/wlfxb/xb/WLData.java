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
package eu.clarin.weblicht.wlfxb.xb;

import eu.clarin.weblicht.wlfxb.ed.xb.ExternalDataStored;
import eu.clarin.weblicht.wlfxb.lx.xb.LexiconStored;
import eu.clarin.weblicht.wlfxb.md.xb.MetaData;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusStored;
import javax.xml.bind.annotation.*;

/**
 * Class <tt>WLData</tt> represents TCF annotations. 
 * Corresponds to <a href="http://clarin-d.de/images/weblicht-tutorials/resources/tcf-04/schemas/latest/d-spin_0_4.rnc">TCF specification</a>.
 * 
 * <tt>WLData</tt> annotations can be: {@link eu.clarin.weblicht.wlfxb.tc.api.TextCorpus} 
 * annotations represent annotations on written connected text, 
 * {@link eu.clarin.weblicht.wlfxb.lx.api.Lexicon} annotations represent 
 * linguistic information on a list of words, 
 * {@link eu.clarin.weblicht.wlfxb.ed.api.ExternalData} annotations represent 
 * annotations that are related to the annotations inside TCF but themselves 
 * are stored outside of TCF.
 *
 * Class <tt>WLData</tt> can be marshaled into XML representation of TCF. See
 * also {@link eu.clarin.weblicht.wlfxb.io.WLDObjector}.
 *
 * @author Yana Panchenko
 */
@XmlRootElement(name = WLData.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class WLData {

    public static final String XML_NAME = "D-Spin";
    public static final String XML_NAMESPACE = "http://www.dspin.de/data";
    public static final String XML_VERSION = "0.4";
    @XmlAttribute
    private String version;
    @XmlElement(name = "MetaData", namespace = "http://www.dspin.de/data/metadata")
    private MetaData metaData;
    @XmlElement(name = "ExternalData", namespace = "http://www.dspin.de/data/extdata")
    private ExternalDataStored extData;
    @XmlElement(name = "TextCorpus", namespace = "http://www.dspin.de/data/textcorpus")
    private TextCorpusStored textCorpus;
    @XmlElement(name = "Lexicon", namespace = "http://www.dspin.de/data/lexicon")
    private LexiconStored lexicon;

    WLData() {
    }

    /**
     * Gets version of the <tt>WLData</tt>.
     *
     * @return the version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets meta data of the <tt>WLData</tt>.
     *
     * @return the meta data.
     */
    public MetaData getMetaData() {
        return metaData;
    }

    /**
     * Gets external data annotations of the <tt>WLData</tt>.
     *
     * @return the external data annotations.
     */
    public ExternalDataStored getExternalData() {
        return extData;
    }

    /**
     * Gets text corpus annotations of the <tt>WLData</tt>.
     *
     * @return the text corpus annotations.
     */
    public TextCorpusStored getTextCorpus() {
        return textCorpus;
    }

    /**
     * Gets lexicon annotations of the <tt>WLData</tt>.
     *
     * @return the lexicon annotations.
     */
    public LexiconStored getLexicon() {
        return lexicon;
    }

    /**
     * Creates <tt>WLData</tt> with the given meta data and external data
     * annotations.
     *
     * @param metaData meta data
     * @param extData external data
     */
    public WLData(MetaData metaData, ExternalDataStored extData) {
        this.version = XML_VERSION;
        this.metaData = metaData;
        this.extData = extData;
    }

    /**
     * Creates <tt>WLData</tt> with the given meta data and text corpus
     * annotations.
     *
     * @param metaData meta data
     * @param textCorpus text corpus
     */
    public WLData(MetaData metaData, TextCorpusStored textCorpus) {
        this.version = XML_VERSION;
        this.metaData = metaData;
        this.textCorpus = textCorpus;
    }

    /**
     * Creates <tt>WLData</tt> with the given meta data, external data and text
     * corpus annotations.
     *
     * @param metaData meta data
     * @param extData external data
     * @param textCorpus text corpus
     */
    public WLData(MetaData metaData, ExternalDataStored extData, TextCorpusStored textCorpus) {
        this.version = XML_VERSION;
        this.metaData = metaData;
        this.extData = extData;
        this.textCorpus = textCorpus;
    }

    /**
     * Creates <tt>WLData</tt> with the given text corpus annotations.
     *
     * @param textCorpus text corpus
     */
    public WLData(TextCorpusStored textCorpus) {
        this.version = XML_VERSION;
        this.metaData = new MetaData();
        this.textCorpus = textCorpus;
    }

    /**
     * Creates <tt>WLData</tt> with the given external data annotations.
     *
     * @param extData external data
     */
    public WLData(ExternalDataStored extData) {
        this.version = XML_VERSION;
        this.metaData = new MetaData();
        this.extData = extData;
    }

    /**
     * Creates <tt>WLData</tt> with the given external data and text corpus
     * annotations.
     *
     * @param extData external data
     * @param textCorpus text corpus
     */
    public WLData(ExternalDataStored extData, TextCorpusStored textCorpus) {
        this.version = XML_VERSION;
        this.metaData = new MetaData();
        this.extData = extData;
        this.textCorpus = textCorpus;
    }

    /**
     * Creates <tt>WLData</tt> with the given meta data and lexicon annotations.
     *
     * @param metaData meta data
     * @param lexicon lexicon
     */
    public WLData(MetaData metaData, LexiconStored lexicon) {
        this.version = XML_VERSION;
        this.metaData = metaData;
        this.lexicon = lexicon;
    }

    /**
     * Creates <tt>WLData</tt> with the given lexicon annotations.
     *
     * @param lexicon lexicon
     */
    public WLData(LexiconStored lexicon) {
        this.version = XML_VERSION;
        this.metaData = new MetaData();
        this.lexicon = lexicon;
    }
}
