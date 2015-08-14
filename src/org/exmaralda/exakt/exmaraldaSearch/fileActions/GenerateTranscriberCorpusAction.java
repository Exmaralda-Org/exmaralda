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
import org.exmaralda.common.corpusbuild.TranscriberBuilder;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.exakt.wizard.folkercorpuswizard.TranscriberCorpusWizard;
/**
 *
 * @author thomas
 */
public class GenerateTranscriberCorpusAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    ProgressBarDialog pbd;

    /** Creates a new instance of OpenCorpusAction */
    public GenerateTranscriberCorpusAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    public void actionPerformed(ActionEvent e) {
        TranscriberCorpusWizard theMagician = new TranscriberCorpusWizard(exaktFrame, true);
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
            final TranscriberBuilder theBuilder = new TranscriberBuilder(new File(comaPath), folkerFiles, directoryName, separateDirectory, writeBasic);
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
