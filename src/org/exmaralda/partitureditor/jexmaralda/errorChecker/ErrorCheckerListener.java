/*
 * ErrorCheckerListener.java
 *
 * Created on 15. November 2007, 10:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda.errorChecker;

/**
 *
 * @author thomas
 */
public interface ErrorCheckerListener {
    
    public void processError(String file, String tier, String start);
    
}
