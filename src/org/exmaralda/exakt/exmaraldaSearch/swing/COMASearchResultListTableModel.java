/*
 * COMASearchResultListTableModel.java
 *
 * Created on 10. Januar 2007, 14:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.swing;

import java.util.*;
import org.exmaralda.exakt.search.SearchResultInterface;
import org.exmaralda.exakt.search.SearchResultList;
import org.exmaralda.exakt.search.SimpleSearchResult;
import org.exmaralda.exakt.exmaraldaSearch.*;


/**
 *
 * @author thomas
 */
public class COMASearchResultListTableModel extends org.exmaralda.exakt.search.swing.SearchResultListTableModel {
    
    private COMACorpusInterface corpus;
    Vector<String[]> metaIdentifiers;
    
    /** Creates a new instance of COMASearchResultListTableModel */
    public COMASearchResultListTableModel(SearchResultList d, COMACorpusInterface c, Vector<String[]>mi) {
        super(d);
        corpus = c;
        metaIdentifiers = mi;
        setLeftContextColumnIndex(3);
    }

    @Override
    public String getColumnName(int column) {
        String retValue;        
        retValue = super.getColumnName(column);
        if (retValue==null){
            if (column==1) return "Communication";
            else if (column==2) return "Speaker";
            retValue = getMetaIdentifiers().elementAt(column-(super.getColumnCount() + 2))[1] + "[" + getMetaIdentifiers().elementAt(column-(super.getColumnCount() + 2))[0] + "]";
        }
        return retValue;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object retValue;        
        retValue = super.getValueAt(rowIndex, columnIndex);
        if ((!isAnalysisColumn(columnIndex)) && (retValue==null)){
            // this means this is a meta data column!
            //System.out.println("Getting value for column " + columnIndex);
            SimpleSearchResult rowData = (SimpleSearchResult)(getSearchResultAt(rowIndex)); 
            String transcriptionLocator = (String)(rowData.getSearchableSegmentLocator().getCorpusComponentLocator());
            String speakerID = rowData.getAdditionalData()[1];
            if (columnIndex==1) {
                return getCorpus().getCommunicationData(transcriptionLocator,"Name*");
            }
            else if (columnIndex==2) {
                return getCorpus().getSpeakerData(transcriptionLocator,speakerID,"Sigle*");
            }
            
            // if we get here, this means this is one of the custom meta data columns
            Vector<String[]> mis = getMetaIdentifiers();
            
            // the following lines test if the data is available at all
            // it may not be when a column is an annotation column
            // this causes a bug
            if (mis==null){return "";}
            int shiftedIndex = columnIndex-(super.getColumnCount() + 2);
            if (shiftedIndex > mis.size()-1){return "";}
            String[] mi = mis.elementAt(shiftedIndex);
            if (mi==null){return "";}
            if (mi.length!=2){return "";}
            
            String metaType = mi[0];
            String metaName = mi[1];
            if (metaType.equals("C")) return getCorpus().getCommunicationData(transcriptionLocator,metaName);
            else if (metaType.equals("T")) return getCorpus().getTranscriptionData(transcriptionLocator,metaName);
            else if (metaType.equals("S")) return getCorpus().getSpeakerData(transcriptionLocator,speakerID,metaName);            
        }
        return retValue;
    }
        
    
    public void removeUnselected(){
        getData().removeUnselected();
        fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        if (getMetaIdentifiers()==null) return super.getColumnCount() + 2;
        return getMetaIdentifiers().size() + super.getColumnCount() + 2;
    }

    public Vector<String[]> getMetaIdentifiers() {
        return metaIdentifiers;
    }

    public void setMetaIdentifiers(Vector<String[]> mi) {
        metaIdentifiers = mi;
        fireTableStructureChanged();
    }
    
    public void selectAll(){
        for (SearchResultInterface sri : getData()){
            sri.setSelected(true);
        }
        fireTableDataChanged();
    }
    
    public void deselectAll(){
        for (SearchResultInterface sri : getData()){
            sri.setSelected(false);
        }
        fireTableDataChanged();
    }
    
    public void selectRange(int[] range){
        for (int pos : range){
            SearchResultInterface sri = getData().get(pos);
            sri.setSelected(true);
        }
        fireTableDataChanged();
    }

    public void deselectRange(int[] range){
        for (int pos : range){
            SearchResultInterface sri = getData().get(pos);
            sri.setSelected(false);
        }
        fireTableDataChanged();
    }

    public COMACorpusInterface getCorpus() {
        return corpus;
    }
    
    public HashSet<String> getTypes(int columnIndex){
        HashSet<String> returnValue = new HashSet<String>();
        for (int rowIndex=0; rowIndex<this.getRowCount(); rowIndex++){
            // changed to accomodate binary values from binary analysis
            Object value = null;
            if (getValueAt(rowIndex, columnIndex)!=null){
                value = getValueAt(rowIndex, columnIndex).toString();
            }
            if (value!=null){
                returnValue.add((String)(value));
            }
        }
        return returnValue;
    }
    
    
    public void filter(int columnIndex, String regexString, boolean invert) throws java.util.regex.PatternSyntaxException {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regexString);
        switch (columnIndex){
            case 3  : getData().filterLeftContext(pattern, invert);
                      fireTableDataChanged();
                      break;
            case 4  : getData().filterMatchText(pattern, invert);
                      fireTableDataChanged();
                      break;
            case 5  : getData().filterRightContext(pattern, invert);
                      fireTableDataChanged();
                      break;
            default : for (int rowIndex = 0; rowIndex < this.getRowCount(); rowIndex++){
                            // TO DO: Take care of boolean values from Binary Analysis!!!
                            String content = null;
                            if (getValueAt(rowIndex,columnIndex)!=null){
                                content = getValueAt(rowIndex,columnIndex).toString();
                            }
                            //content = (String)(getValueAt(rowIndex,columnIndex));
                            SearchResultInterface searchResult = getData().elementAt(rowIndex);
                            boolean matches = ((content!=null) && (pattern.matcher(content).matches()));
                            searchResult.setSelected((searchResult.isSelected()) && (matches^invert));                            
                      }
                      fireTableDataChanged();            
        }
    }
    
    
    
    @Override
    public int analysisColumnIndexToDataIndex(int c){
        return super.analysisColumnIndexToDataIndex(c)-2;
    }

    @Override
    public int dataIndexToAnalysisColumnIndex(int i) {        
        return super.dataIndexToAnalysisColumnIndex(i) + 2;
    }


    
}
