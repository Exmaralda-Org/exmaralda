/*
 * SpeakerListCellRenderer.java
 *
 * Created on 15. Mai 2008, 17:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.listview;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.*;
import org.exmaralda.folker.data.Speaker;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;

/**
 *
 * @author thomas
 */
public class SpeakerListCellRenderer extends javax.swing.DefaultListCellRenderer {
    
    boolean includeName = false;
    
    /** Creates a new instance of SpeakerListCellRenderer */
    public SpeakerListCellRenderer(boolean in) {
        includeName = in;
    }
    
    public SpeakerListCellRenderer(){
        this(false);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component retValue;
        
        String text = "";
        
        if (value!=null){
            Speaker s = (Speaker)value;                
            text = s.getIdentifier();
            if ((s.getName()!=null) && (s.getName().length()>0) && includeName){
                text+=" [" + s.getName() + "]";
            }
        } else {
            text = "---";
        }
        
        retValue = super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
        retValue.setFont(retValue.getFont().deriveFont(java.awt.Font.BOLD));
        if ((value!=null) && (!includeName)){
            Speaker s = (Speaker)value;
            ((JLabel)(retValue)).setToolTipText(s.getName());
        } else {
            ((JLabel)(retValue)).setToolTipText(FOLKERInternationalizer.getString("misc.nospeaker"));

        }
        return retValue;
    }
    
}
