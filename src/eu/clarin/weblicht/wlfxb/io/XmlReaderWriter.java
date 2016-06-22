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

import java.util.NoSuchElementException;
import javax.xml.stream.*;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

/**
 * Class <tt>XmlReaderWriter</tt> helps to work with TCF XML underlying streams.
 * Should not be used directly.
 *
 * @author Yana Panchenko
 */
public class XmlReaderWriter {

    public static final String XML_WL1_MODEL_PI_NAME = "xml-model";
    public static final String NEW_LINE = System.getProperty("line.separator");
    private static final String INDENT = "  ";
    private int indent = -1;
    private boolean charsWritten = false;
    private boolean startWritten = false;
    private boolean ispaceWritten = true;
    private final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    private XMLEventReader xmlEventReader;
    private XMLEventWriter xmlEventWriter;
    private boolean fragment = false;

    public XmlReaderWriter(XMLEventReader xmlEventReader, XMLEventWriter xmlEventWriter) {
        this.xmlEventReader = xmlEventReader;
        this.xmlEventWriter = xmlEventWriter;
    }

    public void setOutputAsXmlFragment(boolean outputAsXmlFragment) {
        this.fragment = outputAsXmlFragment;
    }

    public XMLEvent readEvent() throws WLFormatException {
        try {
            return xmlEventReader.nextEvent();
        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        } catch (NoSuchElementException e) {
            throw new WLFormatException(e.getMessage(), e);
        } 

    }

    public void startExternalFragment(int incIndent) throws XMLStreamException {
        if (!ispaceWritten) {
            XMLEvent e = eventFactory.createIgnorableSpace(NEW_LINE);
            xmlEventWriter.add(e);
            ispaceWritten = true;
        }
        for (int i = 0; i < incIndent; i++) {
            incIndent();
        }
        addIndent();
    }

    public void endExternalFragment(int decIndent) throws XMLStreamException {
        XMLEvent e = eventFactory.createIgnorableSpace(NEW_LINE);
        xmlEventWriter.add(e);
        ispaceWritten = true;
        for (int i = 0; i < decIndent; i++) {
            decIndent();
        }
    }

    public void add(XMLEvent xmlEvent) throws XMLStreamException {

        if (xmlEventWriter == null) {
            return;
        }
        XMLEvent e;
        switch (xmlEvent.getEventType()) {
            case XMLStreamConstants.START_ELEMENT:
                if (!ispaceWritten) {
                    e = eventFactory.createIgnorableSpace(NEW_LINE);
                    xmlEventWriter.add(e);
                }
                incIndent();
                addIndent();
                xmlEventWriter.add(xmlEvent);
                charsWritten = false;
                startWritten = true;
                ispaceWritten = false;
                break;
            case XMLStreamConstants.END_ELEMENT:
                if (!charsWritten && !startWritten) {
                    if (!ispaceWritten) {
                        e = eventFactory.createIgnorableSpace(NEW_LINE);
                        xmlEventWriter.add(e);
                    }
                    addIndent();
                }
                decIndent();
                xmlEventWriter.add(xmlEvent);
                charsWritten = false;
                startWritten = false;
                ispaceWritten = false;
                break;
            case XMLStreamConstants.CHARACTERS: //TODO check on big data
                //go back to previous version if doesn't work on big data:
                // xmlEventWriter.add(eventFactory.createCharacters(characters.getData()));
                Characters characters = xmlEvent.asCharacters();
                if (characters.isWhiteSpace()) {
                    if (!ispaceWritten) {
                        e = eventFactory.createIgnorableSpace(NEW_LINE);
                        xmlEventWriter.add(e);
                        ispaceWritten = true;
                        charsWritten = false;
                    }
                } else {
                    xmlEventWriter.add(xmlEvent);
                    ispaceWritten = false;
                    charsWritten = true;
                }
                startWritten = false;
                break;
            case XMLStreamConstants.COMMENT:
                if (!ispaceWritten) {
                    e = eventFactory.createIgnorableSpace(NEW_LINE);
                    xmlEventWriter.add(e);
                }
                incIndent();
                addIndent();
                xmlEventWriter.add(xmlEvent);
                decIndent();
                charsWritten = false;
                ispaceWritten = false;
                startWritten = false;
                break;
            case XMLStreamConstants.START_DOCUMENT:
                if (!fragment) {
                    xmlEventWriter.add(xmlEvent);
                }
                ispaceWritten = false;
                break;
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                if (!fragment) {
                    if (!ispaceWritten) {
                        e = eventFactory.createIgnorableSpace(NEW_LINE);
                        xmlEventWriter.add(e);
                    }
                    xmlEventWriter.add(xmlEvent);
                }
                ispaceWritten = false;
                break;
            default:
                xmlEventWriter.add(xmlEvent);
                charsWritten = false;
                ispaceWritten = false;
                startWritten = false;
                break;
        }
    }

    void incIndent() {
        indent++;
    }

    void decIndent() {
        indent--;
    }

    void addIndent() throws XMLStreamException {
        XMLEvent xmlEvent;
        for (int i = 0; i < indent; i++) {
            xmlEvent = eventFactory.createIgnorableSpace(INDENT);
            xmlEventWriter.add(xmlEvent);
        }
    }

    public void close() throws WLFormatException {

        try {
            if (xmlEventReader != null) {
                xmlEventReader.close();
            }
        } catch (XMLStreamException e) {
            try {
                if (xmlEventWriter != null) {
                    xmlEventWriter.close();
                }
            } catch (XMLStreamException e2) {
                throw new WLFormatException(e2);
            }
            throw new WLFormatException(e);
        }

    }

    public void readWriteToTheEnd() throws WLFormatException {
        try {
            while (xmlEventReader.hasNext()) {
                add(xmlEventReader.nextEvent());
            }
            if (xmlEventWriter != null) {
                xmlEventWriter.flush();
            }
        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        } catch (NoSuchElementException e) {
            throw new WLFormatException(e.getMessage(), e);
        } finally {
            close();
        }
    }

    // precondition read pointer is just before the start tag with the local name tagName
    // postcondition read pointer is just after the end of the tag with the local name tagName
    public void readWriteElement(String tagName) throws WLFormatException {
        try {
            while (xmlEventReader.hasNext()) {
                XMLEvent event = xmlEventReader.nextEvent();
                switch (event.getEventType()) {
                    case XMLStreamConstants.END_ELEMENT:
                        add(event);
                        if (event.asEndElement().getName().getLocalPart().equals(tagName)) {
                            return;
                        }
                        break;
                    default:
                        add(event);
                        break;
                }
            }

        } catch (XMLStreamException e) {
            throw new WLFormatException(e.getMessage(), e);
        } catch (NoSuchElementException e) {
            throw new WLFormatException(e.getMessage(), e);
        } 

    }

    // precondition read pointer is at any place before start tag with the local name startTag
    // postcondition read pointer is just before the start of the tag with the local name startTag
    public void readWriteUpToStartElement(String startTag) throws XMLStreamException {
        boolean startTagIsNext = false;
        while (xmlEventReader.hasNext() && !startTagIsNext) {
            XMLEvent peekedEvent = xmlEventReader.peek();
            switch (peekedEvent.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    String elementName = peekedEvent.asStartElement().getName().getLocalPart();
                    if (elementName.equals(startTag)) {
                        startTagIsNext = true;
                    } else {
                        XMLEvent readEvent = xmlEventReader.nextEvent();
                        add(readEvent);
                    }
                    break;
                default:
                    XMLEvent readEvent = xmlEventReader.nextEvent();
                    add(readEvent);
                    break;
            }
        }
    }

    public void readWriteUpToEndElement(String endTag) throws XMLStreamException {
        boolean endTagIsNext = false;
        while (xmlEventReader.hasNext() && !endTagIsNext) {
            XMLEvent peekedEvent = xmlEventReader.peek();
            switch (peekedEvent.getEventType()) {
                case XMLStreamConstants.END_ELEMENT:
                    String elementName = peekedEvent.asEndElement().getName().getLocalPart();
                    if (elementName.equals(endTag)) {
                        endTagIsNext = true;
                    } else {
                        XMLEvent readEvent = xmlEventReader.nextEvent();
                        add(readEvent);
                    }
                    break;
                default:
                    XMLEvent readEvent = xmlEventReader.nextEvent();
                    add(readEvent);
                    break;
            }
        }
    }
}
