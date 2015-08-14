/*
 * EditMetaInformationAction.java
 *
 * Created on 17. Juni 2003, 09:44
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.exmaralda.folker.application.ApplicationControl;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.masker.MaskFileDialog;
import org.exmaralda.masker.MaskTimeCreator;
import org.exmaralda.masker.Masker;
import org.exmaralda.masker.MaskerProgressDialog;
import org.exmaralda.masker.WavFileException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
/**
 *
 * opens a dialog for editing the meta information 
 * Menu: File --> Edit meta information
 * @author  thomas
 */
public class MaskAudioAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditMetaInformationAction */
    public MaskAudioAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Mask audio file...", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("MaskAudioAction!");
        table.commitEdit(true);
        try {
            maskAudio();
        } catch (Exception ex) {
            ex.printStackTrace();
            String message = "File could not be masked:\n" + ex.getLocalizedMessage();
            javax.swing.JOptionPane.showMessageDialog(table, message);
        }
    }
    
    private void maskAudio() throws IOException, WavFileException, URISyntaxException, ClassNotFoundException, JexmaraldaException{
        BasicTranscription transcription = table.getModel().getTranscription();
        JFrame frame = (JFrame) table.getTopLevelAncestor();
        
        // select a tier as the basis for masking
        SingleTierSelectionDialog tierSelectionDialog = new SingleTierSelectionDialog(transcription, frame, true);
        tierSelectionDialog.setVisible(true);
        if (tierSelectionDialog.getReturnStatus()==SingleTierSelectionDialog.RET_CANCEL) return;
        String tierID = tierSelectionDialog.getSelectedTierID();
        
        // get input and output for masking
        MaskFileDialog maskFileDialog = new MaskFileDialog(frame, true);
        maskFileDialog.setFile(transcription.getHead().getMetaInformation().getReferencedFile("wav"));
        maskFileDialog.setLocationRelativeTo(frame);
        maskFileDialog.setVisible(true);
        if (!maskFileDialog.approved) return;

        final File in = maskFileDialog.getSourceFile();
        final File out = maskFileDialog.getTargetFile();
        final int method = maskFileDialog.getMethod();

        // Security checks for both files
        if (!(in.canRead())){
            JOptionPane.showMessageDialog(frame, 
                    in.getAbsolutePath() + "\n" +
                    FOLKERInternationalizer.getString("option.cannotaccessfile"));
        }            
        if (out.exists()){
            int retVal = JOptionPane.showConfirmDialog( frame, 
                                                        FOLKERInternationalizer.getString("option.fileexists"),
                                                        FOLKERInternationalizer.getString("option.confirmation"),
                                                        JOptionPane.YES_NO_OPTION);
            if (retVal!=JOptionPane.YES_OPTION) return;                 
            if (!(out.canWrite())){
                JOptionPane.showMessageDialog(frame, 
                        in.getAbsolutePath() + "\n" +
                        FOLKERInternationalizer.getString("option.cannotaccessfile"));
            }
        }

        // Do the actual masking
        final Masker masker = new Masker(in, out);
        final double[][] maskTimes = MaskTimeCreator.createTimesFromEXMARaLDATier(transcription, tierID);


        final MaskerProgressDialog pbd = new MaskerProgressDialog(frame, false); 
        pbd.setTitle(FOLKERInternationalizer.getString("masker.progressdialogtitle"));
        pbd.setLocationRelativeTo(frame);
        pbd.setVisible(true);
        masker.addMaskerListener(pbd);
        final Runnable doDisplayMaskDone = new Runnable() {
                @Override
                public void run() {
                    maskDone(in, out);
                }
        };
        Thread maskThread = new Thread() {
                @Override
                public void run() {
                    try {
                            masker.mask(method, maskTimes); 
                            pbd.setVisible(false);
                            try {
                                    javax.swing.SwingUtilities.invokeAndWait(doDisplayMaskDone);
                            } catch (Exception ex) {
                                    ex.printStackTrace();
                            }
                    } catch (Exception ex) {
                            ex.printStackTrace();
                    }
                }
        };
        maskThread.start();
                

    }
    
    void maskDone(File in, File out) {
        String text = 
                FOLKERInternationalizer.getString("masker.accomplished") + "\n" 
                + FOLKERInternationalizer.getString("masker.sourcefile") + ": " + in.getAbsolutePath() + "\n"
                + FOLKERInternationalizer.getString("masker.targetfile") + ": " + out.getAbsolutePath();
        JOptionPane.showMessageDialog((JFrame) table.getTopLevelAncestor(), 
                text, FOLKERInternationalizer.getString("masker.accomplished"), JOptionPane.INFORMATION_MESSAGE);
    }
    
    
}
