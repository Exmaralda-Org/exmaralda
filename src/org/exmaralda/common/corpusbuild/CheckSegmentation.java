/*
 * Test.java
 *
 * Created on 10. Oktober 2006, 17:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import org.exmaralda.partitureditor.jexmaralda.*;
import java.util.*;
import java.net.*;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class CheckSegmentation extends AbstractBasicTranscriptionChecker {
    
   

    /** Creates a new instance of Test */
    public CheckSegmentation() {
    }
    

    public void processTranscription(BasicTranscription bt, String currentFilename) throws URISyntaxException, org.xml.sax.SAXException  {
        String name = bt.getHead().getMetaInformation().getTranscriptionName();
        Vector v = new org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation().getSegmentationErrors(bt);
        for (Object o : v){
            org.exmaralda.partitureditor.fsm.FSMException fsmex = (org.exmaralda.partitureditor.fsm.FSMException)o;
            String text = name + ": " + fsmex.getMessage().substring(7);
            String filename = currentFilename;
            addError(filename, fsmex.getTierID(),fsmex.getTLI() , text);
        }                                    
    }

    
    
}
