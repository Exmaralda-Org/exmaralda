/*
 * OpenCorpusAction.java
 *
 * Created on 9. Februar 2007, 11:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.fileActions;

import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.exmaralda.common.corpusbuild.FOLKERBuilder;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.exakt.wizard.folkercorpuswizard.FolkerCorpusWizard;
/**
 *
 * @author thomas
 */
public class GenerateFOLKERCorpusAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    ProgressBarDialog pbd;

    /** Creates a new instance of OpenCorpusAction */
    public GenerateFOLKERCorpusAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    public void actionPerformed(ActionEvent e) {
        FolkerCorpusWizard theMagician = new FolkerCorpusWizard(exaktFrame, true);
        theMagician.setLocationRelativeTo(exaktFrame);
        theMagician.setVisible(true);

        if (!theMagician.complete){
            return;
        }

        Object[] theData = theMagician.getData();
        final String comaPath = (String)theData[0];
        Vector<File> folkerFiles = (Vector<File>)theData[1];
        Object[] parameters = (Object[])theData[2];
        boolean separateDirectory = ((Boolean)parameters[0]).booleanValue();
        String directoryName = (String)parameters[1];
        boolean writeBasic = ((Boolean)parameters[2]).booleanValue();
        try {
            final FOLKERBuilder theBuilder = new FOLKERBuilder(new File(comaPath), folkerFiles, directoryName, separateDirectory, writeBasic);
            pbd = new ProgressBarDialog(exaktFrame, false);
            pbd.setLocationRelativeTo(exaktFrame);
            pbd.setTitle("Generating corpus... ");
            theBuilder.addSearchListener(pbd);
            pbd.setVisible(true);

            final Runnable doItWhenIsOver = new Runnable() {
                 public void run() {
                    File file = new File(comaPath);
                    if (file!=null){
                        exaktFrame.doOpen(file);
                        exaktFrame.setLastCorpusPath(file);
                    }
                 }
             };
            Thread generateThread = new Thread(){
                @Override
                public void run(){
                    try {
                        theBuilder.doBuild();
                        if (theBuilder.getCountSegmentationErrors()>0){
                            String message = Integer.toString(theBuilder.getCountSegmentationErrors())
                                    + " transcriptions could not be segmented using\n"
                                    + "the cGAT minimal segmentation algorithm.\n"
                                    + "The corpus wordlist is therefore incomplete.\n"
                                    + "Use the corpus manager to edit segmentation errors in this corpus.";
                            JOptionPane.showMessageDialog(exaktFrame, message, "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                        javax.swing.SwingUtilities.invokeLater(doItWhenIsOver);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        pbd.setVisible(false);
                        exaktFrame.showErrorDialog(ex);

                    }
                }
            };
            generateThread.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        /*File file = theMagician.resultFile;
        if (file!=null){
            exaktFrame.doOpen(file);
            exaktFrame.setLastCorpusPath(file);
        }*/
    }
    
}
