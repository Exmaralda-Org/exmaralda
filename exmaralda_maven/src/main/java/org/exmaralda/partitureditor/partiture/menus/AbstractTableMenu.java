/*
 * AbstractTableMenu.java
 *
 * Created on 1. Juli 2003, 14:11
 */

package org.exmaralda.partitureditor.partiture.menus;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.common.helpers.Internationalizer;

/**
 *
 * @author  thomas
 */
public abstract class AbstractTableMenu extends javax.swing.JMenu {
    
    PartitureTableWithActions table;
    
    /** Creates a new instance of AbstractTableMenu */
    public AbstractTableMenu(PartitureTableWithActions t) {
        table = t;            
    }
    
    public void setText(String text){
        super.setText(Internationalizer.getString(text));
    }
    
}
