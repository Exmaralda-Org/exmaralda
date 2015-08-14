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

public class ImportSpeakerAction extends ComaAction {
	private JFileChooser fc;

	private File file;

	public ImportSpeakerAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.ImportSpeaker"), icon, c);
	}

	public ImportSpeakerAction(Coma c) {
		this(c, (javax.swing.ImageIcon) null);
	}

	public ImportSpeakerAction(Coma c, String selectedFile) {
		super(selectedFile, (javax.swing.ImageIcon) null, c);
		file = new File(selectedFile);
	}

	public ImportSpeakerAction(Coma c, File selectedFile) {
		super(selectedFile.getName(), (javax.swing.ImageIcon) null, c);
		file = selectedFile;
	}

	public void actionPerformed(ActionEvent actionEvent) {
		coma.importSpeaker(null);
	}

}