/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.wizard.folkercorpuswizard;

import java.io.File;
import java.util.Collections;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author thomas
 */
public class FileSelectionTableModel extends AbstractTableModel {

    Vector<File> files;
    Vector<Boolean> selections;

    public FileSelectionTableModel(Vector<File> files) {
        this.files = files;
        selections = new Vector<Boolean>();
        for (File f : files){
            Boolean truth = new Boolean(true);
            selections.addElement(truth);

        }
    }

    public int getRowCount() {
        return files.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex){
            case 0 : return selections.elementAt(rowIndex);
            case 1 : return files.elementAt(rowIndex);
        }
        return null;
    }

    int countSelected() {
        int count = 0;
        for (Boolean b : selections){
            if (b.booleanValue()){
                count++;
            }
        }
        return count;
    }

    Vector<File> getSelected() {
        Vector<File> result = new Vector<File>();
        for (int pos=0; pos<selections.size(); pos++){
            if (selections.elementAt(pos).booleanValue()){
                result.add(files.elementAt(pos));
            }
        }
        return result;
    }

    void toggleSelection(int index) {
        boolean s = selections.elementAt(index).booleanValue();
        Boolean newValue = new Boolean(!s);
        selections.setElementAt(newValue, index);
        this.fireTableCellUpdated(index, 0);
    }

}
