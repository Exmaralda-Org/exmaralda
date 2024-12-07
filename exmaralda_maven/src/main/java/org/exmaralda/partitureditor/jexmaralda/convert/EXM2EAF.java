/*
 * EXM2EAF.java
 *
 * Created on 28. Januar 2004, 14:18
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

/**
 *
 * @author  thomas
 */
public class EXM2EAF {
    
    /** Creates a new instance of EXM2EAF */
    public EXM2EAF() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length!=2){
            System.out.println("Usage: EXM2EAF exmaralda-file eaf-file");
            System.exit(0);
        }
        String exmaraldaFileName = args[0];
        String eafFileName = args[1];
        ELANConverter converter = new ELANConverter();
        try{
            org.exmaralda.partitureditor.jexmaralda.BasicTranscription bt = 
                new org.exmaralda.partitureditor.jexmaralda.BasicTranscription(exmaraldaFileName);
            converter.writeELANToFile(bt, eafFileName);
        } catch (Exception e){
            e.printStackTrace();
        }        
    }
    
}
