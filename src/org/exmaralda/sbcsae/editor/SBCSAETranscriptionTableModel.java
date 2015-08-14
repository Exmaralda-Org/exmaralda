/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.editor;

import org.jdom.*;
import org.jdom.xpath.*;
import java.util.*;
/**
 *
 * @author thomas
 */
public class SBCSAETranscriptionTableModel extends javax.swing.table.AbstractTableModel {

    Document transcription;
    Vector<Element> intonationUnits = new Vector<Element>();
    
    public boolean modified = false;
    
    public SBCSAETranscriptionTableModel(Document d) throws JDOMException{
        transcription = d;
        XPath iu = XPath.newInstance("//intonation-unit");
        List l = iu.selectNodes(d);
        for (Object o : l){
            Element e = (Element)o;
            intonationUnits.addElement(e);
        }
    }
    
    public Document getTranscription(){
        return transcription;
    }

    public int getRowCount() {
        return intonationUnits.size();
    }

    public int getColumnCount() {
        return 5;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Element iu = intonationUnits.elementAt(rowIndex);
        switch(columnIndex){
            case 0 : return Integer.toString(rowIndex+1);
            case 1 : return iu.getAttributeValue("startTime");
            case 2 : return iu.getAttributeValue("endTime");
            case 3 : return iu.getAttributeValue("speaker");
            case 4 : return iu.getText();
        }
        return null;
    }
    
    public double getStartTime(int row){
        Element iu = intonationUnits.elementAt(row);
        return Double.parseDouble(iu.getAttributeValue("startTime"));        
    }
    
    public double getEndTime(int row){
        Element iu = intonationUnits.elementAt(row);
        return Double.parseDouble(iu.getAttributeValue("endTime"));        
    }    

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex>0);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Element iu = intonationUnits.elementAt(rowIndex);
        switch(columnIndex){
            case 1 : 
                iu.setAttribute("startTime", (String)aValue);
                modified = true;
                break;
            case 2 : 
                iu.setAttribute("endTime", (String)aValue);
                modified = true;
                break;                
            case 3 : 
                iu.setAttribute("speaker", (String)aValue);
                modified = true;
                break;
            case 4 :
                iu.setText((String)aValue);
                modified = true;
                break;                        
        }
        this.fireTableCellUpdated(rowIndex, columnIndex);
    }

    void remove(int selectedRow) {
        //Element iu = (Element)(transcription.getRootElement().getChildren("intonation-unit").get(selectedRow));
        //iu.detach();
        intonationUnits.elementAt(selectedRow).detach();
        intonationUnits.removeElementAt(selectedRow);
        fireTableDataChanged();
        System.out.println("DID IT!");
        modified = true;        
    }


    
    
}
