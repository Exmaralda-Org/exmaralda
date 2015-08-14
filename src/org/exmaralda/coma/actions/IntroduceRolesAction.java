/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.SplashScreen;

/**
 * coma2/org.sfb538.coma2/AboutAction.java
 * 
 * @author woerner
 * 
 */
public class IntroduceRolesAction extends ComaAction {
	private JDialog aboutDialog;

	/**
	 * Abstract action to display an about screen.
	 * 
	 * @param c
	 *            the Coma-instance
	 * @param icon
	 *            an icon for the menu/button where the action will appear
	 */
	public IntroduceRolesAction(Coma c, javax.swing.ImageIcon icon) {
		super(
				"Introduce roles\n (ONLY USE THIS IF YOU ARE ABSOLOUTELY SURE WHAT YOU ARE DOING!",
				icon, c);
	}

	public IntroduceRolesAction(Coma c) {
		this(c, null);
	}

	/** Displays the about screen */
	public void actionPerformed(ActionEvent actionEvent) {
		Object[] options = { "Err... Yes?", "Go Ahead! Kill the Settings too while you're at it!", "I wimp out! Cancel!!" };
		int n = JOptionPane.showOptionDialog(coma,
				"<html>This...     changes...   <b>EVERYTHING</b><br><br>Speaker roles will be saved with speakers instead inside the communication's settings.<br></html>",
				"Introduce Roles", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE, null, options, options[2]);
		switch(n) {
		case JOptionPane.CANCEL_OPTION:
			return;

		case JOptionPane.YES_OPTION:
			coma.introduceRoles(false);
		case JOptionPane.NO_OPTION:
//			NOMEANSYES
			coma.introduceRoles(true);
		}

	}

}