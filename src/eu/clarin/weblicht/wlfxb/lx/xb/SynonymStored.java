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

import eu.clarin.weblicht.wlfxb.lx.api.Sig;
import eu.clarin.weblicht.wlfxb.lx.api.Synonym;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 * @author Yana Panchenko
 *
 */
@XmlRootElement(name = SynonymStored.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class SynonymStored implements Synonym {

    public static final String XML_NAME = "synonym";
    @XmlElement
    protected SigStored sig;
    @XmlElement(name = TermStored.XML_NAME)
    protected List<TermStored> terms = new ArrayList<TermStored>();


    @Override
    public Sig getSig() {
        return sig;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (sig != null) {
            sb.append(sig);
            sb.append(" ");
        }
        sb.append(terms.toString());
        return sb.toString();
    }
}
