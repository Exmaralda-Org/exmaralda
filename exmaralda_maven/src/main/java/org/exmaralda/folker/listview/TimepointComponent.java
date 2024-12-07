/*
 * TimepointComponent.java
 *
 * Created on 2. Juli 2008, 14:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.listview;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import org.exmaralda.folker.data.Timepoint;
import org.exmaralda.folker.utilities.TimeStringFormatter;

/**
 *
 * @author thomas
 */
public class TimepointComponent extends JButton implements MouseWheelListener, MouseListener {
    
    Timepoint timepoint;
    ContributionTextPane contributionTextPane;
    
    /** Creates a new instance of TimepointComponent */
    public TimepointComponent(Timepoint tp, ContributionTextPane ctp) {
        timepoint = tp;
        contributionTextPane = ctp;
        setToolTipText(TimeStringFormatter.formatMiliseconds(tp.getTime(),2));
        setText(TimeStringFormatter.formatMiliseconds(tp.getTime(),2));
        //setText(org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(tp.getTime(),2));
        //setText("");
        setFont(getFont().deriveFont(9.0f));
        setForeground(Color.DARK_GRAY);    
        setMinimumSize(new java.awt.Dimension(20,20));
        setPreferredSize(new java.awt.Dimension(80,20));
        setMaximumSize(new java.awt.Dimension(80,20));
        /*addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPressed(evt);
            }
        });*/        
        addMouseListener(this);
        addMouseWheelListener(this);
        //setBorder(new javax.swing.border.LineBorder(Color.BLUE,1,true));        
    }

    void buttonPressed(java.awt.event.ActionEvent evt){        
        contributionTextPane.fireTimepointPressed(timepoint);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int delta = e.getWheelRotation();
        double newTime = timepoint.getTime() + delta*10.0;
        Timepoint tpBefore = timepoint.getTimeline().getPreviousTimepoint(timepoint);
        double timeBefore = 0.0;
        if (tpBefore!=null){ 
            timeBefore = tpBefore.getTime();
        }
        Timepoint tpAfter = timepoint.getTimeline().getNextTimepoint(timepoint);
        double timeAfter = timepoint.getTime();
        if (tpAfter!=null){
            timeAfter = timepoint.getTimeline().getNextTimepoint(timepoint).getTime();            
        }
        
        if ((newTime>timeBefore+10) && (newTime<timeAfter-10)){
            timepoint.setTime(newTime);
            setText(TimeStringFormatter.formatMiliseconds(newTime, 2));
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        contributionTextPane.fireTimepointPressed(timepoint, e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    
    
}
