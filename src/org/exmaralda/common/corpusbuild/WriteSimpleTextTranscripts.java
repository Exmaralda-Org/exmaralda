/*
 * MakePartiturFiles.java
 *
 * Created on 21. November 2006, 15:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import org.exmaralda.partitureditor.jexmaralda.segment.SegmentedToListInfo;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.jdom.*;
import org.exmaralda.partitureditor.jexmaralda.*;

/**
 *
 * @author thomas
 */
public class WriteSimpleTextTranscripts extends AbstractBasicTranscriptionProcessor {
    
    String suffix = "";        
    
    
    /** Creates a new instance of WriteSimpleTextTranscripts */
    public WriteSimpleTextTranscripts(String corpusName, String s) {
        super(corpusName);
        suffix = s;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            WriteSimpleTextTranscripts wul = new WriteSimpleTextTranscripts(args[0], args[2]);
            wul.doIt();
            wul.output(args[1]);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
    }

    public void processTranscription(BasicTranscription bt) {
        try {
            SegmentedTranscription st = bt.toSegmentedTranscription();
            SegmentedToListInfo info = new SegmentedToListInfo(st, SegmentedToListInfo.TURN_SEGMENTATION);
            ListTranscription lt = st.toListTranscription(info);
            lt.getBody().sort();           
            String listTXTFilename =    getCurrentDirectoryname() 
                                    + System.getProperty("file.separator")
                                    + "presentation"
                                    + System.getProperty("file.separator")
                                    + getNakedFilenameWithoutSuffix()
                                    + suffix
                                    + ".txt";                        
            lt.writeSimpleTextToFile(listTXTFilename);
            outappend(listTXTFilename +  " written.\n");   
        } catch (IOException ex) {
            ex.printStackTrace();
        }        
    }
    
}
