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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.AbstractSegment;
import org.exmaralda.partitureditor.jexmaralda.AbstractSegmentVector;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Describable;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.SegmentList;
import org.exmaralda.partitureditor.jexmaralda.Segmentation;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTier;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.TierFormat;
import org.exmaralda.partitureditor.jexmaralda.TimedSegment;
import org.exmaralda.partitureditor.jexmaralda.convert.ConverterEvent;
import org.exmaralda.partitureditor.jexmaralda.convert.ConverterListener;
import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;
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
     * @param t
     * @param icon */
    public DeepLAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("DeepL...", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        try {
            table.commitEdit(true);
            System.out.println("DeepLAction!");
            deepL();
            // changed for #465
            table.transcriptionChanged = true;
            table.clearUndo();
            table.clearSearchResult();
            table.setFrameEndPosition(-2);
        } catch (JexmaraldaException | IOException | JDOMException | SAXException | FSMException ex) {
            System.out.println(ex.getLocalizedMessage());
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

        deepLParameterDialog.setParameters(
                apiKey, 
                settings.get("DEEPL-SOURCE-LANGUAGE", "DE"),            // new for #507
                settings.get("DEEPL-TARGET-LANGUAGE", "EN-GB"),         // new for #507    
                settings.get("DEEPL-FORMALITY-LEVEL", "default"),       // new for #507
                settings.getBoolean("DEEPL-LANGUAGE-TIER", false),      // new for #507
                settings.getBoolean("DEEPL-USE-SEGMENTATION", false),   // new for #507
                settings.getBoolean("DEEPL-USE-PRO", false),
                table.preferredSegmentation, 
                table.getModel().getTranscription(), 
                table.selectionStartRow);
        
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
        boolean usePro = (Boolean) deepLParameters.get("USE-PRO");
        
        // new for #507        
        settings.put("DEEPL-SOURCE-LANGUAGE", sourceLanguage);
        settings.put("DEEPL-TARGET-LANGUAGE", targetLanguage);
        settings.put("DEEPL-FORMALITY-LEVEL", formalityLevel);
        settings.put("DEEPL-LANGUAGE-TIER", Boolean.toString(addLanguageTiers));
        settings.put("DEEPL-USE-SEGMENTATION", Boolean.toString(useSegmentation));
        settings.put("DEEPL-USE-PRO", Boolean.toString(usePro));

        // do this in a thread so we can report progress
        Thread deepLThread;
        deepLThread = new Thread(){
            @Override
            public void run() {
                DeepLConnector.API_TYPE apiType = DeepLConnector.API_TYPE.FREE;
                if (usePro){
                    apiType = DeepLConnector.API_TYPE.PROFESSIONAL;
                }
                
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
                
                int segmentationCode = AbstractSegmentation.getSegmentationCode((String) deepLParameters.get("SEGMENTATION-ALGORITHM"));
                AbstractSegmentation segmentationAlgorithm = AbstractSegmentation.getSegmentationAlgorithm(segmentationCode);
                String wordSegmentationName = AbstractSegmentation.getWordSegmentationName(segmentationCode);
                

                
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
                                String[] translation = deepLConnector.callDeepL(originalText, sourceLanguage, targetLanguage, formalityLevel, apiType);
                                addEvent(translation, event.getStart(), event.getEnd(), targetTier, languageTier);
                            }
                        } else if (!(useSegmentation) || !(sourceTier.getType().equals("t"))){                            
                            List<List<Event>> segmentChains = sourceTier.getSegmentChains(table.getModel().getTranscription().getBody().getCommonTimeline());
                            for (List<Event> segmentChain : segmentChains){
                                // 26-05-2023 -- moved this for #297
                                String originalText = "";
                                for (Event e : segmentChain){
                                    originalText+=e.getDescription();
                                }
                                String[] translation = deepLConnector.callDeepL(originalText, sourceLanguage, targetLanguage, formalityLevel, apiType);
                                addEvent(translation, segmentChain.get(0).getStart(), segmentChain.get(segmentChain.size()-1).getEnd(), targetTier, languageTier);
                            }
                        } else {
                            // segmentation is used
                            BasicTranscription copyTranscription = table.getModel().getTranscription().makeCopy();
                            for (int pos=0; pos<copyTranscription.getBody().getNumberOfTiers(); pos++){
                                Tier t = copyTranscription.getBody().getTierAt(pos);
                                if (!(t.getID().equals(sourceTier.getID()))) {
                                    copyTranscription.getBody().removeTierWithID(t.getID());
                                    pos--;
                                }
                            }
                            SegmentedTranscription segmentedTranscription = segmentationAlgorithm.BasicToSegmented(copyTranscription);
                            SegmentedTier segmentedSourceTier = segmentedTranscription.getBody().getSegmentedTierAt(0);
                            Segmentation wordSegmentation = segmentedSourceTier.getSegmentationWithName(wordSegmentationName);
                            SegmentList allContributionChains = wordSegmentation.getAllSegmentsWithName("sc");
                            for (Object o : allContributionChains){
                                TimedSegment segmentChain = (TimedSegment)o;
                                Vector allSegmentsMatching = segmentChain.getAllSegmentsMatching("[A-Za-z]*:(w|ip)");
                                List<String> tokens = new ArrayList<>();
                                for (Object o2 : allSegmentsMatching){
                                    Describable d = (Describable)o2;
                                    if(!(d.getDescription().equals("(")||d.getDescription().equals(")")||d.getDescription().equals("[")||d.getDescription().equals("]"))){
                                        tokens.add(d.getDescription());
                                    }
                                }
                                originalText = String.join(" ", tokens);
                                String[] translation = deepLConnector.callDeepL(originalText, sourceLanguage, targetLanguage, formalityLevel, apiType);
                                addEvent(translation, segmentChain.getStart(), segmentChain.getEnd(), targetTier, languageTier);
                                
                            }
                        }
                    } catch (JexmaraldaException | IOException | URISyntaxException | SAXException | FSMException ex) {
                        Logger.getLogger(DeepLAction.class.getName()).log(Level.SEVERE, null, ex);
                        pbd.addText("Error: " + ex.getLocalizedMessage());
                        pbd.setTextAreaBackgroundColor(java.awt.Color.RED);
                        String shortenedMessage = ex.getLocalizedMessage();
                        if (shortenedMessage.length()>300){
                            shortenedMessage = shortenedMessage.substring(0,300) + "...";
                        }
                        JOptionPane.showMessageDialog(pbd, shortenedMessage);
                    } 
                }
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pbd.addText("DeepL calls done.");
                            pbd.setTextAreaBackgroundColor(java.awt.Color.decode("#356811"));                            
                            success();
                        } catch (IOException | JexmaraldaException ex) {
                            pbd.addText("Error: " + ex.getLocalizedMessage());
                            pbd.setTextAreaBackgroundColor(java.awt.Color.RED);                            
                            String shortenedMessage = ex.getLocalizedMessage();
                            if (shortenedMessage.length()>300){
                                shortenedMessage = shortenedMessage.substring(0,300) + "...";
                            }
                            JOptionPane.showMessageDialog(pbd, shortenedMessage);
                        }
                    }
                });
                
                
                
            }

            private void addEvent(String[] translation, String start, String end, Tier targetTier, Tier languageTier) {
                pbd.addText(translation[0] + " " + translation[1]);
                Event translationEvent = new Event();
                translationEvent.setStart(start);
                translationEvent.setEnd(end);
                translationEvent.setDescription(translation[0]);
                targetTier.addEvent(translationEvent);

                if (addLanguageTiers){
                    Event languageEvent = new Event();
                    languageEvent.setStart(start);
                    languageEvent.setEnd(end);
                    languageEvent.setDescription(translation[1]);                                    
                    languageTier.addEvent(languageEvent);
                }
            }
            
        };
        
        
        deepLThread.start();

        
    }
    
    public void success() throws IOException, JexmaraldaException{       
        table.getModel().fireDataReset();
        table.transcriptionChanged = true;
    }

    @Override
    public void processConverterEvent(ConverterEvent converterEvent) {
        System.out.println(converterEvent.getMessage());
        pbd.addText(converterEvent.getMessage());
    }
}



