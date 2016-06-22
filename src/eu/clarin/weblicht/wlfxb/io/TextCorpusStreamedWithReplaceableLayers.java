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

import eu.clarin.weblicht.wlfxb.io.WLFormatException;
import eu.clarin.weblicht.wlfxb.io.XmlReaderWriter;
import eu.clarin.weblicht.wlfxb.tc.api.TextCorpusLayer;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusLayerStoredAbstract;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusLayerTag;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusStored;
import javanet.staxutils.IndentingXMLEventWriter;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * Class <tt>TextCorpusStreamedWithReplaceableLayers</tt> can be used for 
 * applications where an existing TCF layer has to be replaced or removed. 
 * New layers can also be added. A care should be taken by the user of this 
 * class, that there are no layers left in the TCF that depend on the layers 
 * being replaced.
 * 
 * Do not use this class for the usual WebLicht services use case (WebLicht services 
 * are not allowed to change existing TCF layers). For the usual use case within 
 * WebLicht, use {@link eu.clarin.weblicht.wlfxb.io.TextCorpusStreamed} class.
 * 
 * @author Yana Panchenko <yana.panchenko@uni-tuebingen.de>
 */
public class TextCorpusStreamedWithReplaceableLayers extends TextCorpusStored implements Closeable {

    private EnumSet<TextCorpusLayerTag> layersToRead;
    private EnumSet<TextCorpusLayerTag> layersToReplace;
    private EnumSet<TextCorpusLayerTag> layersFound = EnumSet.noneOf(TextCorpusLayerTag.class);
    private EnumSet<TextCorpusLayerTag> readSucceeded = EnumSet.noneOf(TextCorpusLayerTag.class);
    
    private XMLEventReader xmlEventReader;
    private XMLEventWriter xmlEventWriter;
    private XmlReaderWriter xmlReaderWriter;
    private static final int LAYER_INDENT_RELATIVE = 1;
    private boolean closed = false;

    
    /**
     * Creates a <tt>TextCorpusStreamedWithReplaceableLayers</tt> from the 
     * given TCF input stream,specified annotation layers and the output stream.
     *
     * @param inputStream the underlying input stream with linguistic
     * annotations in TCF format.
     * @param layersToRead the annotation layers of <tt>TextCorpus</tt> that
     * should be read into this <tt>TextCorpusStreamedWithReplaceableLayers</tt>.
     * @param layersToReplace the annotation layers of <tt>TextCorpus</tt> that
     * should not be read into <tt>TextCorpusStreamedWithReplaceableLayers</tt> 
     * and should not be rewritten into the output stream.
     * @param outputStream the underlying output stream into which the
     * annotations from the input stream and any new created annotations will be
     * written (in TCF format).
     *
     * @throws WLFormatException if an error in input format or an I/O error
     * occurs.
     */
    public TextCorpusStreamedWithReplaceableLayers(InputStream inputStream,
            EnumSet<TextCorpusLayerTag> layersToRead, 
            EnumSet<TextCorpusLayerTag> layersToReplace,
            OutputStream outputStream)
            throws WLFormatException {
        super("unknown");
        getLayersToReadWithDependencies(layersToRead);
        getLayersToUpdate(layersToReplace);
        if (intersect(layersToRead, layersToReplace)) {
            throw new WLFormatException("Layers to replace should not be among the layers to read.");
        }
        try {
            initializeReaderAndWriter(inputStream, outputStream, false);
            process();
        } catch (WLFormatException e) {
            xmlReaderWriter.close();
            throw e;
        }
    }

    private void initializeReaderAndWriter(InputStream inputStream, OutputStream outputStream, boolean outputAsXmlFragment) throws WLFormatException {
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

    private void process() throws WLFormatException {
        try {
            xmlReaderWriter.readWriteUpToStartElement(TextCorpusStored.XML_NAME);
            // process TextCorpus start element
            XMLEvent event = xmlEventReader.nextEvent();
            super.lang = event.asStartElement().getAttributeByName(new QName("lang")).getValue();
            // add processed TextCorpus start back
            xmlReaderWriter.add(event);
            // create TextCorpus object
            // read layers requested stopping before TextCorpus end element
            processLayers();
            super.connectLayers();
            // if no writing requested finish reading the document
            if (xmlEventWriter == null) {
                xmlReaderWriter.readWriteToTheEnd();
            }
        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        } catch (NoSuchElementException e) {
            throw new WLFormatException(e.getMessage(), e);
        } 
        if (layersToRead.size() != readSucceeded.size()) {
            layersToRead.removeAll(readSucceeded);
            throw new WLFormatException("Following layers could not be read: " + layersToRead.toString());
        }
    }

    private void processLayers() throws WLFormatException {
        boolean textCorpusEnd = false;
        XMLEvent peekedEvent;
        try {
            peekedEvent = xmlEventReader.peek();
            while (!textCorpusEnd && peekedEvent != null) {
                if (peekedEvent.getEventType() == XMLStreamConstants.END_ELEMENT
                        && peekedEvent.asEndElement().getName().getLocalPart().equals(TextCorpusStored.XML_NAME)) {
                    textCorpusEnd = true;
                } else if (peekedEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    processLayer();
                    peekedEvent = xmlEventReader.peek();
                } else {
                    XMLEvent readEvent = xmlReaderWriter.readEvent();
                    xmlReaderWriter.add(readEvent);
                    peekedEvent = xmlEventReader.peek();
                }
            }
        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        }

        if (!textCorpusEnd) {
            throw new WLFormatException(TextCorpusStored.XML_NAME + " end tag not found");
        }
    }

    private void processLayer() throws WLFormatException {

        XMLEvent peekedEvent;
        try {
            peekedEvent = xmlEventReader.peek();
            // now we assume that this event is start of a TextCorpus layer
            String tagName = peekedEvent.asStartElement().getName().getLocalPart();
            TextCorpusLayerTag layerTag = TextCorpusLayerTag.getFromXmlName(tagName);

            if (layerTag == null) { // unknown layer, just add it to output
                //readWriteElement(tagName);
                xmlReaderWriter.readWriteElement(tagName);
            } else {
                if (this.layersToRead.contains(layerTag)) { // known layer, and is requested for reading
                    // add it to the output, but store its data
                    readLayerData(layerTag);
                } else { // known layer, and is not requested for reading
                    if (this.layersToReplace.contains(layerTag)) {
                        // skip it
                        skipLayer(xmlEventReader, layerTag);
                    } else {
                        // just add it to the output
                        xmlReaderWriter.readWriteElement(tagName);
                    }
                }
                layersFound.add(layerTag);
            }
        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        }


    }

    private void readLayerData(TextCorpusLayerTag layerTag) throws WLFormatException {
        JAXBContext context;
        Unmarshaller unmarshaller;
        try {
            context = JAXBContext.newInstance(layerTag.getLayerClass());
            unmarshaller = context.createUnmarshaller();
            TextCorpusLayerStoredAbstract layer = (TextCorpusLayerStoredAbstract) unmarshaller.unmarshal(xmlEventReader);
            super.layersInOrder[layerTag.ordinal()] = layer;
            marshall(super.layersInOrder[layerTag.ordinal()]);
        } catch (JAXBException e) {
            throw new WLFormatException(e.getMessage(), e);
        }
        readSucceeded.add(layerTag);
    }

    private void marshall(TextCorpusLayer layer) throws WLFormatException {
        if (xmlEventWriter == null) {
            return;
        }
        TextCorpusLayerTag layerTag = TextCorpusLayerTag.getFromClass(layer.getClass());
        if (layersFound.contains(layerTag) && !layersToReplace.contains(layerTag)) {
            throw new WLFormatException(layerTag.getXmlName() + " cannot be marshalled: the document already contains this annotation layer.");
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
        } catch (JAXBException e) {
            throw new WLFormatException(e.getMessage(), e);
        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        }
    }


    /**
     * Closes the input and output streams associated with this object and
     * releases any associated system resources. Before the streams are closed,
     * all in-memory annotations of the <tt>TextCorpus</tt> and
     * not-processed part of the input stream are written to the output stream.
     * Therefore, it's important to call <tt>close()<tt> method, so that all the
     * in-memory annotations are saved to the output stream. Once closed, 
     * adding further annotations will have no effect on the output stream.
     *
     * @throws WLFormatException if an error in input format or an I/O error
     * occurs.
     */
    @Override
    public void close() throws WLFormatException {

        if (closed) {
            return;
        }
        closed = true;
        try {
            boolean[] layersRead = new boolean[super.layersInOrder.length];
            for (TextCorpusLayerTag layerRead : layersToRead) {
                layersRead[layerRead.ordinal()] = true;
            }

            for (int i = 0; i < super.layersInOrder.length; i++) {
                // if it's a newly added layer
                if (super.layersInOrder[i] != null && !layersRead[i] //&& !super.layersInOrder[i].isEmpty() 
                        ) {
                    marshall(super.layersInOrder[i]);
                }
            }
        } finally {
            xmlReaderWriter.readWriteToTheEnd();
        }
    }

    private void getLayersToReadWithDependencies(EnumSet<TextCorpusLayerTag> layersToRead) {
        this.layersToRead = EnumSet.copyOf(layersToRead);
        for (TextCorpusLayerTag tag : layersToRead) {
            this.layersToRead.addAll(tag.withDependentLayers());
        }
    }
    
    private void getLayersToUpdate(EnumSet<TextCorpusLayerTag> layersToReplace) {
        this.layersToReplace = EnumSet.copyOf(layersToReplace);
    }

    private boolean intersect(EnumSet<TextCorpusLayerTag> layersA, EnumSet<TextCorpusLayerTag> layersB) {
        for (TextCorpusLayerTag layerA : layersA) {
            if (layersB.contains(layerA)) {
                return true;
            }
        }
        return false;
    }

    private void skipLayer(XMLEventReader xmlEventReader, TextCorpusLayerTag layerTag) throws XMLStreamException {
        boolean foundEnd = false;
        while (!foundEnd) {
            XMLEvent peekedEvent = xmlEventReader.peek();
            if (peekedEvent.getEventType() == XMLStreamConstants.END_ELEMENT) {
                String tagName = peekedEvent.asEndElement().getName().getLocalPart();
                TextCorpusLayerTag layerTagFound = TextCorpusLayerTag.getFromXmlName(tagName);
                if (layerTag == layerTagFound) {
                    foundEnd = true;
                }
            }
            xmlEventReader.nextEvent();
        }
    }
 
}
