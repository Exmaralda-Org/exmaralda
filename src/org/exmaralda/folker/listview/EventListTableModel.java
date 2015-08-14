/*
 * EventListTableModel.java
 *
 * Created on 20. Maerz 2008, 15:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.listview;

import org.exmaralda.folker.data.Event;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.Speaker;
import org.exmaralda.folker.data.Timepoint;

/**
 *
 * @author thomas
 */
public class EventListTableModel extends AbstractListTranscriptionTableModel {
    
    /** Creates a new instance of EventListTableModel */
    public EventListTableModel(EventListTranscription elt) {
        super(elt);
    }

    @Override
    public int getRowCount() {
        return eventListTranscription.getNumberOfEvents() + ADDITIONAL_ROWS;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex>=eventListTranscription.getNumberOfEvents()) return null;
        Event event = eventListTranscription.getEventAt(rowIndex);
        switch (columnIndex) {
            case 0 : return rowIndex +1;
            case 1 : return event.getStartpoint();
            case 2 : return event.getEndpoint();
            case 3 : return event.getSpeaker();
            case 4 : return event.getText();
            case 5 : return ((event.getText().length()>0) && (event.getText().matches(getCheckRegex())));  // changed 04-08-2014
            case 6 : return !(eventListTranscription.selfOverlaps(event));
        }
        return null;
    }

    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (rowIndex>=eventListTranscription.getNumberOfEvents()) return false;
        return ((columnIndex==3) || (columnIndex==4));
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Event event = eventListTranscription.getEventAt(rowIndex);
        switch (columnIndex) {
            case 3 : event.setSpeaker((Speaker)aValue);
                    for (int row=0; row<getRowCount(); row++){
                        fireTableCellUpdated(row,6);
                    }
                     break;
            case 4 : event.setText((String)aValue);
                     this.fireTableCellUpdated(rowIndex, columnIndex+1);
                     break;
        }                
        this.fireTableCellUpdated(rowIndex, columnIndex);
        DOCUMENT_CHANGED = true;
    }
    
    public int addEvent(double start, double end){
        int insertedRow = eventListTranscription.addEvent(start, end);
        fireTableRowsInserted(insertedRow, insertedRow);
        return insertedRow;
    }
    
    public void fireSpeakersChanged(){
        for (int row=0; row<getRowCount(); row++){
            fireTableCellUpdated(row,3);
            fireTableCellUpdated(row,6);
        }
    }
    
    public Timepoint getMaxTimepoint(int[] indices){
        Timepoint maxTimepoint = getTranscription().getEventAt(indices[0]).getEndpoint();
        double maxTime = maxTimepoint.getTime();            
        for (int row : indices){
            if (row>=this.getTranscription().getNumberOfEvents()) continue;
            org.exmaralda.folker.data.Event event = getTranscription().getEventAt(row);
            maxTime = Math.max(maxTime, event.getEndpoint().getTime());
        }
        maxTimepoint = getTranscription().getTimeline().findTimepoint(maxTime);        
        return maxTimepoint;
    }

    
    
}
