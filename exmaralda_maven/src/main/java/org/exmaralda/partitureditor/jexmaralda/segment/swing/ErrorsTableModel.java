/*
 * ErrorsTableModel.java
 *
 * Created on 15. Februar 2005, 10:40
 */

package org.exmaralda.partitureditor.jexmaralda.segment.swing;

import java.util.*;
import org.exmaralda.partitureditor.fsm.FSMException;

/**
 *
 * @author  thomas
 */
public class ErrorsTableModel extends javax.swing.table.AbstractTableModel {
    
    Vector errors;
    
    /** Creates a new instance of ErrorsTableModel */
    public ErrorsTableModel(Vector e) {
        errors = e;        
    }
    
    public void setErrors(Vector e){
        errors = e;
        this.fireTableDataChanged();
    }
    
    public int getColumnCount() {
        return 4;
    }    
    
    public int getRowCount() {
        return errors.size();
    }
    
    public FSMException getFSMExceptionAt(int row){
        return (FSMException)(errors.elementAt(row));
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        FSMException fsme = (FSMException)(errors.elementAt(rowIndex));
        switch (columnIndex){
            case 0 : return fsme.getTierID();
            case 1 : return fsme.getTLI();
            case 2 : return fsme.getMessage();
            case 3 : return StringUtilities.stripXMLElements(fsme.getProcessedOutput());            
        }
        return null;
    }
    
    public String getColumnName(int column){
        switch (column){
            case 0  : return "Tier";
            case 1  : return "TLI";
            case 2  : return "Error";
            case 3  : return "Processed Output";
        }
        return null;
    }
    
    
}
