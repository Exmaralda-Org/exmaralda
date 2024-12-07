/*
 * TierToolBar.java
 *
 * Created on 1. Juli 2003, 16:13
 */

package org.exmaralda.partitureditor.partiture.toolbars;

import org.exmaralda.partitureditor.partiture.*;
import javax.swing.*;

/**
 *
 * @author  thomas
 */

public class TierToolBar extends AbstractTableToolBar {
    
    private JButton addTierButton;
    private JButton insertTierButton;
    private JButton removeTierButton;
    private JButton moveTierUpButton;
    private JButton changeTierOrderButton;
    private JButton hideTierButton;
    private JButton showAllTiersButton;
    

    /** Creates a new instance of TierToolBar */
    public TierToolBar(PartitureTableWithActions t) {

        super(t, "Tier");
        
        this.setMaximumSize(new java.awt.Dimension(240, 30));
        this.setPreferredSize(new java.awt.Dimension(240, 30));
        
        addTierButton = this.add(table.addTierAction);
        addTierButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        insertTierButton = this.add(table.insertTierAction);
        insertTierButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        removeTierButton = this.add(table.removeTierAction);
        removeTierButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        moveTierUpButton = this.add(table.moveTierUpAction);
        moveTierUpButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        changeTierOrderButton = this.add(table.changeTierOrderAction);
        changeTierOrderButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        hideTierButton = this.add(table.hideTierAction);
        hideTierButton.setPreferredSize(new java.awt.Dimension(24,24));
        
        showAllTiersButton = this.add(table.showAllTiersAction);
        showAllTiersButton.setPreferredSize(new java.awt.Dimension(24,24));
                
        setToolTipTexts();
        
    }
    
}
