/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaraldaswing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
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
        JLabel mediaTypeLabel = (JLabel) dflcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        //mediaTypeLabel.setText(file.getName());
        if (path.startsWith("---")){            
            mediaTypeLabel.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/status/dialog-error.png")));
        } else if (file.getName().toUpperCase().matches("^.*\\.(MP3|WAV|OGG|WMA|AIF)$")){
            mediaTypeLabel.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/mimetypes/audio-x-generic.png")));
        } else if (file.getName().toUpperCase().matches("^.*\\.(MPG|MP4|AVI|MOV|WEBM|DIVX|WMV)$")){
            mediaTypeLabel.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/mimetypes/video-x-generic.png")));            
        } else {
            mediaTypeLabel.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/status/image-missing.png")));            
        }
        
        if (file.exists()){
            //mediaTypeLabel.setForeground(Color.black);
            mediaTypeLabel.setToolTipText(path);            
        } else {
            //mediaTypeLabel.setForeground(Color.red);
            mediaTypeLabel.setToolTipText(path + " does not exist.");            
        }
        // BOLD FOR RECOMMENDED FILE TYPES
        // added MP4 for issue #219
        if (file.getName().toUpperCase().matches("^.*\\.(WAV|MPG|MP4)$")){
            //mediaTypeLabel.setFont(mediaTypeLabel.getFont().deriveFont(Font.BOLD));
        }

        String html = "<html>";
        boolean fileExists = file.exists();
        boolean isRecommended = file.getName().toUpperCase().matches("^.*\\.(WAV|MPG|MP4)$");
        if (isRecommended) html+="<b>";
        html+="<span style=\"font-size:14pt;";
        if (fileExists) {
            html+="color:black";
        } else {
            html+="color:red";
        }
        html+="\">";
        html+=file.getName();
        String color = "gray";
        if (isSelected) color="lightGray";
        html+="</span>";
        if (isRecommended) html+="</b>";        
        html+="<br/><span style=\"color:" + color + "; font-size:10pt;\">";
        html+=file.getAbsolutePath().substring(Math.max(0, file.getAbsolutePath().length()-60));
        html+="</span></html>";
        
        mediaTypeLabel.setText(html);
        
        


        return mediaTypeLabel;
        
    }
    
}
