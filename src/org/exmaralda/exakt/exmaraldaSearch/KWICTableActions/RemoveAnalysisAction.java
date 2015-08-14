/*
 * WordWiseReversedSortAction.java
 *
 * Created on 19. Februar 2007, 13:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.event.ActionEvent;
import org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICTable;
import javax.swing.*;
import org.exmaralda.exakt.search.analyses.AnalysisInterface;
import org.exmaralda.exakt.search.swing.AbstractOKCancelDialog;
import org.exmaralda.exakt.search.swing.AnalysisSelectionPanel;
/**
 *
 * @author thomas
 */
public class RemoveAnalysisAction extends AbstractKWICTableAction {
    
    int count=0;
    int selectedColumn;
    
    
    /** Creates a new instance of WordWiseReversedSortAction */
    public RemoveAnalysisAction(COMAKWICTable t, String title) {
        super(t,title);
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    public void setSelectedColumn(int selectedColumn) {
        this.selectedColumn = selectedColumn;
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        AnalysisInterface analysis = table.getWrappedModel().getAnalysisForColumn(selectedColumn);
        String text = "Are you sure you want to remove\n the analysis '" + analysis.getName() +"'?'";
        int reply = JOptionPane.showConfirmDialog(table, text, "Remove analysis", JOptionPane.YES_NO_OPTION);
        if (reply==JOptionPane.OK_OPTION){
            table.getWrappedModel().removeAnalysisAtColumn(selectedColumn);
        }
        selectedColumn = -1;
    }
    
}
