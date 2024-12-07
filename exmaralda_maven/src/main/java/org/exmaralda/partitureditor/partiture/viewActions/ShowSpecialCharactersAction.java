/*
 * ShowSpecialCharactersAction.java
 *
 * Created on 17. Juni 2003, 15:33
 */

package org.exmaralda.partitureditor.partiture.viewActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;

/**
 *
 * @author  thomas
 */

public class ShowSpecialCharactersAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ShowSpecialCharactersAction */
    public ShowSpecialCharactersAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Show special characters", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("showSpecialCharactersAction!");
        table.showSpecialCharacters();        
    }
    
    
    
}
