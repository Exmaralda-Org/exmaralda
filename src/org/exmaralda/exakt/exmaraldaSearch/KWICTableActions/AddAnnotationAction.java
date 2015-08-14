/*
 * WordWiseReversedSortAction.java
 *
 * Created on 19. Februar 2007, 13:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.exmaralda.exakt.exmaraldaSearch.swing.AddAnnotationDialog;
import org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICTable;
import org.exmaralda.exakt.search.SearchResultList;
/**
 *
 * @author thomas
 */
public class AddAnnotationAction extends AbstractKWICTableAction {
    
    int count=0;
    
    
    /** Creates a new instance of WordWiseReversedSortAction */
    public AddAnnotationAction(COMAKWICTable t, String title) {
        super(t,title);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            HashSet<String> annotationNames = table.getWrappedModel().getCorpus().getAnnotationNames();
            if (annotationNames.size()<=0){
                JOptionPane.showMessageDialog(table, "No annotations in this corpus.");
                return;
            }
            AddAnnotationDialog aad = new AddAnnotationDialog((JFrame)(table.getTopLevelAncestor()), true);
            aad.setCategories(annotationNames);
            aad.setLocationRelativeTo(table);
            aad.setVisible(true);

            table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            SearchResultList srl = table.getWrappedModel().getData();
            // changed 18-10-2011 - bug fix
            SearchResultList newsrl = srl.addAnnotationAsAnalysis(aad.getCategory(), table.getWrappedModel(), aad.getOverlapType());
            //SearchResultList newsrl = srl.addAnnotationAsAnalysis(aad.getCategory(), table.getWrappedModel().getCorpus(), aad.getType());
            table.getWrappedModel().setData(newsrl);
            table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(table, ex.getLocalizedMessage());
        }
    }
    
}
