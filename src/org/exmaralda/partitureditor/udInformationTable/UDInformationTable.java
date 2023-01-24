/*
 * UDInformationTable.java
 *
 * Created on 8. Februar 2002, 12:17
 */

package org.exmaralda.partitureditor.udInformationTable;

import java.awt.Component;
import static java.awt.Font.BOLD;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
/**
 *
 * @author  Thomas
 * @version 
 */
public class UDInformationTable extends JTable implements TableModelListener {
       
    private UDInformationTableModel tableModel;
    private OpenTextAreaButton[] buttons;
    
    /** Creates new UDInformationTable */
    public UDInformationTable() {
        tableModel = new UDInformationTableModel();
        setModel(tableModel);
        tableModel.addTableModelListener(this);
        setRowHeight(getRowHeight() + 4);
        this.getColumnModel().getColumn(0).setCellRenderer(new UDInformationTableCellRenderer());
    }

    /** Creates new UDInformationTable
     * @param udi */
    public UDInformationTable(org.exmaralda.partitureditor.jexmaralda.UDInformationHashtable udi) {
        tableModel = new UDInformationTableModel(udi);
        setModel(tableModel);
        tableModel.addTableModelListener(this);
    }
    
    public org.exmaralda.partitureditor.jexmaralda.UDInformationHashtable getUDInformation(){
        return tableModel.getUDInformation();
    }
    
    public void setUDInformation(org.exmaralda.partitureditor.jexmaralda.UDInformationHashtable info){
        commitCellEdit();
        tableModel.setUDInformation(info);                
    }
    
    public void addAttribute(){
        commitCellEdit();
        tableModel.addAttribute();
        this.changeSelection(getRowCount()-1, 0, false, false);
        this.editCellAt(getRowCount()-1, 0);
        
    }
    
    public void removeAttribute(){        
        if (getSelectedRow()>-1){
            commitCellEdit();
            int remind = getSelectedRow();
            tableModel.removeAttribute(getSelectedRow());
            if (this.getRowCount()>remind){
                this.changeSelection(Math.max(0,remind), 0, false, false);
            } else if (getRowCount()>0){
                this.changeSelection(getRowCount()-1,0, false, false);
            }
        }
    }
    
    public void moveAttributeUp(){
        if (getSelectedRow()>0){            
            commitCellEdit();
            int remind = getSelectedRow();
            tableModel.moveAttributeUp(getSelectedRow());
            this.changeSelection(remind-1, 0, false, false);
        }
    }

    public void moveAttributeDown(){
        if ((getSelectedRow()>-1) && (getSelectedRow()<this.getRowCount()-1)){
            commitCellEdit();
            int remind = getSelectedRow();
            tableModel.moveAttributeUp(getSelectedRow()+1);
            this.changeSelection(remind+1, 0, false, false);
        }
    }
    
    public void editValueInTextField(){
        if (getSelectedRow()>-1){
            int row = getSelectedRow();
            LargeTextDialog dialog = new LargeTextDialog((javax.swing.JDialog)this.getTopLevelAncestor(), true);
            if (dialog.editText((String)tableModel.getValueAt(row,0), (String)tableModel.getValueAt(row,1))){
                tableModel.setValueAt(dialog.getText(), row, 1);               
            }
        }
    }
    
    public void commitCellEdit(){
        if (this.isEditing()){
            this.getCellEditor().stopCellEditing();
        }
    }

    private static class UDInformationTableCellRenderer extends DefaultTableCellRenderer {

        public UDInformationTableCellRenderer() {
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column)
        {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setFont(c.getFont().deriveFont(BOLD)); 

            return c;
        }        
    }

            
    
}
