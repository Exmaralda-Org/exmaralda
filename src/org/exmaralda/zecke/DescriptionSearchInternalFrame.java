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
public class DescriptionSearchInternalFrame extends AbstractSearchInternalFrame {
    
    SimpleDescriptionSearch search;
    DescriptionSearchResult result;
    TableSorter tableSorter;
    DescriptionSearchResultTableModel meinTableModel;

    /** Creates a new instance of TranscriptionSearchInternalFrame */
    public DescriptionSearchInternalFrame() {
        super();
        
        setTitle("Description search");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/zecke/description.png")));        
        
        meinTableModel = new DescriptionSearchResultTableModel(new DescriptionSearchResult());
        tableSorter = new TableSorter(meinTableModel);      
        getSearchResultTable().setModel(tableSorter);
        tableSorter.setTableHeader(getSearchResultTable().getTableHeader());      
        
        this.showSegmentChainButton.setVisible(false);
        this.contexPlustButton.setVisible(false);
        this.contextMinusButton.setVisible(false);
        
        formatTable();
    }
    
    public void setCorpus(CorpusTree ct){
        search = new SimpleDescriptionSearch(ct);
        search.addSearchListener(this);
    }
    
    public AbstractSearchResult getSearchResult(){
        return result;
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
        meinTableModel.setContextSize(meinTableModel.getContextSize()+5);        
    }                                                  
   
    public void lessContext(){
        meinTableModel.setContextSize(Math.max(1,meinTableModel.getContextSize()-5));
    }
    
    public AbstractSearchResultItem getSelectedSearchResultItem(){
        int sel = getSearchResultTable().getSelectedRow();
        if (sel<0) return null;
        int no = Integer.parseInt((String)(getSearchResultTable().getValueAt(sel,0)));
        DescriptionSearchResultItem tsri = (DescriptionSearchResultItem)(result.elementAt(no)); 
        return tsri;
    }

    void search(){
        statusLabel.setText("Searching...");
        currentProgress = 0;
        try {
            String searchExpression = getSearchExpression();
            result = (DescriptionSearchResult)(search.search(AbstractSearch.REGULAR_EXPRESSION_SEARCH_TYPE, searchExpression));

            meinTableModel = new DescriptionSearchResultTableModel(result);
            tableSorter = new TableSorter(meinTableModel);
            getSearchResultTable().setModel(tableSorter);
            tableSorter.setTableHeader(getSearchResultTable().getTableHeader());
                      
            //searchResultTable.setModel(new TranscriptionSearchResultTableModel(result));
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
        getSearchResultTable().getColumnModel().getColumn(6).setPreferredWidth(70);                
    }
    
    public void showSegmentChain(){
        return;
    }
    
    public CorpusTree getCorpus() {
        // TO DO!!!
        return null;
    }
    
    
    
}
