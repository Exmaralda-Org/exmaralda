/*
 * NewAction.java
 *
 * Created on 16. Juni 2003, 16:23
 */

package org.exmaralda.partitureditor.partiture.webServicesActions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.TierFormat;
import org.exmaralda.partitureditor.jexmaralda.convert.ConverterEvent;
import org.exmaralda.partitureditor.jexmaralda.convert.ConverterListener;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;
import org.exmaralda.webservices.DeepLConnector;
import org.exmaralda.webservices.swing.WebServiceProgessDialog;
import org.exmaralda.webservices.swing.DeepLParameterDialog;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;



/**
 * Creates a new transcription
 * Menu: File --> New
 * @author  thomas
 */
public class DeepLAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction implements ConverterListener {
    
    WebServiceProgessDialog pbd;
    

    /** Creates a new instance of NewAction
     * @param t */
    public DeepLAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("DeepL...", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        try {
            table.commitEdit(true);
            System.out.println("DeepLAction!");
            deepL();
            table.transcriptionChanged = false;
            table.clearUndo();
            table.clearSearchResult();
            table.setFrameEndPosition(-2);
        } catch (JexmaraldaException | IOException | JDOMException | SAXException | FSMException ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(table.getParent(), ex.getLocalizedMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //List<Tier> resultTiers;
    //List<Tier> languageTiers;
    
    
    private void deepL() throws JexmaraldaException, IOException, JDOMException, SAXException, FSMException{
        
        // let the user define parameters
        DeepLParameterDialog deepLParameterDialog = new DeepLParameterDialog(table.parent, true);
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor");
        
        // retrieve values from preferences
        String apiKey = settings.get("DEEPL-API-KEY", "");

        deepLParameterDialog.setParameters(apiKey, table.preferredSegmentation, table.getModel().getTranscription(), table.selectionStartRow);
        
        deepLParameterDialog.setLocationRelativeTo(table);
        deepLParameterDialog.setVisible(true);
        if (!deepLParameterDialog.approved) return;
        final HashMap<String, Object> deepLParameters = deepLParameterDialog.getDeepLParameters();
        
        pbd = new WebServiceProgessDialog(table.parent, false);
        pbd.setLocationRelativeTo(table.parent);
        pbd.setTitle("DeepL translation... ");
        pbd.setVisible(true);
        
        
        // write the parameters to the preferences
        String apiKeyInput = (String) deepLParameters.get("API-KEY");
        settings.put("DEEPL-API-KEY", apiKeyInput);
        
        DeepLConnector deepLConnector = new DeepLConnector(apiKeyInput);             
        String sourceLanguage = (String) deepLParameters.get("SOURCE-LANGUAGE");
        String targetLanguage = (String) deepLParameters.get("TARGET-LANGUAGE");
        String formalityLevel = (String) deepLParameters.get("FORMALITY-LEVEL");
        boolean addLanguageTiers = (Boolean) deepLParameters.get("LANGUAGE-TIER");
        boolean useSegmentation = (Boolean) deepLParameters.get("USE-SEGMENTATION");

        // do this in a thread so we can report progress
        Thread deepLThread = new Thread(){
            @Override
            public void run() {
                boolean useSelectedTier = (boolean) deepLParameters.get("SELECTED-TIER");
                int[] tierPositions;
                int tiersAdded = 0;
                if (useSelectedTier){
                    tierPositions = new int[1];
                    tierPositions[0] = table.selectionStartRow;
                } else {
                    String tierCategory = (String) deepLParameters.get("TIER-CATEGORY");
                    tierPositions = table.getModel().getTranscription().getBody().findTiersWithCategory(tierCategory);                         
                }

                //resultTiers = new ArrayList<>();
                //languageTiers = new ArrayList<>();
                
                for (int tierPosition : tierPositions){
                    try {
                        int adjustedTierPosition = tierPosition + tiersAdded;
                        Tier sourceTier = table.getModel().getTranscription().getBody().getTierAt(adjustedTierPosition);
                        
                        Tier targetTier = new Tier(table.getModel().getTranscription().getBody().getFreeID(), sourceTier.getSpeaker(), "DeepL-" + targetLanguage, "a");
                        targetTier.setDisplayName("[DeepL-" + targetLanguage + "]");
                        table.getModel().getTranscription().getBody().insertTierAt(targetTier, adjustedTierPosition + 1);
                        table.getModel().getTierFormatTable().addTierFormat(new TierFormat("a", targetTier.getID()));
                        tiersAdded++;
                        //resultTiers.add(targetTier);
                        
                        Tier languageTier = new Tier(table.getModel().getTranscription().getBody().getFreeID(), sourceTier.getSpeaker(), "DeepL-Lang", "a");
                        languageTier.setDisplayName("lang");
                        if (addLanguageTiers){
                            table.getModel().getTranscription().getBody().insertTierAt(languageTier, adjustedTierPosition + 2);                             
                            table.getModel().getTierFormatTable().addTierFormat(new TierFormat("a", languageTier.getID()));
                            tiersAdded++;
                        }
                        //languageTiers.add(languageTier);
                        
                        boolean eventByEvent = (boolean) deepLParameters.get("EVENT-BY-EVENT");
                        if (eventByEvent){
                            for (int pos=0; pos<sourceTier.getNumberOfEvents(); pos++){
                                Event event = sourceTier.getEventAt(pos);
                                String originalText = event.getDescription();
                                String[] translation = deepLConnector.callDeepL(originalText, sourceLanguage, targetLanguage, formalityLevel, DeepLConnector.API_TYPE.FREE);
                                pbd.addText(translation[0] + " " + translation[1]);
                                Event translationEvent = new Event();
                                translationEvent.setStart(event.getStart());
                                translationEvent.setEnd(event.getEnd());
                                translationEvent.setDescription(translation[0]);
                                targetTier.addEvent(translationEvent);
                                
                                if (addLanguageTiers){
                                    Event languageEvent = new Event();
                                    languageEvent.setStart(event.getStart());
                                    languageEvent.setEnd(event.getEnd());
                                    languageEvent.setDescription(translation[1]);                                    
                                    languageTier.addEvent(languageEvent);
                                }
                            }
                        } else {
                            List<List<Event>> segmentChains = sourceTier.getSegmentChains(table.getModel().getTranscription().getBody().getCommonTimeline());
                            for (List<Event> segmentChain : segmentChains){
                                if (!(useSegmentation)){
                                    String originalText = "";
                                    for (Event e : segmentChain){
                                        originalText+=e.getDescription();
                                    }
                                    String[] translation = deepLConnector.callDeepL(originalText, sourceLanguage, targetLanguage, formalityLevel, DeepLConnector.API_TYPE.FREE);
                                    pbd.addText(translation[0] + " " + translation[1]);
                                    Event translationEvent = new Event();
                                    translationEvent.setStart(segmentChain.get(0).getStart());
                                    translationEvent.setEnd(segmentChain.get(segmentChain.size()-1).getEnd());
                                    translationEvent.setDescription(translation[0]);
                                    targetTier.addEvent(translationEvent);

                                    if (addLanguageTiers){
                                        Event languageEvent = new Event();
                                        languageEvent.setStart(segmentChain.get(0).getStart());
                                        languageEvent.setEnd(segmentChain.get(segmentChain.size()-1).getEnd());
                                        languageEvent.setDescription(translation[1]);                                    
                                        languageTier.addEvent(languageEvent);
                                    }
                                    
                                }
                            }
                        }
                    } catch (JexmaraldaException | IOException | URISyntaxException ex) {
                        Logger.getLogger(DeepLAction.class.getName()).log(Level.SEVERE, null, ex);
                        pbd.addText("Error: " + ex.getLocalizedMessage());
                    }
                        
                }
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pbd.addText("DeepL calls done.");
                            success();
                        } catch (IOException | JexmaraldaException ex) {
                            pbd.addText("Error: " + ex.getLocalizedMessage());
                            JOptionPane.showMessageDialog(pbd, ex);
                        }
                    }
                });
                
                    

            }
            
        };
        deepLThread.start();

        
    }
    
    public void success() throws IOException, JexmaraldaException{       
        /*int add = 1;
        for (int tierPos : tierPositions){
            Tier tier = resultTiers.get(tierPos);
            table.getModel().getTranscription().getBody().insertTierAt(tier, tierPos + add);
            add++;
            System.out.println("Added " + tier.toXML());
        }
        if (languageTier){
            int add2 = 2;
            for (int tierPos : tierPositions){
                Tier tier = languageTiers.get(tierPos);
                table.getModel().getTranscription().getBody().insertTierAt(tier, tierPos + add);
                add2+=2;
                System.out.println("Added " + tier.toXML());
            }
            
        }*/
        table.getModel().fireDataReset();
    }

    @Override
    public void processConverterEvent(ConverterEvent converterEvent) {
        System.out.println(converterEvent.getMessage());
        pbd.addText(converterEvent.getMessage());
    }
}



