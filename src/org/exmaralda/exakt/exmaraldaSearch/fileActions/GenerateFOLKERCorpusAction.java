/*
 * OpenCorpusAction.java
 *
 * Created on 9. Februar 2007, 11:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.fileActions;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.corpusbuild.FOLKERBuilder;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.exakt.wizard.corpuswizards.FolkerCorpusWizard;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;
/**
 *
 * @author thomas
 */
public class GenerateFOLKERCorpusAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    ProgressBarDialog pbd;

    /** Creates a new instance of OpenCorpusAction
     * @param ef
     * @param title
     * @param icon */
    public GenerateFOLKERCorpusAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            FolkerCorpusWizard theMagician = new FolkerCorpusWizard(exaktFrame, true);
            theMagician.setLocationRelativeTo(exaktFrame);
            theMagician.setVisible(true);
            
            if (!theMagician.complete){
                return;
            }
            
            Object[] theData = theMagician.getData();
            final String comaPath = (String)theData[0];
            List<File> folkerFiles = (List<File>)theData[1];
            Object[] parameters = (Object[])theData[2];
            boolean separateDirectory = ((Boolean)parameters[0]);
            String directoryName = (String)parameters[1];
            boolean writeBasic = ((Boolean)parameters[2]);
            final FOLKERBuilder theBuilder = new FOLKERBuilder(new File(comaPath), folkerFiles, directoryName, separateDirectory, writeBasic); /*File file = theMagician.resultFile;
            if (file!=null){
            exaktFrame.doOpen(file);
            exaktFrame.setLastCorpusPath(file);
            }*/
            pbd = new ProgressBarDialog(exaktFrame, false);
            pbd.setLocationRelativeTo(exaktFrame);
            pbd.setTitle("Generating corpus... ");
            theBuilder.addSearchListener(pbd);
            pbd.setVisible(true);
            final Runnable doItWhenIsOver = new Runnable() {
                @Override
                public void run() {
                    File file = new File(comaPath);
                    exaktFrame.doOpen(file);
                    exaktFrame.setLastCorpusPath(file);
                }
            };
            Thread generateThread = new Thread(){
                @Override
                public void run(){
                    try {
                        FOLKERBuilder.BUILD_METHODS buildMethod = FOLKERBuilder.BUILD_METHODS.BUILD_VIA_EXB;
                        boolean isOnlyFln = true;
                        for (File f : folkerFiles){
                            if (!(f.getName().toLowerCase().endsWith(".fln"))){
                                isOnlyFln = false;
                                System.out.println("Building via EXB");
                                break;
                            }
                        }
                        if (isOnlyFln){
                            buildMethod = FOLKERBuilder.BUILD_METHODS.BUILD_VIA_EXS;
                            System.out.println("Building via EXS");
                        }
                        theBuilder.doBuild(buildMethod);
                        if (theBuilder.getCountSegmentationErrors()>0){
                            String message = Integer.toString(theBuilder.getCountSegmentationErrors())
                                    + " transcriptions could not be segmented using\n"
                                    + "the cGAT minimal segmentation algorithm.\n"
                                    + "The corpus wordlist is therefore incomplete.\n"
                                    + "Use the corpus manager to edit segmentation errors in this corpus.";
                            JOptionPane.showMessageDialog(exaktFrame, message, "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                        javax.swing.SwingUtilities.invokeLater(doItWhenIsOver);
                    } catch (HeadlessException | IOException | URISyntaxException | ParserConfigurationException | TransformerException | JexmaraldaException | JDOMException | SAXException ex) {
                        System.out.println(ex.getLocalizedMessage());
                        pbd.setVisible(false);
                        exaktFrame.showErrorDialog(ex);
                    }
                }
            };
            generateThread.start();
        } catch (IOException ex) {
            Logger.getLogger(GenerateFOLKERCorpusAction.class.getName()).log(Level.SEVERE, null, ex);
            pbd.setVisible(false);
            exaktFrame.showErrorDialog(ex);
        }
    }
    
}
