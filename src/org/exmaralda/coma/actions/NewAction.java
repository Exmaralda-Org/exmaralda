/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.Ui;

/**
 * coma2/org.sfb538.coma2.fileActions/newAction.java
 * 
 * @author woerner
 * 
 */
public class NewAction extends ComaAction {
	public NewAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.NewAction"), icon, c);
		this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_N, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
	}

	public NewAction(Coma c) {
		this(c, null);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		if (coma.changesChecked())
			coma.newCorpus();
	}
}
