/*
 * TimelineItemTableCellRenderer.java
 *
 * Created on 15. November 2007, 14:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.partiture;

import com.klg.jclass.cell.JCCellInfo;
import java.awt.Component;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author thomas
 */
public class TimelineItemTableCellRenderer implements com.klg.jclass.cell.JCComponentCellRenderer,
                                                      java.awt.event.MouseWheelListener {
    
    javax.swing.JPanel panel = new javax.swing.JPanel();
    //javax.swing.JButton button = new javax.swing.JButton("Hallo!");
    javax.swing.JLabel label = new javax.swing.JLabel();
    PartitureTableWithActions table;
    
    /** Creates a new instance of TimelineItemTableCellRenderer */
    public TimelineItemTableCellRenderer(PartitureTableWithActions t) {
        panel.add(label);
        label.addMouseWheelListener(this);
        //panel.add(button);
        table = t;
    }

    public Component getRendererComponent(JCCellInfo jCCellInfo, Object object, boolean b) {
        label.setForeground(java.awt.Color.green);
        org.exmaralda.partitureditor.jexmaralda.TimelineItem tli = (org.exmaralda.partitureditor.jexmaralda.TimelineItem)object;
        if (tli.getTime()>=0){
            //label.setText(tli.getTimeAsString());
            // changed 01-12-2008
            String text = org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(tli.getTime()*1000.0, 1);
            label.setText(text);
        } else {
            label.setText("");
        }
        return panel;
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        //System.out.println(e.getScrollAmount());
    }
    
}
