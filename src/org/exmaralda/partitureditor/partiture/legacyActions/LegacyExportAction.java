/*
 * ExportAGAction.java
 *
 * Created on 17. Juni 2003, 11:00
 */

package org.exmaralda.partitureditor.partiture.legacyActions;

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
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.LegacyExportFileDialog;
import org.jdom.JDOMException;
import org.jdom.transform.XSLTransformException;
import org.xml.sax.SAXException;

/**
 *
 * exports the current transcription to some 3rd party format
 * Menu: File --> Export...
 * @author  thomas
 */
public class LegacyExportAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {

    /** Creates a new instance of ExportAGAction
     * @param t
     * @param icon */
    public LegacyExportAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Legacy Export...", icon, t);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        try {
            System.out.println("legacyExportAction!");
            table.commitEdit(true);
            export();
        } catch (IOException ex) {
            Logger.getLogger(LegacyExportAction.class.getName()).log(Level.SEVERE, null, ex);
            String message = "File could not be exported:\n" + ex.getLocalizedMessage();
            javax.swing.JOptionPane.showMessageDialog(table, message);            
        } catch (ParserConfigurationException | TransformerException | JexmaraldaException | FSMException | JDOMException ex) {
            Logger.getLogger(LegacyExportAction.class.getName()).log(Level.SEVERE, null, ex);
            String message = "File could not be exported:\n" + ex.getLocalizedMessage();
            javax.swing.JOptionPane.showMessageDialog(table, message);            
        } catch (Exception ex) {
            Logger.getLogger(LegacyExportAction.class.getName()).log(Level.SEVERE, null, ex);
            String message = "File could not be exported:\n" + ex.getLocalizedMessage();
            javax.swing.JOptionPane.showMessageDialog(table, message);            
        }
    }

    private void export() throws SAXException, IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException, FSMException, JDOMException, XSLTransformException, Exception{
        ExmaraldaApplication ea = (ExmaraldaApplication)(table.parent);
        String userNode = ea.getPreferencesNode();
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(userNode);
        String startDirectory = settings.get("LastExportDirectory", PreferencesUtilities.getProperty("workingDirectory", ""));

        LegacyExportFileDialog dialog = new LegacyExportFileDialog(startDirectory);
        //ActionUtilities.setFileFilter("last-export-filter", table.getTopLevelAncestor(), dialog);

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
        } else if (selectedFileFilter==dialog.AGFileFilter){
            AIFConverter.writeAIFToFile(trans, filename);
        }

        //ActionUtilities.memorizeFileFilter("last-export-filter", table.getTopLevelAncestor(), dialog);
        table.status("Transcription exported as " + filename);

    }


}
