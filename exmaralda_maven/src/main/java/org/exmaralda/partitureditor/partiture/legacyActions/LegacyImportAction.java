/*
 * ExportAGAction.java
 *
 * Created on 17. Juni 2003, 11:00
 */

package org.exmaralda.partitureditor.partiture.legacyActions;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.convert.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.folker.utilities.PreferencesUtilities;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.LegacyImportFileDialog;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * imports some 3rd party format
 * Menu: File --> Import...
 * @author  thomas
 */
public class LegacyImportAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {

    private final String[] encodings = {"US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE"};

    BasicTranscription importedTranscription = null;  
    boolean needsStratification = false;
    boolean needsCleanup = false;
    File importedFile;
    
    ProgressBarDialog pbd = new ProgressBarDialog((JFrame)(table.getTopLevelAncestor()), false);
    
    /** Creates a new instance of ExportAGAction
     * @param t
     * @param icon */
    public LegacyImportAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Legacy Import...", icon, t);
    }
    
    public void setImportedTranscription(BasicTranscription bt, File importedFile, boolean needsStratification, boolean needsCleanup){
        importedTranscription = bt;        
        this.importedFile = importedFile;
        this.needsStratification = needsStratification;
        this.needsCleanup = needsCleanup;
    }

    final Runnable doThreadImportFinished = new Runnable() {
        @Override
        public void run() {
            pbd.setVisible(false);
            if (needsStratification){
                table.stratify(importedTranscription);
            }
            if (needsCleanup){
                table.cleanup(importedTranscription);
            }            
            table.getModel().setTranscription(importedTranscription);
            table.setupMedia();
            table.setupPraatPanel();
            table.setFilename("untitled.exb");
            table.linkPanelDialog.getLinkPanel().emptyContents();
            table.largeTextField.setText("");
            table.restoreAction.setEnabled(false);
            table.reconfigureAutoSaveThread();   
            table.status("File " + importedFile.getAbsolutePath() + " imported");
        }
    };

    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        try {
            System.out.println("LegacyImportAction!");
            table.commitEdit(true);
            importFile();
            table.clearUndo();
            table.clearSearchResult();
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException | JexmaraldaException | JDOMException ex) {
            Logger.getLogger(LegacyImportAction.class.getName()).log(Level.SEVERE, null, ex);
            String message = "File could not be imported:\n" + ex.getLocalizedMessage();
            javax.swing.JOptionPane.showMessageDialog(table, message);            
        }
    }
    
    
    

    private void importFile() throws SAXException, IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException, JDOMException{
        // check if the user wants to save changes
        boolean proceed = true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;

        ExmaraldaApplication ea = (ExmaraldaApplication)(table.parent);
        String userNode = ea.getPreferencesNode();
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(userNode);
        String startDirectory = settings.get("LastImportDirectory", PreferencesUtilities.getProperty("workingDirectory", ""));

        // determine what to import
        LegacyImportFileDialog dialog = new LegacyImportFileDialog(startDirectory);
        //ActionUtilities.setFileFilter("last-import-filter", table.getTopLevelAncestor(), dialog);


        int retValue = dialog.showOpenDialog(table.parent);
        if (retValue!=javax.swing.JFileChooser.APPROVE_OPTION) return;
        ParameterFileFilter selectedFileFilter = (ParameterFileFilter)(dialog.getFileFilter());
        File selectedFile = dialog.getSelectedFile();
        settings.put("LastImportDirectory", selectedFile.getAbsolutePath());
        String filename = selectedFile.getAbsolutePath();

        // now do the real import      
        
        if (selectedFileFilter==dialog.TASXFileFilter){
            TASXConverter tc = new TASXConverter();
            importedTranscription = tc.readTASXFromFile(filename);
        } else if (selectedFileFilter==dialog.WinPitchFileFilter){
            WinPitchConverter tc = new WinPitchConverter();
            importedTranscription = tc.readWinPitchFromFile(filename);
        } else if (selectedFileFilter==dialog.AGFileFilter){
            AIFConverter ac = new AIFConverter();
            importedTranscription = ac.readAIFFromFile(filename);
        } else if (selectedFileFilter==dialog.RioDeJaneiroFileFilter){
            RioDeJaneiroTextConverter ser = null;
            if (dialog.encodingComboBox.getSelectedIndex()==0){
                ser = new RioDeJaneiroTextConverter(filename);
            } else {
                ser = new RioDeJaneiroTextConverter(filename, dialog.encodings[dialog.encodingComboBox.getSelectedIndex()]);
            }
            importedTranscription = ser.parseBasicTranscription();

        } else if (selectedFileFilter==dialog.HIATDOSFileFilter){
            org.exmaralda.partitureditor.exHIATDOS.swing.ImportHIATDOSDialog ihdd =
                    new org.exmaralda.partitureditor.exHIATDOS.swing.ImportHIATDOSDialog(table.parent, true);
            ihdd.setDatFile(filename);
            boolean success = ihdd.importHIATDOS();
            if (success){
                importedTranscription = ihdd.getTranscription();
            }
        } else if (selectedFileFilter==dialog.exSyncFileFilter){
            org.exmaralda.partitureditor.exSync.ExSyncDocument esd
                    = new org.exmaralda.partitureditor.exSync.ExSyncDocument(filename);
            StringBuffer messages = new StringBuffer();
            esd.messages = messages;
            importedTranscription = esd.toBasicTranscription();
            org.exmaralda.partitureditor.exSync.swing.MessageDialog md =
                    new org.exmaralda.partitureditor.exSync.swing.MessageDialog(table.parent, true, messages);
            md.show();
        } else if (selectedFileFilter==dialog.TransanaXMLFileFilter){
            TransanaConverter tc = new TransanaConverter();
            importedTranscription = tc.readTransanaFromXMLFile(selectedFile);
         }
        if (importedTranscription!=null){
            if ((selectedFileFilter==dialog.TASXFileFilter)
                    || (selectedFileFilter==dialog.AGFileFilter)){
                // 22-11-2010: cleaning up an unstratified transcription causes trouble
                // switched
                table.stratify(importedTranscription);
                table.cleanup(importedTranscription);
            } 
            
            table.getModel().setTranscription(importedTranscription);
            table.setupMedia();
            table.setupPraatPanel();

            table.setFilename("untitled.exb");
            table.linkPanelDialog.getLinkPanel().emptyContents();
            table.largeTextField.setText("");
            table.restoreAction.setEnabled(false);
            table.reconfigureAutoSaveThread();


            //ActionUtilities.memorizeFileFilter("last-import-filter", table.getTopLevelAncestor(), dialog);

            table.status("File " + filename + " imported");
        } else {
            System.out.println("Not setting the imported transcription because it is NULL.");            
        }
    }


}