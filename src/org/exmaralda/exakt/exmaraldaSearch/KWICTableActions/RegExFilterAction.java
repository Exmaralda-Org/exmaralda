/*
 * RegExFilterAction.java
 *
 * Created on 1. Maerz 2007, 11:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICTable;

/**
 *
 * @author thomas
 */
public class RegExFilterAction extends AbstractKWICTableAction {
        
    org.exmaralda.exakt.exmaraldaSearch.swing.RegExFilterPanel regexFilterPanel;
    org.exmaralda.exakt.search.swing.AbstractOKCancelDialog filterDialog;
    
    private int selectedColumn = -1;
            
    /** Creates a new instance of RegExFilterAction
     * @param t
     * @param title */
    public RegExFilterAction(COMAKWICTable t, String title) {
        super(t,title);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("*** REGEX FILTER ACTION");
        regexFilterPanel = new org.exmaralda.exakt.exmaraldaSearch.swing.RegExFilterPanel(table.getWrappedModel());
        Container c = table.getTopLevelAncestor();
        if (c instanceof JFrame){
            javax.swing.JFrame exaktFrame = (javax.swing.JFrame)(table.getTopLevelAncestor());
            filterDialog = new org.exmaralda.exakt.search.swing.AbstractOKCancelDialog(exaktFrame,true,regexFilterPanel);
        } else if (c instanceof JDialog){
            javax.swing.JDialog exaktDialog = (javax.swing.JDialog)(table.getTopLevelAncestor());
            filterDialog = new org.exmaralda.exakt.search.swing.AbstractOKCancelDialog(exaktDialog,true,regexFilterPanel);
        } 
        filterDialog.setTitle("Filter");
        filterDialog.setPreferredSize(new java.awt.Dimension(500, filterDialog.getPreferredSize().height));
        java.awt.Dimension dialogSize = filterDialog.getPreferredSize();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        filterDialog.setLocation(screenSize.width/2 - dialogSize.width/2, screenSize.height/2 - dialogSize.height*2/3);                
        regexFilterPanel.setSelectedColumn(getSelectedColumn());
        filterDialog.setVisible(true);
        if (filterDialog.isApproved()){
            int column = regexFilterPanel.getColumnIndex();
            String regex = regexFilterPanel.getRegularExpression();
            boolean invert = regexFilterPanel.getInvert();
            
            // 16-05-2025: issue #515
            boolean fullMatch = regexFilterPanel.getFullMatch();
            //table.getWrappedModel().filter(column, regex, invert);
            table.getWrappedModel().filter(column, regex, invert, fullMatch);

            // 16-05-2025, issue #514
            Preferences prefs = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.EXAKT");
            boolean deleteUnfiltered = prefs.getBoolean("delete-unfiltered", false);
            if (deleteUnfiltered){
                table.getWrappedModel().removeUnselected();
            }


            table.setCellEditors();
            table.adjustColumns();
            
            ((ExmaraldaApplication)(table.getTopLevelAncestor())).status("Filter " + regex + " applied to " + table.getColumnName(column) + ".");
            
        }
        selectedColumn = -1;
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    public void setSelectedColumn(int selectedColumn) {
        this.selectedColumn = selectedColumn;
    }
    
}
