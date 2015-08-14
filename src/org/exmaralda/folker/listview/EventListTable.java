/*
 * EventListTableView.java
 *
 * Created on 7. Mai 2008, 16:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.listview;

import java.awt.Rectangle;
import javax.swing.ListSelectionModel;
import javax.swing.table.*;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.Timepoint;

/**
 *
 * @author thomas
 */
public class EventListTable extends AbstractListTranscriptionTable  {
   
    public EventTextCellEditor eventTextCellEditor;
    //public EventTextCellRenderer eventTextCellRenderer;
    
    /** Creates a new instance of EventListTableView */
    public EventListTable() {
        
        setGridColor(new java.awt.Color(102, 204, 255));
        setIntercellSpacing(new java.awt.Dimension(0, 0));
        setShowHorizontalLines(false);        
        setRowHeight(22);
        setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        
    }

    /*@Override
    public void setFont(String fontName) {
        super.setFont(fontName);
        if (mainFont!=null){
            eventTextCellRenderer.setFont(mainFont);
            eventTextCellEditor.setFont(mainFont);
            System.out.println(">>>>>> Been there, done that.");
        }
        reformat();
    }*/


       
    @Override
    void reformat(){
        
        super.reformat();
        
        TableColumnModel cmodel = getColumnModel();
        
        SpeakerTableCellRenderer speakerCellRenderer = new SpeakerTableCellRenderer(true);
        cmodel.getColumn(3).setCellRenderer(speakerCellRenderer);

        //eventTextCellEditor = new EventTextCellEditor(org.exmaralda.folker.utilities.Constants.GAT_EVENT);
        eventTextCellEditor = new EventTextCellEditor(this.getCheckRegex());
        //eventTextCellRenderer = new EventTextCellRenderer();
        if (mainFont!=null){
            eventTextCellEditor.setFont(mainFont);
            //eventTextCellRenderer.setFont(mainFont);
        }
        cmodel.getColumn(4).setCellEditor(eventTextCellEditor);
        //cmodel.getColumn(4).setCellRenderer(eventTextCellRenderer);
    }
    
    public EventListTableModel getEventListTableModel(){
        return ((EventListTableModel)dataModel);
    }

    public void makeVisible(org.exmaralda.folker.data.Event event){
        EventListTranscription elt = ((AbstractListTranscriptionTableModel) getModel()).getTranscription();
        int index = elt.getEventlist().getEvents().indexOf(event);
        Rectangle r = getCellRect(index, 0, true);
        r.grow(0,40);
        scrollRectToVisible(r);
        getSelectionModel().setSelectionInterval(index, index);
    }
    
    
    
    public void makeVisible(double time) {
        EventListTranscription elt = ((AbstractListTranscriptionTableModel) getModel()).getTranscription();
        int row = elt.findFirstIndexForTime(time);
        Rectangle r = getCellRect(row, 0, true);
        r.grow(0,40);
        scrollRectToVisible(r);
    }

    /**
     * if one or more events are selected: returns the start point 
     * of the first of these events, otherwise: returns the timepoint
     * corresponding to the first visible row
     */
    @Override
    public Timepoint getVisiblePosition() {
        if (getSelectedRow() > 0) {
            return ((AbstractListTranscriptionTableModel) getModel()).getTranscription().getEventAt(getSelectedRow()).getStartpoint();
        }
        int firstRow = Math.min(((AbstractListTranscriptionTableModel) getModel()).getTranscription().getNumberOfEvents(), rowAtPoint(getVisibleRect().getLocation()));
        if (firstRow < ((AbstractListTranscriptionTableModel) getModel()).getTranscription().getNumberOfEvents()) {
            return ((AbstractListTranscriptionTableModel) getModel()).getTranscription().getEventAt(firstRow).getStartpoint();
        }
        return null;
    }

    

    
}
