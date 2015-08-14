/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.SplashScreen;
import org.exmaralda.coma.root.Ui;


/**
 * coma2/org.sfb538.coma2/AboutAction.java
 * @author woerner
 * dies ist ein test!
 */
public class AboutAction extends ComaAction {
	private JDialog aboutDialog;

	/**
	 * Abstract action to display an about screen.
	 *
	 * @param c	the Coma-instance
	 * @param icon an icon for the menu/button where the action will appear
	 */
	public AboutAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.about"), icon, c);
		this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_F1, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
	}

	public AboutAction(Coma c) {
		this(c, null);
	}

	/** Displays the about screen */
	public void actionPerformed(ActionEvent actionEvent) {
		SplashScreen splash = new SplashScreen(coma,false);
	}

}