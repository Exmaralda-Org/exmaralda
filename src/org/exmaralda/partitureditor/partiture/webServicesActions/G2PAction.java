/*
 * NewAction.java
 *
 * Created on 16. Juni 2003, 16:23
 */

package org.exmaralda.partitureditor.partiture.webServicesActions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
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
import org.exmaralda.webservices.G2PConnector;
import org.exmaralda.webservices.swing.WebServiceProgessDialog;
import org.exmaralda.webservices.swing.DeepLParameterDialog;
import org.exmaralda.webservices.swing.G2PParameterDialog;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;



/**
 * Creates a new transcription
 * Menu: File --> New
 * @author  thomas
 */
public class G2PAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction implements ConverterListener {
    
    WebServiceProgessDialog pbd;
    

    /** Creates a new instance of NewAction
     * @param t
     * @param icon */
    public G2PAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("G2P Grapheme Phoneme Conversion...", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        try {
            table.commitEdit(true);
            System.out.println("G2PAction!");
            g2p();
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
    
    
    private void g2p() throws JexmaraldaException, IOException, JDOMException, SAXException, FSMException{
        
        // let the user define parameters
        G2PParameterDialog g2pParameterDialog = new G2PParameterDialog(table.parent, true);
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor");
        
        // retrieve values from preferences
        String apiKey = settings.get("DEEPL-API-KEY", "");

        g2pParameterDialog.setParameters(apiKey, table.preferredSegmentation, table.getModel().getTranscription(), table.selectionStartRow);
        
        g2pParameterDialog.setLocationRelativeTo(table);
        g2pParameterDialog.setVisible(true);
        if (!g2pParameterDialog.approved) return;
        final HashMap<String, Object> g2pParameters = g2pParameterDialog.getG2PParameters();
        String sourceLanguage = (String) g2pParameters.get("SOURCE-LANGUAGE");
        boolean useSpaces = (Boolean) g2pParameters.get("USE-SPACES");
        boolean useBrackets = (Boolean) g2pParameters.get("USE-BRACKETS");
        
        String[][] webServiceParametersArray = {
            {"iform","txt"},
            {"oform","txt"},
            {"lng", (String) g2pParameters.get("lng")},
            {"outsym", (String) g2pParameters.get("outsym")},
            {"syl", (String) g2pParameters.get("syl")},
            {"stress", (String) g2pParameters.get("stress")},            
        };
        final HashMap<String, Object> webServiceParameters = new HashMap<>();
        for (String[] pair : webServiceParametersArray){
            webServiceParameters.put(pair[0], pair[1]);
        }
        
        
        pbd = new WebServiceProgessDialog(table.parent, false);
        pbd.setLocationRelativeTo(table.parent);
        pbd.setTitle("G2P Conversion... ");
        pbd.setVisible(true);
        
        
        
        G2PConnector g2pConnector = new G2PConnector();             
        boolean useSegmentation = (Boolean) g2pParameters.get("USE-SEGMENTATION");

        // do this in a thread so we can report progress
        Thread deepLThread = new Thread(){
            @Override
            public void run() {
                boolean useSelectedTier = (boolean) g2pParameters.get("SELECTED-TIER");
                int[] tierPositions;
                int tiersAdded = 0;
                if (useSelectedTier){
                    tierPositions = new int[1];
                    tierPositions[0] = table.selectionStartRow;
                } else {
                    String tierCategory = (String) g2pParameters.get("TIER-CATEGORY");
                    tierPositions = table.getModel().getTranscription().getBody().findTiersWithCategory(tierCategory);                         
                }

                //resultTiers = new ArrayList<>();
                //languageTiers = new ArrayList<>();
                
                for (int tierPosition : tierPositions){
                    try {
                        int adjustedTierPosition = tierPosition + tiersAdded;
                        Tier sourceTier = table.getModel().getTranscription().getBody().getTierAt(adjustedTierPosition);
                        
                        Tier targetTier = new Tier(table.getModel().getTranscription().getBody().getFreeID(), sourceTier.getSpeaker(), "G2P", "a");
                        targetTier.setDisplayName("[G2P]");
                        table.getModel().getTranscription().getBody().insertTierAt(targetTier, adjustedTierPosition + 1);
                        table.getModel().getTierFormatTable().addTierFormat(new TierFormat("a", targetTier.getID()));
                        tiersAdded++;
                        //resultTiers.add(targetTier);
                        
                        
                        boolean eventByEvent = (boolean) g2pParameters.get("EVENT-BY-EVENT");
                        if (eventByEvent){
                            for (int pos=0; pos<sourceTier.getNumberOfEvents(); pos++){
                                Event event = sourceTier.getEventAt(pos);
                                String originalText = event.getDescription();
                                File tempInputFile = createTempInputFile(originalText);
                                String result = g2pConnector.callG2P(tempInputFile, webServiceParameters).replace("\n", "");
                                if (!useSpaces){
                                    result = result.replace(" ", "");
                                }
                                if (useBrackets){
                                    result = "[" + result.replaceAll("\\t", "]  [") + "]"; 
                                } else {
                                    result = result.replaceAll("\\t", "  ");                                    
                                }
                                pbd.addText(originalText + " --> " + result);
                                Event g2pEvent = new Event();
                                g2pEvent.setStart(event.getStart());
                                g2pEvent.setEnd(event.getEnd());
                                g2pEvent.setDescription(result);
                                targetTier.addEvent(g2pEvent);                             
                            }
                        } else {
                            List<List<Event>> segmentChains = sourceTier.getSegmentChains(table.getModel().getTranscription().getBody().getCommonTimeline());
                            for (List<Event> segmentChain : segmentChains){
                                if (!(useSegmentation)){
                                    String originalText = "";
                                    for (Event e : segmentChain){
                                        originalText+=e.getDescription();
                                    }
                                    File tempInputFile = createTempInputFile(originalText);
                                    String result = g2pConnector.callG2P(tempInputFile, webServiceParameters).replace("\n", "");
                                    if (!useSpaces){
                                        result = result.replace(" ", "");
                                    }
                                    if (useBrackets){
                                        result = "[" + result.replaceAll("\\t", "]  [") + "]"; 
                                    } else {
                                        result = result.replaceAll("\\t", "  ");                                    
                                    }
                                    pbd.addText(originalText + " --> " + result);
                                    Event g2pevent = new Event();
                                    g2pevent.setStart(segmentChain.get(0).getStart());
                                    g2pevent.setEnd(segmentChain.get(segmentChain.size()-1).getEnd());
                                    g2pevent.setDescription(result);
                                    targetTier.addEvent(g2pevent);
                                } else {
                                    // to do : segmentation is used
                                }
                                
                            }
                        }
                    } catch (JexmaraldaException | IOException  ex) {
                        Logger.getLogger(G2PAction.class.getName()).log(Level.SEVERE, null, ex);
                        pbd.addText("Error: " + ex.getLocalizedMessage());
                    } catch (JDOMException ex) {
                        Logger.getLogger(G2PAction.class.getName()).log(Level.SEVERE, null, ex);
                    }
                        
                }
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pbd.addText("G2P calls done.");
                            pbd.setTextAreaBackgroundColor(java.awt.Color.decode("#356811"));
                            success();
                        } catch (IOException | JexmaraldaException ex) {
                            pbd.addText("Error: " + ex.getLocalizedMessage());
                            JOptionPane.showMessageDialog(pbd, ex);
                        }
                    }
                });
                
                    

            }

            private File createTempInputFile(String originalText) throws IOException {
                File tempInputFile = File.createTempFile("G2P", ".txt");
                Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(tempInputFile), "UTF-8"));
                try {
                    out.write(originalText);
                } finally {
                    out.close();
                }      
                return tempInputFile;
            }

            private String normalize(String result) {
                return result.replaceAll(" ", "").replaceAll("\\t", "] [");
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



