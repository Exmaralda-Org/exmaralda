/*
 * Test2.java
 *
 * Created on 28. Oktober 2002, 16:31
 */

package org.exmaralda.partitureditor.exHIATDOS;

/**
 *
 * @author  Thomas
 */
public class Test2 {
    
    /** Creates a new instance of Test2 */
    public Test2() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            String filename = "e:\\AAA_Beispiele\\K5_HIAT_DOS\\Basis\\A6t003";
            System.out.println("Reading...");
            HIATDOSReader hdr = new HIATDOSReader(filename + ".alt");
            hdr.readHead("C:\\Programme\\HIAT-DOS\\neu.inf");
            hdr.readSpeakers("E:\\AAA_Beispiele\\K5_HIAT_DOS\\A6t011\\A6T011a.SIG");
        } catch (Throwable t) {t.printStackTrace();}
                 
    }
    
}
