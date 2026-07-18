/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.orthonormal.gui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import org.exmaralda.folker.utilities.TimeStringFormatter;
import org.exmaralda.orthonormal.application.ApplicationControl;

/**
 *
 * @author bernd
 */
public class PlayLabel extends JLabel implements MouseListener {
    
    ApplicationControl applicationControl;
    double startTime;
    
    public PlayLabel(double time, ApplicationControl ac){
        applicationControl = ac;
        startTime = time;

        setText("\u25B7");
        setFont(getFont().deriveFont(16.0f));

        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));          
        setBackground(WordListTableCellRenderer.selectedBgColor);
        setForeground(Color.WHITE);
        
        setToolTipText("Play from " + TimeStringFormatter.formatMiliseconds(time));
        
        addMouseListener(this);
        
        
        
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        applicationControl.playFromeTime(startTime);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setForeground(WordListTableCellRenderer.selectedBgColor);
        setBackground(Color.WHITE);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setBackground(WordListTableCellRenderer.selectedBgColor);
        setForeground(Color.WHITE);
    }
    
}
