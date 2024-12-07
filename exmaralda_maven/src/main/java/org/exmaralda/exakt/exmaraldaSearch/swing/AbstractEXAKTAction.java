/*
 * AbstractAction.java
 *
 * Created on 9. Februar 2007, 11:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.swing;

/**
 *
 * @author thomas
 */
public abstract class AbstractEXAKTAction extends javax.swing.AbstractAction {
    
    public EXAKT exaktFrame;
    
    /** Creates a new instance of AbstractAction */
    public AbstractEXAKTAction(EXAKT ef, String title, javax.swing.ImageIcon icon) {
        super(title, icon);
        exaktFrame = ef;
    }

    /** Creates a new instance of AbstractAction */
    public AbstractEXAKTAction(EXAKT ef, String title) {
        super(title);
        exaktFrame = ef;
    }
    
}
