/*
 * GetHIATSegmentationErrorsAction.java
 *
 * Created on 15. Februar 2005, 13:33
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.exmaralda.orthonormal.lexicon.AutoNormalizer;
import org.exmaralda.orthonormal.lexicon.LexiconException;
import org.exmaralda.orthonormal.lexicon.NORMALIZATION_PROFILES;
import org.exmaralda.orthonormal.lexicon.NormalizationProfile;
import org.exmaralda.orthonormal.lexicon.XMLLexicon;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.TierFormat;
import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.jexmaralda.errorChecker.EditErrorsDialog;
import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.GenericSegmentation;
import org.exmaralda.partitureditor.jexmaraldaswing.AddNormalisationTiersDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.AutoAnnotationDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.TierSelectionDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;


/**
 *
 * @author  thomas
 */
public class AutoNormalisationAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of GetHIATSegmentationErrorsAction */
    public AutoNormalisationAction(PartitureTableWithActions t) {
        super("Auto normalize tier(s)...", t);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("AutoNormalisationAction!");
        table.commitEdit(true);
        autoNormalize();
    }
    
    private void autoNormalize() {
        //TierFormatTable tft = table.getModel().getTierFormatTable();
        
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor");
        
        // retrieve values from preferences
        String normProfile = settings.get("NORMALISATION-PROFILE", "German / FOLK");



        AddNormalisationTiersDialog addNormalisationTiersDialog = new AddNormalisationTiersDialog(table.parent, true);
        if (table.selectionStartRow>=0 && table.selectionStartCol<0){
            Tier tier = table.getModel().getTranscription().getBody().getTierAt(table.selectionStartRow);
            if (tier.getType().equals("t")){
                addNormalisationTiersDialog.setSelectedTierOption();
            }
        }
        addNormalisationTiersDialog.setProfile(normProfile);

        addNormalisationTiersDialog.setLocationRelativeTo(table);
        addNormalisationTiersDialog.setVisible(true);
        if (!addNormalisationTiersDialog.approved) return;
        
        boolean selectedTierOnly = addNormalisationTiersDialog.selectedTierOnly();
        String profileName = addNormalisationTiersDialog.getProfileName();
        
        NormalizationProfile profile = NORMALIZATION_PROFILES.getProfileForName(profileName);
        
        String[] tierIDs = table.getModel().getTranscription().getBody().getTiersOfType("t");
        
        
        if (selectedTierOnly){
            Tier tier = table.getModel().getTranscription().getBody().getTierAt(table.selectionStartRow);
            String tierID = tier.getID();
            tierIDs = new String[] {tierID};
        }
        try {
            for (String tierID : tierIDs){
                XMLLexicon lexicon = new XMLLexicon();
                lexicon.read(profile.lexiconPath);
                AutoNormalizer autoNormalizer = new AutoNormalizer(lexicon);
                AbstractSegmentation segmentationAlgorithm = AbstractSegmentation.getSegmentationAlgorithm(AbstractSegmentation.getSegmentationCode(profile.segmentationAlgorithmName));
                String wordSegmentationName = AbstractSegmentation.getWordSegmentationName(AbstractSegmentation.getSegmentationCode(profile.segmentationAlgorithmName));
                BasicTranscription bt = table.getModel().getTranscription();
                Tier normalizeBasicTranscriptionTier = autoNormalizer.normalizeBasicTranscriptionTier(bt, tierID, segmentationAlgorithm, wordSegmentationName);

                System.out.println(normalizeBasicTranscriptionTier.toXML());
                int row = table.getModel().getTranscription().getBody().lookupID(tierID);
                table.getModel().insertTier(normalizeBasicTranscriptionTier, row + 1);
            }

            table.resetData();
            table.status("Auto normalization applied");
            table.transcriptionChanged = true;
            
            settings.put("NORMALISATION-PROFILE", profileName);


        } catch (IOException | SAXException | FSMException | JDOMException | LexiconException | JexmaraldaException ex) {
            Logger.getLogger(AutoNormalisationAction.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(table, ex.getLocalizedMessage());
        }
            
        
    }

    
    
}
