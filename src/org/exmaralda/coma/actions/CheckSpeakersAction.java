/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.corpusbuild.comafunctions.AddSpeakersDialog;
import org.exmaralda.common.corpusbuild.comafunctions.SpeakersChecker;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.jdom.JDOMException;

/**
 * 
 * @author thomas Second attempt at application crossover! Bleeding hell!!!!!
 */
public class CheckSpeakersAction extends org.exmaralda.coma.root.ComaAction {

	ProgressBarDialog pbd;

	public CheckSpeakersAction(String text, ImageIcon icon, Coma c) {
		super(text, icon, c);
	}

	public void actionPerformed(ActionEvent e) {
		final File file = coma.getData().getOpenFile();
		if (file == null) {
			JOptionPane.showMessageDialog(coma,
					Ui.getText("err.noCorpusLoaded"));
			return;
		}
		try {

			final SpeakersChecker checker = new SpeakersChecker();

			pbd = new ProgressBarDialog(coma, false);
			pbd.setLocationRelativeTo(coma);
			pbd.setTitle(Ui.getText("progress.checkSpkErrors")
					+ coma.getData().getOpenFile().getName());
			checker.addSearchListener(pbd);
			pbd.setVisible(true);

			final Runnable doDisplaySaveDialog = new Runnable() {
				public void run() {
					displayAddSpeakersDialog(checker);
				}

			};
			Thread checkThread = new Thread() {
				@Override
				public void run() {
					try {
						checker.checkCorpus(coma.getData().getDocument(),
								file.getParent());
						javax.swing.SwingUtilities
								.invokeLater(doDisplaySaveDialog);
					} catch (Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(coma, ex);
						pbd.setVisible(false);
					}
				}
			};
			checkThread.start();

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(coma, ex.getMessage());
		}

	}

	private void displayAddSpeakersDialog(SpeakersChecker checker) {
		pbd.setVisible(false);
		int errorCount = checker.getErrors().getChildren().size();
		if (errorCount == 0) {
			JOptionPane.showMessageDialog(coma, Ui.getText("result.noUndSpkFound"));
			return;
		} else {
			AddSpeakersDialog dialog = new AddSpeakersDialog(this.coma, true,
					checker, coma);
			dialog.setLocationRelativeTo(coma);
			dialog.setVisible(true);
		}
	}

}
