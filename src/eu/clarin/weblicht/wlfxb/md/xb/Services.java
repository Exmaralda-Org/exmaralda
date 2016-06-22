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
package eu.clarin.weblicht.wlfxb.md.xb;

import eu.clarin.weblicht.bindings.cmd.chains.ToolchainCMD;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement; 


@XmlRootElement(name = Services.XML_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class Services {
    
    public static final String XML_NAME = "Services";
    
    @XmlElement(name = "CMD", namespace = "http://www.clarin.eu/cmd/")
    private ToolchainCMD toolchainCmd;

    public ToolchainCMD getToolchainCmd() {
        return toolchainCmd;
    }

    @Override
    public String toString() {
        return "Services{" + "toolchainCmd=" + toolchainCmd + '}';
    }
    
}
