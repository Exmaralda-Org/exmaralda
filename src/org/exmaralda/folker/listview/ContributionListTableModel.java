/*
 * EventListTableModel.java
 *
 * Created on 20. März 2008, 15:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.listview;

import org.exmaralda.folker.data.Contribution;
import org.exmaralda.folker.data.Event;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.Speaker;
import org.exmaralda.folker.data.Timeline;
import org.exmaralda.folker.data.Timepoint;

/**
 *
 * @author thomas
 */
public class ContributionListTableModel extends AbstractListTranscriptionTableModel {
    
    String noSpeakerCheckRegex = ".*";
    static String GAT_EMPTY_BOUNDARY = "(?<![\\Q.;,??\\|\\E]) $";
    
    /** Creates a new instance of EventListTableModel */
    public ContributionListTableModel(EventListTranscription elt) {
        super(elt);
    }

    @Override
    public int getRowCount() {
        return eventListTranscription.getNumberOfContributions() + ADDITIONAL_ROWS;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex>=eventListTranscription.getNumberOfContributions()) return null;
        Contribution contribution = eventListTranscription.getContributionAt(rowIndex);
        switch (columnIndex) {
            case 0 : return rowIndex+1;
            case 1 : return contribution.getStartpoint();
            case 2 : return contribution.getEndpoint();
            case 3 : return contribution.getSpeaker();
            case 4 : return contribution.getText();
            case 5 : // case distinction: on transcript level 3, contributions with speakers
                     // have to meet requirements different from contributions without speakers
                     if (contribution.getSpeaker()!=null){
                         String contributionText = contribution.getText();
                         boolean syntaxOK = 
                                contributionText.matches(getCheckRegex())
                                || contributionText.replaceAll(GAT_EMPTY_BOUNDARY, "| ").matches(getCheckRegex());
                        return Boolean.valueOf(syntaxOK); //new Boolean(contribution.getText().matches(getCheckRegex()));
                     } else {
                        return Boolean.valueOf(contribution.getText().matches(getNoSpeakerCheckRegex())); //new Boolean(contribution.getText().matches(getNoSpeakerCheckRegex()));
                     }
            case 6 : return Boolean.valueOf(contribution.isOrdered()); //new Boolean(contribution.isOrdered());
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (rowIndex>=eventListTranscription.getNumberOfEvents()) return false;
        return (columnIndex==3);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Contribution contribution = eventListTranscription.getContributionAt(rowIndex);
        switch (columnIndex) {
            case 3 : contribution.setSpeaker((Speaker)aValue);
                     break;
        }                
        //this.fireTableCellUpdated(rowIndex, columnIndex);
        getTranscription().updateContributions();
        fireTableDataChanged();        
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
        }
    }

    public void setNoSpeakerCheckRegex(String nscrb) {
        noSpeakerCheckRegex = nscrb;
    }

    public String getNoSpeakerCheckRegex(){
        return noSpeakerCheckRegex;
    }

    void split(Event splitEvent, int splitPosition) {
        Timeline timeline = eventListTranscription.getTimeline();
        double startTime = splitEvent.getStartpoint().getTime();
        double endTime = splitEvent.getEndpoint().getTime();
        double tolerance = timeline.tolerance;
        if ((endTime - startTime) <= 2*tolerance){
            System.out.println(startTime + " " + endTime + " " + tolerance);
            return;
        }
        String text = splitEvent.getText();
        // modified 01-07-2010 to avoid division by zero when there is no text
        double splitRatio = 0.5;
        if (text.length()>0){
            splitRatio = ((double)splitPosition/text.length());
        }
        double splitTime = Math.min(endTime - (timeline.tolerance+1), startTime + Math.max(timeline.tolerance+1, (endTime - startTime) * splitRatio));
        Timepoint oldEndpoint = splitEvent.getEndpoint();
        Timepoint splitTimepoint = timeline.addTimepoint(splitTime);
        splitEvent.setEndpoint(splitTimepoint);
        splitEvent.setText(text.substring(0,splitPosition));
        
        Event secondEvent = new Event(splitTimepoint, oldEndpoint, text.substring(splitPosition), splitEvent.getSpeaker());
        getTranscription().addEvent(secondEvent);
        getTranscription().updateContributions();
        fireTableDataChanged();
        DOCUMENT_CHANGED = true;        
    }

    public Timepoint getMaxTimepoint(int[] indices) {
        Timepoint maxTimepoint = getTranscription().getContributionAt(indices[0]).getEndpoint();
        double maxTime = maxTimepoint.getTime();            
        for (int row : indices){
            if (row>=this.getTranscription().getNumberOfContributions()) continue;
            org.exmaralda.folker.data.Contribution contribution = getTranscription().getContributionAt(row);
            maxTime = Math.max(maxTime, contribution.getEndpoint().getTime());
        }
        maxTimepoint = getTranscription().getTimeline().findTimepoint(maxTime);        
        return maxTimepoint;
    }
    
    
}
