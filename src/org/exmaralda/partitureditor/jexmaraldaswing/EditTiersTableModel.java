/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaraldaswing;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelListener;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;

/**
 *
 * @author Thomas_Schmidt
 */
public class EditTiersTableModel extends javax.swing.table.AbstractTableModel {

    BasicTranscription transcription;
    boolean changed = false;
    
    public EditTiersTableModel(BasicTranscription transcription) {
        this.transcription = transcription;
    }
    
    @Override
    public int getRowCount() {
        return transcription.getBody().getNumberOfTiers();
    }
    
    public void moveDown(int fromIndex, int toIndex) throws JexmaraldaException{
        Tier tierAfter = transcription.getBody().getTierAt(toIndex + 1);
        transcription.getBody().removeTierAt(toIndex + 1);
        transcription.getBody().insertTierAt(tierAfter, fromIndex);
        changed = true;
        fireTableRowsUpdated(fromIndex, toIndex+1);
    }
    
    public void moveUp(int fromIndex, int toIndex) throws JexmaraldaException{
        Tier tierBefore = transcription.getBody().getTierAt(fromIndex -1);
        transcription.getBody().removeTierAt(fromIndex -1);
        transcription.getBody().insertTierAt(tierBefore, toIndex);
        changed = true;
        fireTableRowsUpdated(fromIndex-1, toIndex+1);
    }

    @Override
    public int getColumnCount() {
        return 10;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex){
            case 0 : return ""; // count;
            case 1 : return "Display name";
            case 2 : return "Category";
            case 3 : return "Type";
            case 4 : return "Tier ID";
            case 5 : return "Speaker";
            case 6 : return "Speaker ID";
            case 7 : return "# Events";
            case 8 : return "Parent tier ID";
            case 9 : return "# Annotation mismatches";
            default: return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex){
            case 7 : 
            case 9 : return Integer.class;
            default: return String.class; 
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex==1 || columnIndex==2 || columnIndex==3 || columnIndex==6);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Tier tier = transcription.getBody().getTierAt(rowIndex);
        String speakerID = tier.getSpeaker();
        switch (columnIndex){
            case 0 : return (rowIndex + 1);
            case 1 : return tier.getDisplayName();
            case 2 : return tier.getCategory();
            case 3 : return tier.getType();
            case 4 : return tier.getID();
            case 5 : 
                if (speakerID==null) return null;
                try {
                    return transcription.getHead().getSpeakertable().getSpeakerWithID(speakerID).getAbbreviation();
                } catch (JexmaraldaException ex) {
                    Logger.getLogger(EditTiersTableModel.class.getName()).log(Level.SEVERE, null, ex);
                    return "### ERROR";
                }
            case 6 : return speakerID;
            case 7 : return tier.getNumberOfEvents();
            case 8 : 
                if (!(tier.getType().equals("a"))) return null; 
                if (speakerID==null) return "### ERROR";
                for (int i=0; i<transcription.getBody().getNumberOfTiers(); i++){
                    Tier tryTier = transcription.getBody().getTierAt(i);
                    if (tryTier.getType().equals("t") && tryTier.getSpeaker().equals(speakerID)){
                        return tryTier.getID();
                    }
                }
                return "### ERROR";
            case 9 : 
                if (!(tier.getType().equals("a"))) return null; 
                String[] mismatches = tier.getAnnotationMismatches(transcription);
                if (mismatches!=null){
                    return mismatches.length;
                }
            default : return null;    
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Tier tier = transcription.getBody().getTierAt(rowIndex);
        switch (columnIndex){
            case 1 : 
                tier.setDisplayName(((String)aValue));
                changed = true;
                fireTableCellUpdated(rowIndex, columnIndex);
                break;
            case 2 : tier.setCategory(((String)aValue));
                fireTableCellUpdated(rowIndex, columnIndex);
                changed = true;
                break;            
            case 3 : 
                tier.setType(((String)aValue));
                fireTableDataChanged();
                changed = true;
                break;
            case 6 : 
                tier.setSpeaker((String)aValue);
                fireTableDataChanged();
                changed = true;
                break;                
        }
        this.fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        super.addTableModelListener(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        super.removeTableModelListener(l);
    }
    
}
