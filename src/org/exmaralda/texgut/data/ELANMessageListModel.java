/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.texgut.data;

import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author bernd
 */
public class ELANMessageListModel extends AbstractListModel {

    public ELANMessageListModel(List<ELANMessage> messages) {
        this.messages = messages;
    }

    List<ELANMessage> messages;
    
    
    
    @Override
    public int getSize() {
        return messages.size();
    }

    @Override
    public Object getElementAt(int index) {
        return messages.get(index);
    }

    public void addMessage(ELANMessage message) {
        messages.add(message);
        int newSize = messages.size();
        this.fireIntervalAdded(message, newSize-1, newSize-1);
    }
    
}
