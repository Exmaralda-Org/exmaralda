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

/**
 * coma2/org.sfb538.coma2.fileActions/newAction.java
 * 
 * @author woerner
 */
public class SaveTemplatesAction extends ComaAction {
	private JFileChooser fc;

	private File file;

	private boolean saveAs;

	public SaveTemplatesAction(Coma c, javax.swing.ImageIcon icon, boolean saveAs) {
		super((saveAs) ? Ui.getText("cmd.SaveTemplatesAsAction") : Ui.getText("cmd.SaveTemplatesAction"), icon, c);
		this.saveAs = saveAs;
		//this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	public SaveTemplatesAction(Coma c) {
		this(c, (javax.swing.ImageIcon) null, false);
	}

	public SaveTemplatesAction(Coma c, boolean saveAs) {
		this(c, (javax.swing.ImageIcon) null, saveAs);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		coma.getData().getTemplates().saveTemplates(saveAs, coma);
	}
}