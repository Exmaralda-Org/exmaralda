/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.partiture;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.folker.application.AbstractTimeviewPartiturPlayerControl;
import org.exmaralda.folker.timeview.AbstractTimeProportionalViewer;
import org.exmaralda.folker.timeview.ChangeZoomDialog;
import org.exmaralda.folker.timeview.TimeSelectionEvent;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.TimelineItem;
import org.exmaralda.partitureditor.sound.AVFPlayer;
import org.exmaralda.partitureditor.sound.JDSPlayer;
import org.exmaralda.partitureditor.sound.JavaFXPlayer;
import org.exmaralda.partitureditor.sound.MMFPlayer;
import org.exmaralda.partitureditor.sound.Playable;
import org.exmaralda.partitureditor.sound.PlayableEvent;
import org.exmaralda.webservices.Whisper4EXMARaLDA;
import org.exmaralda.webservices.WhisperConnector;
import org.exmaralda.webservices.swing.InsertWhisperResultDialog;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class PartiturEditorTimeviewPlayerControl extends AbstractTimeviewPartiturPlayerControl {

    JToggleButton zoomToggleButton;

    public PartiturEditorTimeviewPlayerControl(ExmaraldaApplication ac, AbstractTimeProportionalViewer tv, PartitureTableWithActions pt, Playable p) {
        super(ac, tv, pt, p);
        changeZoomDialog = new ChangeZoomDialog((JFrame)ac, false);
        changeZoomDialog.zoomLevelSlider.addChangeListener(this);
        changeZoomDialog.magnifyLevelSlider.addChangeListener(this);
    }

    @Override
    public void displayException(Exception ex) {
        // TODO?
        ex.printStackTrace();
    }

    @Override
    public void processPlayableEvent(PlayableEvent e) {
        // TODO?
        super.processPlayableEvent(e);
    }

    @Override
    public void processTimeSelectionEvent(TimeSelectionEvent event) {
        // TODO?
        // changed 11-02-2020
        super.processTimeSelectionEvent(event);
        // possible place for issue #377
        super.moveTimepoints(event);
        if ((playerState==PLAYER_IDLE)){
             if (player instanceof JDSPlayer){
                JDSPlayer jds = (JDSPlayer)player;
                if ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED)){
                    jds.updateVideo(selectionStart/1000.0);
                } else {
                    jds.updateVideo(selectionEnd/1000.0);
                }
            } else if (player instanceof JavaFXPlayer){
                JavaFXPlayer jfx = (JavaFXPlayer)player;
                if ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED)){
                    jfx.updateVideo(selectionStart/1000.0);
                } else {
                    jfx.updateVideo(selectionEnd/1000.0);
                } 
            } else if (player instanceof AVFPlayer){
                AVFPlayer avf = (AVFPlayer)player;
                if ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED)){
                    avf.updateVideo(selectionStart/1000.0);
                } else {
                    avf.updateVideo(selectionEnd/1000.0);
                }            
            } else if (player instanceof MMFPlayer){
                MMFPlayer mmf = (MMFPlayer)player;
                //HEY HO BERND THE BUILDER!
                 //Timeview cursor update may cause problems?
                if ((event.getType()==TimeSelectionEvent.START_TIME_CHANGED)){
                    mmf.updateVideo(selectionStart/1000.0);
                } else {
                    mmf.updateVideo(selectionEnd/1000.0);
                }
            } 
        }
        
    }

    @Override
    public void detachSelection(){
        super.detachSelection();
        unselectTimepoints();
        detachSelectionAction.setEnabled(false);
    }

    @Override
    public JToggleButton getZoomToggleButton() {
        return this.zoomToggleButton;
    }

    @Override
    public void whisperASR() {        
        String wavPath = partitur.getModel().getTranscription().getHead().getMetaInformation().getReferencedFile("wav");
        if (wavPath==null){
            String message = "No WAV audio file assigned to this transcription.\n" 
                             + "Whisper cannot run without a WAV audio file.\n" 
                             + "Please use Transcription > Recording to assign a WAV audio file.";
            JOptionPane.showMessageDialog(partitur, message);
            return;
        }
                
        
        int editRow = -1;
        int editCol = -1;
        
        // get the current selection
        if (partitur.isEditing && timeViewer.selectionAttached){
            editRow = partitur.selectionStartRow;
            editCol = partitur.selectionStartCol;
        }
        
        final int editRowF = editRow;
        final int editColF = editCol;

        if ((selectionStart<0) || (selectionEnd<0) || (selectionStart==selectionEnd)) {
            String message = "Select a stretch of the waveform to send it to Whisper." ;
            JOptionPane.showMessageDialog(partitur, message);            
            return;
        }
        
        final double startTime = selectionStart / 1000.0;
        final double endTime = selectionEnd / 1000.0;
        
        partitur.commitEdit(true);

        // do this in a thread so we can report progress
        Thread whisperThread = new Thread(){
            @Override
            public void run() {
                try { 
                    Whisper4EXMARaLDA w4e = new Whisper4EXMARaLDA();
                    final File audioFile = w4e.createWhisperInputFile(partitur.getModel().getTranscription(), startTime, endTime);
                    
                    long fileLengthInBytes = audioFile.length();
                    if (fileLengthInBytes > 24000000){
                        String message = "Audio file length is " + fileLengthInBytes / 1000000 + "MB. \n";
                        message+="Whisper will only accept files up to 25MB.\n ";
                        message+="Please try with a shorter stretch of audio.\n ";
                        JOptionPane.showMessageDialog(partitur, message);
                        return;
                    }
                    
                    

                    // call Whisper with the file 
                    String whisperKey = Whisper4EXMARaLDA.getWhisperKey();
                    WhisperConnector wc = new WhisperConnector(whisperKey);
                    System.out.println("Trying Whisper with " + audioFile.getAbsolutePath() + Double.toString(endTime));
                    //String result = wc.callWhisperSimple(audioFile);
                    String language = Whisper4EXMARaLDA.getWhisperLanguage();
                    String prompt = Whisper4EXMARaLDA.getWhisperPrompt();
                    String result = wc.callWhisperRegular(audioFile, language, prompt);
                    

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                whisperSuccess(result, editRowF, editColF, startTime, endTime);
                            } catch (IOException | JexmaraldaException ex) {
                                System.out.println(ex.getMessage());
                                JOptionPane.showMessageDialog(partitur, ex);
                            }
                        }
                    });


                } catch (JexmaraldaException | IOException | SAXException | FSMException | JDOMException | URISyntaxException ex) {
                    System.out.println(ex.getMessage());
                    JOptionPane.showMessageDialog(partitur, ex);
                } 
            }
            
        };
        
        
        whisperASRAction.setEnabled(false);
        
        whisperThread.start();

        
    }
    
    public void whisperSuccess(String result, int editRow, int editCol, double startTime, double endTime) throws IOException, JexmaraldaException{
        result = postProcessWhisperResult(result);
        if ((editRow>=0) && (editCol>=0)){
            partitur.getModel().setTableDataItem(result, editRow, editCol);
        } else {
            InsertWhisperResultDialog dialog = new InsertWhisperResultDialog((JFrame)partitur.getTopLevelAncestor(), true,
            partitur.getModel().getTranscription(), result, startTime, endTime); 
            dialog.setLocationRelativeTo(partitur);
            dialog.setVisible(true);
            
            Map<String, Object> param = dialog.getParameters();
            if (param.get("TIER-ID")!=null){
                String tierID = (String)(param.get("TIER-ID"));
                String text = (String)(param.get("TEXT"));
                
                Tier tier = partitur.getModel().getTranscription().getBody().getTierWithID(tierID);
                /*String tli1 = partitur.getModel().getTranscription().getBody().getCommonTimeline().insertTimelineItemWithTime(startTime, 0.01);
                partitur.getModel().fireColumnLabelsChanged();
                String tli2 = partitur.getModel().getTranscription().getBody().getCommonTimeline().insertTimelineItemWithTime(endTime, 0.01);
                partitur.getModel().fireColumnLabelsChanged();*/
                int[] indices = partitur.getModel().insertInterval(startTime, endTime, 0.01);
                int col1 = indices[0];
                int col2 = indices[1];
                TimelineItem tli1 = partitur.getModel().getTimelineItem(col1);
                TimelineItem tli2 = partitur.getModel().getTimelineItem(col2); 
                //String tli1 = partitur.getModel().getTranscription().getBody().getCommonTimeline().getTimelineItemAt(indices[0]).getID();
                //String tli2 = partitur.getModel().getTranscription().getBody().getCommonTimeline().getTimelineItemAt(indices[1]).getID();
                
                Event newEvent = new Event(tli1.getID(), tli2.getID(), text);
                tier.addEvent(newEvent);
                int row = partitur.getModel().getTranscription().getBody().lookupID(tier.getID());                
                partitur.getModel().fireEventAdded(row, col1, col2);
                partitur.getModel().fireFormatReset();
                //partitur.getModel().fireEventAdded(row, partitur.getModel().getColumnNumber(tli1), partitur.getModel().getColumnNumber(tli2));
                
                partitur.setNewSelection(row, col1, true);
                
                partitur.transcriptionChanged = true;
                
            }
        }        
        whisperASRAction.setEnabled(true);        
    }

    private String postProcessWhisperResult(String result) {
        // ": "We're really good friends on top of everything."
        if (result.startsWith("\": \"") && result.trim().endsWith("\"")){
            String better = result.substring(4);
            better = better.substring(0, better.lastIndexOf("\""));
            return better;
        }
        return result;
    }
    
    public void scrollToTime(double time) {
        int index3 = partitur.getModel().getTranscription().getBody().getCommonTimeline().getPositionForTime(time);
        partitur.setLeftColumn(index3);
        partitur.setSelection(-1, index3, -1, index3);
    }
    
    
    








}
