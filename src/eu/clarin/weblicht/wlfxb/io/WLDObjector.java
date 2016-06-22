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

import eu.clarin.weblicht.wlfxb.md.xb.MetaData;
import eu.clarin.weblicht.wlfxb.tc.api.TextCorpus;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusStored;
import eu.clarin.weblicht.wlfxb.utils.CommonConstants;
import eu.clarin.weblicht.wlfxb.xb.WLData;
import javanet.staxutils.IndentingXMLEventWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;

/**
 * Class <tt>WLDObjector</tt> helps to read/write
 * {@link eu.clarin.weblicht.wlfxb.xb.WLData} from/to TCF stream.
 *
 * @author Yana Panchenko
 */
public class WLDObjector {
    
    public static WLData read(InputStream inputStream) throws WLFormatException {
        WLData data = null;
        try {
            JAXBContext context = JAXBContext.newInstance(WLData.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            data = ((WLData) unmarshaller.unmarshal(inputStream));
        } catch (JAXBException e) {
            throw new WLFormatException(e.getMessage(), e);
        }
        return data;
    }

    public static WLData read(Reader reader) throws WLFormatException {
        WLData data = null;
        try {
            JAXBContext context = JAXBContext.newInstance(WLData.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            data = ((WLData) unmarshaller.unmarshal(reader));
        } catch (JAXBException e) {
            throw new WLFormatException(e.getMessage(), e);
        }
        return data;
    }

    public static WLData read(File file) throws WLFormatException {
        WLData data = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            data = read(fis);
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
        return data;
    }

    public static void write(WLData wlData, OutputStream outputStream) throws WLFormatException {
        write(wlData.getMetaData(), wlData.getTextCorpus(), outputStream, false);
    }

    public static void write(WLData wlData, File file) throws WLFormatException {
        write(wlData.getMetaData(), wlData.getTextCorpus(), file, false);
    }

    public static void write(WLData wlData, OutputStream outputStream, boolean outputAsXmlFragment) throws WLFormatException {
        write(wlData.getMetaData(), wlData.getTextCorpus(), outputStream, outputAsXmlFragment);
    }

    public static void write(WLData wlData, File file, boolean outputAsXmlFragment) throws WLFormatException {
        write(wlData.getMetaData(), wlData.getTextCorpus(), file, outputAsXmlFragment);
    }

    public static void write(MetaData md, TextCorpus tc, File file, boolean outputAsXmlFragment) throws WLFormatException {
        // IMPORTANT: using JAXB marshaller for marshalling directly into File or FileOutputStream
        // replaces quotes with &quot; entities which is not desirable for linguistic data, therefore
        // XMLEventWriter should be used...
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            write(md, tc, outputStream, outputAsXmlFragment);
        } catch (Exception e) {
            throw new WLFormatException(e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new WLFormatException(e);
                }
            }
        }
    }

    public static void write(MetaData md, TextCorpus tc,
            OutputStream outputStream, boolean outputAsXmlFragment)
            throws WLFormatException {

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLOutputFactory xmlOututFactory = XMLOutputFactory.newInstance();
        XMLEvent e;
        XMLEventWriter xmlEventWriter = null;
        
        try {
            xmlEventWriter = new IndentingXMLEventWriter(xmlOututFactory.createXMLEventWriter(outputStream, "UTF-8"));

            if (!outputAsXmlFragment) {
                e = eventFactory.createStartDocument("UTF-8");
                xmlEventWriter.add(e);
                e = eventFactory.createIgnorableSpace(XmlReaderWriter.NEW_LINE);
                xmlEventWriter.add(e);
                e = eventFactory.createProcessingInstruction(
                        XmlReaderWriter.XML_WL1_MODEL_PI_NAME,
                        CommonConstants.XML_WL1_MODEL_PI_CONTENT);
                xmlEventWriter.add(e);
                e = eventFactory.createIgnorableSpace(XmlReaderWriter.NEW_LINE);
                xmlEventWriter.add(e);
            }

            Attribute versionAttr = eventFactory.createAttribute("version", WLData.XML_VERSION);
            List<Attribute> attrs = new ArrayList<Attribute>();
            attrs.add(versionAttr);
            Namespace ns = eventFactory.createNamespace(WLData.XML_NAMESPACE);
            List<Namespace> nss = new ArrayList<Namespace>();
            nss.add(ns);
            e = eventFactory.createStartElement("", WLData.XML_NAMESPACE, WLData.XML_NAME, attrs.iterator(), nss.iterator());
            xmlEventWriter.add(e);
            e = eventFactory.createIgnorableSpace(XmlReaderWriter.NEW_LINE);
            xmlEventWriter.add(e);

            JAXBContext mdContext = JAXBContext.newInstance(MetaData.class);
            Marshaller mdMarshaller = mdContext.createMarshaller();
            //does not work with XMLEventWriter:
            //mdMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            mdMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            mdMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, CommonConstants.CMD_SCHEMA_LOCATION);
            mdMarshaller.marshal(md, xmlEventWriter);
            
            e = eventFactory.createIgnorableSpace(XmlReaderWriter.NEW_LINE);
            xmlEventWriter.add(e);

            JAXBContext tcContext = JAXBContext.newInstance(TextCorpusStored.class);
            Marshaller tcMarshaller = tcContext.createMarshaller();
            //does not work with XMLEventWriter:
            //tcMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            tcMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            tcMarshaller.marshal(tc, xmlEventWriter);
            
            e = eventFactory.createIgnorableSpace(XmlReaderWriter.NEW_LINE);
            xmlEventWriter.add(e);

            e = eventFactory.createEndElement("", WLData.XML_NAMESPACE, WLData.XML_NAME);
            xmlEventWriter.add(e);

            if (!outputAsXmlFragment) {
                e = eventFactory.createEndDocument();
                xmlEventWriter.add(e);
            }
        } catch (Exception ex) {
            throw new WLFormatException(ex.getMessage(), ex);
        } finally {
            if (xmlEventWriter != null) {
                try {
                    xmlEventWriter.flush();
                    xmlEventWriter.close();
                } catch (XMLStreamException ex2) {
                    throw new WLFormatException(ex2.getMessage(), ex2);
                }
            }
        }
    }
}
