/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaraldaswing;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Schmidt
 */
public class RecordingsListCellRenderer implements ListCellRenderer {

    DefaultListCellRenderer dflcr = new DefaultListCellRenderer();
    
    public RecordingsListCellRenderer() {}

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String path = null;
        File file = null;
        if (value instanceof File){
            file = (File)value;
            path = file.getAbsolutePath();
        } else {
            path = (String) value;
            file = new File(path);
        }
        //JLabel label = new JLabel();
        JLabel label = (JLabel) dflcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setText(file.getName());
        if (path.startsWith("---")){            
            label.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/status/dialog-error.png")));
        } else if (file.getName().toUpperCase().matches("^.*\\.(MP3|WAV|OGG|WMA|AIF)$")){
            label.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/mimetypes/audio-x-generic.png")));
        } else {
            label.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/mimetypes/video-x-generic.png")));            
        }
        if (file.exists()){
            label.setForeground(Color.black);
            label.setToolTipText(path);            
        } else {
            label.setForeground(Color.red);
            label.setToolTipText(path + " does not exist.");            
        }
        label.setFont(label.getFont().deriveFont(14.0f));
        return label;
        
    }
    
}
