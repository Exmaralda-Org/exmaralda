/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaraldaswing;

import java.util.Vector;
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
public class TabulateEventsTableModel extends AbstractTableModel {

    BasicTranscription transcription;
    Tier mainTier;
    Tier[] dependentTiers;
    
    JTable theTable;

    public TabulateEventsTableModel(BasicTranscription transcription, Tier mainTier, Tier[] dependentTiers) {
        this.transcription = transcription;
        this.mainTier = mainTier;
        this.dependentTiers = dependentTiers;
    }
    
    
    @Override
    public int getRowCount() {
        return mainTier.getNumberOfEvents();
    }

    @Override
    public int getColumnCount() {
        return 5 + dependentTiers.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        try {
            Event event = mainTier.getEventAt(row);
            switch(column){
                case 0 : return theTable.getRowSorter().convertRowIndexToView(row) + 1;
                case 1 : return transcription.getBody().getCommonTimeline().getTimelineItemWithID(event.getStart()).getTime();
                case 2 : return transcription.getBody().getCommonTimeline().getTimelineItemWithID(event.getEnd()).getTime();
                case 3 : 
                    double duration = transcription.getBody().getCommonTimeline().getTimelineItemWithID(event.getEnd()).getTime()
                            - transcription.getBody().getCommonTimeline().getTimelineItemWithID(event.getStart()).getTime();
                    return duration;
                case 4 : return event.getDescription();
                default :
                    Vector<Event> intersectingEvents 
                            = dependentTiers[column-5].getEventsIntersecting(transcription.getBody().getCommonTimeline(), event.getStart(), event.getEnd());
                    String concat = "";
                    for (Event e : intersectingEvents){
                        concat+=e.getDescription() + "/";
                    }
                    if (!intersectingEvents.isEmpty()){
                     concat = concat.substring(0, concat.length()-1);
                    }
                    return concat;
            }
        } catch (JexmaraldaException ex) {
            Logger.getLogger(TabulateEventsTableModel.class.getName()).log(Level.SEVERE, null, ex);
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
            default :
                return dependentTiers[columnIndex-5].getDisplayName();
        }
    }
     
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (mainTier.getNumberOfEvents()==0) {
            return Object.class;
        }
        switch(columnIndex){
            case 0 : return Integer.class;
            case 1 : return Double.class;
            case 2 : return Double.class;
            case 3 : return Double.class;
            case 4 : return String.class;
        }
        return String.class;
    }    

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex==4); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String newDescription = (String)aValue;
        mainTier.getEventAt(rowIndex).setDescription(newDescription);
        this.fireTableCellUpdated(rowIndex, columnIndex);
    }
    
}
