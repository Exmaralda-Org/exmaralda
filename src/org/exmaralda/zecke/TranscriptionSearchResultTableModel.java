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
public class TranscriptionSearchResultTableModel extends javax.swing.table.AbstractTableModel {
    
    TranscriptionSearchResult searchResult;
    public int numberOfChars = 40;
    String[][] exemplaryMetaData;

    /** Creates a new instance of TranscriptionSearchResultTableModel */
    public TranscriptionSearchResultTableModel(TranscriptionSearchResult tsr) {
	searchResult = tsr;
    }
    
    public void setSearchResult(TranscriptionSearchResult tsr) {
        searchResult = tsr;        
        if (searchResult.size()>0){
            exemplaryMetaData = ((TranscriptionSearchResultItem)(searchResult.elementAt(0))).additionalMetaData;
        }
        this.fireTableDataChanged();
        this.fireTableChanged(null);
    }
    
    public int getColumnCount() {
        int additionalCount = 0;
        if (exemplaryMetaData!=null){
            System.out.println("##################");
            additionalCount+=exemplaryMetaData.length;
        }
        return 6 + additionalCount;
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
            default :   int index = column-6;
                        return exemplaryMetaData[index][0];
        }
    }
    
    public int getRowCount() {
	return searchResult.size();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        TranscriptionSearchResultItem tsri = (TranscriptionSearchResultItem)(searchResult.elementAt(rowIndex));
        switch (columnIndex){
            case 0 : return Integer.toString(rowIndex);
            case 1 : return tsri.transcriptionName;
            case 2 : return tsri.speakerAbb;
            case 3 : return tsri.getLeftContext().substring(Math.max(0,tsri.getLeftContext().length()-numberOfChars));
            case 4 : return tsri.getMatch();
            case 5 : return tsri.getRightContext().substring(0, Math.min(numberOfChars,tsri.getRightContext().length()));
            default : int index = columnIndex-6;
                      return tsri.additionalMetaData[index][1];
        }
        //return new String();
    }
    
}
