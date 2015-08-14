/*
 * AbstractTableAction.java
 *
 * Created on 16. Juni 2003, 16:54
 */

package org.exmaralda.partitureditor.partiture;

import org.exmaralda.partitureditor.partiture.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import org.exmaralda.common.helpers.Internationalizer;

/**
 * superclass of all actions that make up menu items,
 * toolbar buttons, popup menu items etc.
 * simply provides constructors that associate an AbstractAction
 * with a PartiturTableWithActions
 * @author  thomas
 */
public abstract class AbstractTableAction extends javax.swing.AbstractAction {
    
    public PartitureTableWithActions table;
    public String originalText;
    
    /** Creates a new instance of AbstractTableAction */
    public AbstractTableAction(String text, javax.swing.ImageIcon icon, PartitureTableWithActions t) {
        super(Internationalizer.getString(text),icon);
        originalText = text;
        table = t;
    }
    
    public AbstractTableAction(String text, PartitureTableWithActions t) {
        super(Internationalizer.getString(text));
        originalText = text;
        table = t;
    }
          
        
}
