/*
 * AbstractKWICTableAction.java
 *
 * Created on 19. Februar 2007, 13:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICTable;

/**
 *
 * @author thomas
 */
public abstract class AbstractKWICTableAction extends javax.swing.AbstractAction {
    
    public COMAKWICTable table;
    
    /** Creates a new instance of AbstractKWICTableAction */
    public AbstractKWICTableAction(COMAKWICTable t, String title, javax.swing.ImageIcon icon) {
        super(title, icon);
        table = t;
    }
        
    /** Creates a new instance of AbstractAction */
    public AbstractKWICTableAction(COMAKWICTable t, String title) {
        super(title);
        table = t;
    }
        
}
