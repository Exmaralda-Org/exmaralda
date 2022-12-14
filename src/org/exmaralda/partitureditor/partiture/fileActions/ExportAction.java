/*
 * ExportAGAction.java
 *
 * Created on 17. Juni 2003, 11:00
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.ExportFileDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.convert.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.utilities.PreferencesUtilities;
import org.exmaralda.orthonormal.data.NormalizedFolkerTranscription;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.segment.CHATSegmentation;
import org.jdom.JDOMException;
import org.jdom.transform.XSLTransformException;
import org.xml.sax.SAXException;

/**
 *
 * exports the current transcription to some 3rd party format
 * Menu: File --> Export...
 * @author  thomas
 */
public class ExportAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {

    /** Creates a new instance of ExportAGAction
     * @param t
     * @param icon */
    public ExportAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Export...", icon, t);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        try {
            System.out.println("exportAction!");
            table.commitEdit(true);
            export();
        } catch (IOException ex) {
            Logger.getLogger(ExportAction.class.getName()).log(Level.SEVERE, null, ex);
            String message = "File could not be exported:\n" + ex.getLocalizedMessage();
            javax.swing.JOptionPane.showMessageDialog(table, message);            
        } catch (ParserConfigurationException | TransformerException | JexmaraldaException | FSMException | JDOMException ex) {
            Logger.getLogger(ExportAction.class.getName()).log(Level.SEVERE, null, ex);
            String message = "File could not be exported:\n" + ex.getLocalizedMessage();
            javax.swing.JOptionPane.showMessageDialog(table, message);            
        } catch (Exception ex) {
            Logger.getLogger(ExportAction.class.getName()).log(Level.SEVERE, null, ex);
            String message = "File could not be exported:\n" + ex.getLocalizedMessage();
            javax.swing.JOptionPane.showMessageDialog(table, message);            
        }
    }

    private void export() throws SAXException, IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException, FSMException, JDOMException, XSLTransformException, Exception{
        ExmaraldaApplication ea = (ExmaraldaApplication)(table.parent);
        String userNode = ea.getPreferencesNode();
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(userNode);
        String startDirectory = settings.get("LastExportDirectory", PreferencesUtilities.getProperty("workingDirectory", ""));

        ExportFileDialog dialog = new ExportFileDialog(startDirectory);
        ActionUtilities.setFileFilter("last-export-filter", table.getTopLevelAncestor(), dialog);

        int retValue = dialog.showSaveDialog(table.parent);
        if (retValue!=javax.swing.JFileChooser.APPROVE_OPTION) return;
        ParameterFileFilter selectedFileFilter = (ParameterFileFilter)(dialog.getFileFilter());
        File selectedFile = dialog.getSelectedFile();
        settings.put("LastExportDirectory", selectedFile.getAbsolutePath());        
        String filename = selectedFile.getAbsolutePath();

        //check whether or not the selected file has an extension
        if (!selectedFile.getName().contains(".")){
            filename+="." + selectedFileFilter.getSuffix();
        }

        File exportFile = new File(filename);
        //check whether the export file already exists
        if (exportFile.exists()){
            int confirm =
                javax.swing.JOptionPane.showConfirmDialog(table, exportFile.getAbsolutePath() + "\nalready exists. Overwrite?");
            if (confirm==javax.swing.JOptionPane.CANCEL_OPTION) return;
            // issue #292 : this is quite obviously utter nonsense
            //if (confirm==javax.swing.JOptionPane.NO_OPTION) export();
            if (confirm==javax.swing.JOptionPane.NO_OPTION) return;
        }

        // now do the real export
        BasicTranscription trans = table.getModel().getTranscription().makeCopy();

        if (selectedFileFilter==dialog.TASXFileFilter){
            TASXConverter tc = new TASXConverter();
            tc.writeTASXToFile(trans, filename);
        } else if (selectedFileFilter==dialog.PraatFileFilter){
            PraatConverter pc = new PraatConverter();
            pc.writePraatToFile(trans, filename);
        } else if (selectedFileFilter==dialog.AGFileFilter){
            AIFConverter.writeAIFToFile(trans, filename);
        } else if (selectedFileFilter==dialog.EAFFileFilter){
            ELANConverter ec = new ELANConverter();
            ec.writeELANToFile(trans, filename);
        } else if (selectedFileFilter==dialog.AudacityLabelFileFilter){
            //AudacityConverter ec = new AudacityConverter();
            switch (dialog.audacityExportAccessoryPanel.getMethod()){
                case AudacityConverter.ALL_TIERS :
                    AudacityConverter.writeAudacityToFile(trans, filename);
                    break;
                case AudacityConverter.SELECTED_TIERS :
                    AudacityConverter.writeAudacityToFile(trans, filename, table.selectionStartRow, table.selectionEndRow);
                    break;
                case AudacityConverter.TIMELINE :
                    AudacityConverter.writeTimelineToFile(trans, filename);
                    break;
            }
        } else if (selectedFileFilter==dialog.TEIFileFilter){
            TEIConverter ec;
            switch (dialog.teiExportAccessoryPanel.getMethod()){
                case TEIConverter.GENERIC_METHOD :
                    ec = new TEIConverter();
                    ec.writeGenericTEIToFile(trans, filename);
                    break;
                case TEIConverter.AZM_METHOD :
                    ec = new TEIConverter();
                    ec.writeTEIToFile(trans, filename);
                    break;
                case TEIConverter.MODENA_METHOD :
                    ec = new TEIConverter("/org/exmaralda/partitureditor/jexmaralda/xsl/EXMARaLDA2TEI_Modena.xsl");
                    ec.writeModenaTEIToFile(trans, filename);
                    break;
                case TEIConverter.HIAT_METHOD :
                    ec = new TEIConverter();
                    ec.writeHIATTEIToFile(trans, filename);
                    break;
                case TEIConverter.HIAT_NEW_METHOD :
                    ec = new TEIConverter();
                    ec.writeNewHIATTEIToFile(trans, filename, dialog.teiExportAccessoryPanel.getGenerateWordIDs());
                    break;
                case TEIConverter.CGAT_METHOD :
                    ec = new TEIConverter();
                    ec.setLanguage(dialog.teiExportAccessoryPanel.getLanguage());
                    // changed 16-01-2015: ISO!
                    ec.writeFOLKERISOTEIToFile(trans, filename);
                    break;
                case TEIConverter.ISO_NON_SEGMENTED_METHOD :
                    ec = new TEIConverter();
                    ec.setLanguage(dialog.teiExportAccessoryPanel.getLanguage());
                    ec.writeNonSegmentedISOTEIToFile(trans, filename);
                    break;
                case TEIConverter.HIAT_ISO_METHOD :
                    ec = new TEIConverter();
                    ec.setLanguage(dialog.teiExportAccessoryPanel.getLanguage());
                    // #152
                    ec.writeHIATISOTEIToFile(trans, filename, table.hiatFSM, false); 
                    break;
                case TEIConverter.ISO_EVENT_TOKEN_METHOD :
                    ec = new TEIConverter();
                    ec.setLanguage(dialog.teiExportAccessoryPanel.getLanguage());
                    ec.writeEventTokenISOTEIToFile(trans, filename);
                    break;
                case TEIConverter.ISO_GENERIC_METHOD :
                    ec = new TEIConverter();
                    ec.setLanguage(dialog.teiExportAccessoryPanel.getLanguage());
                    // #152 
                    ec.writeGenericSegmentedISOTEIToFile(trans, filename, table.genericFSM);
                    break;
            }
        } else if (selectedFileFilter==dialog.TEIModenaFileFilter){
            TEIConverter ec = new TEIConverter("/org/exmaralda/partitureditor/jexmaralda/xsl/EXMARaLDA2TEI_Modena.xsl");
            ec.writeModenaTEIToFile(trans, filename);
        } else if (selectedFileFilter==dialog.TCFFileFilter){
            String language = (String) dialog.tcfExportAccessoryPanel.languageComboBox.getSelectedItem();
            String segmentation = (String) dialog.tcfExportAccessoryPanel.segmentationComboBox.getSelectedItem();
            TCFConverter tcfConverter = new TCFConverter();
            // issue #38 : user must be able to choose the segmentation algorithm
            //tcfConverter.writeHIATTCFToFile(trans, filename, language);
            tcfConverter.writeTCFToFile(trans, filename, language, segmentation);
        } else if (selectedFileFilter==dialog.CHATTranscriptFileFilter){
            switch(dialog.chatExportAccessoryPanel.getMethod()){
                case CHATConverter.CHAT_SEGMENTATION_METHOD :
                    exportCHATTranscript(trans, filename, "UTF-8");
                    break;
                case CHATConverter.HIAT_SEGMENTATION_METHOD :
                    CHATConverter.writeHIATSegmentedCHATFile(trans, exportFile);
                    break;
                case CHATConverter.EVENT_METHOD :
                    CHATConverter.writeEventSegmentedCHATFile(trans, exportFile);
                    break;
            }
        } else if (selectedFileFilter==dialog.ExmaraldaSegmentedTranscriptionFileFilter){
            SegmentedTranscription st = trans.toSegmentedTranscription();
            st.setEXBSource(table.filename);
            st.writeXMLToFile(filename, "none");
        } else if ((selectedFileFilter==dialog.FOLKERTranscriptionFileFilter) || (selectedFileFilter==dialog.FLKTranscriptionFileFilter)){
            EventListTranscription elt = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(trans, true);
            org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.writeXML(elt, exportFile, new org.exmaralda.folker.data.GATParser(), 0);
        } else if (selectedFileFilter==dialog.FLNTranscriptionFileFilter){
            // issue #108
            EventListTranscription elt = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(trans, true);
            File tempFile = File.createTempFile("TEMP_FLK", "FLK");
            tempFile.deleteOnExit();
            org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.writeXML(elt, tempFile, new org.exmaralda.folker.data.GATParser(), 2);
            NormalizedFolkerTranscription normalizedFolkerTranscription = org.exmaralda.orthonormal.io.XMLReaderWriter.readFolkerTranscription(tempFile);
            IOUtilities.writeDocumentToLocalFile(exportFile.getAbsolutePath(), normalizedFolkerTranscription.getDocument());            
        } else if (selectedFileFilter==dialog.TreeTaggerFilter){
            new TreeTaggerConverter().writeText(trans, exportFile);
        } else if (selectedFileFilter==dialog.F4TextFileFilter){
            trans.getBody().getCommonTimeline().completeTimes(false, trans);
            //new F4Converter().writeText(trans, exportFile, F4Converter.SPEAKER_CONTRIBUTIONS, "RTF");
            // this I changed for issue #258
            // I wouldn't know what the other variant is good for... 
            new F4Converter().writeText(trans, exportFile, F4Converter.EVENTS_FROM_T_TIERS, "TXT");
        } else if (selectedFileFilter==dialog.TsvFileFilter){
            new TsvConverter().writeText(trans, exportFile);
        } else if (selectedFileFilter==dialog.SRTFileFilter){
            trans.getBody().getCommonTimeline().completeTimes(false, trans);
            new SrtConverter().writeText(trans, exportFile);
        }

        ActionUtilities.memorizeFileFilter("last-export-filter", table.getTopLevelAncestor(), dialog);
        table.status("Transcription exported as " + filename);

    }

    void exportCHATTranscript(BasicTranscription bt, String filename, String encoding) throws JexmaraldaException, FSMException, SAXException, FileNotFoundException, IOException{
         // segment the basic transcription and transform it into a list transcription
         CHATSegmentation segmenter = new org.exmaralda.partitureditor.jexmaralda.segment.CHATSegmentation(table.chatFSM);
         System.out.println("Segmenter initialized");
         ListTranscription lt = segmenter.BasicToUtteranceList(bt);
         System.out.println("List transformation completed");
         String text = CHATSegmentation.toText(lt);
         System.out.println("Text generated");
         System.out.println("started writing document...");
         FileOutputStream fos = new FileOutputStream(new File(filename));
         if (encoding.length()==0){
             fos.write(text.getBytes());
         } else {
             fos.write(text.getBytes(encoding));
         }
         fos.close();
         System.out.println("document written.");

    }

}
