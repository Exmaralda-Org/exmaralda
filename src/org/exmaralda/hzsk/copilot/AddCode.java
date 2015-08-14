/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.hzsk.copilot;

/**
 *
 * @author Schmidt
 */

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class AddCode extends AbstractCorpusProcessor {
    
    StringBuffer out = new StringBuffer();
    
    /** Creates a new instance of CheckAnnotations */
    public AddCode() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            AddCode ca = new AddCode();
            ca.doIt(false);
            ca.output();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void processTranscription(BasicTranscription bt) {
        String name = new File(currentFilename).getName();
        String code = name.substring(0, name.indexOf("."));
        bt.getHead().getMetaInformation().getUDMetaInformation().setAttribute("Code", code);
        try {
            bt.writeXMLToFile(currentFilename, "none");
        } catch (IOException ex) {
            Logger.getLogger(AddCode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void output(){
    }
    
    public void processTranscription(SegmentedTranscription st) {
    }

    
}
