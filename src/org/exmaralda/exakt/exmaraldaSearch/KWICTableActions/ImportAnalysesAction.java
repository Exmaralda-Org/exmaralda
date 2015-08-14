/*
 * WordWiseReversedSortAction.java
 *
 * Created on 19. Februar 2007, 13:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import org.jdom.JDOMException;
import org.exmaralda.exakt.search.SearchResultList;
import org.exmaralda.exakt.search.analyses.AnalysisInterface;
import org.exmaralda.exakt.exmaraldaSearch.*;
import org.exmaralda.exakt.exmaraldaSearch.swing.*;

/**
 *
 * @author thomas
 */
public class ImportAnalysesAction extends AbstractKWICTableAction {
        
    
    /** Creates a new instance of WordWiseReversedSortAction */
    public ImportAnalysesAction(COMAKWICTable t, String title) {
        super(t,title);
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose a concordance");
        fileChooser.setFileFilter(new org.exmaralda.exakt.utilities.XMLFileFilter());
        
        int retValue = fileChooser.showOpenDialog((JFrame)(table.getTopLevelAncestor()));
        if (retValue==fileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            try {
                SearchResultList templateConcordance = new SearchResultList();
                templateConcordance.read(file);
                Vector<AnalysisInterface> analyses = templateConcordance.getAnalyses();
                for (AnalysisInterface ai : analyses){
                    table.getWrappedModel().addAnalysis(ai);
                    table.setCellEditors();                    
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JDOMException ex) {
                ex.printStackTrace();
            }
            
        }
    }

    
}
