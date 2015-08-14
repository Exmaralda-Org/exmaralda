/*
 * PraatPanelListener.java
 *
 * Created on 17. Mai 2004, 12:02
 */

package org.exmaralda.partitureditor.praatPanel;

/**
 *
 * @author  thomas
 */
public interface PraatPanelListener extends java.util.EventListener {
       
    public void processTime(PraatPanelEvent event);
    
}
