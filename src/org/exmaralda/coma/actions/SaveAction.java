/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.ComaXMLOutputter;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.helpers.ExmaraldaFileFilter;
import org.jdom.Document;

/**
 * coma2/org.sfb538.coma2.fileActions/SaveAction.java action for save- and
 * save-as commands
 * 
 * @author woerner
 * 
 */
public class SaveAction extends ComaAction {
	private static final long serialVersionUID = 4601685287146653101L;

	boolean saveAs = false;

	public SaveAction(Coma c, javax.swing.ImageIcon icon, boolean sa) {

		super((sa ? Ui.getText("cmd.SaveAsAction") : Ui
				.getText("cmd.SaveAction")), icon, c);
		saveAs = sa;
		if (!sa) {
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					KeyEvent.VK_S, Toolkit.getDefaultToolkit()
							.getMenuShortcutKeyMask()));
		}
	}

	public SaveAction(Coma c) {
		this(c, null, false);
	}

	public SaveAction(Coma c, boolean sa) {
		this(c, null, sa);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		System.out.println("Save " + (saveAs ? "as" : ""));
		if ((!saveAs) && (coma.getData().getOpenFile().exists())) {
			save();
		} else {
			saveAs();
		}
	}

	/**
	 * saves the open file. calls saveAs() if it has not been saved yet.
	 */
	public boolean save() {
		if (coma.getData().getOpenFile().exists()) {
			Document saveDoc = coma.getData().getDocument();
			ComaXMLOutputter outputter = new ComaXMLOutputter();
			try {
				FileOutputStream out = new FileOutputStream(coma.getData()
						.getOpenFile());
				outputter.output(saveDoc, out);
				out.close();
				coma.xmlSaved();
				return true;

			} catch (IOException e) {
				coma.status("saving failed: " + e);
				return false;
			}
		} else {
			return saveAs();
		}

	}

	public boolean saveAs() {
		File file = null;

		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(coma.getData().getOpenFile() == null ? new File(
				"/") : coma.getData().getOpenFile().getParentFile());
		fc.addChoosableFileFilter(new ExmaraldaFileFilter("Coma-Files",
				new String[] { "coma", "xml" }, true));
		int returnVal = fc.showSaveDialog(coma);
		if (fc.getSelectedFile() != null) {
			file = fc.getSelectedFile();

		}

		if (file != null) {
			boolean doit = true;
			if (file.exists()) {
				if (JOptionPane.showConfirmDialog(coma, Ui
						.getText("msg.overwriteWarning"), "Coma",
						JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
					doit = false;

			}
			if (doit) {
				File fl;
				if ((file.getName().toLowerCase().endsWith(".coma"))
						|| (file.getName().toLowerCase().endsWith(".xml"))) {
					fl = file;
				} else {
					fl = new File(file.getPath() + ".coma");
				}
				Document saveDoc = data.getDocument();
				ComaXMLOutputter outputter = new ComaXMLOutputter();
				try {
					FileOutputStream out = new FileOutputStream(fl);
					outputter.output(saveDoc, out);
					out.close();
					coma.setOpenFile(fl);
					return true;
				} catch (IOException e) {
					coma.status("saving failed: " + e);
					return false;
				}
			}
		} else {
			coma.status("@save as cancelled");
		}
		return false;
	}
}
