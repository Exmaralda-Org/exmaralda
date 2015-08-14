/*
 * SearchHistoryComboBox.java
 *
 * Created on 16. Januar 2007, 11:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search.swing;

import javax.swing.*;

import org.exmaralda.exakt.search.*;


/**
 *
 * @author thomas
 */
public class SearchHistoryComboBox extends javax.swing.JComboBox{
    
    /** Creates a new instance of SearchHistoryComboBox */
    public SearchHistoryComboBox(SearchHistory searchHistory) {
        String[] empty = {"", "?"};
        //searchHistory.add(0, empty);
        DefaultComboBoxModel model = new DefaultComboBoxModel(searchHistory);
        setModel(model);
        setRenderer(new SearchHistoryListCellRenderer());
        setEditable(true);
        setEditor(new RegularExpressionTextField());
    }
    
}
