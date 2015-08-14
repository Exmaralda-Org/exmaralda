/*
 * Created on 11.04.2005 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.exmaralda.coma.helpers.CCW;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.Ui;

/**
 * coma2/org.sfb538.coma2.fileActions/ImportTranscriptionsAction.java
 * 
 * @author woerner
 * 
 */
public class CreateCorpusFromTranscriptionsAction extends ComaAction {
	private File file;

	private File cf;

	/**
	 * @param text
	 * @param c
	 */
	public CreateCorpusFromTranscriptionsAction(String text, Coma c) {
		super(text, c);
	}

	public CreateCorpusFromTranscriptionsAction(Coma c) {
		super(Ui.getText("cmd.ImportTranscriptions"), null, c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		if (!coma.xmlChanged) {
			create();
		} else {
			int n = JOptionPane.showConfirmDialog(coma,
					Ui.getText("q.saveChanges"), Ui.getText("unsavedChanges"),
					JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				if (coma.getData().getOpenFile().exists()) {
					new SaveAction(coma).save();
				} else {
					new SaveAction(coma).saveAs();
				}
				create();
			} else {
				create();
			}
		}
	}

	private void create() {

		cf = null;

		CCW ccwInst = new CCW(coma, true);
		ccwInst.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		ccwInst.setLocationRelativeTo(coma);
		ccwInst.setVisible(true);
		cf = ccwInst.resultFile;

		System.out.println(cf);
		if (cf != null) {
			coma.openComaXML(cf);
			JOptionPane.showMessageDialog(coma,
					Ui.getText("message.comaGenerated"));

		} else {
			JOptionPane.showMessageDialog(coma,
					Ui.getText("err.fileNotWritten"), Ui.getText("error"),
					JOptionPane.ERROR_MESSAGE);

		}
	}

}
