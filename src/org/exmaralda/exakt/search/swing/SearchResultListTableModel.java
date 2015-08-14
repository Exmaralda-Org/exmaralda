/*
 * SearchResultListTableModel.java
 *
 * Created on 10. Januar 2007, 13:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search.swing;

import org.exmaralda.exakt.search.analyses.*;
import org.exmaralda.exakt.search.*;
/**
 *
 * @author thomas
 */
public abstract class SearchResultListTableModel extends javax.swing.table.AbstractTableModel {
    
    SearchResultList data;
    
    static Class BOOLEAN_CLASS;
    static Class STRING_CLASS;
    //static Class CLOSEDCATEGORYLISTANALYSIS_CLASS;
    
    int FIXED_ADDITIONAL_DATA = 3;
    
    int leftContextColumnIndex = 1;
    
    private int maxContextSize = 50;
    
    
    /** Creates a new instance of SearchResultListTableModel */
    public SearchResultListTableModel(SearchResultList d) {
        data = d;
        try {
            BOOLEAN_CLASS = Class.forName("java.lang.Boolean");
            STRING_CLASS = Class.forName("java.lang.String");
            //CLOSEDCATEGORYLISTANALYSIS_CLASS = Class.forName("exakt.analyses.ClosedCategoryListAnalysis");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    
    public AnalysisInterface getAnalysisForColumn(int c){
        if (!isAnalysisColumn(c)) return null;
        return data.getAnalyses().elementAt(c-this.getLeftContextColumnIndex()-3);
    }
    
    public int addAnalysis(AnalysisInterface a){
        data.getAnalyses().add(a);        
        //System.out.println(a.getName() + " added. Now has " + getColumnCount() + " columns");
        this.fireTableStructureChanged();
        return dataIndexToAnalysisColumnIndex(data.getAnalyses().size()-1);
    }
    
    public int setAnalysisForColumn(int column, AnalysisInterface a){
        //int index = this.analysisColumnIndexToDataIndex(column);
        int index = column-this.getLeftContextColumnIndex()-3;
        //System.out.println("Column: " + column + " Index: " + index);
        data.getAnalyses().set(index, a);
        return index;
    }
    
    public void removeAnalysisAtColumn(int column) {
        // remove the analysis itself
        int index2 = column-getLeftContextColumnIndex()-3;        
        data.getAnalyses().removeElementAt(index2);

        // remove values of the analysis
        int index = analysisColumnIndexToDataIndex(column);
        System.out.println("Index is " + index);
        for (int pos=0; pos<getRowCount(); pos++){
            SearchResultInterface searchResult = this.getData().get(pos);
            if (index<searchResult.getAdditionalData().length){
                String[] additionalData = searchResult.getAdditionalData();
                String[] newAdditionalData = new String[additionalData.length-1];
                if (additionalData.length>1){
                    System.arraycopy(additionalData, 0, newAdditionalData, 0, index);
                    System.arraycopy(additionalData, index+1, newAdditionalData, index, additionalData.length-index-1);
                }
                searchResult.setAdditionalData(newAdditionalData);
            }
        }
        
        fireTableStructureChanged();        
    }
    

    public void setData(SearchResultList d){
        data = d;        
        this.fireTableDataChanged();
    }
    
    public SearchResultList getData(){
        return data;
    }

    public boolean isAnalysisColumn(int columnIndex){
        int rci = getLeftContextColumnIndex()+2;
        return ((columnIndex>rci) && (columnIndex<=rci+data.getAnalyses().size()));
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
       return (columnIndex==0 || isAnalysisColumn(columnIndex));
    }
    
    public void setLeftContextColumnIndex(int lcci){
        leftContextColumnIndex = lcci;
    }
    
    public int getLeftContextColumnIndex(){
        return leftContextColumnIndex;
    }

    public void sample(int howMany){
        data.sample(howMany);
        fireTableDataChanged();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex==0){return BOOLEAN_CLASS;}
        if (isAnalysisColumn(columnIndex)){
            AnalysisInterface ai = getAnalysisForColumn(columnIndex);
            /*if (ai instanceof exakt.analyses.ClosedCategoryListAnalysis){
                return CLOSEDCATEGORYLISTANALYSIS_CLASS;
            }*/
            if (ai instanceof org.exmaralda.exakt.search.analyses.BinaryAnalysis){
                return BOOLEAN_CLASS;
            }
        }
        return STRING_CLASS;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public String getColumnName(int column) {
        int lcci = getLeftContextColumnIndex();
        if (column==0) return "S";
        else if (column==lcci) return "Left Context";
        else if (column==lcci+1) return "Match";
        else if (column==lcci+2) return "Right Context";
        else if (isAnalysisColumn(column)){
            return getAnalysisForColumn(column).getName();
        }
        return null;
    }

    public SearchResultInterface getSearchResultAt(int rowIndex){
        return data.elementAt(rowIndex);
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        int lcci = getLeftContextColumnIndex();
        SearchResultInterface searchResult = getSearchResultAt(rowIndex);
        if (columnIndex==0) {
            // first column: check boxes
            return new Boolean(searchResult.isSelected());
        } else if (columnIndex==lcci) {
            // left context
            String leftContext = searchResult.getLeftContextAsString();
            String retValue = leftContext.substring(Math.max(0,leftContext.length()-getMaxContextSize()));
            return retValue;
        } else if (columnIndex==lcci+1) {
            // match text
            return searchResult.getMatchTextAsString();
        } else if (columnIndex==lcci+2) {
            // right context
            String rightContext = searchResult.getRightContextAsString();
            String retValue = rightContext.substring(0, Math.min(rightContext.length(),getMaxContextSize()));
            return retValue;
        } else if (isAnalysisColumn(columnIndex)){
            // analysis columns
            int index = this.analysisColumnIndexToDataIndex(columnIndex);
            AnalysisInterface ai = getAnalysisForColumn(columnIndex);
            if (index<searchResult.getAdditionalData().length){
                String returnValue = searchResult.getAdditionalData()[index];
                if (returnValue==null) return null;
                if (ai instanceof org.exmaralda.exakt.search.analyses.BinaryAnalysis){
                    return Boolean.valueOf(returnValue);
                }
                return returnValue;
            }
            return null;
        }
        return null;
    }

    public int getMaxContextSize() {
        return maxContextSize;
    }

    public void setMaxContextSize(int maxContextSize) {
        this.maxContextSize = maxContextSize;
        fireTableDataChanged();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        //System.out.println("Setting value "  + aValue + " at " + rowIndex + " / " + columnIndex + "...");
        if (columnIndex==0){
            SearchResultInterface result = getSearchResultAt(rowIndex);
            Boolean value = (Boolean)aValue;
            result.setSelected(value.booleanValue());
            this.fireTableCellUpdated(rowIndex, columnIndex);
        } else if (isAnalysisColumn(columnIndex)){
            //System.out.println("It doth be an analysis");
            AnalysisInterface ai = getAnalysisForColumn(columnIndex);
            String stringValue = null;
            if (ai instanceof org.exmaralda.exakt.search.analyses.BinaryAnalysis){
                Boolean value = (Boolean)aValue;
                stringValue = value.toString();
            } else {
                stringValue = (String)aValue;
            }
            SearchResultInterface result = getSearchResultAt(rowIndex);
            result.setAdditionalData(analysisColumnIndexToDataIndex(columnIndex), stringValue);            
            this.fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    public int analysisColumnIndexToDataIndex(int c){
        return c - 4 + FIXED_ADDITIONAL_DATA;
    }
    
    public int dataIndexToAnalysisColumnIndex(int i){
        return i + 4;
    }

    @Override
    public int getColumnCount() {        
        return 1 + 3 + data.getAnalyses().size();
    }
    
    
    
    
    
}
