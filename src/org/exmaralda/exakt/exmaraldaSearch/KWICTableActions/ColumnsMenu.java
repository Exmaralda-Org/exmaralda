/*
 * FileMenu.java
 *
 * Created on 9. Februar 2007, 11:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import org.exmaralda.exakt.exmaraldaSearch.swing.*;


/**
 *
 * @author thomas
 */
public class ColumnsMenu extends javax.swing.JMenu {
    
    EXAKT exaktFrame;
    
    private JMenuItem filterMenuItem;
    private JMenuItem specifyMetadataMenuItem;
    private JMenuItem addAnnotationMenuItem;
    private JMenuItem addAnalysisMenuItem;
    private JMenuItem importAnalysesMenuItem;
    
    
    /** Creates a new instance of FileMenu */
    public ColumnsMenu(EXAKT ef) {
        setText("Columns");
        exaktFrame = ef;

        filterMenuItem = new JMenuItem("Filter...");
        filterMenuItem.addActionListener(new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                filter(e);
            }
        });
        this.add(filterMenuItem);

        this.addSeparator();

        specifyMetadataMenuItem = new JMenuItem("Metadata...");
        specifyMetadataMenuItem.addActionListener(new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                specifyMetadata(e);
            }
        });
        this.add(specifyMetadataMenuItem);


        this.addSeparator();

        addAnnotationMenuItem = new JMenuItem("Add annotation...");
        addAnnotationMenuItem.addActionListener(new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                addAnnotation(e);
            }
        });
        this.add(addAnnotationMenuItem);

        addAnalysisMenuItem = new JMenuItem("Add analysis...");
        addAnalysisMenuItem.addActionListener(new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                addAnalysis(e);
            }
        });
        this.add(addAnalysisMenuItem);


        importAnalysesMenuItem = new JMenuItem("Import analyses...");
        importAnalysesMenuItem.addActionListener(new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                importAnalyses(e);
            }
        });
        this.add(importAnalysesMenuItem);        

        this.enableMenuItems(false);
    }

    void filter(ActionEvent e){
        if (exaktFrame.getActiveSearchPanel()==null) return;
        RegExFilterAction filterAction = exaktFrame.getActiveSearchPanel().getKWICTable().filterAction;
        int col = exaktFrame.getActiveSearchPanel().getKWICTable().getSelectedColumn();
        filterAction.setSelectedColumn(col);
        filterAction.actionPerformed(e);        
    }

    void specifyMetadata(ActionEvent e){
        if (exaktFrame.getActiveSearchPanel()==null) return;
        exaktFrame.getActiveSearchPanel().specifyMetadata();
    }

    void addAnnotation(ActionEvent e){
        if (exaktFrame.getActiveSearchPanel()==null) return;
        exaktFrame.getActiveSearchPanel().getKWICTable().addAnnotationAction.actionPerformed(e);
    }

    void addAnalysis(ActionEvent e){
        if (exaktFrame.getActiveSearchPanel()==null) return;
        exaktFrame.getActiveSearchPanel().getKWICTable().addAnalysisAction.actionPerformed(e);
    }

    void importAnalyses(ActionEvent e){
        if (exaktFrame.getActiveSearchPanel()==null) return;
        exaktFrame.getActiveSearchPanel().getKWICTable().importAnalysesAction.actionPerformed(e);
    }

    public void enableMenuItems(boolean enabled){
        filterMenuItem.setEnabled(enabled);
        specifyMetadataMenuItem.setEnabled(enabled);
        addAnalysisMenuItem.setEnabled(enabled);
        importAnalysesMenuItem.setEnabled(enabled);
    }

    
    
}
