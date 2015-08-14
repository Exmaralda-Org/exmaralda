/*
 * ToolBarPanel.java
 *
 * Created on 9. Maerz 2004, 09:22
 */

package org.exmaralda.partitureditor.partiture.toolbars;

import javax.swing.*;
import java.awt.*;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class ToolBarPanel extends javax.swing.JPanel {
    
    /** the file tool bar */
    private JToolBar fileToolBar;
    /** the tier tool bar */
    private JToolBar tierToolBar;
    /** the event tool bar */
    private JToolBar eventToolBar;
    /** the timeline tool bar */
    public JToolBar timelineToolBar;
    /** the format tool bar */
    private JToolBar formatToolBar;


    /** Creates a new instance of ToolBarPanel */
    public ToolBarPanel(PartitureTableWithActions table) {

        fileToolBar = new FileToolBar(table);
        eventToolBar = new EventToolBar(table);
        tierToolBar = new TierToolBar(table);
        timelineToolBar = new TimelineToolBar(table);
        formatToolBar = new FormatToolBar(table);
        
        setLayout(new java.awt.FlowLayout(FlowLayout.LEFT));
        add(fileToolBar);
        add(eventToolBar);
        add(tierToolBar);
        add(timelineToolBar);
        add(formatToolBar);


        
    }
    
}
