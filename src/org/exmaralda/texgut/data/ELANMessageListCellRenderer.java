/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.texgut.data;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 * @author bernd
 */
public class ELANMessageListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component listCellRendererComponent = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        if (!isSelected){
            listCellRendererComponent.setBackground(Color.black);
        }
        ELANMessage message = (ELANMessage)value;
        switch (message.type){
            case MESSAGE : listCellRendererComponent.setForeground(Color.GREEN); break;
            case WARNING : listCellRendererComponent.setForeground(Color.ORANGE);  break;
            case ERROR : listCellRendererComponent.setForeground(Color.RED);  break;
            case FORMAT : listCellRendererComponent.setForeground(Color.lightGray);  break;
        }
        ((JLabel)listCellRendererComponent).setText(message.description);
        if (message.file!=null){
            ((JLabel)listCellRendererComponent).setToolTipText(message.file.getAbsolutePath());            
        }
        return listCellRendererComponent;
    }
    
}
