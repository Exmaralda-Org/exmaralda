/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaraldaswing;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;

// new for #382


/**
 *
 * @author bernd
 */
public class ListEventsTableModel extends AbstractTableModel {

    BasicTranscription transcription;
    Tier tier;
    int row;
    
    JTable theTable;

    public ListEventsTableModel(BasicTranscription transcription, Tier tier, int row) {
        this.transcription = transcription;
        this.tier = tier;
        this.row = row;
    }
    
    
    @Override
    public int getRowCount() {
        return tier.getNumberOfEvents();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            Event event = tier.getEventAt(row);
            switch(column){
                case 0 : return theTable.getRowSorter().convertRowIndexToView(row) + 1;
                case 1 : return transcription.getBody().getCommonTimeline().getTimelineItemWithID(event.getStart()).getTime();
                case 2 : return transcription.getBody().getCommonTimeline().getTimelineItemWithID(event.getEnd()).getTime();
                case 3 : 
                    double duration = transcription.getBody().getCommonTimeline().getTimelineItemWithID(event.getEnd()).getTime()
                            - transcription.getBody().getCommonTimeline().getTimelineItemWithID(event.getStart()).getTime();
                    return duration;
                case 4 : return event.getDescription();
            }
        } catch (JexmaraldaException ex) {
            Logger.getLogger(ListEventsTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "-";
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        switch(columnIndex){
            case 0 : return "";
            case 1 : return "Start";
            case 2 : return "End";
            case 3 : return "Length";
            case 4 : return "Description";
        }
        return null;
    }
     
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (tier.getNumberOfEvents()==0) {
            return Object.class;
        }
        switch(columnIndex){
            case 0 : return Integer.class;
            case 1 : return Double.class;
            case 2 : return Double.class;
            case 3 : return Double.class;
            case 4 : return String.class;
        }
        return Object.class;
    }    

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex==4); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String newDescription = (String)aValue;
        tier.getEventAt(rowIndex).setDescription(newDescription);
        this.fireTableCellUpdated(rowIndex, columnIndex);
    }
    
}
