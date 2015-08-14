/*
 * SaveTierFormatTableAction.java
 *
 * Created on 19. Juni 2003, 10:57
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.partiture.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.SaveTierFormatTableAsDialog;
/**
 *
 * @author  thomas
 */

public class SaveTierFormatTableAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of SaveTierFormatTableAction */
    public SaveTierFormatTableAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Save format table", icon, t);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift S"));        
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("saveTierFormatTableAction!");
        table.commitEdit(true);
        saveTierFormatTable();        
    }
    
    private void saveTierFormatTable(){
        /*if (!table.getFormatFilename().equals("untitled.xml")){
            try{
                table.getModel().getTierFormatTable().writeXMLToFile(table.getFormatFilename(),"none");
                table.formatChanged = false;
            } catch (Throwable t){
                saveTierFormatTableAs();
            }
        } else {
            saveTierFormatTableAs();
        }*/
    }
    
    private void saveTierFormatTableAs(){
        SaveTierFormatTableAsDialog dialog = new SaveTierFormatTableAsDialog(table.filename, table.getModel().getTierFormatTable());
        boolean success = dialog.saveTierFormatTableAs(table.parent);
        table.formatChanged = !success;
        if (success){
            //table.formatDirectory = dialog.getFilename();
            //table.setFormatFilename(dialog.getFilename());
        }
    }    
    
    
    
    
}
