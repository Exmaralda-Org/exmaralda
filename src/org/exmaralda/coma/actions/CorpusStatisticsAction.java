/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.coma.actions;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.corpusbuild.comafunctions.SegmentsCounter;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaraldaswing.ChooseStylesheetDialog;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 * 
 * @author thomas Second attempt at application crossover! Bleeding hell!!!!!
 */
public class CorpusStatisticsAction extends org.exmaralda.coma.root.ComaAction {

	ProgressBarDialog pbd;

	public CorpusStatisticsAction(String text, ImageIcon icon, Coma c) {
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

			final SegmentsCounter counter = new SegmentsCounter();

			pbd = new ProgressBarDialog(coma, false);
			pbd.setLocationRelativeTo(coma);
			pbd.setTitle("Counting segments in "
					+ coma.getData().getOpenFile().getName());
			counter.addSearchListener(pbd);
			pbd.setVisible(true);

			final Runnable doDisplaySaveDialog = new Runnable() {
				public void run() {
					displaySaveDialog(counter);
				}

			};
			Thread checkThread = new Thread() {
				@Override
				public void run() {
					try {
						counter.countSegments(coma.getData().getDocument(),
								file.getParentFile());
						javax.swing.SwingUtilities
								.invokeLater(doDisplaySaveDialog);
					} catch (IOException ex) {
						ex.printStackTrace();
					} catch (SAXException ex) {
						ex.printStackTrace();
					} catch (JexmaraldaException ex) {
						ex.printStackTrace();
					} catch (URISyntaxException ex) {
						ex.printStackTrace();
					} catch (JDOMException ex) {
						ex.printStackTrace();
					}
				}
			};
			checkThread.start();

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(coma, ex.getMessage());
		}

	}

	private void displaySaveDialog(SegmentsCounter counter) {
		if (counter.getErrorList().size() > 0) {
			String out = "<html>" + Ui.getText("err.processingFiles") + "<br/>";
			int countErr = 0;
			for (String error : counter.getErrorList()) {

				countErr++;
				if (countErr < 5) {
					out += error + "<br/>";
				}

			}
			if (countErr > 4) {
				out += "(+" + (counter.getErrorList().size() - 4)
						+ " files)";
			}
			JOptionPane.showMessageDialog(coma, out);
		}

		pbd.setVisible(false);
		String[][] internalPaths = {
				{ "Grouped by communication", "Grouped by speaker",
						"No Stylesheet (show XML)" },
				{
						"/org/exmaralda/common/corpusbuild/comafunctions/Count_By_Communication.xsl",
						"/org/exmaralda/common/corpusbuild/comafunctions/Count_By_Speaker.xsl",
						"" } };
		ChooseStylesheetDialog d = new ChooseStylesheetDialog(coma, true,
				internalPaths, "");
		d.setLocationRelativeTo(coma);
		d.setVisible(true);
		String xslPath = d.getStylesheet();

		// JFileChooser fc = new JFileChooser();
		// fc.setFileFilter(new ParameterFileFilter("html", "HTML file"));
		// if (fc.showSaveDialog(coma)==JFileChooser.APPROVE_OPTION){

		Document result = counter.getCountResultDocument();

		// Document comaDocument =
		// org.exmaralda.exakt.utilities.FileIO.readDocumentFromLocalFile(CORPUS_FILE);
		Document comaDocument = (Document) (coma.getData().getDocument()
				.clone());
		Element corp = comaDocument.getRootElement();
		corp.detach();
		result.getRootElement().addContent(corp);
		// try {
		// org.exmaralda.exakt.utilities.FileIO.writeDocumentToLocalFile("c:\\count.xml",
		// result);
		// } catch (IOException ex) {
		// ex.printStackTrace();
		// }

		try {
			File OUT_FILE = File.createTempFile("comacountresult", ".xml");
			org.exmaralda.exakt.utilities.FileIO.writeDocumentToLocalFile(
					OUT_FILE, result);

			// String OUT_FILE2_NAME = fc.getSelectedFile().getAbsolutePath();
			// if (fc.getSelectedFile().getName().indexOf(".")<0){
			// OUT_FILE2_NAME+=".html";
			// }
			// File OUT_FILE2 = new File(OUT_FILE2_NAME);
			if (xslPath.equals("")) {
				Desktop.getDesktop().browse(OUT_FILE.toURI());

			} else {
				File OUT_FILE2 = File
						.createTempFile("comacountresult", ".html");

				org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory ssf = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory(
						true);
				String html = "";
				if (xslPath.startsWith("/")) {
					html = ssf.applyInternalStylesheetToExternalXMLFile(
							xslPath, OUT_FILE.getAbsolutePath());
				} else {
					html = ssf.applyExternalStylesheetToExternalXMLFile(
							xslPath, OUT_FILE.getAbsolutePath());
				}
				FileOutputStream fos = new FileOutputStream(OUT_FILE2);
				fos.write(html.getBytes("UTF-8"));
				fos.close();
				Desktop.getDesktop().browse(OUT_FILE2.toURI());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(coma, ex.getLocalizedMessage());
		}
	}
}

// }
