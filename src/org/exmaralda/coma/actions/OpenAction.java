/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.helpers.ExmaraldaFileFilter;

/**
 * coma2/org.sfb538.coma2.fileActions/newAction.java
 * 
 * @author woerner
 */
public class OpenAction extends ComaAction {

	private static final long serialVersionUID = -5986853291853049249L;
	private JFileChooser fc;
	private File file;

	public OpenAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.OpenAction"), icon, c);
		this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_O, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
	}

	public OpenAction(Coma c) {
		this(c, (javax.swing.ImageIcon) null);
	}

	public OpenAction(Coma c, String selectedFile) {
		super(selectedFile, (javax.swing.ImageIcon) null, c);
		file = new File(selectedFile);
	}

	public OpenAction(Coma c, File selectedFile) {
		super(selectedFile.getName(), (javax.swing.ImageIcon) null, c);
		file = selectedFile;
	}

	public void actionPerformed(ActionEvent actionEvent) {
		if (!coma.xmlChanged) {
			open();
		} else {
			int n = JOptionPane.showConfirmDialog(coma, Ui
					.getText("q.saveChanges"), Ui.getText("unsavedChanges"),
					JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				return;
			} else {
				open();
			}
		}
	}

	private void open() {
		if (file != null) {
			coma.status(file);
			coma.openComaXML(file);
		} else {
			fc = new JFileChooser(coma.prefs.get("recentDir", "/"));
			fc.addChoosableFileFilter(new ExmaraldaFileFilter("Coma-Files",
					new String[] { "coma", "xml" }, true));
			int dialogStatus = fc.showOpenDialog(coma);
			if (dialogStatus == 0) {
				coma.openComaXML(fc.getSelectedFile());
			} else {
				coma.status("@open cancelled" + dialogStatus);
			}
		}

	}

}