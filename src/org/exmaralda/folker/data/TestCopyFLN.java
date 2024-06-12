/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.folker.data;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class TestCopyFLN {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestCopyFLN().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(TestCopyFLN.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestCopyFLN.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(TestCopyFLN.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(TestCopyFLN.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(TestCopyFLN.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(TestCopyFLN.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerException, TransformerConfigurationException, JexmaraldaException {
       File f = new File("D:\\FOLK\\FOLK_E_00380\\FOLK_E_00380_SE_01_T_01.flk");
       EventListTranscription elt = org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.readXML(f, 0);
       System.out.println("Start copy");
       EventListTranscription copy = elt.makeCopy();
       System.out.println("Done copy");
       org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.writeXML(copy, new File("D:\\FOLK\\FOLK_E_00380\\FOLK_E_00380_SE_01_T_01_COPY.flk"), new GATParser(), 2);
    }
    
}
