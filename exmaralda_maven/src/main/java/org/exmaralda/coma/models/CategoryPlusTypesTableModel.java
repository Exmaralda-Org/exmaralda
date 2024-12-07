/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.coma.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import org.exmaralda.common.corpusbuild.CategoryPlusType;

/**
 *
 * @author bernd
 */
public class CategoryPlusTypesTableModel extends AbstractTableModel {

    Map<CategoryPlusType, Integer> categoryPlusTypeMap = new HashMap<>();
    List<CategoryPlusType> keys = new ArrayList<>();

    public CategoryPlusTypesTableModel(Map<CategoryPlusType, Integer> cptm) {
        categoryPlusTypeMap = cptm;
        keys.addAll(cptm.keySet());
    }


    @Override
    public int getRowCount() {
        return keys.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CategoryPlusType cpt = keys.get(rowIndex);
        switch(columnIndex){
            case 0 : return cpt.category;
            case 1 : return cpt.type;
            case 2 : return categoryPlusTypeMap.get(cpt);
        }
        return null;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex){
            case 0 : return String.class;
            case 1 : return String.class;
            case 2 : return Integer.class;
        }
        return Object.class;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0 : return "Category";
            case 1 : return "Type";
            case 2 : return "Count";
        }
        return super.getColumnName(column); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
