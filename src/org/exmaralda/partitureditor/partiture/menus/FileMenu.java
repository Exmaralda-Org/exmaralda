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

/**
 *
 * @author  thomas
 */
public class FileMenu extends AbstractTableMenu {
    
    private final JMenuItem newMenuItem;
    private final JMenuItem openMenuItem;
    private final JMenuItem saveMenuItem;
    
    private final JMenuItem printMenuItem;


   
    /** Creates a new instance of FileMenu */
    public FileMenu(PartitureTableWithActions t) {
        super(t);

        this.setText("File");
        this.setMnemonic(java.awt.event.KeyEvent.VK_F);
        
        
      
        newMenuItem = this.add(table.newAction);
        // this is because MAC OS X ignores the accelerator assigned to the action and this in turn is because
        // there is a bug in 1.3.1. (fixed in 1.4.)
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        newMenuItem.setToolTipText("Create a new empty transcription with one speaker and one tier");
                
        addSeparator();

        add(table.newFromWizardAction).setToolTipText("Create a new transcription by defining its properties in a wizard step-by-step");
        add(table.newFromSpeakertableAction).setToolTipText("Define speakers, then create a new transcription with one tier for each speaker");
        add(table.newFromTimelineAction).setToolTipText("Choose a media file, then pre-segment it and create a new transcription with the resulting time intervals");
        add(table.newFromSilenceDetectionAction).setToolTipText("Run a silence detection on a WAV file and create a new transcription with the resulting time intervals");

        addSeparator();
        
        openMenuItem = this.add(table.openAction);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));        
        openMenuItem.setToolTipText("Open a basic transcription (*.exb) from file");
        
        add(table.restoreAction).setToolTipText("Go back to the last saved version of the current transcription");
        
        saveMenuItem = this.add(table.saveAction);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveMenuItem.setToolTipText("Save the transcription under its current name and location");
        
        add(table.saveAsAction).setToolTipText("Save the transcription under a new name and/or location");
        
        add(new javax.swing.JSeparator());

        add(table.editErrorsAction).setToolTipText("Open an error list created by the Corpus Manager");

        add(new javax.swing.JSeparator());

        add(table.pageSetupAction).setToolTipText("Define the page setup for printing");
        
        //add(table.editPartiturParametersAction);
        
        printMenuItem = this.add(table.printAction);
        printMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));        
        printMenuItem.setToolTipText("Print the transcctiption");
        
        addSeparator();
        //-------------------------------------------------
       
        add(table.outputAction).setToolTipText("Generate an output of this transcription to view/process in a browser, a word processor, etc.");
        add(table.importAction).setToolTipText("Import a transcription in a format from another tool (e.g. Praat, FOLKER, ELAN)");
        add(table.exportAction).setToolTipText("Export the transcription to the format of another tool (e.g. Praat, FOLKER, ELAN)");

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
