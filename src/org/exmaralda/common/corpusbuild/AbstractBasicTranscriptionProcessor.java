/*
 * AbstractBasicTranscriptionProcessor.java
 *
 * Created on 16. April 2007, 09:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import org.exmaralda.partitureditor.jexmaralda.*;
import org.xml.sax.*;
import java.io.*;


/**
 *
 * @author thomas
 */
public abstract class AbstractBasicTranscriptionProcessor extends AbstractCorpusProcessor {
    
    BasicTranscription transcription;
    
    public AbstractBasicTranscriptionProcessor(String corpusPath) {
        super(corpusPath);
    }

    public void process(String filename) throws JexmaraldaException, SAXException {
        BasicTranscription bt = new BasicTranscription(filename);
        System.out.println(filename + " read successfully.");
        transcription = bt;
        processTranscription(bt);                        
    }

    public String getXpathToTranscriptions() {
        return BASIC_FILE_XPATH;
    }
    
    public abstract void processTranscription(BasicTranscription bt);
    
    public void write() throws JexmaraldaException, IOException {
         transcription.writeXMLToFile(getCurrentFilename(), "none");        
    }
    
    
    
}
