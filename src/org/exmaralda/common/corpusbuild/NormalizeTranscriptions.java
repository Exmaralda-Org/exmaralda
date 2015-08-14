/*
 * CheckTierProperties.java
 *
 * Created on 16. Oktober 2006, 14:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;

/**
 *
 * @author thomas
 */
public class NormalizeTranscriptions extends AbstractBasicTranscriptionProcessor {
    
    /** Creates a new instance of CheckTierProperties */
    public NormalizeTranscriptions(String corpusName) {
        super(corpusName);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            NormalizeTranscriptions nt = new NormalizeTranscriptions(args[0]);
            nt.doIt();
            nt.output(args[1]);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void processTranscription(BasicTranscription bt) {
        try {
            bt.normalize();
            bt.normalizeWhiteSpace();
            outappend(getCurrentFilename() + "normalized.\n");
            write();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
    
}
