/*
 * AbstractTableToolBar.java
 *
 * Created on 1. Juli 2003, 15:57
 */

package org.exmaralda.partitureditor.partiture.toolbars;

import org.exmaralda.partitureditor.partiture.*;
import javax.swing.*;
import org.exmaralda.common.helpers.Internationalizer;

/**
 *
 * @author  thomas
 */
public class AbstractTableToolBar extends JToolBar {
    
    PartitureTableWithActions table;

    /** Creates a new instance of AbstractTableToolBar */
    public AbstractTableToolBar(PartitureTableWithActions t, String title) {
        super(title);
        table = t;
        setFloatable(true);
        setRollover(true);
    }
    
    void setToolTipTexts(){
        for (int pos=0; pos<this.getComponentCount(); pos++){
            java.awt.Component comp = this.getComponentAtIndex(pos);
            if (comp instanceof JButton){
                JButton button = (JButton)comp;
                org.exmaralda.partitureditor.partiture.AbstractTableAction action = (org.exmaralda.partitureditor.partiture.AbstractTableAction)(button.getAction());
                if (action==null) continue;
                String text = Internationalizer.getString(action.originalText);
                button.setToolTipText(text);
            }
        }
    }
    
}
