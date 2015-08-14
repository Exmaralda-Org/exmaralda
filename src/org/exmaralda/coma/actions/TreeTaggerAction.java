/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.tagging.CorpusTreeTagger;
import org.exmaralda.tagging.TaggingProfiles;
import org.exmaralda.tagging.TreeTagger;
import org.exmaralda.tagging.swing.COMATreeTaggingDialog;

/**
 * 
 * @author thomas Second attempt at application crossover! Bleeding hell!!!!!
 */
public class TreeTaggerAction extends org.exmaralda.coma.root.ComaAction {

	ProgressBarDialog pbd;


	public TreeTaggerAction(String text, ImageIcon icon, Coma c) {
		super(text, icon, c);
	}

        @Override
	public void actionPerformed(ActionEvent e) {
		final File file = coma.getData().getOpenFile();
		if (file == null) {
			JOptionPane.showMessageDialog(coma,
					Ui.getText("err.noCorpusLoaded"));
			return;
		}

		// 1. display the dialog
                COMATreeTaggingDialog dialog = new COMATreeTaggingDialog(coma, true);
                dialog.setLocationRelativeTo(coma);
                dialog.setVisible(true);
                if (dialog.getReturnStatus()==COMATreeTaggingDialog.RET_CANCEL) return;

                // 2. initialise the tagger
                String TTD = dialog.treeTaggerParametersPanel.getTreeTaggerDirectory();
                String PF = dialog.treeTaggerParametersPanel.getParameterFile();
                String PFE = dialog.treeTaggerParametersPanel.getParameterFileEncoding();
                String[] OPT = dialog.treeTaggerParametersPanel.getOptions();

                String taggingProfileName = dialog.taggingOptionsPanel.getTaggingProfileName();
                String sextantSuffix = dialog.taggingOptionsPanel.getFilenameSuffix();
                boolean writeSextant = dialog.taggingOptionsPanel.writeSextantFiles();
                boolean integrateSextant = dialog.taggingOptionsPanel.integrateSextantFiles();

                TreeTagger tt;
                CorpusTreeTagger ctt;
                try {
                    tt = new TreeTagger(TTD, PF, PFE, OPT);
                    //String[] FIXED_OPT = {"-token","-lemma","-sgml","-no-unknown"};
                    //tt = new TreeTagger(TTD, PF, PFE, FIXED_OPT);
                    System.out.println("Tree Tagger initialised");
                    ctt = new CorpusTreeTagger(
                            coma.getData().getOpenFile(), tt,
                            TaggingProfiles.getSegmentationXPathForProfile(taggingProfileName),
                            TaggingProfiles.getTokenXPathForProfile(taggingProfileName),
                            writeSextant,
                            integrateSextant,
                            sextantSuffix);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(coma, "Could not initialize TreeTagger.\n" + ex.getLocalizedMessage());
                    return;
                }

                //2a. setting up was succcessful so we can write the preferences
                TaggingProfiles.writePreferences(TTD, PF, PFE, OPT, taggingProfileName, writeSextant, integrateSextant, sextantSuffix);


                // 3. Do the tagging
		final CorpusTreeTagger theTagger = ctt;
		pbd = new ProgressBarDialog(coma, false);
		pbd.setLocationRelativeTo(coma);
		pbd.setTitle("TreeTagging... ");
		pbd.setAlwaysOnTop(true);
		theTagger.addSearchListener(pbd);
		pbd.setVisible(true);
		Thread tagThread = new Thread() {
			@Override
			public void run() {
				try {
                                    theTagger.tagCorpus();
                                    System.out.println("------ tagging done.");
                                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                                            public void run() {
                                                    displayDoneMessage();
                                            }
					});
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		tagThread.start();

        }

        private void displayDoneMessage(){
            pbd.setVisible(false);
            JOptionPane.showMessageDialog(coma, "Tagging done.");
        }


}
