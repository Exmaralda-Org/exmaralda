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
public class EditAnalysisAction extends AbstractKWICTableAction {
    
    int count=0;
    AnalysisSelectionPanel asp;
    AbstractOKCancelDialog dialog;
    int selectedColumn;
    
    
    /** Creates a new instance of WordWiseReversedSortAction */
    public EditAnalysisAction(COMAKWICTable t, String title) {
        super(t,title);
        asp = new AnalysisSelectionPanel();
        dialog = new AbstractOKCancelDialog((JFrame)(table.getTopLevelAncestor()), true, asp);
        dialog.setTitle("Edit analysis");
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    public void setSelectedColumn(int selectedColumn) {
        this.selectedColumn = selectedColumn;
    }
    
    
    public void actionPerformed(ActionEvent e) {
        asp.setAnalysis(table.getWrappedModel().getAnalysisForColumn(selectedColumn));
        dialog.setVisible(true);
        if (dialog.isApproved()){
            AnalysisInterface ai = asp.getAnalysis();
            table.getWrappedModel().setAnalysisForColumn(selectedColumn,ai);            
            table.getWrappedModel().fireTableStructureChanged();
            table.setCellEditors();
        }
        selectedColumn = -1;
    }
    
}
