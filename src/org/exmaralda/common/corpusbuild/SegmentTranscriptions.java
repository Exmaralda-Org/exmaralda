/*
 * WriteSegmentedTranscriptions.java
 *
 * Created on 12. Oktober 2006, 14:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import org.exmaralda.partitureditor.jexmaralda.*;
import org.xml.sax.*;
import org.exmaralda.partitureditor.fsm.*;
import java.io.*;

/**
 *
 * @author thomas
 */
public class SegmentTranscriptions extends AbstractBasicTranscriptionProcessor {
    
    String suffix = "_s";
    String customFSM = "";
    
   /** Creates a new instance of WriteSegmentedTranscriptions */
    public SegmentTranscriptions(String corpusName, String s) {
        super(corpusName);
        suffix = s;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            SegmentTranscriptions t = new SegmentTranscriptions(args[0], args[2]);
            if (args.length>3){
                t.customFSM = args[3];
            }
            t.doIt();
            t.output(args[1]);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void processTranscription(BasicTranscription bt) {
        try {
            SegmentedTranscription st;
            if (customFSM.equals("NO_SEGMENTATION")){
                st = bt.toSegmentedTranscription();
            } else if (customFSM.equals("CHAT_MINIMAL")){
                org.exmaralda.partitureditor.jexmaralda.segment.CHATMinimalSegmentation cs
                        = new org.exmaralda.partitureditor.jexmaralda.segment.CHATMinimalSegmentation();
                st = cs.BasicToSegmented(bt);
            } else {
                org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation hs;
                if (customFSM.length()>0){
                    hs = new org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation(customFSM);
                } else {
                    hs = new org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation();
                }
                st = hs.BasicToSegmented(bt);
            }
            org.exmaralda.partitureditor.jexmaralda.segment.SegmentCountForMetaInformation.count(st);
            st.setEXBSource(currentFilename);
            int index = currentFilename.lastIndexOf(".");
            String segmentedFilename = getCurrentFilename().substring(0, index) + suffix + ".exs";
            outappend("Segmentation successful:\t" + currentFilename + "\t" + segmentedFilename + "\n");
            st.writeXMLToFile(segmentedFilename, "none");
        } catch (SAXException ex) {
            outappend("Segmentation failed:\t" + getCurrentFilename() + "\t" + ex.getMessage() + "\n");
            ex.printStackTrace();
        } catch (FSMException ex) {
            outappend("Segmentation failed:\t" + getCurrentFilename() + "\t" + ex.getMessage() + "\n");
            ex.printStackTrace();
        } catch (IOException ex) {
            outappend("Segmentation failed:\t" + getCurrentFilename() + "\t" + ex.getMessage() + "\n");
            ex.printStackTrace();
        }
    }
    
    
}
