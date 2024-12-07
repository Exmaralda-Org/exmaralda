/*
 * EAF2EXM.java
 *
 * Created on 28. Januar 2004, 14:07
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

/**
 *
 * @author  thomas
 */
public class EAF2EXM {
    
    /** Creates a new instance of EAF2EXM */
    public EAF2EXM() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length!=2){
            System.out.println("Usage: EAF2EXM eaf-file exmaralda-file");
            System.exit(0);
        }
        String eafFileName = args[0];
        String exmaraldaFileName = args[1];
        ELANConverter converter = new ELANConverter();
        try{
            org.exmaralda.partitureditor.jexmaralda.BasicTranscription bt = converter.readELANFromFile(eafFileName);
            bt.writeXMLToFile(exmaraldaFileName, "none");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
}
