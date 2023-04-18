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
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.exmaralda.exakt.exmaraldaSearch.swing.AddAnnotationDialog;
import org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICSearchPanel;
import org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICTable;
import org.exmaralda.exakt.search.SearchResultList;
import org.xml.sax.SAXException;
/**
 *
 * @author thomas
 */
public class AddAnnotationAction extends AbstractKWICTableAction {
    
    int count=0;
    
    
    /** Creates a new instance of WordWiseReversedSortAction
     * @param t
     * @param title */
    public AddAnnotationAction(COMAKWICTable t, String title) {
        super(t,title);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("*** ADD ANNOTATION ACTION");
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
            table.adjustColumns();
            
            
            
            
            
        } catch (HeadlessException | SAXException ex) {
            System.out.println(ex.getLocalizedMessage());
            JOptionPane.showMessageDialog(table, ex.getLocalizedMessage());
        }
    }
    
}
