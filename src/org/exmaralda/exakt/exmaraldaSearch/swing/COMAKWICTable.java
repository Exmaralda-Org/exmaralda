/*
 * COMAKWICTable.java
 *
 * Created on 15. Januar 2007, 11:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.swing;

import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import javax.swing.*;
import java.util.*;
import org.exmaralda.exakt.search.SearchResultList;
import org.exmaralda.exakt.search.analyses.AnalysisInterface;
import org.exmaralda.exakt.search.analyses.BinaryAnalysis;
import org.exmaralda.exakt.search.analyses.ClosedCategoryListAnalysis;
import org.exmaralda.exakt.search.swing.KWICTableEvent;
import org.exmaralda.exakt.search.swing.KWICTableListener;
import org.exmaralda.exakt.exmaraldaSearch.*;


/**
 *
 * @author thomas
 */
public class COMAKWICTable  extends javax.swing.JTable 
                            implements   java.awt.event.MouseListener, 
                                         javax.swing.event.TableModelListener {
    
    
    Vector<KWICTableListener> listenerList = new Vector<KWICTableListener>();
    boolean tableInitialised = false;
    
    public org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.CopyAction copyAction;
    public org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.ImportAnalysesAction importAnalysesAction;
    public org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.SampleAction sampleAction;
    public org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.SelectAllAction selectAllAction;
    public org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.DeselectAllAction deselectAllAction;
    public org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.SelectHighlightedAction selectHighlightedAction;
    public org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.DeselectHighlightedAction deselectHighlightedAction;
    public org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.MoreContextAction moreContextAction;
    public org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.LessContextAction lessContextAction;
    public org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.RemoveUnselectedAction removeUnselectedAction;
    public org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.RegExFilterAction filterAction;
    public org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.AddAnalysisAction addAnalysisAction;
    public org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.EditAnalysisAction editAnalysisAction;
    public org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.RemoveAnalysisAction removeAnalysisAction;
    public org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.CalculateAnalysisAction calculateAnalysisAction;
    public org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.AddAnnotationAction addAnnotationAction;
    
    org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.KWICTablePopupMenu popup;
    
    /** Creates a new instance of COMAKWICTable */
    public COMAKWICTable() {
        putClientProperty("Quaqua.Table.style", "striped");
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //this.setColumnSelectionAllowed(true);
        setWrappedModel(new COMASearchResultListTableModel(new SearchResultList(), new COMACorpus(), new Vector<String[]>()));

        //formatTable();
        //tableInitialised = true;

        initActions();

        //this.setAutoCreateColumnsFromModel(true);
        
        addMouseListener(this);
        getTableHeader().addMouseListener(this);
        getSelectionModel().addListSelectionListener(this);
                
    }
    
    public void addKWICTableListener(KWICTableListener ktl){
         listenerList.addElement(ktl);        
    }
    
    private void initActions(){
        copyAction = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.CopyAction(this, "Copy");
        importAnalysesAction = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.ImportAnalysesAction(this, "Import analyses...");
        sampleAction = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.SampleAction(this, "Sample...");
        selectAllAction = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.SelectAllAction(this, "Select all");
        deselectAllAction = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.DeselectAllAction(this, "Deselect all");
        selectHighlightedAction = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.SelectHighlightedAction(this, "Select highlighted");
        deselectHighlightedAction = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.DeselectHighlightedAction(this, "Deselect highlighted");
        moreContextAction = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.MoreContextAction(this, "More context");
        lessContextAction = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.LessContextAction(this, "Less context");
        removeUnselectedAction = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.RemoveUnselectedAction(this,"Remove unselected");
        filterAction = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.RegExFilterAction(this,"Filter");
        addAnalysisAction = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.AddAnalysisAction(this,"Add analysis");        
        addAnnotationAction = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.AddAnnotationAction(this,"Add annotation");
        editAnalysisAction = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.EditAnalysisAction(this,"Edit analysis");        
        removeAnalysisAction = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.RemoveAnalysisAction(this,"Remove analysis");        
        calculateAnalysisAction = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.CalculateAnalysisAction(this,"Calculate analysis");        
        popup = new org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.KWICTablePopupMenu(this);    
        
    }
    
    public void setCellEditors(){
        int count = -1;
        for (AnalysisInterface ai : this.getWrappedModel().getData().getAnalyses()){
            count++;
            if (!(ai instanceof ClosedCategoryListAnalysis)) continue;
            ClosedCategoryListAnalysis ca = (ClosedCategoryListAnalysis)ai;                
            // changed (increment 1) for row numbering in version 0.4, 22-Jan-2008
            //int columnIndex = this.getWrappedModel().dataIndexToAnalysisColumnIndex(count);
            int columnIndex = this.getWrappedModel().dataIndexToAnalysisColumnIndex(count) + 1;
            //System.out.println("Setting editor for column " + columnIndex);
            javax.swing.table.TableColumn tc = this.getColumnModel().getColumn(columnIndex);
            DefaultCellEditor editor = new DefaultCellEditor(new JComboBox(ca.getCategories()));
            tc.setCellEditor(editor);     
        }        
    }
    

    private void formatTable(){               
        if ((getModel()!=null) && (getModel().getColumnCount()>6)){
            // added for row numbering in version 0.4, 22-Jan-2008
            if (!tableInitialised){
                javax.swing.table.TableColumn numberingColumn = getColumnModel().getColumn(0);
                numberingColumn.setPreferredWidth(30);

                // changed (increment 1) for row numbering in version 0.4, 22-Jan-2008
                javax.swing.table.TableColumn selectionColumn = getColumnModel().getColumn(1);
                selectionColumn.setMaxWidth(30);
                selectionColumn.setPreferredWidth(30);

                javax.swing.table.TableColumn transcriptionColumn = getColumnModel().getColumn(2);
                transcriptionColumn.setMaxWidth(500);
                transcriptionColumn.setPreferredWidth(Math.max(transcriptionColumn.getPreferredWidth(),150));

                javax.swing.table.TableColumn speakerColumn = getColumnModel().getColumn(3);
                speakerColumn.setMaxWidth(200);
                speakerColumn.setPreferredWidth(Math.max(speakerColumn.getPreferredWidth(),70));

                javax.swing.table.TableColumn leftContextColumn = getColumnModel().getColumn(4);
                leftContextColumn.setMaxWidth(800);
                leftContextColumn.setPreferredWidth(Math.max(leftContextColumn.getPreferredWidth(),270));

                javax.swing.table.TableColumn matchTextColumn = getColumnModel().getColumn(5);
                matchTextColumn.setMaxWidth(800);
                matchTextColumn.setPreferredWidth(Math.max(matchTextColumn.getPreferredWidth(),70));

                javax.swing.table.TableColumn rightContextColumn = getColumnModel().getColumn(6);
                rightContextColumn.setMaxWidth(800);
                rightContextColumn.setPreferredWidth(Math.max(rightContextColumn.getPreferredWidth(),270));
            }
            javax.swing.table.JTableHeader header = this.getTableHeader();
            //header.setPreferredSize(new java.awt.Dimension(header.getPreferredSize().width, 30));
            header.revalidate();
            header.repaint();
            tableInitialised = true;
        }        
    }
    
    public void setWrappedModel(COMASearchResultListTableModel model){
        COMAKWICTableSorter tableSorter = new COMAKWICTableSorter(model);
        setModel(tableSorter);        
        tableSorter.setTableHeader(getTableHeader());      
        //setCellEditors();
    }
    
    public COMASearchResultListTableModel getWrappedModel(){
        COMAKWICTableSorter tableSorter = (COMAKWICTableSorter)getModel();
        return (COMASearchResultListTableModel)(tableSorter.getTableModel());
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        TableCellRenderer retValue;        
        switch (column){
            // added for row numbering in version 0.4, 22-Jan-2008
            case 0 :    //numbering column
                        DefaultTableCellRenderer dtcr0 = new DefaultTableCellRenderer();
                        dtcr0.setForeground(java.awt.Color.BLACK);
                        dtcr0.setBackground(java.awt.Color.LIGHT_GRAY);
                        return dtcr0;
            // changed (increment 1) for row numbering in version 0.4, 22-Jan-2008
            case 2 :    // communication column
                        DefaultTableCellRenderer dtcr5 = new KWICTableCellRenderer(Color.BLACK);
                        return dtcr5;
            case 3 :    // speaker column     
                        DefaultTableCellRenderer dtcr1 = new KWICTableCellRenderer(Color.BLUE);
                        dtcr1.setForeground(java.awt.Color.BLUE);
                        return dtcr1;
            case 4 :    // left context column
                        DefaultTableCellRenderer dtcr2 = new KWICTableCellRenderer(Color.BLACK);
                        dtcr2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);        
                        return dtcr2;
            case 5 :    // match text column
                        DefaultTableCellRenderer dtcr3 = new DefaultTableCellRenderer();
                        dtcr3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);        
                        dtcr3.setForeground(java.awt.Color.RED);
                        return dtcr3;
            case 6 :    // right context column
                        DefaultTableCellRenderer dtcr6 = new KWICTableCellRenderer(Color.BLACK);
                        return dtcr6;
            default :   // other columns
                        // changed (decrement 1) for row numbering in version 0.4, 22-Jan-2008
                        // if ((getWrappedModel().isAnalysisColumn(column)) 
                        //    && (!(getWrappedModel().getAnalysisForColumn(column) instanceof BinaryAnalysis))){
                        if ((getWrappedModel().isAnalysisColumn(column-1)) 
                            && (!(getWrappedModel().getAnalysisForColumn(column-1) instanceof BinaryAnalysis))){
                            //((java.awt.Component)(retValue)).setBackground(java.awt.Color.YELLOW);
                            DefaultTableCellRenderer dtcr4 = new DefaultTableCellRenderer();
                            dtcr4.setForeground(new java.awt.Color(0,128,64));
                            return dtcr4;
                        } else {
                            retValue = super.getCellRenderer(row, column);
                            return retValue;                            
                        }
                        //return dtcr4;
        }        
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        //System.out.println("Table changed!");
        super.tableChanged(e);
        if (e.getSource() instanceof COMASearchResultListTableModel){                        
            //System.out.println("Type = " + e.getType() + " (" + e.toString() + ")");

            // changed 20-10-2009: remember the column widths
            int[] widths = new int[this.getColumnCount()];
            for (int pos=0; pos<getColumnCount(); pos++){
                widths[pos] = this.getColumnModel().getColumn(pos).getPreferredWidth();
            }

            createDefaultColumnsFromModel();

            for (int pos=0; pos<widths.length; pos++){
                if (pos<getColumnModel().getColumnCount()){
                    getColumnModel().getColumn(pos).setPreferredWidth(widths[pos]);
                }
            }

            setCellEditors();
            tableInitialised = false;
        }
        formatTable();
    }

    public void mouseReleased(MouseEvent e) {
        if (e.getSource() instanceof JTableHeader){
            JTableHeader h = (JTableHeader) e.getSource();
            TableColumnModel columnModel = h.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            // changed for row numbering in version 0.4, 22-Jan-2008
            //int column = columnModel.getColumn(viewColumn).getModelIndex();
            if (viewColumn==0) return;
            int column = columnModel.getColumn(viewColumn-1).getModelIndex();
            System.out.println(column);
            //super.mouseReleased(e);
            if (e.isPopupTrigger()){
                showPopup(e, column);
            }        
        }
    }

    public void mousePressed(MouseEvent e) {
        if (e.getSource() instanceof JTableHeader){
            JTableHeader h = (JTableHeader) e.getSource();
            TableColumnModel columnModel = h.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            // changed for row numbering in version 0.4, 22-Jan-2008
            //int column = columnModel.getColumn(viewColumn).getModelIndex();
            if ((viewColumn==0) || (viewColumn>=columnModel.getColumnCount())) return;
            int column = columnModel.getColumn(viewColumn-1).getModelIndex();
            System.out.println(column);
            //super.mousePressed(e);
            if (e.isPopupTrigger()){
                showPopup(e, column);
            }
        }
        
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount()==2){
            fireSearchResultDoubleClicked();
        }                
    }
    
    public void fireSearchResultDoubleClicked(){
        int viewSelection = getSelectedRow();
        if (viewSelection<0) return;
        int selectedRow = ((COMAKWICTableSorter)(getModel())).modelIndex(viewSelection);
        KWICTableEvent event = new KWICTableEvent(KWICTableEvent.DOUBLE_CLICK,getWrappedModel().getSearchResultAt(selectedRow));
        fireEvent(event);        
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e) {
        super.valueChanged(e);
        int viewSelection = getSelectedRow();
        if (viewSelection<0) return;
        int selectedRow = ((COMAKWICTableSorter)(getModel())).modelIndex(viewSelection);
        KWICTableEvent event = new KWICTableEvent(KWICTableEvent.SELECTION, getWrappedModel().getSearchResultAt(selectedRow));
        fireEvent(event);
        
    }

    protected void fireEvent(KWICTableEvent ev){
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listenerList.size()-1; i>=0; i-=1) {           
            listenerList.elementAt(i).processEvent(ev);
         }                        
    }
    
    public void showPopup(java.awt.event.MouseEvent e, int column){
        popup.show(this.getTableHeader(), e.getX(), e.getY(), column);
    }

    
    

    
    
}
