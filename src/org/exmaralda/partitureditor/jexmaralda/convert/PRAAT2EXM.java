/*
 * PRAAT2EXM.java
 *
 * Created on 13. Oktober 2004, 14:11
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import org.exmaralda.partitureditor.jexmaralda.*;
/**
 *
 * @author  thomas
 */
public class PRAAT2EXM {
    
    /** Creates a new instance of PRAAT2EXM */
    public PRAAT2EXM() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length!=2){
            System.out.println("Usage: PRAAT2EXM praatFile.textGrid exmaraldaFile.xml");
            System.exit(0);
        }
        String praatFile = args[0];
        String exmFile = args[1];
        try {
            PraatConverter converter = new PraatConverter();
            BasicTranscription bt = converter.readPraatFromFile(praatFile);
            bt.writeXMLToFile(exmFile, "none");
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
            
        // TODO code application logic here
    }
    
}
