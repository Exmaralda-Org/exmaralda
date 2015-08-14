/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.folker.gui;

import javax.swing.table.AbstractTableModel;
import org.jdom.Element;

/**
 *
 * @author Schmidt
 */
public class KeyTableModel extends AbstractTableModel {

    Element keyElement;

    public KeyTableModel(Element keyElement) {
        this.keyElement = keyElement;
    }
    
    
    
    @Override
    public int getRowCount() {
        return keyElement.getChildren("key-entry").size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Element keyEntry = (Element) keyElement.getChildren("key-entry").get(rowIndex);
        if (columnIndex==0){
            return keyEntry.getAttributeValue("real");
        }
        return keyEntry.getAttributeValue("mask");
    }
    
    @Override
    public String getColumnName(int column) {
        switch (column){
            case 0 : return "Realname";
            case 1 : return "Maskenname";
            default : return "Eintrag";
        }
    }
    
    
    public void addKey(String realName, String maskName){
        Element keyEntry = new Element("key-entry");
        keyEntry.setAttribute("real", realName);
        keyEntry.setAttribute("mask", maskName);
        keyElement.addContent(keyEntry);
        fireTableRowsInserted(getRowCount()-1, getRowCount()-1);        
    }
    
    public void removeKey(int index){
        Element keyEntry = (Element) keyElement.getChildren("key-entry").get(index);
        keyEntry.detach();
        fireTableRowsDeleted(index, index);
    }
    
    public void changeKey(int index, String realName, String maskName){
        Element keyEntry = (Element) keyElement.getChildren("key-entry").get(index);
        keyEntry.setAttribute("real", realName);
        keyEntry.setAttribute("mask", maskName);
        fireTableRowsUpdated(index, index);
        
    }
        
    
}
