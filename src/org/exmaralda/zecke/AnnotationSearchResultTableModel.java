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
public class AnnotationSearchResultTableModel extends javax.swing.table.AbstractTableModel {
    
    AnnotationSearchResult searchResult;
    public int numberOfChars = 40;

    /** Creates a new instance of TranscriptionSearchResultTableModel */
    public AnnotationSearchResultTableModel(AnnotationSearchResult tsr) {
	searchResult = tsr;
    }
    
    public void setSearchResult(AnnotationSearchResult asr) {
        searchResult = asr;        
        this.fireTableDataChanged();
    }
    
    
    public int getColumnCount() {
        return 8;
    }
    
    public void setContextSize(int cs){
        numberOfChars = cs;
        this.fireTableDataChanged();
    }
    
    public int getContextSize(){
        return numberOfChars;
    }
    
    public Class getColumnClass(int columnIndex) {
	if (columnIndex==6){
            return String[].class;
        } 
        return super.getColumnClass(columnIndex);
    }    
    
    public String getColumnName(int column){
        switch (column){
            case 0  : return "No";
            case 1  : return "Transcript";
            case 2  : return "Speaker";
            case 3  : return "Left context";
            case 4  : return "Match";
            case 5  : return "Right context";
            case 6  : return "Annotation";
            case 7  : return "Category";
            default : return "";
        }
    }
    
    public int getRowCount() {
	return searchResult.size();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        AnnotationSearchResultItem tsri = (AnnotationSearchResultItem)(searchResult.elementAt(rowIndex));
        switch (columnIndex){
            case 0 : return Integer.toString(rowIndex);
            case 1 : return tsri.transcriptionName;
            case 2 : return tsri.speakerAbb;
            case 3 : return tsri.getLeftContext().substring(Math.max(0,tsri.getLeftContext().length()-numberOfChars));
            case 4 : return tsri.getMatch();
            case 5 : return tsri.getRightContext().substring(0, Math.min(numberOfChars,tsri.getRightContext().length()));
            case 6 : String allLeftContext = tsri.getLeftAnnotationMatchContext();
                     String leftContext = "";
                     if ((allLeftContext!=null) && (allLeftContext.length()>0)){
                        leftContext = allLeftContext.substring(Math.max(0,allLeftContext.length()-10));  
                     }
                     String allRightContext = tsri.getRightAnnotationMatchContext();
                     String rightContext = "";
                     if ((allRightContext!=null) && (allRightContext.length()>0)){
                        rightContext = allRightContext.substring(0, Math.min(10,allRightContext.length()-1));                     
                     }
                     String[] value = {leftContext, tsri.getAnnotationMatch(), rightContext};
                     return value;
            case 7 : return tsri.category;
        }
        return new String();
    }
    
}
