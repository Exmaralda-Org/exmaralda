/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.jdom.JDOMException;

/**
 * 
 * @author thomas First attempt at application crossover! Bleeding hell!!!!!
 */
public class ExaktSearchAction extends org.exmaralda.coma.root.ComaAction {

	ProgressBarDialog pbd;

	public ExaktSearchAction(String text, ImageIcon icon, Coma c) {
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

			// setup the EXAKT panel
			final org.exmaralda.exakt.exmaraldaSearch.COMACorpus comaCorpus = new org.exmaralda.exakt.exmaraldaSearch.COMACorpus();

			pbd = new ProgressBarDialog(coma, false);
			pbd.setLocationRelativeTo(coma);
			pbd.setTitle("Initialising "
					+ coma.getData().getOpenFile().getName());
			comaCorpus.addSearchListener(pbd);
			pbd.setVisible(true);

			final Runnable doDisplayKwicPanel = new Runnable() {
				public void run() {
					displayKwicPanel(comaCorpus);
				}
			};
			Thread openThread = new Thread() {
				@Override
				public void run() {
					try {
						comaCorpus.readCorpus(coma.getData().getOpenFile());
						javax.swing.SwingUtilities
								.invokeLater(doDisplayKwicPanel);
						// corpusToBeAdded = corpus;
						// try {
						// javax.swing.SwingUtilities.invokeAndWait(doDisplayKwicPanel);
						/*
						 * } catch (InvocationTargetException ex) {
						 * ex.printStackTrace(); } catch (InterruptedException
						 * ex) { ex.printStackTrace(); }
						 */
					} catch (JDOMException ex) {
						ex.printStackTrace();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			};
			openThread.start();

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(coma, ex.getMessage());
		}

	}

	private void displayKwicPanel(
			org.exmaralda.exakt.exmaraldaSearch.COMACorpus comaCorpus) {
		pbd.setVisible(false);
		org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICSearchPanel kwicPanel = new org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICSearchPanel(
				comaCorpus);
		kwicPanel.setPreferredSize(new java.awt.Dimension(800, 600));
		kwicPanel.setSize(new java.awt.Dimension(800, 600));
		kwicPanel.splitPane.setDividerLocation((int) Math.round(0.8 * 600.0));

		// put the EXAKT panel in a dialog and display it
		JDialog dialog = new JDialog(coma, false);
		dialog.setTitle("EXAKT search");
		dialog.getContentPane().add(kwicPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(coma);
		dialog.setVisible(true);
	}

}
