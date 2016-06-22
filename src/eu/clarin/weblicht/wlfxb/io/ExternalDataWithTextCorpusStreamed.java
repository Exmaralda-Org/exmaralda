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

import eu.clarin.weblicht.wlfxb.ed.api.ExternalData;
import eu.clarin.weblicht.wlfxb.ed.api.ExternalDataLayer;
import eu.clarin.weblicht.wlfxb.ed.xb.ExternalDataLayerStored;
import eu.clarin.weblicht.wlfxb.ed.xb.ExternalDataLayerTag;
import eu.clarin.weblicht.wlfxb.ed.xb.ExternalDataStored;
import eu.clarin.weblicht.wlfxb.tc.api.TextCorpus;
import eu.clarin.weblicht.wlfxb.tc.api.TextCorpusLayer;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusLayerStoredAbstract;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusLayerTag;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusStored;
import eu.clarin.weblicht.wlfxb.xb.WLData;
import javanet.staxutils.IndentingXMLEventWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;

/**
 * Class <tt>ExternalDataWithTextCorpusStreamed</tt> represents TCF TextCorpus 
 * and ExternalData annotations. The class is used for accessing specified 
 * annotation layers and (optionally) adding any new annotation layers from/to 
 * TextCorpus and/or ExternalData. Only specified in the constructor
 * annotation layers are loaded into the memory. In case all the annotation
 * layers should be loaded into the memory, use {@link eu.clarin.weblicht.wlfxb.xb.WLData} class.
 * 
 * @author Yana Panchenko
 */
public class ExternalDataWithTextCorpusStreamed {

    private EnumSet<ExternalDataLayerTag> edLayersToRead = EnumSet.noneOf(ExternalDataLayerTag.class);
    private EnumSet<TextCorpusLayerTag> tcLayersToRead = EnumSet.noneOf(TextCorpusLayerTag.class);
    private TextCorpusStored textCorpus;
    private boolean hasTextCorpus = false;
    private ExternalDataStored extData;
    private boolean hasExtData = false;
    private XMLEventReader xmlEventReader;
    private File tempFile;
    private XMLEventWriter xmlEventWriter;
    private XmlReaderWriter xmlReaderWriter;
    private static final int LAYER_INDENT_RELATIVE = 1;
    private boolean closed = false;

    /**
     * Creates a <tt>ExternalDataWithTextCorpusStreamed</tt> from the given TCF
     * input stream and specified annotation layers.
     * 
     * @param inputStream the underlying input stream with linguistic annotations 
     * in TCF format.
     * @param edLayersToRead the annotation layers of <tt>ExternalData</tt> that
     * should be read into this <tt>ExternalDataWithTextCorpusStreamed</tt>.
     * @param tcLayersToRead the annotation layers of <tt>TextCorpus</tt> that
     * should be read into this <tt>ExternalDataWithTextCorpusStreamed</tt>.
     * 
     * @throws WLFormatException if an error in input format or an I/O error occurs.
     */
    public ExternalDataWithTextCorpusStreamed(InputStream inputStream,
            EnumSet<ExternalDataLayerTag> edLayersToRead,
            EnumSet<TextCorpusLayerTag> tcLayersToRead)
            throws WLFormatException {
        getLayersToReadWithDependencies(edLayersToRead, tcLayersToRead);
        extData = new ExternalDataStored();
        textCorpus = new TextCorpusStored("unknown");
        try {
            initializeReaderAndWriter(inputStream, null, false);
            readLayers();
        } catch (WLFormatException e) {
            Logger.getLogger(ExternalDataWithTextCorpusStreamed.class.getName()).log(Level.SEVERE, null, e);
            cleanup();
            throw e;
        }
    }

    /**
     * Creates a <tt>ExternalDataWithTextCorpusStreamed</tt> from the given TCF
     * input stream, specified annotation layers and the output stream.
     * 
     * @param inputStream the underlying input stream with linguistic annotations 
     * in TCF format.
     * @param edLayersToRead the annotation layers of <tt>ExternalData</tt> that
     * should be read into this <tt>ExternalDataWithTextCorpusStreamed</tt>.
     * @param tcLayersToRead the annotation layers of <tt>TextCorpus</tt> that
     * should be read into this <tt>ExternalDataWithTextCorpusStreamed</tt>.
     * @param outputStream the underlying output stream into which the annotations 
     * from the input stream and any new created annotations will be written (in 
     * TCF format).
     * 
     * @throws WLFormatException if an error in input format or an I/O error occurs.
     */
    public ExternalDataWithTextCorpusStreamed(InputStream inputStream,
            EnumSet<ExternalDataLayerTag> edLayersToRead,
            EnumSet<TextCorpusLayerTag> tcLayersToRead,
            OutputStream outputStream)
            throws WLFormatException {
        extData = new ExternalDataStored();
        textCorpus = new TextCorpusStored("unknown");
        getLayersToReadWithDependencies(edLayersToRead, tcLayersToRead);

        OutputStream osTemp = null;
        InputStream isTemp = null;
        try {
            osTemp = getTempOutputStream();
            initializeReaderAndWriter(inputStream, osTemp, false);
            readLayers();
            isTemp = getTempInputStream();
            initializeReaderAndWriter(isTemp, outputStream, false);
        } catch (WLFormatException e) {
            Logger.getLogger(ExternalDataWithTextCorpusStreamed.class.getName()).log(Level.SEVERE, null, e);
            cleanup();
            throw e;
        }
    }

    /**
     * Gets <tt>TextCorpus</tt>.
     * 
     * @return TextCorpus
     */
    public TextCorpus getTextCorpus() {
        return textCorpus;
    }

    /**
     * Gets <tt>ExternalData</tt>.
     * 
     * @return ExternalData
     */
    public ExternalData getExternalData() {
        return extData;
    }

    private OutputStream getTempOutputStream() throws WLFormatException {
        OutputStream os = null;
        try {
            tempFile = File.createTempFile("wlftemp", null);
            os = new FileOutputStream(tempFile);
        } catch (IOException e) {
            Logger.getLogger(ExternalDataWithTextCorpusStreamed.class.getName()).log(Level.SEVERE, null, e);
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ex) {
                    Logger.getLogger(ExternalDataWithTextCorpusStreamed.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            cleanup();
            throw new WLFormatException(e);
        }
        return os;
    }

    private InputStream getTempInputStream() throws WLFormatException {
        InputStream is = null;
        try {
            is = new FileInputStream(tempFile);
        } catch (IOException e) {
            Logger.getLogger(ExternalDataWithTextCorpusStreamed.class.getName()).log(Level.SEVERE, null, e);
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(ExternalDataWithTextCorpusStreamed.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            cleanup();
            throw new WLFormatException(e);
        }
        return is;
    }

    private void initializeReaderAndWriter(
            InputStream inputStream,
            OutputStream outputStream, boolean outputAsXmlFragment) throws WLFormatException {
        if (inputStream != null) {
            try {
                XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
                xmlEventReader = xmlInputFactory.createXMLEventReader(inputStream, "UTF-8");
            } catch (XMLStreamException e) {
                throw new WLFormatException(e.getMessage(), e);
            }
        }
        if (outputStream != null) {
            try {
                XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
                xmlEventWriter = new IndentingXMLEventWriter(xmlOutputFactory.createXMLEventWriter(outputStream, "UTF-8"));
            } catch (XMLStreamException e) {
                throw new WLFormatException(e.getMessage(), e);
            }
        }
        xmlReaderWriter = new XmlReaderWriter(xmlEventReader, xmlEventWriter);
        xmlReaderWriter.setOutputAsXmlFragment(outputAsXmlFragment);
    }

    private void readLayers() throws WLFormatException {

        readUpToExtDataOrTextCorpus();
        if (hasExtData) {
            readExternalDataLayers();
        }
        readUpToExtDataOrTextCorpus();
        if (hasTextCorpus) {
            readTextCorpusLayers();
        }
        xmlReaderWriter.readWriteToTheEnd();
    }

    private void readUpToExtDataOrTextCorpus() throws WLFormatException {
        XMLEvent peekedEvent;
        try {
            peekedEvent = xmlEventReader.peek();
            while (!hasExtData || !hasTextCorpus) {
                if (peekedEvent.getEventType() == XMLStreamConstants.START_ELEMENT
                        && ((peekedEvent.asStartElement().getName().getLocalPart() == null ? ExternalDataStored.XML_NAME == null : peekedEvent.asStartElement().getName().getLocalPart().equals(ExternalDataStored.XML_NAME))
                        || (peekedEvent.asStartElement().getName().getLocalPart() == null ? TextCorpusStored.XML_NAME == null : peekedEvent.asStartElement().getName().getLocalPart().equals(TextCorpusStored.XML_NAME)))) {
                    if (peekedEvent.asStartElement().getName().getLocalPart() == null ? ExternalDataStored.XML_NAME == null : peekedEvent.asStartElement().getName().getLocalPart().equals(ExternalDataStored.XML_NAME)) {
                        this.hasExtData = true;
                    } else if (peekedEvent.asStartElement().getName().getLocalPart() == null ? TextCorpusStored.XML_NAME == null : peekedEvent.asStartElement().getName().getLocalPart().equals(TextCorpusStored.XML_NAME)) {
                        this.hasTextCorpus = true;
                    }
                    return;
                } else {
                    XMLEvent readEvent = xmlReaderWriter.readEvent();
                    xmlReaderWriter.add(readEvent);
                    peekedEvent = xmlEventReader.peek();
                }
            }

        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        }
    }

    private void readExternalDataLayers() throws WLFormatException {
        try {
            xmlReaderWriter.readWriteUpToStartElement(ExternalDataStored.XML_NAME);
            XMLEvent event = xmlEventReader.nextEvent();
            xmlReaderWriter.add(event);
            // read layers requested stopping before ExternalData end element
            ExternalDataLayerStored[] layers = new ExternalDataLayerStored[this.edLayersToRead.size()];
            int counter = 0;

            boolean extDataEnd = false;
            XMLEvent peekedEvent;
            peekedEvent = xmlEventReader.peek();
            while (!extDataEnd && peekedEvent != null) {
                if (peekedEvent.getEventType() == XMLStreamConstants.END_ELEMENT
                        && peekedEvent.asEndElement().getName().getLocalPart().equals(ExternalDataStored.XML_NAME)) {
                    extDataEnd = true;
                } else if (peekedEvent.getEventType() == XMLStreamConstants.START_ELEMENT
                        && this.edLayersToRead.contains(ExternalDataLayerTag.getFromXmlName(peekedEvent.asStartElement().getName().getLocalPart()))) {
                    ExternalDataLayerStored layer = readExternalDataLayer();
                    layers[counter++] = layer;
                    peekedEvent = xmlEventReader.peek();
                } else {
                    XMLEvent readEvent = xmlReaderWriter.readEvent();
                    xmlReaderWriter.add(readEvent);
                    peekedEvent = xmlEventReader.peek();
                }
            }
            if (!extDataEnd) {
                throw new WLFormatException(ExternalDataStored.XML_NAME + " end tag not found");
            }
            extData = ExternalDataStored.compose(layers);

        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        } catch (NoSuchElementException e) {
            throw new WLFormatException(e.getMessage(), e);
        }
    }

    private ExternalDataLayerStored readExternalDataLayer() throws WLFormatException {

        XMLEvent peekedEvent;
        ExternalDataLayerStored layer = null;
        try {
            peekedEvent = xmlEventReader.peek();
            // now we assume that this event is start of a TextCorpus layer
            String tagName = peekedEvent.asStartElement().getName().getLocalPart();
            ExternalDataLayerTag layerTag = ExternalDataLayerTag.getFromXmlName(tagName);

            if (layerTag == null) { // unknown layer, just add it to output
                xmlReaderWriter.readWriteElement(tagName);
            } else if (this.edLayersToRead.contains(layerTag)) { // known layer, and is requested for reading
                // add it to the output, but store its data
                layer = readExternalLayerData(layerTag);
            } else { // known layer, and is not requested for reading
                // just add it to the output
                xmlReaderWriter.readWriteElement(tagName);
            }
        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        }

        return layer;
    }

    private ExternalDataLayerStored readExternalLayerData(ExternalDataLayerTag layerTag) throws WLFormatException {
        JAXBContext context;
        Unmarshaller unmarshaller;
        ExternalDataLayerStored layer;
        try {
            context = JAXBContext.newInstance(layerTag.getLayerClass());
            unmarshaller = context.createUnmarshaller();
            layer = (ExternalDataLayerStored) unmarshaller.unmarshal(xmlEventReader);
            // marshall it back to xml
            marshall(layer);
        } catch (JAXBException e) {
            throw new WLFormatException(e.getMessage(), e);
        }
        return layer;
    }

    private void readTextCorpusLayers() throws WLFormatException {
        try {
            xmlReaderWriter.readWriteUpToStartElement(TextCorpusStored.XML_NAME);
            // process TextCorpus start element
            XMLEvent event = xmlEventReader.nextEvent();
            String lang = event.asStartElement().getAttributeByName(new QName("lang")).getValue();
            // add processed TextCorpus start back
            xmlReaderWriter.add(event);
            // read layers requested stopping before ExternalData end element
            TextCorpusLayerStoredAbstract[] layers = new TextCorpusLayerStoredAbstract[this.tcLayersToRead.size()];
            int counter = 0;

            boolean tcEnd = false;
            XMLEvent peekedEvent;
            peekedEvent = xmlEventReader.peek();
            while (!tcEnd && peekedEvent != null) {
                if (peekedEvent.getEventType() == XMLStreamConstants.END_ELEMENT
                        && peekedEvent.asEndElement().getName().getLocalPart().equals(TextCorpusStored.XML_NAME)) {
                    tcEnd = true;
                } else if (peekedEvent.getEventType() == XMLStreamConstants.START_ELEMENT
                        && this.tcLayersToRead.contains(TextCorpusLayerTag.getFromXmlName(peekedEvent.asStartElement().getName().getLocalPart()))) {
                    TextCorpusLayerStoredAbstract layer = readTextCorpusLayer();
                    layers[counter++] = layer;
                    peekedEvent = xmlEventReader.peek();
                } else {
                    XMLEvent readEvent = xmlReaderWriter.readEvent();
                    xmlReaderWriter.add(readEvent);
                    peekedEvent = xmlEventReader.peek();
                }
            }
            if (!tcEnd) {
                throw new WLFormatException(TextCorpusStored.XML_NAME + " end tag not found");
            }
            if (!filled(layers)) {
                throw new WLFormatException(TextCorpusStored.XML_NAME + " not all requested layers are found");
            }

            textCorpus = TextCorpusStored.compose(lang, layers);

        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        } catch (NoSuchElementException e) {
            throw new WLFormatException(e.getMessage(), e);
        }
    }

    private boolean filled(Object[] layers) {
        for (Object o : layers) {
            if (o == null) {
                return false;
            }
        }
        return true;
    }

    private TextCorpusLayerStoredAbstract readTextCorpusLayer() throws WLFormatException {

        XMLEvent peekedEvent;
        TextCorpusLayerStoredAbstract layer = null;
        try {
            peekedEvent = xmlEventReader.peek();
            // now we assume that this event is start of a TextCorpus layer
            String tagName = peekedEvent.asStartElement().getName().getLocalPart();
            TextCorpusLayerTag layerTag = TextCorpusLayerTag.getFromXmlName(tagName);

            if (layerTag == null) { // unknown layer, just add it to output
                xmlReaderWriter.readWriteElement(tagName);
            } else if (this.tcLayersToRead.contains(layerTag)) { // known layer, and is requested for reading
                // add it to the output, but store its data
                layer = readTextCorpusLayerData(layerTag);
            } else { // known layer, and is not requested for reading
                // just add it to the output
                xmlReaderWriter.readWriteElement(tagName);
            }
        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        }

        return layer;
    }

    private TextCorpusLayerStoredAbstract readTextCorpusLayerData(TextCorpusLayerTag layerTag) throws WLFormatException {
        JAXBContext context;
        Unmarshaller unmarshaller;
        TextCorpusLayerStoredAbstract layer = null;
        try {
            context = JAXBContext.newInstance(layerTag.getLayerClass());
            unmarshaller = context.createUnmarshaller();
            layer = (TextCorpusLayerStoredAbstract) unmarshaller.unmarshal(xmlEventReader);
            // marshall it back to xml
            marshall(layer);
        } catch (JAXBException e) {
            throw new WLFormatException(e.getMessage(), e);
        }
        return layer;
    }

    private void marshall(Object layer) throws WLFormatException {
        if (xmlEventWriter == null) {
            return;
        }
        JAXBContext context;
        try {
            xmlReaderWriter.startExternalFragment(LAYER_INDENT_RELATIVE);
            context = JAXBContext.newInstance(layer.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(layer, xmlEventWriter);
            xmlReaderWriter.endExternalFragment(LAYER_INDENT_RELATIVE);
        } catch (Exception e) {
            Logger.getLogger(ExternalDataWithTextCorpusStreamed.class.getName()).log(Level.SEVERE, null, e);
            cleanup();
            throw new WLFormatException(e.getMessage(), e);
        }
    }

    /**
     * Closes the input and output streams associated with this object and 
     * releases any associated system resources. Before the streams are closed, 
     * all in-memory annotations of the <tt>ExternalDataWithTextCorpusStreamed</tt>
     * and not-processed part of the input stream are written to the output stream. 
     * Therefore, it's important to call <tt>close()<tt> method, so that all the 
     * in-memory annotations are saved to the output stream. Once the 
     * <tt>ExternalDataWithTextCorpusStreamed</tt> has been closed, adding 
     * further annotations will have no effect on the output stream.
     * 
     * @throws WLFormatException if an error in input format or an I/O error occurs.
     */
    public void close() throws WLFormatException {
        
        if (closed) {
            return;
        }
        closed = true;

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent e;
        
        try {
            if (this.xmlEventWriter != null) { 

                // rewrite all before end of ExternalData or, if ExternalData is not present, before start of TextCorpus
                if (!this.hasExtData) {
                    // up to TextCorpus start
                    xmlReaderWriter.readWriteUpToStartElement(TextCorpusStored.XML_NAME);
                    // create the ExternalData start element
                    List<Attribute> attrs = new ArrayList<Attribute>(0);
                    Namespace ns = eventFactory.createNamespace(ExternalDataStored.XML_NAMESPACE);
                    List<Namespace> nss = new ArrayList<Namespace>();
                    nss.add(ns);
                    e = eventFactory.createStartElement("",
                            ExternalDataStored.XML_NAMESPACE, ExternalDataStored.XML_NAME,
                            attrs.iterator(), nss.iterator());
                    xmlEventWriter.add(e);
                    e = eventFactory.createIgnorableSpace(XmlReaderWriter.NEW_LINE);
                    xmlEventWriter.add(e);
                } else {
                    xmlReaderWriter.readWriteUpToEndElement(ExternalDataStored.XML_NAME);
                }

                // write new layers of ExternalData
                List<ExternalDataLayer> edLayers = extData.getLayers();
                for (ExternalDataLayer layer : edLayers) {
                    if (!edLayersToRead.contains(ExternalDataLayerTag.getFromClass(layer.getClass()))) {
                        marshall(layer);
                    }
                }

                if (!this.hasExtData) {
                    // create the ExternalData end element
                    e = eventFactory.createEndElement("",
                            ExternalDataStored.XML_NAMESPACE, ExternalDataStored.XML_NAME);
                    xmlEventWriter.add(e);
                    e = eventFactory.createIgnorableSpace(XmlReaderWriter.NEW_LINE);
                    xmlEventWriter.add(e);
                }



                if (!this.hasTextCorpus) {
                    // up to end D-Spin
                    xmlReaderWriter.readWriteUpToStartElement(WLData.XML_NAME);
                    // create the TextCorpus start element
                    List<Attribute> attrs = new ArrayList<Attribute>(0);
                    Namespace ns = eventFactory.createNamespace(TextCorpusStored.XML_NAMESPACE);
                    List<Namespace> nss = new ArrayList<Namespace>();
                    nss.add(ns);
                    e = eventFactory.createStartElement("",
                            TextCorpusStored.XML_NAMESPACE, TextCorpusStored.XML_NAME,
                            attrs.iterator(), nss.iterator());
                    xmlEventWriter.add(e);
                    e = eventFactory.createIgnorableSpace(XmlReaderWriter.NEW_LINE);
                    xmlEventWriter.add(e);
                } else {
                    // rewrite all before end of TextCorpus
                    xmlReaderWriter.readWriteUpToEndElement(TextCorpusStored.XML_NAME);
                }

                //  write new layers of TextCorpus:
                List<TextCorpusLayer> tcLayers = textCorpus.getLayers();
                for (TextCorpusLayer layer : tcLayers) {
                    if (!tcLayersToRead.contains(TextCorpusLayerTag.getFromClass(layer.getClass()))) {
                        marshall(layer);
                    }
                }

                if (!this.hasTextCorpus) {
                    // create the TextCorpus end element
                    e = eventFactory.createEndElement("",
                            TextCorpusStored.XML_NAMESPACE, TextCorpusStored.XML_NAME);
                    xmlEventWriter.add(e);
                    e = eventFactory.createIgnorableSpace(XmlReaderWriter.NEW_LINE);
                    xmlEventWriter.add(e);
                }

                // write to the end
                xmlReaderWriter.readWriteToTheEnd();
            }
        } catch (XMLStreamException ex) {
            throw new WLFormatException(ex.getMessage(), ex);
        } finally {
            cleanup();
        }

    }

    @Override
    public String toString() {
        return this.extData.toString() + "\n" + this.textCorpus.toString();
    }

    private void cleanup() {
        if (xmlEventWriter != null) {
            try {
                xmlEventWriter.close();
            } catch (XMLStreamException ex) {
                Logger.getLogger(ExternalDataWithTextCorpusStreamed.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (xmlEventReader != null) {
            try {
                xmlEventReader.close();
            } catch (XMLStreamException ex) {
                Logger.getLogger(ExternalDataWithTextCorpusStreamed.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (xmlReaderWriter != null) {
            try {
                xmlReaderWriter.close();
            } catch (WLFormatException ex) {
                Logger.getLogger(ExternalDataWithTextCorpusStreamed.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (tempFile != null) {
             tempFile.delete();
        }
    }

    private void getLayersToReadWithDependencies(EnumSet<ExternalDataLayerTag> edLayersToRead, EnumSet<TextCorpusLayerTag> tcLayersToRead) {
        
        if (edLayersToRead != null) {
            this.edLayersToRead = EnumSet.copyOf(edLayersToRead);
        }
        if (tcLayersToRead != null) {
            this.tcLayersToRead = EnumSet.copyOf(tcLayersToRead);
            for (TextCorpusLayerTag tag : tcLayersToRead) {
                this.tcLayersToRead.addAll(tag.withDependentLayers());
            }
        }
    }
}
