/*
 * Timer.java
 *
 * Created on 6. November 2002, 09:31
 */

package org.exmaralda.partitureditor.partiture;

/**
 *
 * @author  Thomas
 */
public class Timer {
    
    double start = 0;
    double end = 0;
    
    /** Creates a new instance of Timer */
    public Timer() {
    }
    
    public void setStart(){
        start=System.currentTimeMillis();
    }
    
    public void out(String processName){
        end = System.currentTimeMillis();
        double dur = end - start;
        //System.out.println(processName + ":\t" + Double.toString(dur));
    }
    
}
