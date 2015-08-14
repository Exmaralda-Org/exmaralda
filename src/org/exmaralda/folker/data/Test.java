/*
 * Test.java
 *
 * Created on 9. Mai 2008, 16:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.data;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class Test {
    
    /** Creates a new instance of Test */
    public Test() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new Test().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void doit() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException{
       File f = new File("C:\\Users\\Schmidt\\Desktop\\TEST\\Gugumus\\FOLK_BEWT_01_A03_3004_1038_Christian_Gugumus.flk");
       EventListTranscription elt = org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.readXML(f, 0);
       TranscriptionHead  transcriptionHead = new TranscriptionHead(f);
       int howMany = transcriptionHead.extractMaskSegments(elt);
       System.out.println(howMany + " extracted");
            //if (howMany>0){
            //    JOptionPane.showMessageDialog(applicationFrame, "Es wurden " + Integer.toString(howMany) + " Maskierungssegmente\nverschoben.");
            //}
        
    }
    
}
