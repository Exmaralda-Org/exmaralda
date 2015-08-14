/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.listTable;

import javax.swing.table.AbstractTableModel;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.ListTranscription;
import org.exmaralda.partitureditor.jexmaralda.SpeakerContribution;
import org.exmaralda.partitureditor.jexmaralda.TimelineItem;

/**
 *
 * @author thomas
 */
public class ListTableModel extends AbstractTableModel {

    ListTranscription listTranscription;

    public ListTableModel(ListTranscription listTranscription) {
        this.listTranscription = listTranscription;
    }


    public int getRowCount() {
        return listTranscription.getBody().getNumberOfSpeakerContributions();
    }

    public int getColumnCount() {
        return 3;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex){
            case 0 : return Integer.toString(rowIndex+1);
            case 1 :
                String speaker = "";
                try {
                    speaker = listTranscription.getHead().getSpeakertable().getSpeakerWithID(listTranscription.getBody().getSpeakerContributionAt(rowIndex).getSpeaker()).getAbbreviation();
                } catch (JexmaraldaException e){
                    e.printStackTrace();
                }
                return speaker;
            case 2 :
                return listTranscription.getBody().getSpeakerContributionAt(rowIndex);
        }
        return "";
    }

    int find(String tierID, String timeID) {
        return listTranscription.getBody().find(tierID, timeID);
    }

    double getStartTime(int index) {
        SpeakerContribution sc = listTranscription.getBody().getSpeakerContributionAt(index);
        TimelineItem tli = null;
        try {
            tli = listTranscription.getBody().getCommonTimeline().getTimelineItemWithID(sc.getMain().getStart());
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }
        if (tli!=null){
            return tli.getTime();
        }
        return -1.0;
    }

    double getEndTime(int index) {
        SpeakerContribution sc = listTranscription.getBody().getSpeakerContributionAt(index);
        TimelineItem tli = null;
        try {
            tli = listTranscription.getBody().getCommonTimeline().getTimelineItemWithID(sc.getMain().getEnd());
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }
        if (tli!=null){
            return tli.getTime();
        }
        return -1.0;
    }
}
