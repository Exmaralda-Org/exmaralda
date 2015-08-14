/*
 * UDInformationTableModel.java
 *
 * Created on 3. August 2001, 16:21
 */

package org.exmaralda.partitureditor.jexmaraldaswing;

import java.util.*;
/**
 *
 * @author  Thomas
 * @version 
 */
public class UDInformationTableModel extends javax.swing.table.DefaultTableModel {

    boolean[] canEdit = new boolean [] {false, true};
                      
    /** Creates new UDInformationTableModel */
    public UDInformationTableModel(org.exmaralda.partitureditor.jexmaralda.UDInformationHashtable udInfo) {
        super(new Object[][] {}, new String[] {"Attributes", "Values"});
        String[] allAttributes = udInfo.getAllAttributes();
        if (allAttributes!=null){
            for (int i=0; i<allAttributes.length; i++){
                Object[] newRow = new Object [] {allAttributes[i],
                udInfo.getValueOfAttribute(allAttributes[i])};
                addRow(newRow);
            }
        }
    }
    
   public boolean isCellEditable (int rowIndex, int columnIndex) {
     return canEdit [columnIndex];
   }

   public org.exmaralda.partitureditor.jexmaralda.UDInformationHashtable getUDInformation(){
        org.exmaralda.partitureditor.jexmaralda.UDInformationHashtable result = new org.exmaralda.partitureditor.jexmaralda.UDInformationHashtable();
        for (int row=0; row<getRowCount(); row++){
            result.setAttribute((String)getValueAt(row,0),(String)getValueAt(row,1));
        }
        return result;
   }

}