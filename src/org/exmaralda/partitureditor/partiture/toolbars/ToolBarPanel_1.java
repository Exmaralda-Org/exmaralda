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
public class ToolBarPanel_1 extends javax.swing.JPanel {
    
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

    /** layout container for tool bars */
    private JPanel toolBarPanel1;
    /** layout container for tool bars */
    private JPanel toolBarPanel2;
    /** layout container for tool bars */
    private JPanel separatorPanel1;
    /** layout container for tool bars */
    private JPanel separatorPanel2;
    /** layout container for tool bars */
    private JPanel separatorPanel3;

    /** Creates a new instance of ToolBarPanel */
    public ToolBarPanel_1(PartitureTableWithActions table) {

        fileToolBar = new FileToolBar(table);
        eventToolBar = new EventToolBar(table);
        tierToolBar = new TierToolBar(table);
        timelineToolBar = new TimelineToolBar(table);
        formatToolBar = new FormatToolBar(table);
        
        toolBarPanel1 = new javax.swing.JPanel();
        toolBarPanel2 = new javax.swing.JPanel();
        separatorPanel1 = new javax.swing.JPanel();
        separatorPanel2 = new javax.swing.JPanel();
        separatorPanel3 = new javax.swing.JPanel();
                 
        // the toolbarPanel that holds the two other toolbar panels
        this.setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));      
        
        // the upper tool bar panel
        toolBarPanel1.setLayout(new javax.swing.BoxLayout(toolBarPanel1, javax.swing.BoxLayout.X_AXIS));        
        toolBarPanel1.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // the lower tool bar panel
        toolBarPanel2.setLayout(new javax.swing.BoxLayout(toolBarPanel2, javax.swing.BoxLayout.X_AXIS));
        toolBarPanel2.setAlignmentX(Component.LEFT_ALIGNMENT);

        // a separator between the file toolbar and the format toolbar
        separatorPanel1.setLayout(new javax.swing.BoxLayout(separatorPanel1, javax.swing.BoxLayout.X_AXIS));        
        separatorPanel1.setMaximumSize(new java.awt.Dimension(50, 30));
        separatorPanel1.setPreferredSize(new java.awt.Dimension(50, 30));
        
        // a separator between the tier toolbar and the event toolbar
        separatorPanel2.setLayout(new javax.swing.BoxLayout(separatorPanel2, javax.swing.BoxLayout.X_AXIS));        
        separatorPanel2.setMaximumSize(new java.awt.Dimension(50, 30));
        separatorPanel2.setPreferredSize(new java.awt.Dimension(50, 30));

        // a separator between the tier toolbar and the event toolbar
        separatorPanel3.setLayout(new javax.swing.BoxLayout(separatorPanel3, javax.swing.BoxLayout.X_AXIS));        
        separatorPanel3.setMaximumSize(new java.awt.Dimension(50, 30));
        separatorPanel3.setPreferredSize(new java.awt.Dimension(50, 30));

        toolBarPanel1.add(fileToolBar);
        toolBarPanel1.add(separatorPanel1);
        toolBarPanel1.add(formatToolBar);
        
        toolBarPanel2.add(tierToolBar);
        toolBarPanel2.add(separatorPanel2);
        toolBarPanel2.add(eventToolBar);
        toolBarPanel2.add(separatorPanel3);
        toolBarPanel2.add(timelineToolBar);
        
        add(toolBarPanel1);
        add(toolBarPanel2);
        
    }
    
}
