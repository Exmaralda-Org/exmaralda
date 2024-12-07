/*
 * AbstractListTranscriptionTableModel.java
 *
 * Created on 23. Juni 2008, 14:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.listview;

import org.exmaralda.folker.data.EventListTranscription;

/**
 *
 * @author thomas
 */
public abstract class AbstractListTranscriptionTableModel extends javax.swing.table.AbstractTableModel{
    
    protected EventListTranscription eventListTranscription;
    public int ADDITIONAL_ROWS = 10;

    public boolean DOCUMENT_CHANGED = false;

    public String checkRegex = ".*";


    /** Creates a new instance of AbstractListTranscriptionTableModel */
    public AbstractListTranscriptionTableModel(EventListTranscription elt) {
        eventListTranscription = elt;
    }
    
    public String getCheckRegex() {
        return checkRegex;
    }

    public void setCheckRegex(String checkRegex) {
        this.checkRegex = checkRegex;
    }

    public void fireSelectionChanged(){
        for (int row = 0; row<getRowCount(); row++){
            fireTableCellUpdated(row,1);
            fireTableCellUpdated(row,2);
            // ADDED 08-06-2011: update the time check cell
            fireTableCellUpdated(row, getColumnCount()-1);
        }
    }

   
    public int getColumnCount() {
        return 7;
    }


    public EventListTranscription getTranscription(){
        return eventListTranscription;
    }

    public void reorderTimeline() {
        getTranscription().getTimeline().reorder();
        fireSelectionChanged();
    }
      
    
}
