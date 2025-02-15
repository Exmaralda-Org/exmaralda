/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.exakt.exmaraldaSearch.swing;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.exmaralda.exakt.search.analyses.AnalysisInterface;

/**
 *
 * @author bernd
 */
public class ColumnSelectionComboBoxModel extends DefaultComboBoxModel {

    public static String[] fixedColumns = {"Left context",  "Match text", "Right context", "Communication", "Speaker"};


    public ColumnSelectionComboBoxModel(COMASearchResultListTableModel m) {
        List<String[]> metaIdentifiers = m.getMetaIdentifiers();
        
        for (String s : fixedColumns){
            addElement(s);
        }
        for (AnalysisInterface ai : m.getData().getAnalyses()){
            addElement(ai.getName());
        }
        for (String[] s : metaIdentifiers){
            addElement(s[1] + "[" + s[0] + "]");
        }
        
        
    }
    
    
    
    
    
}
