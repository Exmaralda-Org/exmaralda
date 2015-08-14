/*
 * EventToolBar.java
 *
 * Created on 1. Juli 2003, 16:05
 */

package org.exmaralda.partitureditor.partiture.toolbars;

import org.exmaralda.partitureditor.partiture.*;
import javax.swing.*;

/**
 *
 * @author  thomas
 */
public class EventToolBar extends AbstractTableToolBar {
    
    private JButton deleteEventButton;
    private JButton extendRightButton;
    private JButton extendLeftButton;
    private JButton shrinkRightButton;
    private JButton shrinkLeftButton;
    private JButton moveRightButton;
    private JButton moveLeftButton;
    private JButton shiftRightButton;
    private JButton shiftLeftButton;
    private JButton mergeButton;
    private JButton splitButton;
    private JButton doubleSplitButton;
    private JButton pauseButton;
    
    /** Creates a new instance of EventToolBar */
    public EventToolBar(PartitureTableWithActions t) {

        super(t, "Event");
        
        this.setMaximumSize(new java.awt.Dimension(360, 30));
        this.setPreferredSize(new java.awt.Dimension(360, 30));

        shiftLeftButton = this.add(table.shiftLeftAction);
        shiftLeftButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        shiftRightButton = this.add(table.shiftRightAction);
        shiftRightButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        mergeButton = this.add(table.mergeAction);
        mergeButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        splitButton = this.add(table.splitAction);
        splitButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        doubleSplitButton = this.add(table.doubleSplitAction);
        doubleSplitButton.setPreferredSize(new java.awt.Dimension(24,24));

        extendLeftButton = this.add(table.extendLeftAction);
        extendLeftButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        extendRightButton = this.add(table.extendRightAction);
        extendRightButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        shrinkLeftButton = this.add(table.shrinkLeftAction);
        shrinkLeftButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        shrinkRightButton = this.add(table.shrinkRightAction);
        shrinkRightButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        moveLeftButton = this.add(table.moveLeftAction);
        moveLeftButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        moveRightButton = this.add(table.moveRightAction);
        moveRightButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        deleteEventButton = this.add(table.deleteEventAction);
        deleteEventButton.setPreferredSize(new java.awt.Dimension(24,24));

        pauseButton = this.add(table.insertPauseAction);
        pauseButton.setPreferredSize(new java.awt.Dimension(24,24));
 
        setToolTipTexts();
    }
    
}
