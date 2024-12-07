/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices.swing;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Thomas_Schmidt
 */
public class MAUSLanguagesComboBoxRenderer extends DefaultListCellRenderer {

    @Override
    //public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String myValue = ((String[])value)[1] + " (" + ((String[])value)[0] + ")";
        return super.getListCellRendererComponent(list, myValue, index, isSelected, cellHasFocus); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
