/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.tei;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class ISOTEIProcessor {
    
    
    StylesheetFactory ssf = new StylesheetFactory(true);
    
    public Document normalize(Document doc) throws SAXException, ParserConfigurationException, IOException, TransformerException, JDOMException{
        String xmlOut = ssf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/normalize.xsl", IOUtilities.documentToString(doc));
        return FileIO.readDocumentFromString(xmlOut);
    }
}
