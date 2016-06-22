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
package eu.clarin.weblicht.wlfxb.io;

import eu.clarin.weblicht.wlfxb.xb.WLDProfile;
import java.io.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class WLDProfiler {

    public static WLDProfile read(InputStream inputStream) throws WLFormatException {
        WLDProfile profile = null;
        try {
            JAXBContext context = JAXBContext.newInstance(WLDProfile.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            profile = (WLDProfile) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new WLFormatException(e.getMessage(), e);
        }
        return profile;
    }

    public static WLDProfile read(Reader reader) throws WLFormatException {
        WLDProfile profile = null;
        try {
            JAXBContext context = JAXBContext.newInstance(WLDProfile.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            profile = (WLDProfile) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new WLFormatException(e.getMessage(), e);
        }
        return profile;
    }

    public static WLDProfile read(File file) throws WLFormatException {
        WLDProfile profile = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            profile = read(fis);

        } catch (IOException e) {
            throw new WLFormatException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new WLFormatException(e);
                }
            }
        }
        return profile;
    }
}
