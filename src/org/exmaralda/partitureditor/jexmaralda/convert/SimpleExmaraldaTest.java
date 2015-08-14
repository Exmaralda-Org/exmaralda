/*
 * SimpleExmaraldaTest.java
 *
 * Created on 1. Oktober 2002, 11:25
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;

/**
 *
 * @author  Thomas 
 */
public class SimpleExmaraldaTest {
    
    static String FILENAME = "D:\\AAA_Beispiele\\SimpleExmaralda\\offend.txt";
    static String OUTNAME = "D:\\AAA_Beispiele\\SimpleExmaralda\\Out.xml";
    
    /** Creates a new instance of SimpleExmaraldaTest */
    public SimpleExmaraldaTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            SimpleExmaraldaReader ser = new SimpleExmaraldaReader(FILENAME, "UTF-8");
            BasicTranscription bt = ser.parseBasicTranscription();
            bt.writeXMLToFile(OUTNAME, "none");
        } catch (IOException ioe){
            ioe.printStackTrace();
        } catch (JexmaraldaException je){
            System.out.println("Jexmaralda Exception!");
            System.out.println(je.getMessage());
        }
    }
    
}
