/*
 * PaidusHTMLTest.java
 *
 * Created on 13. Februar 2004, 16:58
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

/**
 *
 * @author  thomas
 */
public class PaidusHTMLTest {
    
    /** Creates a new instance of PaidusHTMLTest */
    public PaidusHTMLTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory ssf = new StylesheetFactory();
        try {
            String result = ssf.applyInternalStylesheetToExternalXMLFile("/E3/BasicTranscription2PaidusHTML.xsl",  "I:\\Paidus\\xml\\Maire_27.xml");
            System.out.println("started writing document...");
            java.io.FileOutputStream fos = new java.io.FileOutputStream(new java.io.File("c:\\paidus_out.html"));
            fos.write(result.getBytes("UTF-8"));
            fos.close();
            System.out.println("document written.");
        } catch (Throwable t){
            t.printStackTrace();
        }
    }
    
}
