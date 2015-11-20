/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.sound;

/**
 *
 * @author Schmidt
 */
public interface SilenceDetectorListener {

    public void processProgress(int countIterations, int totalIterations, long startTime, long time); 
    
}
