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
import java.util.HashMap;
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
public class MakeTierLabels extends AbstractCorpusProcessor {
    
    StringBuffer out = new StringBuffer();
    
    /** Creates a new instance of CheckAnnotations */
    public MakeTierLabels() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            MakeTierLabels ca = new MakeTierLabels();
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
        boolean changed = false;
        for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
            Tier t = bt.getBody().getTierAt(pos);
            String cat = t.getCategory();
            if ("akz".equals(cat)){
                t.setDisplayName("");
                changed = true;
            }
            if ("eng".equals(cat) || "deu".equals(cat)){
                t.setDisplayName(t.getDescription(bt.getHead().getSpeakertable()));
                changed = true;
            }
        }
        if (changed){
            try {
                bt.writeXMLToFile(currentFilename, "none");
            } catch (IOException ex) {
                Logger.getLogger(MakeTierLabels.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void output(){
    }
    
    public void processTranscription(SegmentedTranscription st) {
    }

    
}
