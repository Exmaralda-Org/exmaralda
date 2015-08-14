/*
 * OpenTextAreaButton.java
 *
 * Created on 8. Februar 2002, 13:12
 */

package org.exmaralda.partitureditor.udInformationTable;

import com.klg.jclass.cell.*;
/**
 *
 * @author  Thomas
 * @version 
 */
public class OpenTextAreaButton extends javax.swing.JButton implements JCComponentCellRenderer {

    int number;

    /** Creates new OpenTextAreaButton */
    public OpenTextAreaButton(int n) {
        number = n;
        this.setText("...");               
        this.setSize(new java.awt.Dimension(20,1));
    }
    
    public int getNumber(){
        return number;
    }
        
    public java.awt.Component getRendererComponent(com.klg.jclass.cell.JCCellInfo jCCellInfo, java.lang.Object obj, boolean param) {
        return this;        
    }    

}
