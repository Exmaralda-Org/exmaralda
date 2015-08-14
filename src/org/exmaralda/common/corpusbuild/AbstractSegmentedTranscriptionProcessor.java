/*
 * AbstractBasicTranscriptionProcessor.java
 *
 * Created on 16. April 2007, 09:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.xml.sax.*;


/**
 *
 * @author thomas
 */
public abstract class AbstractSegmentedTranscriptionProcessor extends AbstractCorpusProcessor {
        
    
    public AbstractSegmentedTranscriptionProcessor(String corpusPath) {
        super(corpusPath);
    }

    
    public void process(String filename) throws JexmaraldaException, SAXException {
        org.exmaralda.partitureditor.jexmaralda.sax.SegmentedTranscriptionSaxReader reader = new org.exmaralda.partitureditor.jexmaralda.sax.SegmentedTranscriptionSaxReader();
        SegmentedTranscription st = reader.readFromFile(filename);
        System.out.println(filename + " read successfully.");
        processTranscription(st);
    }

    public String getXpathToTranscriptions() {
        return this.SEGMENTED_FILE_XPATH;
    }
    
    public abstract void processTranscription(SegmentedTranscription bt);
    
    
    
}
