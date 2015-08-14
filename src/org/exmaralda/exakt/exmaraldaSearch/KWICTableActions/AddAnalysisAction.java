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
public class AddAnalysisAction extends AbstractKWICTableAction {
    
    int count=0;
    AnalysisSelectionPanel asp;
    AbstractOKCancelDialog dialog;
    
    
    /** Creates a new instance of WordWiseReversedSortAction */
    public AddAnalysisAction(COMAKWICTable t, String title) {
        super(t,title);
        asp = new AnalysisSelectionPanel();
        dialog = new AbstractOKCancelDialog((JFrame)(table.getTopLevelAncestor()), true, asp);
        dialog.setTitle("Add analysis");
    }

    public void actionPerformed(ActionEvent e) {
        asp.init();
        dialog.setVisible(true);
        if (dialog.isApproved()){
            AnalysisInterface ai = asp.getAnalysis();
            int i = table.getWrappedModel().addAnalysis(ai);
            table.setCellEditors();
        }
    }
    
}
