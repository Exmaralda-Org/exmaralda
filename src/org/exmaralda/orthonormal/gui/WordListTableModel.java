/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.gui;

import java.util.List;
import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class WordListTableModel extends javax.swing.table.AbstractTableModel {

    List<Element> words;

    public WordListTableModel(List<Element> words) {
        this.words = words;
    }

    @Override
    public int getRowCount() {
        return words.size();
    }

    @Override
    public int getColumnCount() {
        if (words.size()>0 && words.get(0).getAttribute("pos")!=null){
            return 5;
        } else {
            return 2;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Element word = words.get(rowIndex);
        return word;
    }

    @Override
    public String getColumnName(int column) {
        if (column==0){
            return "Wort";
        } else if (column==1) {
            return "Normal";
        } else if (column==2) {
            return "Lemma";
        } else if (column==3) {
            return "POS";
        } else if (column==4) {
            return "p(POS)";
        }
        return "";
    }

    public void updateWord(Element word){
        int row = words.indexOf(word);
        fireTableRowsUpdated(row, row);
    }

}
