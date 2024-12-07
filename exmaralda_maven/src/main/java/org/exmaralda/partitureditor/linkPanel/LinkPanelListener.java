/*
 * LinkPanelListener.java
 *
 * Created on 17. Januar 2002, 10:31
 */

package org.exmaralda.partitureditor.linkPanel;

/**
 *
 * @author  Thomas
 * @version 
 */
public interface LinkPanelListener extends java.util.EventListener {

    public void linkChanged(int row, int col);
    
}

