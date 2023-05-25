/*
 * NewFromSpeakertableAction.java
 *
 * Created on 6. Oktober 2003, 16:40
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.masker.WavFileException;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.sound.SilenceDetector;

/**
 * creates a new file from a speakertable (which the user is prompted to create and edit)
 * and a stylesheet (which is either specified in the preferences or a built-in stylesheet is used)
 * Menu: File --> New from speakertable
 * @author  thomas
 */
public class NewFromSilenceDetectionAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    ProgressBarDialog pbd;
    Vector<String> mediaFiles;
    
    /** Creates a new instance of NewFromSpeakertableAction
     * @param t */
    public NewFromSilenceDetectionAction(PartitureTableWithActions t) {
        super("New from silence detection...", t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        try {
            table.commitEdit(true);
            System.out.println("newFromSilenceDetectionAction!");
            newTranscription();
            table.transcriptionChanged = true;
            table.clearUndo();
            table.clearSearchResult();
            table.setFrameEndPosition(-2);
        } catch (IOException ex) {
            Logger.getLogger(NewFromSilenceDetectionAction.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(table, ex.getLocalizedMessage());
        } catch (WavFileException ex) {
            Logger.getLogger(NewFromSilenceDetectionAction.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(table, ex.getLocalizedMessage());
        } catch (JexmaraldaException ex) {
            Logger.getLogger(NewFromSilenceDetectionAction.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(table, ex.getLocalizedMessage());
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(NewFromSilenceDetectionAction.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(table, ex.getLocalizedMessage());
        }
    }
    
    private void newTranscription() throws IOException, WavFileException, JexmaraldaException, UnsupportedAudioFileException {
        boolean proceed = true;
        if (table.transcriptionChanged){proceed = table.checkSave();}
        if (!proceed) return;

        BasicTranscription dummy = new BasicTranscription();
        EditReferencedFilesDialog dialog = new EditReferencedFilesDialog(null, true, dummy.getHead().getMetaInformation().getReferencedFiles());
        dialog.defaultDirectory = table.getFilename();
        dialog.setLocationRelativeTo(this.table);
        dialog.setVisible(true);

        if ((dialog.getReferencedFiles().size()<1) || (dialog.getReferencedFiles().elementAt(0).length()<1)) return;

        mediaFiles = dialog.getReferencedFiles();

        SilenceDetectionParameterDialog sdpd = new SilenceDetectionParameterDialog(table.parent, true);
        
        sdpd.setLocationRelativeTo(table);
        sdpd.setTitle("New from silence detection...");
        sdpd.setVisible(true);
        
        if (sdpd.approved){
            final double[] parameters = sdpd.parametersPanel.getParameters();
            String wavFile = null;
            for (String s : mediaFiles){
                if (s.toUpperCase().endsWith(".WAV")){
                    wavFile = s;
                    break;
                }
            }
            if (wavFile==null){
                JOptionPane.showMessageDialog(table, "No wav audio available.");
                return;
            }
            
            final File wav = new File(wavFile);
            final SilenceDetector sd = new SilenceDetector(wav);
            pbd = new ProgressBarDialog((JFrame)(table.getTopLevelAncestor()), false);
            pbd.setLocationRelativeTo((JFrame)(table.getTopLevelAncestor()));
            pbd.setTitle("Silence Detection... ");
            pbd.setAlwaysOnTop(true);
            pbd.enableTimeEstimate(true);
            sd.addSilenceDetectorListener(pbd);
            pbd.setVisible(true);
            Thread silenceDetectionThread = new Thread() {
                @Override
                public void run() {
                        try {
                            final BasicTranscription newTranscription = sd.performSilenceDetection(wav, parameters);
                            System.out.println("------ tagging done.");
                            SwingUtilities.invokeLater(new Runnable() {
                            @Override
                                    public void run() {
                                        setupTranscription(newTranscription);
                                    }
                                });
                        } catch (IOException | WavFileException | JexmaraldaException | UnsupportedAudioFileException ex) {
                            System.out.println(ex.getMessage());
                        }
                }
            };
            silenceDetectionThread.start();
                    
            
        }



    }
    
    private void setupTranscription(BasicTranscription newTranscription) {
        pbd.setVisible(false);
        Speaker speaker = new Speaker();
        speaker.setID("SPK0");
        speaker.setAbbreviation("X");
        try {
            newTranscription.getHead().getSpeakertable().addSpeaker(speaker);
        } catch (JexmaraldaException ex) {
            // do nothing
        }
        newTranscription.getBody().getCommonTimeline().addTimelineItem();
        newTranscription.getBody().getCommonTimeline().addTimelineItem();
        Tier tier = new Tier("TIE0","SPK0","v","t");
        try {
            newTranscription.getBody().addTier(tier);
        } catch (JexmaraldaException ex) {
            // do nothing
        }
        newTranscription.makeDisplayNames();

        newTranscription.getHead().getMetaInformation().setReferencedFiles(mediaFiles);


        table.getModel().setTranscription(newTranscription);
        table.showAllTiers();
        table.setFilename("untitled.exb");
        table.linkPanelDialog.getLinkPanel().emptyContents();
        table.largeTextField.setText("");
        //table.reexportHTMLAction.setEnabled(false);
        table.restoreAction.setEnabled(false);
        table.reconfigureAutoSaveThread();
        table.setupMedia();
        table.status("New transcription");
    }
    
}
