/*
 * GetHIATSegmentationErrorsAction.java
 *
 * Created on 15. Februar 2005, 13:33
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.jexmaralda.errorChecker.EditErrorsDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.AutoAnnotationDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.TierSelectionDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;


/**
 *
 * @author  thomas
 */
public class AutoAnnotationAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of GetHIATSegmentationErrorsAction */
    public AutoAnnotationAction(PartitureTableWithActions t) {
        super("Auto annotate tiers...", t);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("AutoAnnotationAction!");
        table.commitEdit(true);
        autoAnnotate();
    }
    
    private void autoAnnotate() {
        try {
            TierFormatTable tft = table.getModel().getTierFormatTable();
            TierSelectionDialog tierSelectionDialog = new TierSelectionDialog(table.parent, true);
            boolean approved = tierSelectionDialog.selectTiers(table.getModel().getTranscription(), table.getModel().getTranscription().getBody().getTiersOfType("t"));
            if (!approved) {
                System.out.println("Not approved!");
                return;
            }
            String[] tierIDs = tierSelectionDialog.getSelectedTierIDs();
            
            AutoAnnotationDialog autoAnnotationDialog = new AutoAnnotationDialog(table.parent, true);
            autoAnnotationDialog.setLocationRelativeTo(table);
            autoAnnotationDialog.setVisible(true);
            if (!autoAnnotationDialog.approved) return;
            
            String regex = autoAnnotationDialog.getRegex();
            String category = autoAnnotationDialog.getCategory();
            String value = autoAnnotationDialog.getValue();
            boolean delete = autoAnnotationDialog.getDelete();
            
            Document errorsDocument = table.getModel().getTranscription().getBody().autoAnnotate(tierIDs, 
                    regex, category, value, delete, 
                    table.getModel().getTranscription().getHead().getSpeakertable());

            //table.getModel().setTranscriptionAndTierFormatTable(bt, tft);
            table.stratify(table.getModel().getTranscription());
            table.resetData();
            table.showAllTiers();
            table.linkPanelDialog.getLinkPanel().emptyContents();
            table.largeTextField.setText("");
            //table.setupMedia();
            table.status("Auto annotation applied");
            table.transcriptionChanged = true;
            try {
                List l = XPath.selectNodes(errorsDocument, "//error");
                if (l.size()>0){
                    for (Object o : l){
                        Element e = (Element)o;
                        e.setAttribute("file", table.filename);
                    }
                    //System.out.println(IOUtilities.documentToString(errorsDocument, false));
                    EditErrorsDialog eed = new EditErrorsDialog(table.parent, false);
                    eed.setOpenSaveButtonsVisible(false);
                    eed.setTitle("Auto annotation problems");
                    eed.addErrorCheckerListener(table);
                    eed.setErrorList(errorsDocument);
                    eed.setLocationRelativeTo(table);
                    eed.setVisible(true);
                }
            } catch (JDOMException ex) {
                Logger.getLogger(AutoAnnotationAction.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (JexmaraldaException ex) {
            JOptionPane.showMessageDialog(table, ex.getLocalizedMessage());            
        }
    }

    
    
}
