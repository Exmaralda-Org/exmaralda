/*
 * GetHIATSegmentationErrorsAction.java
 *
 * Created on 15. Februar 2005, 13:33
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.MetaInformation;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.partiture.*;
import org.xml.sax.SAXException;


// this is for issue #107

/**
 *
 * @author  thomas
 */
public class AddTokenLayerAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of GetHIATSegmentationErrorsAction */
    public AddTokenLayerAction(PartitureTableWithActions t) {
        super("Add Token Tiers...", t);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("addTokenLayer!");
        table.commitEdit(true);
        addTokenLayer();
    }
    
    private void addTokenLayer() {
        try {
            table.checkSave();
            BasicTranscription bt = table.getModel().getTranscription();
            SegmentedTranscription st =
                    table.getAbstractSegmentation().BasicToSegmented(bt);
            
            st.getBody().augmentCommonTimeline();
            File outFile = File.createTempFile("EXMARaLDA", ".exs");
            outFile.deleteOnExit();
            st.writeXMLToFile(outFile.getAbsolutePath(), "none");
            StylesheetFactory sf = new StylesheetFactory(true);
            String xslString = "/org/exmaralda/partitureditor/jexmaralda/xsl/Segmented2BasicTokens.xsl";
            String result = sf.applyInternalStylesheetToExternalXMLFile(xslString, outFile.getAbsolutePath());
            BasicTranscription importedTranscription = new BasicTranscription();
            importedTranscription.BasicTranscriptionFromString(result);
            // 06-07-2017: issue #111 (Helau!)
            importedTranscription.getHead().getMetaInformation().resolveReferencedFile(table.filename, MetaInformation.NEW_METHOD);
            table.getModel().setTranscription(importedTranscription);
            table.setupMedia();
            table.setupPraatPanel();

            table.setFilename("untitled.exb");
            table.linkPanelDialog.getLinkPanel().emptyContents();
            table.largeTextField.setText("");
            table.restoreAction.setEnabled(false);
            table.reconfigureAutoSaveThread();
            
        } catch (SAXException | FSMException ex) {
            int optionChosen = JOptionPane
                    .showConfirmDialog(table, "Segmentation Error(s):\n " + ex.getLocalizedMessage() + "\nEdit errors?",
                    "Segmentation Error(s)", JOptionPane.OK_CANCEL_OPTION);
            if (optionChosen==JOptionPane.OK_OPTION){
                table.getSegmentationErrorsAction.actionPerformed(null);
            }
        } catch (ParserConfigurationException | IOException | TransformerException | JexmaraldaException ex) {
            Logger.getLogger(AddTokenLayerAction.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(table, "Something went wrong:\n" + ex.getMessage());
        }
        
    }

    
    
}
