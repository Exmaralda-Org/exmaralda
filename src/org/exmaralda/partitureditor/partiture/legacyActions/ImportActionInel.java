/*
 * ExportAGAction.java
 *
 * Created on 17. Juni 2003, 11:00
 */
package org.exmaralda.partitureditor.partiture.legacyActions;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.ImportFileDialogINEL;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.convert.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.exmaralda.common.ExmaraldaApplication;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * imports some 3rd party format Menu: File --> Import...
 *
 * @author thomas
 */
public class ImportActionInel extends org.exmaralda.partitureditor.partiture.AbstractTableAction {

    private final String[] encodings = {"US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE"};

    /**
     * Creates a new instance of ExportAGAction
     *
     * @param t
     * @param icon
     */
    public ImportActionInel(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("INEL-specific Import", icon, t);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("importActionInel!");
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

    private void importFile() throws SAXException, IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException, JDOMException, URISyntaxException {
        // check if the user wants to save changes
        boolean proceed = true;
        if (table.transcriptionChanged) {
            proceed = table.checkSave();
        }
        if (!proceed) {
            return;
        }

        ExmaraldaApplication ea = (ExmaraldaApplication) (table.parent);
        String userNode = ea.getPreferencesNode();
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(userNode);
        String startDirectory = settings.get("LastImportDirectory", "");

        // determine what to import
        ImportFileDialogINEL dialog = new ImportFileDialogINEL(startDirectory);
        //we can only import flextext currently
        //ActionUtilities.setFileFilter("last-import-filter", table.getTopLevelAncestor(), dialog);
        
        int retValue = dialog.showOpenDialog(table.parent);
        if (retValue != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        ParameterFileFilter selectedFileFilter = (ParameterFileFilter) (dialog.getFileFilter());
        File selectedFile = dialog.getSelectedFile();
        settings.put("LastImportDirectory", selectedFile.getParent());
        String filename = selectedFile.getAbsolutePath();

        // now do the real import
        BasicTranscription importedTranscription = null;

        if (selectedFileFilter == dialog.FlexTextXMLFileFilter) {
            //new for INEL 2017-05-02
            org.exmaralda.partitureditor.jexmaraldaswing.ChooseASettingsFileDialogInel flexDialog
                    = new org.exmaralda.partitureditor.jexmaraldaswing.ChooseASettingsFileDialogInel(table.parent, true);
            String xmlPath = settings.get("ImportXML", "");
            flexDialog.setPath(xmlPath);
            flexDialog.setLocationRelativeTo(table);
            flexDialog.setVisible(true);
            if (flexDialog.returnStatus) {
                File xmlSelectedFile;
                FlexTextConverter fc = new FlexTextConverter();
                if (flexDialog.getPath().startsWith("http")) {
                    xmlSelectedFile = new File(selectedFile.getAbsolutePath()+"settings.xml");
                    InputStream inputStream = new URL(flexDialog.getPath()).openStream();
                    OutputStream outputStream = new FileOutputStream(xmlSelectedFile);
                    IOUtils.copy(inputStream, outputStream);
                } else {
                    if(flexDialog.isCustom()){                       
                        settings.put("ImportXML", flexDialog.getPath());
                        xmlSelectedFile = new File(flexDialog.getPath());
                        importedTranscription = fc.readFlexTextFromTextFile(selectedFile, xmlSelectedFile);
                    }else{
                        settings.put("ImportXML", getClass().getResource(flexDialog.getPath()).toExternalForm().substring(6).replace("/", "\\"));
                        importedTranscription = fc.readFlexTextFromTextFilePredefined(selectedFile, flexDialog.getPath());
                    }
                }
            }
        }

        if (importedTranscription != null) {
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
