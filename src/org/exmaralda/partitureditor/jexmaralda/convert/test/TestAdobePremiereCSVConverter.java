/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaralda.convert.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.AdobePremiereCSVConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.AmberscriptJSONConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.WhisperJSONConverter;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */

// issue #357
public class TestAdobePremiereCSVConverter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, JDOMException, SAXException, ParserConfigurationException, TransformerException, TransformerConfigurationException, JexmaraldaException {
        new TestAdobePremiereCSVConverter().doit();
    }

    private void doit() throws IOException, JDOMException, SAXException, ParserConfigurationException, TransformerException, TransformerConfigurationException, JexmaraldaException {
        BasicTranscription bt = AdobePremiereCSVConverter.readAdobePremiereCSV(new File("C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_02_07_ISSUE_363\\Sequenz 01.csv"));
        bt.writeXMLToFile("C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_02_07_ISSUE_363\\Sequenz 01.exb", "none");
    }
    
}
