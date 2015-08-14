/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.Ui;

public class CollectCorpusAction extends ComaAction {
	private JFileChooser fc;
	private File file;
	public CollectCorpusAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.collectCorpusAction"), icon, c);
	}
	public CollectCorpusAction(Coma c) {
		this(c, (javax.swing.ImageIcon) null);
		this.setEnabled(false);
	}
	public CollectCorpusAction(Coma c, String selectedFile) {
		super(selectedFile, (javax.swing.ImageIcon) null, c);
		file = new File(selectedFile);
	}
	public void actionPerformed(ActionEvent actionEvent) {
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		coma.status("@collectCorpus");
		if (fc.showOpenDialog(coma) == JFileChooser.APPROVE_OPTION) {
			System.out.println("getCurrentDirectory(): "
					+ fc.getCurrentDirectory());
			System.out.println("getSelectedFile() : " + fc.getSelectedFile());
		} else {
			System.out.println("No Selection ");
		}
	}

}