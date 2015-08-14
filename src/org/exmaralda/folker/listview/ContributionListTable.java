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
import javax.swing.*;
import javax.swing.table.*;
import org.exmaralda.folker.data.Contribution;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.Timepoint;

/**
 *
 * @author thomas
 */
public class ContributionListTable extends AbstractListTranscriptionTable  {
   
    public TextAreaCellRenderer textAreaCellRenderer;
    public boolean USE_MULTILINE_CELL_RENDERER = true;
    
    /** Creates a new instance of EventListTableView */
    public ContributionListTable() {
        //setCellSelectionEnabled(true);
        
        setGridColor(new java.awt.Color(102, 204, 255));
        setIntercellSpacing(new java.awt.Dimension(0, 2));
        setShowHorizontalLines(false);        
        //setSelectionMode(getSelectionModel().SINGLE_SELECTION);
        setSelectionMode(getSelectionModel().SINGLE_INTERVAL_SELECTION);
        setRowHeight(20);

    }        
       
    @Override
    public void setFont(String fontName) {
        super.setFont(fontName);
        if (mainFont!=null){
            textAreaCellRenderer.setFont(mainFont);
        }
        reformat();
    }



    @Override
    void reformat(){
        
        super.reformat();
        
        
        if (USE_MULTILINE_CELL_RENDERER){
            TableColumnModel cmodel = getColumnModel();

            textAreaCellRenderer = new TextAreaCellRenderer();
            cmodel.getColumn(4).setCellRenderer(textAreaCellRenderer);
        }

        
    }

    public void makeVisible(org.exmaralda.folker.data.Event selectedEvent) {
        EventListTranscription elt = ((AbstractListTranscriptionTableModel) getModel()).getTranscription();
        Contribution c = elt.findContribution(selectedEvent);
        final int index = elt.getIndexOfContribution(c);
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Rectangle r = getCellRect(index, 3, true);
                    r.grow(0,40);
                    //Rectangle r = getExactCellRect(index);
                    setRowSelectionInterval(index, index);
                    //getSelectionModel().setSelectionInterval(index, index);             
                    scrollRectToVisible(r);        
                }
        });     
    }
    
    Rectangle getExactCellRect(int rowindex){
        int totalHeight = 0;
        for (int row=0; row<rowindex-1; row++){
            TableCellRenderer tcr = getCellRenderer(row,3);
            java.awt.Component c = tcr.getTableCellRendererComponent(this, getModel().getValueAt(row,3), false, false, row, 3);
            int thisHeight = c.getPreferredSize().height;
            //System.out.println(row + " / " + thisHeight);
            totalHeight+=thisHeight;
        }
        return new Rectangle(0,totalHeight,10,10);
    }
    
    void debugMakeVisible(String m){
        //System.out.println(m);
        for (int row=0; row<getModel().getRowCount(); row++){
            int h = getRowHeight(row);
            //System.out.println("Row " + row + " - height " + h);
        }
    }
    
    
    public void makeVisible(double time) {
        EventListTranscription elt = ((AbstractListTranscriptionTableModel)getModel()).getTranscription();
        int row = elt.findFirstContributionIndexForTime(time);
        Rectangle r = getCellRect(row, 0, true);
        scrollRectToVisible(r);
    }

    /**
     * if one or more contributions are selected: returns the start point 
     * of the first of these contributions, otherwise: returns the timepoint
     * corresponding to the first visible row
     */
    @Override
    public Timepoint getVisiblePosition() {
        if (getSelectedRow() > 0) {
            return ((AbstractListTranscriptionTableModel) getModel()).getTranscription().getContributionAt(getSelectedRow()).getStartpoint();
        }
        int firstRow = Math.min(((AbstractListTranscriptionTableModel) getModel()).getTranscription().getNumberOfContributions(), rowAtPoint(getVisibleRect().getLocation()));
        if (firstRow < ((AbstractListTranscriptionTableModel) getModel()).getTranscription().getNumberOfContributions()) {
            return ((AbstractListTranscriptionTableModel) getModel()).getTranscription().getContributionAt(firstRow).getStartpoint();
        }
        return null;
    }
    

    

    
}
