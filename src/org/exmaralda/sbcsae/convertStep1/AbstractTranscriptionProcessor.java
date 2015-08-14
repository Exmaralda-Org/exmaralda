/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.convertStep1;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.jdom.*;

/**
 *
 * @author thomas
 */
public abstract class AbstractTranscriptionProcessor {
    
    public String TRANSCRIPTION_PATH = "T:\\TP-Z2\\DATEN\\SBCSAE\\0.1\\";
    
    public int currentTranscriptionNumber;
    public String filename;
    
    public abstract void processTranscription(Document transcription) throws JDOMException;
    
    public void process() throws FileNotFoundException, IOException, JDOMException{
        TRN2XML transcriptionReader = new TRN2XML();
        for (int i=1; i<=60; i++){
            currentTranscriptionNumber = i;
            filename = "SBC0";
            if (i<10) filename+="0";
            filename+=Integer.toString(i);
            String inputPath = TRANSCRIPTION_PATH + filename + ".trn";            
            Document d = transcriptionReader.readTRN(new java.io.File(inputPath));
            processTranscription(d);
        }                
    }

}
