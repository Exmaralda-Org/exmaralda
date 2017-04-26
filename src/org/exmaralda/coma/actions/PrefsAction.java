/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.PrefsWindow;
import org.exmaralda.coma.root.Ui;


/**
 * coma2/org.sfb538.coma2/AboutAction.java
 * @author woerner
 *
 */
public class PrefsAction extends ComaAction {

	/**
	 * Abstract action to display the preferences window.
	 *
	 * @param c	the Coma-instance
	 * @param icon an icon for the menu/button where the action will appear
	 */
	public PrefsAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.preferences"), icon, c);
	}

	public PrefsAction(Coma c) {
		this(c, null);
	}

	/** Displays the about screen */
	public void actionPerformed(ActionEvent actionEvent) {
		PrefsWindow prefsW = new PrefsWindow(coma);
	}

}