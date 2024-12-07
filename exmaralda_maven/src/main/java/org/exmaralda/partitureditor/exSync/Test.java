/*
 * Test.java
 *
 * Created on 11. April 2002, 14:30
 */

package org.exmaralda.partitureditor.exSync;

/**
 *
 * @author  Thomas
 * @version 
 */
public class Test {

    /** Creates new Test */
    public Test() {
    }

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {        
        try {
            ExSyncDocument esd = new ExSyncDocument("g:\\exmaralda\\exSync\\shik_wa_004_10_y.xml");
            org.exmaralda.partitureditor.jexmaralda.BasicTranscription t = esd.toBasicTranscription();
            t.writeXMLToFile("g:\\exmaralda\\exSync\\shik_wa_004_10_y_conv.xml","none");
            System.out.println("Done.");
        } catch (Throwable t) {t.printStackTrace();}
    }

}
