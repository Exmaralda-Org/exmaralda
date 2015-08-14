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
import org.exmaralda.common.corpusbuild.comafunctions.StructureErrorsChecker;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.ChooseStructureChecksDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.jdom.JDOMException;

/**
 * 
 * @author thomas Second attempt at application crossover! Bleeding hell!!!!!
 */
public class CheckStructureErrorsAction
		extends
			org.exmaralda.coma.root.ComaAction {

	ProgressBarDialog pbd;

	public CheckStructureErrorsAction(String text, ImageIcon icon, Coma c) {
		super(text, icon, c);
	}

	public void actionPerformed(ActionEvent e) {
		final File file = coma.getData().getOpenFile();
		if (file == null) {
			JOptionPane.showMessageDialog(coma,
					Ui.getText("err.noCorpusLoaded"));
			return;
		}
		ChooseStructureChecksDialog dialog = new ChooseStructureChecksDialog(coma, true);
                dialog.setLocationRelativeTo(coma);
                dialog.setVisible(true);
                try {

			final StructureErrorsChecker checker = new StructureErrorsChecker(
                                dialog.orphanedTranscriptionTiersCheckBox.isSelected(),
                                dialog.duplicateTranscriptionTiersCheckBox.isSelected(),
                                dialog.orphanedAnnotationTiersCheckBox.isSelected(),
                                dialog.annotationMismatchesCheckBox.isSelected(),
                                dialog.temporalAnomaliesCheckBox.isSelected()
                                );

			pbd = new ProgressBarDialog(coma, false);
			pbd.setLocationRelativeTo(coma);
			pbd.setTitle(Ui.getText("progress.checkStructErrors")
					+ coma.getData().getOpenFile().getName());
			checker.addSearchListener(pbd);
			pbd.setVisible(true);

			final Runnable doDisplaySaveDialog = new Runnable() {
				public void run() {
					displaySaveDialog(checker);
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

	private void displaySaveDialog(StructureErrorsChecker checker) {
		pbd.setVisible(false);
		int errorCount = checker.getErrors().getChildren().size();
		if (errorCount == 0) {
			JOptionPane.showMessageDialog(coma,
					Ui.getText("result.noStructErrors"));
			return;
		} else {
			String message = Integer.toString(errorCount)
					+ Ui.getText("result.structErrorsFound");
			int choice = JOptionPane.showConfirmDialog(coma, message,
					Ui.getText("prompt.saveErrorList"),
					JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(coma.getData().getOpenFile());
				fc.setFileFilter(new ParameterFileFilter("xml", "XML files"));
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				boolean goon = false;
				while (!goon) {
					int ret = fc.showSaveDialog(coma);
					if (ret == JFileChooser.APPROVE_OPTION) {
						try {
							String filename = fc.getSelectedFile()
									.getAbsolutePath();
							if (fc.getSelectedFile().getName().indexOf(".") < 0) {
								filename += ".xml";
							}
							if (new File(filename).exists()) {
								int ret2 = JOptionPane
										.showConfirmDialog(
												coma,
												filename
														+ "\n"
														+ Ui.getText("msg.overwriteWarning"),
												"", JOptionPane.YES_NO_OPTION);
								if (ret2 == JOptionPane.NO_OPTION)
									continue;
							}
							System.out.println("Writing error list to "
									+ filename);
							// org.exmaralda.common.jdomutilities.IOUtilities.writeDocumentToLocalFile(fc.getSelectedFile().getAbsolutePath(),
							// checker.getErrorsDocoument());
							checker.output(filename);
							goon = true;
						} catch (JDOMException ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(coma,
									ex.getLocalizedMessage());
						} catch (URISyntaxException ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(coma,
									ex.getLocalizedMessage());
						} catch (IOException ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(coma,
									ex.getLocalizedMessage());
						}
					} else {
						goon = true;
					}
				}
			}
		}
	}

}
