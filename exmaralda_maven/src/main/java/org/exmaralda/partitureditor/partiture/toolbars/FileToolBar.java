/*
 * FileToolBar.java
 *
 * Created on 1. Juli 2003, 15:59
 */

package org.exmaralda.partitureditor.partiture.toolbars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.partiture.*;
import javax.swing.*;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.partitureditor.jexmaralda.convert.EXMARaLDATransformationScenarios;
import org.jdom.Element;

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
    public final JComboBox transformationComboBox;
    
    /** Creates a new instance of FileToolBar */
    public FileToolBar(PartitureTableWithActions t) {

        super(t, "File");
        
        this.setMaximumSize(new java.awt.Dimension(470, 30));
        //this.setPreferredSize(new java.awt.Dimension(270, 30));

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
        transformationButton = add(table.transformationAction);
        transformationButton.setPreferredSize(new java.awt.Dimension(24,24));       
        
        // this is new 09-12-2020 for issue #230
        // not very elegant, but I'm not that type of person... 
        transformationComboBox = new JComboBox();
        try {
            List<Element> scenarios = EXMARaLDATransformationScenarios.readScenarios(((ExmaraldaApplication)table.getTopLevelAncestor()));
            transformationComboBox.setModel(EXMARaLDATransformationScenarios.getComboBoxModel(scenarios));
            add(transformationComboBox);
            transformationComboBox.setToolTipText("Apply a transformation scenario");
            transformationComboBox.setPreferredSize(new java.awt.Dimension(200,24));
            transformationComboBox.setVisible(true);
            transformationComboBox.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedIndex = transformationComboBox.getSelectedIndex();
                    table.applyTransformationScenario(scenarios.get(selectedIndex));
                }

            });
        } catch (IOException ex) {
            Logger.getLogger(FileToolBar.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(table, "<html>Could not read scenarios: <br/><br/>" + ex.getLocalizedMessage() + "</html>");            
        }        
        
        

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
