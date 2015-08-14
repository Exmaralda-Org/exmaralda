/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFileChooser;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.helpers.ExmaraldaFileFilter;

public class OpenTemplatesAction extends ComaAction {
	private JFileChooser fc;

	private File file;

	public OpenTemplatesAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.OpenTemplatesAction"), icon, c);
		//this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	public OpenTemplatesAction(Coma c) {
		this(c, (javax.swing.ImageIcon) null);
	}

	public OpenTemplatesAction(Coma c, String selectedFile) {
		super(selectedFile, (javax.swing.ImageIcon) null, c);
		file = new File(selectedFile);
	}

	public OpenTemplatesAction(Coma c, File selectedFile) {
		super(selectedFile.getName(), (javax.swing.ImageIcon) null, c);
		file = selectedFile;
	}

	public void actionPerformed(ActionEvent actionEvent) {
		open();
	}

	private void open() {
		if (file != null) {
			coma.getData().getTemplates().loadTemplates(file);
		} else {

			File file = null;
			if (Coma.os.equals("mac")) {
				// use the native file dialog on the mac
				FileDialog dialog = new FileDialog((Frame) coma, Ui.getText("cmd.openTemplates"), FileDialog.LOAD);
				dialog.setFilenameFilter(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".ctf");
					}
				});
				dialog.show();
				if (dialog.getFile() != null)
					file = new File(dialog.getDirectory() + dialog.getFile());
			} else {
				// use a swing file dialog on the other platforms
				fc = new JFileChooser();
				fc.addChoosableFileFilter(new ExmaraldaFileFilter("Coma-Templates", new String[] { "ctf" }, true));
				int dialogStatus = fc.showOpenDialog(coma);
				file = fc.getSelectedFile();
			}
			if (file != null) {
				coma.getData().getTemplates().loadTemplates(file);
				coma.getData().setTemplateFile(file);
			}
		}
	}
	/*
	 * try { SAXBuilder builder = new SAXBuilder(); coma.worksetDoc =
	 * builder.build("1700.xml"); Element
	 * myRoot=coma.worksetDoc.getRootElement().getChild("CorpusData");
	 * coma.status(myRoot.getName()); System.out.println(myRoot.getName());
	 * System.out.println(myRoot.getContentSize()); } catch (Exception e) {
	 * System.err.println(e.toString()); }
	 */
}