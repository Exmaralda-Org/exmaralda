/*
 * OpenRecentAction.java
 *
 * Created on 8. Maerz 2004, 16:46
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import com.klg.jclass.table.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.*;
import java.io.*;
import java.net.*;
import org.xml.sax.*;
import javax.swing.Action;
import java.awt.Toolkit;

/**
 *
 * @author  thomas
 */
public class OpenRecentAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    String fileToOpen;
    
    /** Creates a new instance of OpenRecentAction */
    public OpenRecentAction(PartitureTableWithActions t, String filename) {
        super(new File(filename).getName(), t);
        fileToOpen = filename;
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        System.out.println("OpenRecentAction!");
        // finish any editing actions that might still be in progress on the partitur
        table.commitEdit(true);
        boolean proceed = true;
        // check if the user wants to save changes
        if (table.transcriptionChanged){proceed = table.checkSave();}
        // if user has cancelled or something's gone wrong with saving, stop here
        if (!proceed) return;

        try {
            BasicTranscription newTranscription = new BasicTranscription(fileToOpen);
            // ... stratify the new transcription
            table.stratify(newTranscription);
            // ... make hidden tiers reappear...
            table.setRowHidden(JCTableEnum.ALLCELLS, false);
            // ...set the transcription for the partitur to the newly read transcription...
            table.getModel().setTranscription(newTranscription);
            // ...update the filename...
            table.homeDirectory = fileToOpen;
            table.setFilename(fileToOpen);
            // ...if the other directories have not yet been set: set them, too...
            if (table.rtfDirectory.length()==0)   {table.rtfDirectory = table.homeDirectory;}
            if (table.htmlDirectory.length()==0)  {table.htmlDirectory = table.homeDirectory;}
            if (table.txtDirectory.length()==0)   {table.txtDirectory = table.homeDirectory;}            
            // ... empty the link panel of its contents...
            table.linkPanelDialog.getLinkPanel().emptyContents();
            // ... and set its directory ...
            table.linkPanelDialog.getLinkPanel().setDirectory(table.homeDirectory);            
            table.setupMedia();
            table.setupPraatPanel();
            table.transcriptionChanged = false;
            table.formatChanged = false;
            table.status("Transcription " + fileToOpen + " opened");

        } catch (JexmaraldaException je){
            javax.swing.JOptionPane.showMessageDialog(  table.getParent(),
                                            "File does not seem to be valid: " + je.getMessage(),
                                            "jexmaralda error",
                                            javax.swing.JOptionPane.ERROR_MESSAGE);
        } catch (SAXException se){
            javax.swing.JOptionPane.showMessageDialog(  table.getParent(),
                                            "File could not be read." + System.getProperty("line.separator")
                                            + "Error message was:" + System.getProperty("line.separator")
                                            + se.getLocalizedMessage(),
                                            "SAX error",
                                            javax.swing.JOptionPane.ERROR_MESSAGE);
        }

        //table.reexportHTMLAction.setEnabled(false);
        table.restoreAction.setEnabled(true);       
        table.clearUndo();
        table.clearSearchResult();
        table.reconfigureAutoSaveThread();
    }
    
}
