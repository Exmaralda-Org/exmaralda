/*
 * ItElement.java
 *
 * Created on 26. Februar 2002, 16:08
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public interface ItElement {
    
    boolean isItBundle();
    
    public java.util.Vector trim(BreakParameters param);
    
    public boolean keepTogetherWithNext();
    
    public void print(java.awt.Graphics2D g, PrintParameters param);

}

