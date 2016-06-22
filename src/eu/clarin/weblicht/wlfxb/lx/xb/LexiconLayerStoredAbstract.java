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
package eu.clarin.weblicht.wlfxb.lx.xb;

import eu.clarin.weblicht.wlfxb.lx.api.LexiconLayer;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Yana Panchenko
 *
 */
@XmlTransient
public abstract class LexiconLayerStoredAbstract implements LexiconLayer {

    /**
     * Should not be used directly by users. Should be extended by all
     * implementations of TextCorpusLayers, as they might depend on other layers
     * and therefore be able to get referenced/referencing information.
     *
     * @param connector
     */
    protected abstract void setLayersConnector(LexiconLayersConnector connector);
}
