/*
 * Basic2HIATSegmented.java
 *
 * Created on 21. Juni 2005, 11:21
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.exmaralda.partitureditor.jexmaralda.command;

import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.segment.*;
import org.exmaralda.partitureditor.jexmaralda.*;


/**
 *
 * @author thomas
 */
public class Basic2HIATSegmented {
    
    static String[] USAGE = {  "Usage: Basic2HIATSegmented <input> <output>",
                               "<input> - the basic transcription which ist to be segmented",
                               "<output> - the segmented transcription to which the result is written" };
    
    /** Creates a new instance of Basic2HIATSegmented */
    public Basic2HIATSegmented() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length!=2){
            printUsage();
        }
        String inputFilename = args[0];
        String outputFilename = args[1];
        try{
            BasicTranscription bt = new BasicTranscription(inputFilename);
            SegmentedTranscription st = new HIATSegmentation().BasicToSegmented(bt);
            st.writeXMLToFile(outputFilename, "none");
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        
        
    }
    
    public static void printUsage() {
        for (int pos=0; pos<USAGE.length; pos++){
            System.out.println(USAGE[pos]);
        }
    }
    
}
