/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.exmaralda.coma.helpers.GUID;
import org.exmaralda.coma.helpers.SegmentationOptionsDialog;
import org.exmaralda.coma.models.TranscriptionMetadata;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.corpusbuild.AbstractCorpusProcessor;
import org.exmaralda.common.corpusbuild.TranscriptionSegmentor;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

/**
 * 
 * @author thomas Second attempt at application crossover! Bleeding hell!!!!!
 */
public class SegmentTranscriptionAction extends
		org.exmaralda.coma.root.ComaAction {

	ProgressBarDialog pbd;
	SegmentationOptionsDialog dialog = new SegmentationOptionsDialog(this.coma,
			true);
	Vector<File> basicTr;
	HashSet<File> existingSegs = new HashSet<File>();
	Hashtable<File, Element> basicTr2Communication;
	Document comaDocument;

	public SegmentTranscriptionAction(String text, ImageIcon icon, Coma c) {
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

		dialog.setLocationRelativeTo(this.coma);
		dialog.setVisible(true);

		if (!dialog.approved)
			return;

		comaDocument = coma.getData().getDocument();
		String CORPUS_BASEDIRECTORY = coma.getData().getOpenFile().getParent();

		List basicTransNSLinks = new Vector();
		List segmentedTransNSLinks = new Vector();
		basicTr = new Vector<File>();
		basicTr2Communication = new Hashtable<File, Element>();

		try {
			basicTransNSLinks = XPath.newInstance(
					AbstractCorpusProcessor.BASIC_FILE_XPATH).selectNodes(
					comaDocument);
			segmentedTransNSLinks = XPath.newInstance(
					AbstractCorpusProcessor.SEGMENTED_FILE_XPATH).selectNodes(
					comaDocument);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		for (Object o : basicTransNSLinks) {
			Element nslink = (Element) (o);
			String fullTranscriptionName = CORPUS_BASEDIRECTORY
					+ System.getProperty("file.separator", "/")
					+ nslink.getText();
			File transcriptionFile = new File(fullTranscriptionName);
			basicTr.add(transcriptionFile);
			basicTr2Communication.put(transcriptionFile, nslink
					.getParentElement().getParentElement());
		}
		for (Object o : segmentedTransNSLinks) {
			Element nslink = (Element) (o);
			String fullTranscriptionName = CORPUS_BASEDIRECTORY
					+ System.getProperty("file.separator", "/")
					+ nslink.getText();
			File transcriptionFile = new File(fullTranscriptionName);
			existingSegs.add(transcriptionFile);
		}

		TranscriptionSegmentor segmentor = new TranscriptionSegmentor(
				(File[]) (basicTr.toArray(new File[0])));

		// the index of the selected segmentation algorithm
		int sa = dialog.optionsPanel.segSelector.getSelectedIndex();
                String customFSM = dialog.optionsPanel.customFSMTextField.getText();
                
		// the selected suffix
		String suff = dialog.optionsPanel.segSuffixTextField.getText();
		// the target directory for segmented transcriptions (null if segmented
		// transcriptions do shall be writteth to the same directory as the
		// basic transcription)
		File td = null;
		if (dialog.optionsPanel.segTargetOption2.isSelected()) {
			td = new java.io.File(file.getParent(),
					dialog.optionsPanel.segTargetOption2TextField.getText());
		}
		// the method for error handling
		int eh = TranscriptionSegmentor.ERRORS_CANCEL;
		if (dialog.optionsPanel.segErrorOption2.isSelected()) {
			eh = TranscriptionSegmentor.ERRORS_IGNORE;
		} else if (dialog.optionsPanel.segErrorOption3.isSelected()) {
			eh = TranscriptionSegmentor.ERRORS_FAILSAFE;
		}
		// whether or not to write an error list
		boolean we = dialog.optionsPanel.errorListCheckBox.isSelected();
		// the file in which to write the error list
		File ep = new java.io.File(file.getParent(), "SegmentationErrors.xml");

		final TranscriptionSegmentor theSegmentor = segmentor;
		final int f_sa = sa;
		final String f_suff = suff;
		final File f_td = td;
		final int f_eh = eh;
		final boolean f_we = we;
		final File f_ep = ep;
                final String f_cfsm = customFSM;
		pbd = new ProgressBarDialog(coma, false);
		pbd.setLocationRelativeTo(coma);
		pbd.setTitle("Segmentation... ");
		pbd.setAlwaysOnTop(true);
		theSegmentor.addSearchListener(pbd);
		pbd.setVisible(true);
		Thread segmentThread = new Thread() {
			@Override
			public void run() {
				try {
					theSegmentor.doSegmentation(f_sa, f_suff, f_td, f_eh, f_we,
							f_ep, f_cfsm);
					System.out.println("------ segmentation done.");
					final HashMap<File, File> segmentedTranscriptions = theSegmentor
							.getSegmentedTranscriptions();
					for (File f : segmentedTranscriptions.keySet()) {
						System.out.println("----- " + f.getAbsolutePath());
						TranscriptionMetadata tm = new TranscriptionMetadata(f,
								true);
						/*
						 * transcriptions.put(f, tm); for (String myS :
						 * tm.getSpeakers().keySet()) { speakerMetadata.put(myS,
						 * tm.getSpeakers().get(myS)); }
						 */
					}
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							success(segmentedTranscriptions);
						}
					});
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		segmentThread.start();
	}

	private void success(HashMap<File, File> segmentedTranscriptions) {
		int count = 0;
		for (File segTr : segmentedTranscriptions.keySet()) {
			File basTr = basicTr.elementAt(count);
			if (!(existingSegs.contains(segTr))) {
				Element trE = new Element("Transcription");
				trE.setAttribute("Id", "T" + new GUID().makeID());
				if (coma.prefs.get("prefs.nameTranscriptionsAfter",
						Ui.getText("communication")).equals(
						Ui.getText("communication"))) {
					trE.addContent(new Element("Name")
							.setText(basicTr2Communication.get(
									segmentedTranscriptions.get(segTr))
									.getAttributeValue("Name")));
				} else {
					trE.addContent(new Element("Name").setText(segTr.getName()));
				}
				trE.addContent(new Element("Filename").setText(segTr.getName()));
				trE.addContent(new Element("NSLink").setText(coma
						.getRelativePath(null, segTr)));

				TranscriptionMetadata tm = new TranscriptionMetadata(segTr,
						true);
				Element dElement = new Element("Description");
				for (String key : tm.getMetadata().keySet()) {
					dElement.addContent(new Element("Key").setAttribute("Name",
							key).setText(tm.getMetadata().get(key)));
				}
				trE.addContent(dElement);
				basicTr2Communication.get(segmentedTranscriptions.get(segTr))
						.addContent(trE);
			}
			count++;

		}

		pbd.setVisible(false);
		String message = Ui.getText("result.segmentedWrittenFor")
				+ Integer.toString(segmentedTranscriptions.size())
				+ " basic transcriptions.";
		JOptionPane.showMessageDialog(coma, message);
	}

}
