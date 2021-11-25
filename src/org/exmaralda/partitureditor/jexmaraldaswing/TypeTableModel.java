/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaraldaswing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author thomas.schmidt
 */
// issue #295
public class TypeTableModel extends AbstractTableModel {

    
    Map<String,Integer> types;
    Map<String, String> mappings;
    List<String> typesList;
    
    public TypeTableModel(Map<String,Integer> types) {
        this.types = types;
        typesList = new ArrayList<>();
        types.keySet().forEach(type -> {
            typesList.add(type);
        });
        Collections.sort(typesList);
        types.keySet().forEach(type -> {
            mappings.put(type, type);
        });
        
    }

    
    
    @Override
    public int getRowCount() {
        return typesList.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch(col){
            case 0 : return typesList.get(row);
            case 1 : return types.get(typesList.get(row));
            case 2 : return mappings.get(typesList.get(row));
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex); //To change body of generated methods, choose Tools | Templates.
        if (columnIndex==2){
            mappings.put(typesList.get(rowIndex), (String) aValue);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex==2);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex){
            case 0 :
            case 2 : return String.class;
            case 1 : return Integer.class;
        }
        return Object.class;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0 : return "Type";
            case 1 : return "Frequency";
            case 2 : return "Map to";
        }
        return super.getColumnName(column); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
    
}
