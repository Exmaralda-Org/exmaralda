/*
 * TranscriptionSearchResultTableModel.java
 *
 * Created on 24. November 2004, 17:00
 */

package org.exmaralda.zecke;

/**
 *
 * @author  thomas
 */
public class DescriptionSearchResultTableModel extends javax.swing.table.AbstractTableModel {
    
    DescriptionSearchResult searchResult;
    public int numberOfChars = 40;

    /** Creates a new instance of TranscriptionSearchResultTableModel */
    public DescriptionSearchResultTableModel(DescriptionSearchResult tsr) {
	searchResult = tsr;
    }
    
    public int getColumnCount() {
        return 7;
    }
    
    public void setContextSize(int cs){
        numberOfChars = cs;
        this.fireTableDataChanged();
    }
    
    public int getContextSize(){
        return numberOfChars;
    }
    
    public String getColumnName(int column){
        switch (column){
            case 0  : return "No";
            case 1  : return "Transcript";
            case 2  : return "Speaker";
            case 3  : return "Left context";
            case 4  : return "Match";
            case 5  : return "Right context";
            case 6  : return "Category";
            default : return "";
        }
    }
    
    public int getRowCount() {
	return searchResult.size();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        DescriptionSearchResultItem tsri = (DescriptionSearchResultItem)(searchResult.elementAt(rowIndex));
        switch (columnIndex){
            case 0 : return Integer.toString(rowIndex);
            case 1 : return tsri.transcriptionName;
            case 2 : return tsri.speakerAbb;
            case 3 : return tsri.getLeftContext().substring(Math.max(0,tsri.getLeftContext().length()-numberOfChars));
            case 4 : return tsri.getMatch();
            case 5 : return tsri.getRightContext().substring(0, Math.min(numberOfChars,tsri.getRightContext().length()));
            case 6 : return tsri.category;
        }
        return new String();
    }
    
}
