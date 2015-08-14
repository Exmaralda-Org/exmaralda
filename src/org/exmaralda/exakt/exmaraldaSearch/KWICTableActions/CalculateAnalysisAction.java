/*
 * WordWiseReversedSortAction.java
 *
 * Created on 19. Februar 2007, 13:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.event.ActionEvent;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.table.*;
import org.exmaralda.exakt.search.SimpleSearchResult;
import org.exmaralda.exakt.search.swing.AbstractOKCancelDialog;
import org.exmaralda.exakt.exmaraldaSearch.*;
import org.exmaralda.exakt.exmaraldaSearch.swing.*;


/**
 *
 * @author thomas
 */
public class CalculateAnalysisAction extends AbstractKWICTableAction {
    
    int count=0;
    CalculateAnalysisPanel cap;
    AbstractOKCancelDialog dialog;
    int selectedColumn;
    
    
    /** Creates a new instance of WordWiseReversedSortAction */
    public CalculateAnalysisAction(COMAKWICTable t, String title) {
        super(t,title);
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    public void setSelectedColumn(int sc) {
        this.selectedColumn = sc;
        //System.out.println("Selected column is now " + sc);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        COMACorpusInterface corpus = table.getWrappedModel().getCorpus();
        cap = new CalculateAnalysisPanel(corpus);
        dialog = new AbstractOKCancelDialog((JFrame)(table.getTopLevelAncestor()), true, cap);
        dialog.setTitle("Calculate analysis");
        dialog.setVisible(true);
        if (dialog.isApproved()){
            
            String[] v = cap.getValues();
            char firstType = v[0].charAt(v[0].length()-2);
            char secondType = v[2].charAt(v[2].length()-2);
            String firstAttribute = v[0].substring(0,v[0].length()-4);
            String secondAttribute = v[2].substring(0,v[2].length()-4);
            
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            long ONE_YEAR = (long)365*24*60*60*1000;
            long ONE_MONTH = (long)30*24*60*60*1000;
            try {ONE_YEAR = dateFormat.parse("1971-12-31T23:59:59").getTime()
                            - dateFormat.parse("1971-01-01T00:00:01").getTime();
            } catch (ParseException ex) {}
            
            for (int row=0; row<table.getWrappedModel().getRowCount(); row++){
                SimpleSearchResult rowData = (SimpleSearchResult)(table.getWrappedModel().getSearchResultAt(row));
                String transcriptionLocator = (String)(rowData.getSearchableSegmentLocator().getCorpusComponentLocator());
                String speakerID = rowData.getAdditionalData()[1];

                String v1 = null;
                if (firstType=='S') v1 = corpus.getSpeakerData(transcriptionLocator, speakerID, firstAttribute);
                else if (firstType=='C') v1 = corpus.getCommunicationData(transcriptionLocator, firstAttribute);
                else if (firstType=='T') v1 = corpus.getTranscriptionData(transcriptionLocator, firstAttribute);

                String v2 = null;
                if (secondType=='S') v2 = corpus.getSpeakerData(transcriptionLocator, speakerID, secondAttribute);
                else if (secondType=='C') v2 = corpus.getCommunicationData(transcriptionLocator, secondAttribute);
                else if (secondType=='T') v2 = corpus.getTranscriptionData(transcriptionLocator, secondAttribute);
                
                String calculatedValue = "???";
                if (v[3].equals("Date") && v[1].endsWith("(Minus)")){
                    try {
                        java.util.Date date1 = dateFormat.parse(v1);
                        java.util.Date date2 = dateFormat.parse(v2);
                        long difference = date1.getTime() - date2.getTime();
                        
                        long years = difference/ONE_YEAR;
                        long months = (difference - years*ONE_YEAR)/ONE_MONTH;
                        
                        String filler1 = ""; if (years<10) filler1="0";
                        String filler2 = ""; if (months<10) filler2="0";
                        
                        calculatedValue = filler1 + Long.toString(years) + ";" + filler2 + Long.toString(months);
                    } catch (ParseException ex) {}
                }                
                table.getWrappedModel().setValueAt(calculatedValue,row,selectedColumn);
            }
        }
        selectedColumn = -1;
    }
    
}
