/*
 * TranscriptionSearchInternalFrame.java
 *
 * Created on 2. Juni 2005, 16:17
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.exmaralda.zecke;

import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author thomas
 */
public class AnnotationSearchInternalFrame extends AbstractSearchInternalFrame {
    
    SimpleAnnotationSearch search;
    //AnnotationSearchResult result;
    //TableSorter tableSorter;
    //AnnotationSearchResultTableModel meinTableModel;

    public static final Comparator ANNOTATION_MATCH_COMPARATOR = new Comparator() {
        public int compare(Object o1, Object o2) {
            String s1 = ((String[])o1)[1];
            String s2 = ((String[])o2)[1];
            System.out.println("Comparing " + s1 + " to " + s2);
            return s1.compareTo(s2);
        }
    };

    /** Creates a new instance of TranscriptionSearchInternalFrame */
    public AnnotationSearchInternalFrame() {
        super();
        
        setTitle("Annotation search");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/zecke/annotation.png")));        

        AnnotationSearchResultTableModel meinTableModel = new AnnotationSearchResultTableModel(new AnnotationSearchResult());
        TableSorter tableSorter = new TableSorter(meinTableModel);  
        tableSorter.setColumnComparator(String[].class, ANNOTATION_MATCH_COMPARATOR);
        getSearchResultTable().setModel(tableSorter);
        tableSorter.setTableHeader(getSearchResultTable().getTableHeader());        
        
        formatTable();
    }
    
    public void setCorpus(CorpusTree ct){
        search = new SimpleAnnotationSearch(ct);
        search.addSearchListener(this);
    }
    
    
    public void performSearch(){
        isSearching = true;
        setCursor( WAIT_CURSOR );
        Thread thread = new Thread(){
            public void run(){
                search();
                setCursor(ORDINARY_CURSOR); 
                progressBar.setValue(100);
                progressBar.setString("100 %");
                isSearching = false;            
            }
        };
        thread.start();                
    }
        
    public void moreContext(){
        AnnotationSearchResultTableModel tsrtm = ((AnnotationSearchResultTableModel)
                ((TableSorter)(getSearchResultTable().getModel())).getTableModel());        
        tsrtm.setContextSize(tsrtm.getContextSize()+5);        
    }                                                  
   
    public void lessContext(){
        AnnotationSearchResultTableModel tsrtm = ((AnnotationSearchResultTableModel)
                ((TableSorter)(getSearchResultTable().getModel())).getTableModel());        
        tsrtm.setContextSize(Math.max(1,tsrtm.getContextSize()-5));        
    }
    
    
    public AbstractSearchResultItem getSelectedSearchResultItem(){
        int sel = getSearchResultTable().getSelectedRow();
        if (sel<0) return null;
        int no = Integer.parseInt((String)(getSearchResultTable().getValueAt(sel,0)));
        //AnnotationSearchResultItem tsri = (AnnotationSearchResultItem)(result.elementAt(no)); 
        AnnotationSearchResultItem tsri = (AnnotationSearchResultItem)
                                            (((AnnotationSearchResultTableModel)
                                            ((TableSorter)(getSearchResultTable().getModel())).getTableModel())
                                            .searchResult.elementAt(no)); 
        
        return tsri;
    }

    void search(){
        statusLabel.setText("Searching...");
        currentProgress = 0;
        try {
            String searchExpression = getSearchExpression();
            AnnotationSearchResult result = (AnnotationSearchResult)(search.search(AbstractSearch.REGULAR_EXPRESSION_SEARCH_TYPE, searchExpression));
            TableSorter tableSorter = (TableSorter)(getSearchResultTable().getModel());
            ((AnnotationSearchResultTableModel)(tableSorter.getTableModel())).setSearchResult(result);
                                   
            formatTable();            

            String message =  Double.toString(search.getTimeForLastSearch()) + " seconds / ";
            message+= result.size() + " results. ";
            statusLabel.setText(message);
            //JOptionPane.showMessageDialog(this, message);
        } catch (JexmaraldaException je){
            String text = "Fehler beim Einlesen des Corpus: " + je.getMessage();
            JOptionPane.showMessageDialog((JFrame)(getTopLevelAncestor()), text);
            statusLabel.setText("Search failed.");            
            return;
        } catch (IOException ioe){
            String text = "Fehler beim Schreiben der Ergebnisse: " + ioe.getMessage();
            JOptionPane.showMessageDialog((JFrame)(getTopLevelAncestor()), text);
            statusLabel.setText("Search failed.");            
            return;            
        } catch (Exception e){
            e.printStackTrace();
            String text = "Fehler beim Auswerten der Suche: " + e.getMessage();
            JOptionPane.showMessageDialog((JFrame)(getTopLevelAncestor()), text);
            statusLabel.setText("Search failed.");            
            return;
        }
    }
    
    private void formatTable(){
        javax.swing.table.DefaultTableCellRenderer dtcr3 = new javax.swing.table.DefaultTableCellRenderer();
        dtcr3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);        
        getSearchResultTable().getColumnModel().getColumn(3).setCellRenderer(dtcr3);        
        
        javax.swing.table.DefaultTableCellRenderer dtcr4 = new javax.swing.table.DefaultTableCellRenderer();
        dtcr4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dtcr4.setForeground(java.awt.Color.RED);                
        getSearchResultTable().getColumnModel().getColumn(4).setCellRenderer(dtcr4);        
        
        AnnotationMatchCellRenderer tcr6 = new AnnotationMatchCellRenderer();
        getSearchResultTable().getColumnModel().getColumn(6).setCellRenderer(tcr6);        
        
        getSearchResultTable().getColumnModel().getColumn(0).setPreferredWidth(50);
        getSearchResultTable().getColumnModel().getColumn(1).setPreferredWidth(100);
        getSearchResultTable().getColumnModel().getColumn(2).setPreferredWidth(70);
        getSearchResultTable().getColumnModel().getColumn(3).setPreferredWidth(250);
        getSearchResultTable().getColumnModel().getColumn(4).setPreferredWidth(70);
        getSearchResultTable().getColumnModel().getColumn(5).setPreferredWidth(250);                
        getSearchResultTable().getColumnModel().getColumn(6).setPreferredWidth(100);                
        getSearchResultTable().getColumnModel().getColumn(7).setPreferredWidth(50);                
    }

    public AbstractSearchResult getSearchResult() {
        return ((AnnotationSearchResultTableModel)
               ((TableSorter)(getSearchResultTable().getModel()))
               .getTableModel()).searchResult;
    }

    public void showSegmentChain() {
        AnnotationSearchResultItem tsri = (AnnotationSearchResultItem)(getSelectedSearchResultItem()); 
        if (tsri==null) return;
        String text = tsri.segment.getDescription();
        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        sb.append(StringUtilities.checkHTML(text.substring(0,tsri.matchStart)));
        sb.append("<b>");
        sb.append(StringUtilities.checkHTML(text.substring(tsri.matchStart, tsri.matchStart + tsri.matchLength)));
        sb.append("</b>");
        sb.append(StringUtilities.checkHTML(text.substring(tsri.matchStart + tsri.matchLength)));
        sb.append("</html>");
        TextDialog d = new TextDialog((JFrame)(getTopLevelAncestor()), true, sb.toString());        
        d.show();        
    }

    public CorpusTree getCorpus() {
        // TO DO!!!
        return null;
    }
    
    
}
