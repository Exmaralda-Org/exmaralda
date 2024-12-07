/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices.swing;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 *
 * @author thomas.schmidt
 */
public class WebLichtChainListCellRenderer extends BasicComboBoxRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        ChainDefinition chainDefinition = (ChainDefinition)value;
        String stringValue = chainDefinition.getName() + " (" + chainDefinition.getLanguage() + ")";
        super.getListCellRendererComponent(list, stringValue, index, isSelected, cellHasFocus);
        setText(stringValue);
        return this;
        //return new JLabel(stringValue);
    }
    
}
