/*
 * Test.java
 *
 * Created on 29. Juli 2002, 16:48
 */

package org.exmaralda.partitureditor.fsm;

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
            FiniteStateMachine fsm = 
                    sr.readFromFile("C:\\exmaralda\\exmaralda\\src\\org\\exmaralda\\partitureditor\\fsm\\xml\\cGAT_Minimal_EQ_SP.xml");
            String result = fsm.process("(0.9) eso es poco agradable (¿eh?) ");
            System.out.println(result);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
