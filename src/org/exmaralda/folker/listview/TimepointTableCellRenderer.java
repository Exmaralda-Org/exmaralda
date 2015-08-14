package org.exmaralda.folker.listview;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.exmaralda.folker.data.Timepoint;
/*
 * TimeCellRenderer.java
 *
 * Created on 14. Maerz 2008, 16:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author thomas
 */
public class TimepointTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
    
    Timepoint currentStartpoint;
    Timepoint currentEndpoint;
    
    double currentLeftmostVisibleTime;
    double currentRightmostVisibleTime;
    
    /** Creates a new instance of TimeCellRenderer */
    public TimepointTableCellRenderer() {
    }
    
    public void setSelectedTimepoints(Timepoint start, Timepoint end){
        currentStartpoint = start;
        currentEndpoint = end;
    }
    
    public void setVisibleTimes(double left, double right){
        currentLeftmostVisibleTime = left;
        currentRightmostVisibleTime = right;
    }
    
    public void removeSelectedTimepoints(){
        currentStartpoint = null;
        currentEndpoint = null;        
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component retValue;
        
        String formatted = "";
        if (value!=null){
            Timepoint tp = (Timepoint)value;
            Double miliseconds = new Double(tp.getTime());
            formatted = org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(miliseconds,2);
        }
        retValue = super.getTableCellRendererComponent(table, formatted, isSelected, hasFocus, row, column);
        if (value==currentStartpoint){
            retValue.setForeground(java.awt.Color.GREEN);
        } else if (value==currentEndpoint){
            retValue.setForeground(java.awt.Color.RED);
        }/* else if ((value!=null) && 
                ((((Timepoint)value).getTime()<this.currentLeftmostVisibleTime) ||
                ((((Timepoint)value).getTime()>this.currentRightmostVisibleTime)))){
            retValue.setForeground(java.awt.Color.GRAY);                        
        }*/ else {
            retValue.setForeground(java.awt.Color.BLUE);            
        }
        ((JLabel)(retValue)).setVerticalAlignment(SwingConstants.TOP);
        return retValue;
    }

    
}
