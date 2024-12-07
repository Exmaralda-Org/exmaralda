/*
 * SegmentationPanelListener.java
 *
 * Created on 15. Januar 2003, 11:46
 */

package org.exmaralda.partitureditor.segmentationPanel;

/**
 *
 * @author  Thomas
 * @version 
 */
public interface SegmentationPanelListener extends java.util.EventListener {
    
    public void getTurnRequested(SegmentationPanelEvent event);

}

