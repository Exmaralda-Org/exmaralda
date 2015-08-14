/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.folker.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.exmaralda.folker.data.Timepoint;
import org.exmaralda.folker.data.TranscriptionHead;
import org.jdom.Element;

/**
 *
 * @author Schmidt
 */
public class MaskTableModel extends AbstractTableModel {

    TranscriptionHead transcriptionHead;
    List maskSegments = new ArrayList<Element>();
    

    public MaskTableModel(TranscriptionHead transcriptionHead) {
        this.transcriptionHead = transcriptionHead;
        //maskSegments.addAll(transcriptionHead.getHeadElement().getChild("mask").getChildren("mask-segment"));        
        maskSegments = transcriptionHead.getHeadElement().getChild("mask").getChildren("mask-segment");
    }
    
    
    
    @Override
    public int getRowCount() {
        return maskSegments.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int column) {
        switch (column){
            case 0 : return "Start";
            case 1 : return "Ende";
            default : return "Eintrag";
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Element maskSegment = (Element) maskSegments.get(rowIndex);
        // <mask-segment start="1733.3288891737711" end="4983.320556374591">Mein allererster Maskierungstext.</mask-segment>
        switch(columnIndex){
            case 0 : return new Timepoint(Double.parseDouble(maskSegment.getAttributeValue("start"))*1000.0);
            case 1 : return new Timepoint(Double.parseDouble(maskSegment.getAttributeValue("end"))*1000.0);
            default : return maskSegment.getText();
        }
    }
    
    public void setStartTime(int rowIndex, double time){
        Element maskSegment = (Element) maskSegments.get(rowIndex);
        maskSegment.setAttribute("start", Double.toString(time / 1000.0));
        fireTableCellUpdated(rowIndex, 0);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex==2);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Element maskSegment = (Element) maskSegments.get(rowIndex);
        maskSegment.setText((String)aValue);
        fireTableCellUpdated(rowIndex, 2);
    }
    
    public void setEndTime(int rowIndex, double time){
        Element maskSegment = (Element) maskSegments.get(rowIndex);
        maskSegment.setAttribute("end", Double.toString(time / 1000.0));
        fireTableCellUpdated(rowIndex, 1);
    }

    void removeEntry(int row) {
        //((Element) transcriptionHead.getHeadElement().getChild("mask").getChildren("mask-segment").get(row)).detach();
        maskSegments.remove(row);
        fireTableRowsDeleted(row, row);
    }
    
    

}
