/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.masker;

/**
 *
 * @author Schmidt
 */
public class MaskerEvent {
    
    public long framesRemaining;
    public long framesTotal;
    public int activity;
    
    public static final int COPY_ACTIVITY = 0;
    public static final int MASK_ACTIVITY = 1;
    public static final int DONE = 2;

    public MaskerEvent(long framesRemaining, long framesTotal, int activity) {
        this.framesRemaining = framesRemaining;
        this.framesTotal = framesTotal;
        this.activity = activity;
    }
    
    
    
}
