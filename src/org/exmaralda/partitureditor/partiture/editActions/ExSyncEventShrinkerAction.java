/*
 * ExSyncEventShrinkerAction.java
 *
 * Created on 17. Juni 2003, 13:51
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.partiture.undo.UndoInformation;
/**
 *
 * @author  thomas
 */
public class ExSyncEventShrinkerAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ExSyncEventShrinkerAction */
    public ExSyncEventShrinkerAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("ExSync event shrinker...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("ExSyncEventShrinkerAction!");
        table.commitEdit(true);
        exSyncEventShrinker();        
    }
    
    private void exSyncEventShrinker(){
        ExSyncEventShrinkerDialog dialog = new ExSyncEventShrinkerDialog(table.parent, true);
        dialog.show();
        if (dialog.change){
            if (table.undoEnabled){
                UndoInformation undoInfo = new UndoInformation(table, "Event shrinker");
                undoInfo.memorizeTranscription(table);
                table.addUndo(undoInfo);
            }
            double percentage = dialog.value/100;
            table.resetFormat(true);
            for (int r=0; r<table.getModel().getNumRows(); r++){
                for (int c=0; c<table.getModel().getNumColumns(); c++){
                    if (table.getModel().containsEvent(r,c)){
                        if (table.getModel().getCellSpan(r,c)>1){
                            int span = table.getModel().getCellSpan(r,c);
                            int requiredWidth = table.getModel().getCellWidth(r,c, table.scaleConstant);
                            while (span>1 && (table.getCombinedPixelWidth(c,span-1) > percentage*requiredWidth)){
                                span--;
                            }
                            if (span != table.getModel().getCellSpan(r,c)){
                                table.getModel().setCellSpan(r,c,span);
                            }
                        }
                    }
                }
            }
            table.resetData();
        }
    }
    
    
}
