/*
 * FSMException.java
 *
 * Created on 31. Juli 2002, 15:45
 */

package org.exmaralda.partitureditor.fsm;

/**
 *
 * @author  Thomas
 * @version 
 */
public class FSMException extends Exception {

    private String processedOutput;
    private String tli;
    private String tierID;

    /** Creates new FSMException */
    public FSMException(String message, String po) {
        super(message);
        processedOutput = po;
        tli = new String();
        tierID = new String();
    }
    
    public FSMException(String message, String processedOutput, String tli, String tierID) {
        super(message);
        this.processedOutput = processedOutput;
        this.tli = tli;
        this.tierID = tierID;
    }

    public String getProcessedOutput(){
        return processedOutput;
    }
    
    public String getTLI(){
        return tli;
    }
    
    public String getTierID(){
        return tierID;
    }
    
    public void setTLI(String t){
        tli = t;
    }

    public void setTierID(String t){
        tierID = t;
    }
}
