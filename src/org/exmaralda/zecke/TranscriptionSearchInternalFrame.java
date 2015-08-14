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

/**
 *
 * @author thomas
 */
public class TranscriptionSearchInternalFrame extends AbstractSearchInternalFrame {
    
    SimpleTranscriptionSearch search;
    CorpusTree corpusTree;
    //TranscriptionSearchResult result;
    //TableSorter tableSorter;
    //TranscriptionSearchResultTableModel meinTableModel;
    
    /** Creates a new instance of TranscriptionSearchInternalFrame */
    public TranscriptionSearchInternalFrame() {
        super();
        
        setTitle("Transcription search");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/zecke/transcription.png")));        

        TranscriptionSearchResultTableModel meinTableModel = new TranscriptionSearchResultTableModel(new TranscriptionSearchResult());
        TableSorter tableSorter = new TableSorter(meinTableModel);      
        getSearchResultTable().setModel(tableSorter);
        tableSorter.setTableHeader(getSearchResultTable().getTableHeader());        
        //getSearchResultTable().setModel(meinTableModel);
        
        formatTable();
    }
    
    public void setCorpus(CorpusTree ct){
        search = new SimpleTranscriptionSearch(ct);
        search.addSearchListener(this);        
        corpusTree = ct;
    }
    
    public CorpusTree getCorpus(){
        return corpusTree;
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
        TranscriptionSearchResultTableModel tsrtm = ((TranscriptionSearchResultTableModel)
                ((TableSorter)(getSearchResultTable().getModel())).getTableModel());        
        tsrtm.setContextSize(tsrtm.getContextSize()+5);        
    }                                                  
   
    public void lessContext(){
        TranscriptionSearchResultTableModel tsrtm = ((TranscriptionSearchResultTableModel)
                ((TableSorter)(getSearchResultTable().getModel())).getTableModel());        
        tsrtm.setContextSize(Math.max(1,tsrtm.getContextSize()-5));        
    }
    
    public AbstractSearchResultItem getSelectedSearchResultItem(){
        int sel = getSearchResultTable().getSelectedRow();
        if (sel<0) return null;
        int no = Integer.parseInt((String)(getSearchResultTable().getValueAt(sel,0)));
        TranscriptionSearchResultItem tsri = (TranscriptionSearchResultItem)
                                            (((TranscriptionSearchResultTableModel)
                                            ((TableSorter)(getSearchResultTable().getModel())).getTableModel())
                                            .searchResult.elementAt(no)); 
        return tsri;
    }

    void search(){
        statusLabel.setText("Searching...");
        currentProgress = 0;
        System.out.println(Runtime.getRuntime().freeMemory());
        try {
            
            String searchExpression = getSearchExpression();
            TranscriptionSearchResult result = (TranscriptionSearchResult)(search.search(AbstractSearch.REGULAR_EXPRESSION_SEARCH_TYPE, searchExpression, getMetaDataKeys()));

            TableSorter tableSorter = (TableSorter)(getSearchResultTable().getModel());
            ((TranscriptionSearchResultTableModel)(tableSorter.getTableModel())).setSearchResult(result);
                        
            formatTable();            

            String message =  Double.toString(search.getTimeForLastSearch()) + " seconds / ";
            message+= result.size() + " results. ";
            statusLabel.setText(message);
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
        
        getSearchResultTable().getColumnModel().getColumn(0).setPreferredWidth(50);
        getSearchResultTable().getColumnModel().getColumn(1).setPreferredWidth(100);
        getSearchResultTable().getColumnModel().getColumn(2).setPreferredWidth(70);
        getSearchResultTable().getColumnModel().getColumn(3).setPreferredWidth(250);
        getSearchResultTable().getColumnModel().getColumn(4).setPreferredWidth(70);
        getSearchResultTable().getColumnModel().getColumn(5).setPreferredWidth(250);                
    }

    public AbstractSearchResult getSearchResult() {
        return ((TranscriptionSearchResultTableModel)
               ((TableSorter)(getSearchResultTable().getModel()))
               .getTableModel()).searchResult;
    }

    public void showSegmentChain() {
        TranscriptionSearchResultItem tsri = (TranscriptionSearchResultItem)(getSelectedSearchResultItem()); 
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
    
    
}
