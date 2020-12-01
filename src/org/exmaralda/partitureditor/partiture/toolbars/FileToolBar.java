/*
 * FileToolBar.java
 *
 * Created on 1. Juli 2003, 15:59
 */

package org.exmaralda.partitureditor.partiture.toolbars;

import org.exmaralda.partitureditor.partiture.*;
import javax.swing.*;

/**
 *
 * @author  thomas
 */
public class FileToolBar extends AbstractTableToolBar {
    
    private final JButton newButton;
    private final JButton openButton;
    private final JButton saveButton;
    
    private final JButton editMetaInformationButton;
    private final JButton editSpeakertableButton;
    private final JButton editRecordingsButton;
    
    //private JButton importButton;
    private final JButton outputButton;
    private final JButton sendHTMLToBrowserButton;
    private final JButton transformationButton; // issue #230
    
    
    /** Creates a new instance of FileToolBar */
    public FileToolBar(PartitureTableWithActions t) {

        super(t, "File");
        
        this.setMaximumSize(new java.awt.Dimension(270, 30));
        this.setPreferredSize(new java.awt.Dimension(270, 30));

        newButton = this.add(table.newAction);
        newButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        openButton = this.add(table.openAction);
        openButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        saveButton = this.add(table.saveAction);
        saveButton.setPreferredSize(new java.awt.Dimension(24,24));
        

        addSeparator();
                
        outputButton = this.add(table.outputAction);
        outputButton.setPreferredSize(new java.awt.Dimension(24,24));
              
        // ADDED IN 1.2.1. (22-Oct-2002)
        sendHTMLToBrowserButton = this.add(table.sendHTMLPartitureToBrowserAction);
        sendHTMLToBrowserButton.setPreferredSize(new java.awt.Dimension(24,24));       
        
        // added 01-12-2020 issue #230
        transformationButton = this.add(table.transformationAction);
        transformationButton.setPreferredSize(new java.awt.Dimension(24,24));       

        addSeparator();

        editMetaInformationButton = this.add(table.editMetaInformationAction);
        editMetaInformationButton.setPreferredSize(new java.awt.Dimension(24,24));

        editSpeakertableButton = this.add(table.editSpeakertableAction);
        editSpeakertableButton.setPreferredSize(new java.awt.Dimension(24,24));

        editRecordingsButton = this.add(table.editRecordingsAction);
        editRecordingsButton.setPreferredSize(new java.awt.Dimension(24,24));

        setToolTipTexts();
    }
    
}
