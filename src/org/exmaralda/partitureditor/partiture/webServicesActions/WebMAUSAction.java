/*
 * NewAction.java
 *
 * Created on 16. Juni 2003, 16:23
 */

package org.exmaralda.partitureditor.partiture.webServicesActions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicBody;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.jexmaralda.TimelineItem;
import org.exmaralda.partitureditor.jexmaralda.convert.PraatConverter;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;
import org.exmaralda.webservices.MAUS4EXMARaLDA;
import org.exmaralda.webservices.MAUSConnector;
import org.exmaralda.webservices.swing.WebServiceProgessDialog;
import org.exmaralda.webservices.swing.MAUSParameterDialog;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;



/**
 * Creates a new transcription
 * Menu: File --> New
 * @author  thomas
 */
public class WebMAUSAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    WebServiceProgessDialog pbd;
    MAUSParameterDialog mausParameterDialog;
    
    /** Creates a new instance of NewAction
     * @param t
     * @param icon */
    public WebMAUSAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Web MAUS...", icon, t);
        mausParameterDialog = new MAUSParameterDialog(table.parent, true);        
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        try {
            table.commitEdit(true);
            System.out.println("WebMAUSAction!");
            webMAUS();
            table.transcriptionChanged = false;
            table.clearUndo();
            table.clearSearchResult();
            table.setFrameEndPosition(-2);
        } catch (JexmaraldaException | IOException | JDOMException | SAXException | FSMException ex) {
            System.out.println(ex.getMessage());
            javax.swing.JOptionPane.showMessageDialog(table.getParent(), ex.getLocalizedMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void webMAUS() throws JexmaraldaException, IOException, JDOMException, SAXException, FSMException{
        
        String wavPath = table.getModel().getTranscription().getHead().getMetaInformation().getReferencedFile("wav");
        if (wavPath==null){
            String message = "No WAV audio file assigned to this transcription.\n" 
                             + "WebMAUS cannot run without a WAV audio file.\n" 
                             + "Please use Transcription > Recording to assign a WAV audio file.";
            JOptionPane.showMessageDialog(table, message);
            return;
        }
        
        // let the user define parameters        
        mausParameterDialog.setLocationRelativeTo(table);
        mausParameterDialog.setVisible(true);
        if (!mausParameterDialog.approved) return;
        final HashMap<String, Object> mausParameters = mausParameterDialog.getMAUSParameters();
        
                       
        // get the transcription and the current selection
        final BasicTranscription bt = table.getModel().getTranscription();
        final String tierID = bt.getBody().getTierAt(table.selectionStartRow).getID();
        final String startID = bt.getBody().getCommonTimeline().getTimelineItemAt(table.selectionStartCol).getID();
        // this must be the bug of issue #42: beware of events stretching more than one timeline item!
        // final String endID = bt.getBody().getCommonTimeline().getTimelineItemAt(table.selectionEndCol+1).getID();
        int span = table.getModel().getCellSpan(table.selectionStartRow, table.selectionEndCol);
        final String endID = bt.getBody().getCommonTimeline().getTimelineItemAt(table.selectionEndCol+span).getID();

        pbd = new WebServiceProgessDialog(table.parent, false);
        pbd.setLocationRelativeTo(table.parent);
        pbd.setTitle("WebMAUS... ");
        //pbd.setAlwaysOnTop(true);
        pbd.setVisible(true);

        // do this in a thread so we can report progress
        Thread mausThread = new Thread(){
            @Override
            public void run() {
                try {
                    pbd.addText("Creating input files...");
                    // pass all parameters to a MAUS4EXMARaLDA instance and let it generate the MAUS input files
                    final MAUS4EXMARaLDA m4e = new MAUS4EXMARaLDA();
                    m4e.setParameters(mausParameters);
                    final File[] files = m4e.createMAUSInputFiles(bt, tierID, startID, endID);
                    pbd.addText("Audio input: " + files[1].getAbsolutePath());
                    pbd.addText("Text input: " + m4e.allText);

                    // call MAUS with the files and write the result to a temporary text grid file
                    MAUSConnector mc = new MAUSConnector();
                    pbd.addText("Language Parameter: " + mausParameters.get("LANGUAGE"));
                    pbd.addText("Calling WebMAUS at " + mc.webMausBasicURL + ".....");
                    String result = mc.callMAUS(files[0], files[1], mausParameters);
                    final File temp = File.createTempFile("MAUSRESULT", ".textGrid");
                    temp.deleteOnExit();
                    FileOutputStream fos = new FileOutputStream(temp);
                    fos.write(result.getBytes("UTF-8"));
                    fos.close();    
                    pbd.addText("WebMAUS result written to " + temp.getAbsolutePath());
                    pbd.addText("Processing result in EXMARaLDA...");

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                pbd.addText("Done.");
                                pbd.setTextAreaBackgroundColor(java.awt.Color.decode("#356811"));                                
                                success(temp, files[1], mausParameters, tierID, m4e.modifiedStartID, m4e.modifiedEndID);
                            } catch (IOException | JexmaraldaException ex) {
                                pbd.addText("Error: " + ex.getLocalizedMessage());
                                JOptionPane.showMessageDialog(pbd, ex);
                            }
                        }
                    });


                } catch (JexmaraldaException | IOException | SAXException | FSMException | JDOMException ex) {
                    pbd.setTextAreaBackgroundColor(java.awt.Color.RED);                                
                    pbd.addText("Error: " + ex.getLocalizedMessage());
                    JOptionPane.showMessageDialog(pbd, ex);
                } 
            }
            
        };
        mausThread.start();

        
    }
    
    public void success(File praatFile, File wavFile, HashMap<String, Object> mausParameters,
            String tierID, String startID, String endID) throws IOException, JexmaraldaException{
        // read the text grid as an EXMARaLDA transcription
        PraatConverter pc = new PraatConverter();
        BasicTranscription bt2 = pc.readPraatFromFile(praatFile.getAbsolutePath(), "UTF-8");
        bt2.getHead().getMetaInformation().setReferencedFile(wavFile.getAbsolutePath());
        bt2.getHead().setSpeakertable(table.getModel().getTranscription().getHead().getSpeakertable());
        
        /*WORDS-ORTHOGRAPHIC", wordsOrthoCheckBox.isSelected());
        WORDS-SAMPA", wordsSAMPACheckBox.isSelected());
        PHONEMES", phonemesCheckBox.isSelected());
        MERGE", mergeWithExistingRadioButton.isSelected()); */
        
        Tier sourceTier = table.getModel().getTranscription().getBody().getTierWithID(tierID);
        bt2.getBody().getTierAt(0).setCategory("w-orth");        
        bt2.getBody().getTierAt(0).setSpeaker(sourceTier.getSpeaker());
        bt2.getBody().getTierAt(0).setID(sourceTier.getID() + "_MAUS_W_ORTH");
        bt2.getBody().getTierAt(0).setDisplayName(bt2.getBody().getTierAt(0).getDescription(bt2.getHead().getSpeakertable()));
        
        bt2.getBody().getTierAt(1).setCategory("w-sampa");
        bt2.getBody().getTierAt(1).setSpeaker(sourceTier.getSpeaker());
        bt2.getBody().getTierAt(1).setID(sourceTier.getID() + "_MAUS_W_SAMPA");
        bt2.getBody().getTierAt(1).setDisplayName(bt2.getBody().getTierAt(1).getDescription(bt2.getHead().getSpeakertable()));

        bt2.getBody().getTierAt(2).setCategory("p-sampa");
        bt2.getBody().getTierAt(2).setSpeaker(sourceTier.getSpeaker());
        bt2.getBody().getTierAt(2).setID(sourceTier.getID() + "_MAUS_P_SAMPA");
        bt2.getBody().getTierAt(2).setDisplayName(bt2.getBody().getTierAt(2).getDescription(bt2.getHead().getSpeakertable()));
        
        
        Boolean phonemes = (Boolean) mausParameters.get("PHONEMES");
        if (!phonemes){
            bt2.getBody().removeTierAt(2);
        }
        Boolean wordsSampa = (Boolean) mausParameters.get("WORDS-SAMPA");
        if (!wordsSampa){
            bt2.getBody().removeTierAt(1);
        }
        Boolean wordsOrtho = (Boolean) mausParameters.get("WORDS-ORTHOGRAPHIC");
        if (!wordsOrtho){
            bt2.getBody().removeTierAt(0);
        }
        bt2.getBody().removeUnusedTimelineItems();
        
        
        File temp2 = File.createTempFile("EXMARaLDA_MAUS", ".exb");
        bt2.writeXMLToFile(temp2.getAbsolutePath(), "none");
        
        // new 31-05-2017: issue #91
        Timeline tl = bt2.getBody().getCommonTimeline();
        for (int pos=0; pos<bt2.getBody().getNumberOfTiers(); pos++){
            Tier tier = bt2.getBody().getTierAt(pos);
            if (!tier.isStratified(tl)){
                String message = "The MAUS result is not stratified, i.e.\n"
                        + "it contains overlapping annotations. \n"
                        + "Operation is aborted. Please check \n"
                        + temp2.getAbsolutePath();
                JOptionPane.showMessageDialog(table, message);
                return;
            }
        }
        
        
        Boolean merge = (Boolean) mausParameters.get("MERGE");
        if (!merge){
            // ...set the transcription for the partitur to the newly read transcription...
            table.getModel().setTranscription(bt2);
            table.setFilename(temp2.getAbsolutePath());
            // make the changes to the media panel (i.e. set the media file if there is one)
            table.setupMedia();
            // make the changes to the praat panel (version 1.3. and later)
            table.setupPraatPanel();
            
            table.transcriptionChanged = false;
            table.formatChanged = false;

            table.status("Transcription " + temp2.getAbsolutePath() + " opened");        
        } else {
            // merge the bleeding two
            // 27-10-2023: #432
            double tolerance = (double) mausParameters.get("TOLERANCE");
            boolean force = (boolean) mausParameters.get("FORCE");
            HashMap<String, String> timelineMappings = new HashMap<>();
            Timeline targetTimeline = table.getModel().getTranscription().getBody().getCommonTimeline();
            double startTime = targetTimeline.getTimelineItemWithID(startID).getTime();
            double endTime = targetTimeline.getTimelineItemWithID(endID).getTime();
            System.out.println("START-TIME " + startTime);
            for (int i = 0; i<bt2.getBody().getCommonTimeline().getNumberOfTimelineItems(); i++){
                TimelineItem tli = bt2.getBody().getCommonTimeline().getTimelineItemAt(i);
                tli.setTime(tli.getTime()+ startTime);
                if (i==0 && force){
                    tli.setTime(startTime);
                }
                if (i==bt2.getBody().getCommonTimeline().getNumberOfTimelineItems()-1 && force){
                    tli.setTime(endTime);
                }
                int found = targetTimeline.findTimelineItem(tli.getTime(), tolerance);
                if (found<0){
                    String newID = targetTimeline.getFreeID();
                    timelineMappings.put(tli.getID(), newID);
                    tli.setID(newID);
                    targetTimeline.insertAccordingToTime(tli);
                } else {
                    String targetTLI = targetTimeline.getTimelineItemAt(found).getID();
                    timelineMappings.put(tli.getID(), targetTLI);
                }
            }
            BasicBody targetBody = table.getModel().getTranscription().getBody();
            for (int j=0; j<bt2.getBody().getNumberOfTiers(); j++){
                Tier tier = bt2.getBody().getTierAt(j);
                for (int k=0; k<tier.getNumberOfEvents(); k++){
                    Event e = tier.getEventAt(k);
                    //System.out.println("Changing event " + e.getDescription() + ": " + e.getStart() + " / " + timelineMappings.get(e.getStart()));
                    e.setStart(timelineMappings.get(e.getStart()));
                    e.setEnd(timelineMappings.get(e.getEnd()));
                }
                tier.updatePositions();
                if (!targetBody.containsTierWithID(tier.getID())){
                    //table.getModel().insertTier(tier, targetBody.lookupID(tierID)+1);
                    targetBody.insertTierAt(tier, targetBody.lookupID(tierID)+1);
                    targetBody.updatePositions();
                } else {
                    Tier targetTier = targetBody.getTierWithID(tier.getID());
                    for (int k=0; k<tier.getNumberOfEvents(); k++){
                        Event e = tier.getEventAt(k);
                        targetTier.addEvent(e);
                    }                   
                }                
            }
            
            //bt2.writeXMLToFile("C:\\Users\\Schmidt\\Desktop\\TEST\\merge_out.exb", "none");
            //table.getModel().fireDataReset();
            table.resetData();           
        }
        

        
    }
}



