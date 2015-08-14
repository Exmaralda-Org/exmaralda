/*
 * UDInformationTableModel.java
 *
 * Created on 8. Februar 2002, 12:46
 */

package org.exmaralda.partitureditor.udInformationTable;

import org.exmaralda.partitureditor.jexmaralda.UDInformationHashtable;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class UDInformationTableModel extends AbstractTableModel {

    private UDInformationHashtable info;
    
    /** Creates new UDInformationTableModel */
    public UDInformationTableModel() {
        super();
        info = new UDInformationHashtable();
    }

    /** Creates new UDInformationTableModel */
    public UDInformationTableModel(org.exmaralda.partitureditor.jexmaralda.UDInformationHashtable udi) {
        info = udi;
    }
    
    public org.exmaralda.partitureditor.jexmaralda.UDInformationHashtable getUDInformation(){
        return info;
    }
    
    public void setUDInformation(org.exmaralda.partitureditor.jexmaralda.UDInformationHashtable udi){
        info = udi;
        fireTableDataChanged();
    }
    
    public void addAttribute(){
        int n = 1;
        while(info.containsAttribute("New" + Integer.toString(n))){n++;}
        info.setAttribute("New" + Integer.toString(n),"");
        fireTableRowsInserted(getRowCount(),getRowCount());
        this.fireTableCellUpdated(getRowCount()-1,0);
        this.fireTableCellUpdated(getRowCount()-1,1);
    }
            
    public void removeAttribute(int row){
        String attName = (String)(info.elementAt(row));
        info.removeAttribute(attName);
        this.fireTableRowsDeleted(row,row);
        fireTableDataChanged();
    }
    
    public void moveAttributeUp(int row){
        info.moveElementUp(row);
        fireTableDataChanged();
    }
    
        
    public void setValueAt(java.lang.Object obj, int row, int col) {
        String newValue = (String)obj;
        if (col==0){
            info.renameAttribute(row,newValue);
        }
        if (col==1){
            info.setAttribute((String)info.elementAt(row),newValue);
        }
        fireTableCellUpdated(row,col);
    }
    
    
    public java.lang.String getColumnName(int col) {
        if (col==0) return "Attribute";
        return "Value";        
    }
    
    
    public boolean isCellEditable(int row, int col) {
        return true;
    }
    
    public java.lang.Object getValueAt(int row, int col) {
        if (col==0){
            return (String)(info.elementAt(row));
        }
        else if (col==1){
            return info.getValueOfAttribute((String)info.elementAt(row));
        }
        return null;        
    }
    
    public int getRowCount() {
        return info.getNumberOfAttributes();        
    }
    
    public int getColumnCount() {
        return 2;
    }
    
    public java.lang.Class getColumnClass(int param) {
        try {
            return Class.forName("java.lang.String");
        } catch (Throwable t) {
            return null;
        }
    }
    
}
