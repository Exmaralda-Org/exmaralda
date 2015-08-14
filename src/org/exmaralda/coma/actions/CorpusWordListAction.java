/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.exmaraldaSearch.COMACorpus;
import org.exmaralda.exakt.tokenlist.HashtableTokenList;
import org.exmaralda.exakt.tokenlist.WordlistDialog;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;

/**
 * 
 * @author thomas Second attempt at application crossover! Bleeding hell!!!!!
 */
public class CorpusWordListAction extends org.exmaralda.coma.root.ComaAction {

	ProgressBarDialog pbd;

	public CorpusWordListAction(String text, ImageIcon icon, Coma c) {
		super(text, icon, c);
	}

	public void actionPerformed(ActionEvent e) {
		final File file = coma.getData().getOpenFile();
		if (file == null) {
			JOptionPane.showMessageDialog(coma,
					Ui.getText("err.noCorpusLoaded"));
			return;
		}

		String wordSegmentName = null;
		// 1. check what the word segment name for this corpus is
		try {
			// find segmented transcription
			XPath xpath = XPath
					.newInstance("//Transcription[Description/Key[@Name='segmented']/text()='true']/NSLink");
			List transcriptionList = xpath.selectNodes(coma.getData().getDocument());
			for (int pos = 0; pos < transcriptionList.size(); pos++) {
				Element nslink = (Element) (transcriptionList.get(pos));
				String fullTranscriptionName = coma.getData().getOpenFile()
						.getParentFile().getAbsolutePath()
						+ System.getProperty("file.separator", "/")
						+ nslink.getText();
				Document st = IOUtilities
						.readDocumentFromLocalFile(fullTranscriptionName);
				Iterator i = st.getDescendants(new ElementFilter("ts"));
				while (i.hasNext()) {
					Element el = (Element) (i.next());
					if (el.getAttributeValue("n").endsWith(":w")) {
						wordSegmentName = el.getAttributeValue("n");
						break;
					}
				}
				if (wordSegmentName != null)
					break;
			}
			if (wordSegmentName == null) {
				JOptionPane.showMessageDialog(coma,
						"No word segmentation in this corpus.");
				return;
			}
			// now read the wordlist
			final HashtableTokenList l = new HashtableTokenList();
			final COMACorpus finalCorpus = new COMACorpus();
			finalCorpus.readCorpus(coma.getData().getOpenFile(), false);
                        // changed 01-08-2012
                        finalCorpus.setWordSegmentName(wordSegmentName);
                        
                        System.out.println("Reading words from " + coma.getData().getOpenFile());
                    
			finalCorpus.explicitlySetWordSegmentName = wordSegmentName;
			pbd = new ProgressBarDialog(coma, false);
			pbd.setTitle("Reading word list from "
					+ coma.getData().getSelectedCorpusName());
			l.addSearchListener(pbd);
			pbd.setLocationRelativeTo(coma);
			pbd.setVisible(true);
			final Runnable doDisplayWordList = new Runnable() {
				public void run() {
					displayWordList(l);
				}
			};
			Thread openThread = new Thread() {
				@Override
				public void run() {
					try {
						l.readWordsFromExmaraldaCorpus(finalCorpus);
						pbd.setVisible(false);
						try {
							javax.swing.SwingUtilities
									.invokeAndWait(doDisplayWordList);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			};
			openThread.start();

		} catch (Exception ex) {
			ex.printStackTrace();
                        JOptionPane.showMessageDialog(coma, ex);                        
		}

	}

	private void displayWordList(HashtableTokenList tl) {
		WordlistDialog dialog = new WordlistDialog(new javax.swing.JFrame(),
				true, tl);
		dialog.setLocationRelativeTo(coma);
		dialog.setTitle(tl.getName());
		dialog.setVisible(true);
	}

}
