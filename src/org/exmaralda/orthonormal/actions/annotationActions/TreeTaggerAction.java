/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.actions.annotationActions;

import org.exmaralda.orthonormal.actions.AbstractApplicationAction;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.orthonormal.application.ApplicationControl;
import org.exmaralda.partitureditor.jexmaraldaswing.OKCancelDialog;
import org.exmaralda.tagging.PostProcessingRules;
import org.exmaralda.tagging.SextantOrthonormalIntegrator;
import org.exmaralda.tagging.TaggingProfiles;
import org.exmaralda.tagging.TreeTaggableOrthonormalTranscription;
import org.exmaralda.tagging.TreeTagger;
import org.exmaralda.tagging.swing.TreeTaggerParametersPanel;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public class TreeTaggerAction extends AbstractApplicationAction {
    
    ProgressBarDialog pbd;
     
     /** Creates a new instance of TreeTaggerAction
     * @param ac
     * @param name
     * @param icon */
    public TreeTaggerAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** TreeTaggerAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;

        // 1. get parameters from a dialog
        TreeTaggerParametersPanel  treeTaggerParametersPanel = new TreeTaggerParametersPanel();
        OKCancelDialog treeTaggerParametersDialog = new OKCancelDialog(ac.getFrame(), true);
        treeTaggerParametersDialog.setTitle("Tree Tagging");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(treeTaggerParametersPanel);
        treeTaggerParametersDialog.getContentPane().add(mainPanel, SwingConstants.CENTER);
        treeTaggerParametersDialog.pack();

        treeTaggerParametersDialog.setLocationRelativeTo(ac.getFrame());
        treeTaggerParametersDialog.setVisible(true);
        if (treeTaggerParametersDialog.getReturnStatus()==OKCancelDialog.RET_CANCEL) return;

        try {
            // 2. initialise the tagger
            String TTD = treeTaggerParametersPanel.getTreeTaggerDirectory();
            String PF = treeTaggerParametersPanel.getParameterFile();
            String PFE = treeTaggerParametersPanel.getParameterFileEncoding();
            String AF = treeTaggerParametersPanel.getAbbreviationsFile();
            String AFE = treeTaggerParametersPanel.getAbbreviationsFileEncoding();
            String[] OPT = treeTaggerParametersPanel.getOptions();
            final TreeTagger tt = new TreeTagger(TTD, PF, PFE, OPT);
            System.out.println("Tree Tagger initialised");
            
            //2a. setting up was succcessful so we can write the preferences
            //TaggingProfiles.writePreferences(TTD, PF, PFE, OPT);
            TaggingProfiles.writePreferences(TTD, PF, PFE, AF, AFE, OPT);
            
            
            PostProcessingRules ppr = new PostProcessingRules();
            ppr.read(PostProcessingRules.FOLK_RULES);
            Document trDoc = ac.getTranscription().getDocument();
            
            // get rid of all existing attributes for pos and lemma
            List l = XPath.selectNodes(trDoc, "//@lemma|//@pos|//@p-pos");
            for (Object o : l){
                Attribute a = (Attribute)o;
                a.detach();
            }
            final File intermediate = File.createTempFile("FLN","TMP");
            intermediate.deleteOnExit();
            FileIO.writeDocumentToLocalFile(intermediate, trDoc);
            
            String xpathToTokens = TreeTaggableOrthonormalTranscription.XPATH_ALL_WORDS_AND_PUNCTUATION;
            final TreeTaggableOrthonormalTranscription ttont = new TreeTaggableOrthonormalTranscription(intermediate, true);
            ttont.setXPathToTokens(xpathToTokens);
                        
            pbd = new ProgressBarDialog(ac.getFrame(), false);
            pbd.setLocationRelativeTo(ac.getFrame());
            pbd.setTitle("TreeTagging... ");
            pbd.setAlwaysOnTop(true);
            pbd.setVisible(true);
            pbd.progressBar.setIndeterminate(true);
            Thread tagThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            File output = File.createTempFile("FLN","TMP");
                            output.deleteOnExit();
                            tt.tag(ttont, output);
                            SextantOrthonormalIntegrator soi = new SextantOrthonormalIntegrator(intermediate.getAbsolutePath());
                            soi.integrate(output.getAbsolutePath());
                            final File output2 = File.createTempFile("FLN_Tagged_",".fln");
                            //output2.deleteOnExit();            
                            soi.writeDocument(output2.getAbsolutePath());
                            System.out.println("------ tagging done.");
                            SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                            taggingDone(output2);
                                    }
                                });
                        } catch (IOException | JDOMException ex) {
                                ex.printStackTrace();
                        }
                    }
            };
            tagThread.start();
            
            
        } catch (IOException | JDOMException ex) {
            Logger.getLogger(TreeTaggerAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        ac.status("File tagged");
    }
    
    private void taggingDone(File taggedFile){
        pbd.setVisible(false);
        JOptionPane.showMessageDialog(applicationControl.getFrame(), "Tagging done. Result is \n" + taggedFile.getAbsolutePath());
        applicationControl.status("Tagging done: " + taggedFile.getAbsolutePath());
        ((ApplicationControl)applicationControl).openTranscriptionFile(taggedFile);
        
    }

        
    
}
