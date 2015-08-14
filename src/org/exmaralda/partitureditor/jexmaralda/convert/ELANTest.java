/*
 * ELANTest.java
 *
 * Created on 13. Oktober 2003, 15:02
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import org.exmaralda.partitureditor.jexmaralda.*;
/**
 *
 * @author  thomas
 */
public class ELANTest {
    
    /** Creates a new instance of ELANTest */
    public ELANTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            //PraatUnicodeMapping pum = new PraatUnicodeMapping();
            //pum.instantiate();
            //BasicTranscription bt = new BasicTranscription("C:\\Dokumente und Einstellungen\\thomas\\Desktop\\paidus2.xml");
            //new PraatConverter().writePraatToFile(bt, "C:\\Dokumente und Einstellungen\\thomas\\Desktop\\paidus.textGrid");
            ELANConverter ec = new ELANConverter();
            BasicTranscription bt = ec.readELANFromFile("C:\\Users\\Schmidt\\Desktop\\wrobel.eaf");
            bt.getBody().smoothTimeline(0.052);
            bt.writeXMLToFile("C:\\Users\\Schmidt\\Desktop\\elan_out.xml","none");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
}
