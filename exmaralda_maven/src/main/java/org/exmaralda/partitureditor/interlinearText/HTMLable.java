/*
 * HTMLable.java
 *
 * Created on 1. Maerz 2002, 16:37
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public interface HTMLable {
    
    public String toHTML(HTMLParameters param);
    
    public void writeHTML(java.io.FileOutputStream fos, HTMLParameters param) throws java.io.IOException;

}

