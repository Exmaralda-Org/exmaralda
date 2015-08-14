/*
 * EventListTableModel.java
 *
 * Created on 20. Maerz 2008, 15:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.experiment;

import org.exmaralda.folker.experiment.transcription.EventInterface;
import org.exmaralda.folker.experiment.transcription.TierEventInterface;

/**
 *
 * @author thomas
 */
public class EventListTableModel extends javax.swing.table.AbstractTableModel{
    
    public int ADDITIONAL_ROWS = 10;
    
    TierEventInterface tierEventInterface;
    
    /** Creates a new instance of EventListTableModel */
    public EventListTableModel(TierEventInterface tei) {
        tierEventInterface = tei;
    }

    public int getColumnCount() {
        return 5;
    }

    public int getRowCount() {
        return tierEventInterface.getNumberOfEvents() + ADDITIONAL_ROWS;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex>=tierEventInterface.getNumberOfEvents()) return null;
        EventInterface ei = tierEventInterface.getEvent(rowIndex);
        switch (columnIndex) {
            case 0 : return ei.getStartTime() * 1000.0;
            case 1 : return ei.getEndTime() * 1000.0;
            case 2 : return ei.getSpeakerID();
            case 3 : return ei.getDescription();
            case 4 : return new Boolean(ei.getDescription().matches(org.exmaralda.folker.utilities.Constants.GAT_EVENT));
        }
        return null;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (rowIndex>=tierEventInterface.getNumberOfEvents()) return false;
        return ((columnIndex==2) || (columnIndex==3));
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        EventInterface ei = tierEventInterface.getEvent(rowIndex);
        switch (columnIndex) {
            case 2 : ei.setSpeakerID((String)aValue);
                     break;
            case 3 : ei.setDescription((String)aValue);
                     this.fireTableCellUpdated(rowIndex, columnIndex+1);
                     break;
        }                
        this.fireTableCellUpdated(rowIndex, columnIndex);
    }
    
    public int addEvent(EventInterface e){
        int insertedRow = tierEventInterface.addEvent(e);
        fireTableRowsInserted(insertedRow, insertedRow);
        return insertedRow;
    }
    
    
}
