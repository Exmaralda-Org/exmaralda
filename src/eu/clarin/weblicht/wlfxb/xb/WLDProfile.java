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
package eu.clarin.weblicht.wlfxb.xb;

import eu.clarin.weblicht.wlfxb.lx.xb.LexiconProfile;
import eu.clarin.weblicht.wlfxb.md.xb.MetaData;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusProfile;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = "D-Spin")
@XmlAccessorType(XmlAccessType.NONE)
public class WLDProfile {

    @XmlAttribute
    private String version;
    @XmlElement(name = "MetaData", namespace = "http://www.dspin.de/data/metadata")
    private MetaData metadata;
    @XmlElement(name = "TextCorpus", namespace = "http://www.dspin.de/data/textcorpus")
    private TextCorpusProfile tcProfile;
    @XmlElement(name = "Lexicon", namespace = "http://www.dspin.de/data/lexicon")
    private LexiconProfile lexProfile;

    public String getVersion() {
        return version;
    }

    public MetaData getMetadata() {
        return metadata;
    }

    public TextCorpusProfile getTcProfile() {
        return tcProfile;
    }

    public LexiconProfile getLexProfile() {
        return lexProfile;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(version);
        sb.append("\n");
        sb.append(metadata);
        sb.append("\n");
        if (tcProfile != null) {
            sb.append(tcProfile);
        } else if (lexProfile != null) {
            sb.append(lexProfile);
        }
        return sb.toString();
    }
}
