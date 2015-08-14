/*
 * this.java
 *
 * Created on 1. Juli 2003, 14:36
 */

package org.exmaralda.partitureditor.partiture.menus;

import javax.swing.*;
import org.exmaralda.partitureditor.partiture.*;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import org.exmaralda.partitureditor.partiture.fileActions.OpenRecentAction;
import org.exmaralda.common.helpers.Internationalizer;

/**
 *
 * @author  thomas
 */
public class FileMenu extends AbstractTableMenu {
    
    private JMenuItem newMenuItem;
    private JMenuItem openMenuItem;
    private JMenuItem saveMenuItem;
    
    private JMenuItem printMenuItem;


   
    /** Creates a new instance of FileMenu */
    public FileMenu(PartitureTableWithActions t) {
        super(t);

        this.setText("File");
        this.setMnemonic(java.awt.event.KeyEvent.VK_F);
        
        
      
        newMenuItem = this.add(table.newAction);
        // this is because MAC OS X ignores the accelerator assigned to the action and this in turn is because
        // there is a bug in 1.3.1. (fixed in 1.4.)
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                
        addSeparator();

        add(table.newFromWizardAction);
        add(table.newFromSpeakertableAction);
        add(table.newFromTimelineAction);

        addSeparator();
        
        openMenuItem = this.add(table.openAction);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));        

        add(table.restoreAction);
        
        saveMenuItem = this.add(table.saveAction);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        
        add(table.saveAsAction);
        
        add(new javax.swing.JSeparator());

        add(table.editErrorsAction);

        add(new javax.swing.JSeparator());

        add(table.pageSetupAction);
        
        //add(table.editPartiturParametersAction);
        
        printMenuItem = this.add(table.printAction);
        printMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));        
        
        addSeparator();
        //-------------------------------------------------
       
        add(table.outputAction);
        add(table.importAction);
        add(table.exportAction);

        addSeparator();
        //-------------------------------------------------
        
    }
    
    public void setupOpenRecentMenu(java.util.Vector recentFiles){
        //openRecentMenu.removeAll();
        int fileCount = 0;
        int pos=0;
        java.util.HashSet hs = new java.util.HashSet();        
        while ((fileCount<5) && (pos<recentFiles.size())){
            String filename = (String)(recentFiles.elementAt(pos));
            if (!hs.contains(filename)){
                if (new java.io.File(filename).exists()){
                    add(new OpenRecentAction(table, filename)).setToolTipText(filename);
                    hs.add(filename);
                    fileCount++;
                }
            }
            pos++;
        }
        //addSeparator();
    }
    
}
