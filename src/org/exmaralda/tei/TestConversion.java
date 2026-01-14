/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.tei;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
import org.jdom.JDOMException;
import org.jdom.transform.XSLTransformException;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class TestConversion {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestConversion().doit();
        } catch (SAXException | JexmaraldaException | FSMException | JDOMException | IOException | ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(TestConversion.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    String IN = "Y:\\zat\\zat_corpus\\2025_amica\\2025-02_amica-01_hsrw\\amica_t1\\autotranscript\\2025-02_amica_t1.exb";
    String OUT = "Y:\\zat\\zat_corpus\\2025_amica\\2025-02_amica-01_hsrw\\amica_t1\\autotranscript\\2025-02_amica_t1.xml";

    private void doit() throws SAXException, JexmaraldaException, FSMException, JDOMException, XSLTransformException, IOException, ParserConfigurationException, TransformerException {
        TEIConverter converter = new TEIConverter();
        converter.writeGenericSegmentedISOTEIToFile(new BasicTranscription(IN), OUT, null);
    }
    
}
