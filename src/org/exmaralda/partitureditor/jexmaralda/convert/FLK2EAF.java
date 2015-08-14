/*
 * EXM2EAF.java
 *
 * Created on 28. Januar 2004, 14:18
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.File;
import org.exmaralda.folker.data.EventListTranscription;

/**
 *
 * @author  thomas
 */
public class FLK2EAF {
    
    /** Creates a new instance of EXM2EAF */
    public FLK2EAF() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length!=2){
            System.out.println("Usage: FLK2EAF folker-file eaf-file");
            System.exit(0);
        }
        String folkerFileName = args[0];
        String eafFileName = args[1];
        ELANConverter converter = new ELANConverter();
        try{
            EventListTranscription elt = 
                org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.readXML(new File(folkerFileName));           
            org.exmaralda.partitureditor.jexmaralda.BasicTranscription bt = 
                org.exmaralda.folker.io.EventListTranscriptionConverter.exportBasicTranscription(elt);
            converter.writeELANToFile(bt, eafFileName);
        } catch (Exception e){
            e.printStackTrace();
        }        
    }
    
}
