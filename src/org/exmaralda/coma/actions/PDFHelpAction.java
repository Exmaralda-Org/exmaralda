/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.SplashScreen;
import org.exmaralda.coma.root.Ui;


/**
 * coma2/org.sfb538.coma2/AboutAction.java
 * @author woerner
 *
 */
public class PDFHelpAction extends ComaAction {

	/**
	 * Abstract action to display an about screen.
	 *
	 * @param c	the Coma-instance
	 * @param icon an icon for the menu/button where the action will appear
	 */
	public PDFHelpAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.about"), icon, c);
	}

	public PDFHelpAction(Coma c) {
		this(c, null);
	}

	/** Displays the about screen */
	public void actionPerformed(ActionEvent actionEvent) {
		SplashScreen splash = new SplashScreen(coma,false);
	}

}