/*
 * FormatToolBar.java
 *
 * Created on 1. Juli 2003, 16:20
 */

package org.exmaralda.partitureditor.partiture.toolbars;

import org.exmaralda.partitureditor.partiture.*;
import javax.swing.*;

/**
 *
 * @author  thomas
 */
public class FormatToolBar extends AbstractTableToolBar {
    
    //private JButton reformatButton;
    private JButton editScaleConstantButton;
    
    //private JButton openTierFormatTableButton;
    //private JButton editTierFormatTableButton;
    private JButton editTierFormatButton;
    private JButton editRowLabelFormatButton;
    private JButton editColumnLabelFormatButton;
    private JButton underlineButton;

    /** Creates a new instance of FormatToolBar */
    public FormatToolBar(PartitureTableWithActions t) {

        super(t, "Format");
        
        this.setPreferredSize(new java.awt.Dimension(200,30));
        this.setMaximumSize(new java.awt.Dimension(200,30));

        //reformatButton = this.add(table.reformatAction);
        //reformatButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        editScaleConstantButton = this.add(table.changeScaleConstantAction);
        editScaleConstantButton.setPreferredSize(new java.awt.Dimension(24,24));        
        
        addSeparator();
        //this.add(new javax.swing.JSeparator());

        //openTierFormatTableButton = this.add(table.openTierFormatTableAction);
        //openTierFormatTableButton.setPreferredSize(new java.awt.Dimension(24,24));
                
        //editTierFormatTableButton = this.add(table.editTierFormatTableAction);
        //editTierFormatTableButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        editTierFormatButton = this.add(table.editTierFormatAction);
        editTierFormatButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        editRowLabelFormatButton = this.add(table.editRowLabelFormatAction);
        editRowLabelFormatButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        editColumnLabelFormatButton = this.add(table.editColumnLabelFormatAction);
        editColumnLabelFormatButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        addSeparator();
        //this.add(new javax.swing.JSeJSeparator());

        underlineButton = this.add(table.underlineAction);
        underlineButton.setPreferredSize(new java.awt.Dimension(24,24));

        setToolTipTexts();
        
    }
    
}
