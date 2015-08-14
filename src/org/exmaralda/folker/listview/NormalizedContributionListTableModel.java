/*
 * EventListTableModel.java
 *
 * Created on 20. Maerz 2008, 15:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.listview;

import javax.swing.table.AbstractTableModel;
import org.exmaralda.orthonormal.data.NormalizedFolkerTranscription;
import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class NormalizedContributionListTableModel extends AbstractTableModel {
    
    NormalizedFolkerTranscription transcription;
    public int ADDITIONAL_ROWS = 10;
    public boolean DOCUMENT_CHANGED = false;


    /** Creates a new instance of EventListTableModel */
    public NormalizedContributionListTableModel(NormalizedFolkerTranscription nft) {
        transcription = nft;
    }

    public int getRowCount() {
        return transcription.getNumberOfContributions();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex>=transcription.getNumberOfContributions()) return null;
        Element contribution = transcription.getContributionAt(rowIndex);
        switch (columnIndex) {
            case 0 : return rowIndex+1;
            case 1 : return transcription.getTimeForId(contribution.getAttributeValue("start-reference"));
            case 2 : return transcription.getTimeForId(contribution.getAttributeValue("end-reference"));
            case 3 : return transcription.getSpeakerForId(contribution.getAttributeValue("speaker-reference"));
            case 4 : return contribution;
        }
        return null;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // do nothing - table is not editable
    }
    
    public int getColumnCount() {
        return 5;
    }

    
    
}
