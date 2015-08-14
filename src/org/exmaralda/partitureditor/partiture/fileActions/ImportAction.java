/*
 * ExportAGAction.java
 *
 * Created on 17. Juni 2003, 11:00
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.ImportFileDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.convert.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.partitureditor.jexmaraldaswing.ChooseTextSplitterDialog;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * imports some 3rd party format
 * Menu: File --> Import... 
 * @author  thomas
 */
public class ImportAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    private String[] encodings = {"US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE"};

    /** Creates a new instance of ExportAGAction */
    public ImportAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Import...", icon, t);  
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("importAction!");
        table.commitEdit(true);
        try {
            importFile();
            table.clearUndo();
            table.clearSearchResult();
        } catch (Exception ex) {
            ex.printStackTrace();
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
        String startDirectory = settings.get("LastImportDirectory", "");

        // determine what to import
        ImportFileDialog dialog = new ImportFileDialog(startDirectory);
        ActionUtilities.setFileFilter("last-import-filter", table.getTopLevelAncestor(), dialog);


        int retValue = dialog.showOpenDialog(table.parent);
        if (retValue!=javax.swing.JFileChooser.APPROVE_OPTION) return;
        ParameterFileFilter selectedFileFilter = (ParameterFileFilter)(dialog.getFileFilter());
        File selectedFile = dialog.getSelectedFile();
        settings.put("LastImportDirectory", selectedFile.getParent());
        String filename = selectedFile.getAbsolutePath();
                
        // now do the real import
        BasicTranscription importedTranscription = null;
        
        if (selectedFileFilter==dialog.TASXFileFilter){
            TASXConverter tc = new TASXConverter();
            importedTranscription = tc.readTASXFromFile(filename);            
        } else if (selectedFileFilter==dialog.TranscriberFileFilter){
            TranscriberConverter tc = new TranscriberConverter();
            importedTranscription = tc.readTranscriberFromFile(filename);
        }  else if (selectedFileFilter==dialog.CHATTranscriptFileFilter){
            CHATConverter tc = new CHATConverter(new File(filename));
            importedTranscription = tc.convert();
        } else if (selectedFileFilter==dialog.WinPitchFileFilter){
            WinPitchConverter tc = new WinPitchConverter();
            importedTranscription = tc.readWinPitchFromFile(filename);
        } else if (selectedFileFilter==dialog.AnvilFileFilter){
            AnvilConverter tc = new AnvilConverter();
            importedTranscription = tc.readAnvilFromFile(filename);
        } else if (selectedFileFilter==dialog.PraatFileFilter){
            // detect the encoding first
            String encoding = "";
            encoding = EncodingDetector.detectEncoding(selectedFile);
            if (encoding.length()==0){
                Object o = javax.swing.JOptionPane.showInputDialog(table, 
                            "Cannot detect the file encoding. Please select", 
                            "Choose a file encoding",
                            javax.swing.JOptionPane.PLAIN_MESSAGE,
                            null,
                            encodings,
                            "ISO-8859-1");
                encoding = (String)o;
            }
            PraatConverter pc = new PraatConverter();
            importedTranscription = pc.readPraatFromFile(filename, encoding);            
        } else if (selectedFileFilter==dialog.AGFileFilter){
            AIFConverter ac = new AIFConverter();
            importedTranscription = ac.readAIFFromFile(filename);
        } else if (selectedFileFilter==dialog.PhonFileFilter){
            PhonConverter pc = new PhonConverter();
            importedTranscription = pc.readPhonFromFile(filename);
        } else if (selectedFileFilter==dialog.EAFFileFilter){
            ELANConverter ec = new ELANConverter();
            importedTranscription = ec.readELANFromFile(filename);            
        } else if (selectedFileFilter==dialog.TEIFileFilter){   
            TEIConverter teic = new TEIConverter();
            importedTranscription = teic.readTEIFromFile(filename);            
        } else if (selectedFileFilter==dialog.TextFileFilter){     
            ChooseTextSplitterDialog ctsd = new ChooseTextSplitterDialog(null, true);
            ctsd.setVisible(true);
            String regex = ctsd.getRegex();
            TextConverter tc = new TextConverter(regex);
            if (dialog.encodingComboBox.getSelectedIndex()==0){
                tc.readText(selectedFile);
            } else {
                tc.readText(selectedFile,dialog.encodings[dialog.encodingComboBox.getSelectedIndex()]);
            }
            importedTranscription = tc.convert();               
        } else if (selectedFileFilter==dialog.AudacityLabelFileFilter){
            importedTranscription = new AudacityConverter().readAudacityFromFile(selectedFile);
        } else if (selectedFileFilter==dialog.TreeTaggerFilter){
            // NEW 06-07-2009 for Anne Siekmeyer
            TreeTaggerConverter ttc = new TreeTaggerConverter();
            ttc.appendSpaces = dialog.appendSpacesCheckBox.isSelected();
            if (dialog.encodingComboBox.getSelectedIndex()==0){
                ttc.readText(selectedFile);
            } else {
                ttc.readText(selectedFile,dialog.encodings[dialog.encodingComboBox.getSelectedIndex()]);
            }
            importedTranscription = ttc.importText();
        } else if (selectedFileFilter==dialog.SimpleExmaraldaFileFilter){
            SimpleExmaraldaReader ser = null;
            if (dialog.encodingComboBox.getSelectedIndex()==0){
                ser = new SimpleExmaraldaReader(filename);
            } else {
                ser = new SimpleExmaraldaReader(filename, dialog.encodings[dialog.encodingComboBox.getSelectedIndex()]);
            }
            importedTranscription = ser.parseBasicTranscription();               
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
        } else if (selectedFileFilter==dialog.FOLKERTranscriptionFileFilter){
            importedTranscription =
                    org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.readXMLAsBasicTranscription(new File(filename));
        } else if (selectedFileFilter==dialog.XSLStylesheetImportFilter){
            // added 24-09-2009
            org.exmaralda.partitureditor.jexmaraldaswing.ChooseXSLStylesheetDialog xslDialog =
                    new org.exmaralda.partitureditor.jexmaraldaswing.ChooseXSLStylesheetDialog(table.parent, true);
            //ExmaraldaApplication ea = (ExmaraldaApplication)(table.parent);
            //String userNode = ea.getPreferencesNode();
            //java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(userNode);
            String xslPath = settings.get("ImportXSL", "");
            xslDialog.setPath(xslPath);
            xslDialog.setLocationRelativeTo(table);
            xslDialog.setVisible(true);

            if (xslDialog.returnStatus){
                xslPath = xslDialog.getPath();
                // TODO: Carry out the import
                settings.put("ImportXSL", xslPath);
                StylesheetFactory sf = new StylesheetFactory(true);
                String transformResult = sf.applyExternalStylesheetToExternalXMLFile(xslPath, filename);
                importedTranscription = new BasicTranscription();
                importedTranscription.BasicTranscriptionFromString(transformResult);
            } else {
                return;
            }
        }
        

        if (importedTranscription!=null){
            if ((selectedFileFilter==dialog.TASXFileFilter) 
                    || (selectedFileFilter==dialog.EAFFileFilter)                    
                    || (selectedFileFilter==dialog.CHATTranscriptFileFilter)
                    || (selectedFileFilter==dialog.AnvilFileFilter)
                    || (selectedFileFilter==dialog.AGFileFilter)){
                // 22-11-2010: cleaning up an unstratified transcription causes trouble
                // switched
                table.stratify(importedTranscription);
                table.cleanup(importedTranscription);
            } else if ((selectedFileFilter==dialog.FOLKERTranscriptionFileFilter) || (selectedFileFilter==dialog.WinPitchFileFilter)){
                table.stratify(importedTranscription);
            }
            table.getModel().setTranscription(importedTranscription);
            //try {
                table.setupMedia();
            /*} catch (Exception e){
                JOptionPane.showMessageDialog(table, "Problem setting up media:\n" + e.getLocalizedMessage());
            }*/
            table.setupPraatPanel();

            table.setFilename("untitled.exb");
            table.linkPanelDialog.getLinkPanel().emptyContents();
            table.largeTextField.setText("");
            table.restoreAction.setEnabled(false);
            table.reconfigureAutoSaveThread();


            ActionUtilities.memorizeFileFilter("last-import-filter", table.getTopLevelAncestor(), dialog);

            table.status("File " + filename + " imported");
        }
    }
    
    
}
