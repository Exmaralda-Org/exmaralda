/*
 * Test.java
 *
 * Created on 29. Juli 2002, 16:48
 */

package org.exmaralda.partitureditor.fsm;

import org.xml.sax.*;

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
            FSMSaxReader sr = new FSMSaxReader();
            FiniteStateMachine fsm = sr.readFromFile("C:\\DATEN\\CODE\\EXMARaLDA\\src\\org\\exmaralda\\partitureditor\\fsm\\xml\\cGAT_Minimal.xml");
            String result = fsm.process("((lacht/hhh°))");
            System.out.println(result);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
