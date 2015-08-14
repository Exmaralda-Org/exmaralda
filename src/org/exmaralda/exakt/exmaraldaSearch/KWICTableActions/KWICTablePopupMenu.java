/*
 * KWICTablePopupMenu.java
 *
 * Created on 16. Februar 2007, 15:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.Component;
import javax.swing.*;
import org.exmaralda.exakt.exmaraldaSearch.*;
import org.exmaralda.exakt.exmaraldaSearch.swing.*;

/**
 *
 * @author thomas
 */
public class KWICTablePopupMenu extends javax.swing.JPopupMenu {
    
    COMAKWICTable table;
    
    JMenuItem selectAllMenuItem;
    JMenuItem deselectAllMenuItem;
    JMenuItem selectHighlightedMenuItem;
    JMenuItem deselectHighlightedMenuItem;
    JMenuItem filterMenuItem;
    JMenuItem editAnalysisMenuItem;
    JMenuItem removeAnalysisMenuItem;
    JMenuItem calculateAnalysisMenuItem;
    
    /** Creates a new instance of KWICTablePopupMenu */
    public KWICTablePopupMenu(COMAKWICTable t) {        
        
        table = t;
        
        selectAllMenuItem = this.add(table.selectAllAction);           
        deselectAllMenuItem = this.add(table.deselectAllAction);           
        selectHighlightedMenuItem = this.add(table.selectHighlightedAction);           
        deselectHighlightedMenuItem = this.add(table.deselectHighlightedAction);           
        filterMenuItem = this.add(table.filterAction);
        editAnalysisMenuItem = this.add(table.editAnalysisAction);
        removeAnalysisMenuItem = this.add(table.removeAnalysisAction);
        calculateAnalysisMenuItem = this.add(table.calculateAnalysisAction);
    }
    
    public void show(Component invoker, int x, int y, int column) {
        selectAllMenuItem.setVisible(column==0);
        deselectAllMenuItem.setVisible(column==0);
        selectHighlightedMenuItem.setVisible((column==0) && (table.getSelectedRowCount()>0));
        deselectHighlightedMenuItem.setVisible((column==0) && (table.getSelectedRowCount()>0));
        editAnalysisMenuItem.setVisible(table.getWrappedModel().isAnalysisColumn(column));
        removeAnalysisMenuItem.setVisible(table.getWrappedModel().isAnalysisColumn(column));
        calculateAnalysisMenuItem.setVisible(table.getWrappedModel().isAnalysisColumn(column) && table.getWrappedModel().getAnalysisForColumn(column) instanceof org.exmaralda.exakt.search.analyses.FreeAnalysis);
                
        filterMenuItem.setVisible(column!=0);
        
        if (column!=0){
            table.filterAction.setSelectedColumn(column);
            table.editAnalysisAction.setSelectedColumn(column);
            table.removeAnalysisAction.setSelectedColumn(column);
            table.calculateAnalysisAction.setSelectedColumn(column);
        }
        
        
        super.show(invoker, x, y);
    }
    
}
