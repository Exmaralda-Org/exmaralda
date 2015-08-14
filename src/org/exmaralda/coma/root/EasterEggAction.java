/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.root;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * coma2/org.sfb538.coma2/AboutAction.java
 * @author woerner
 *
 */
public class EasterEggAction extends ComaAction {
	/**
	 * Abstract action to display an aboutess screen.
	 *
	 * @param c	the Coma-instance
	 * @param icon an icon for the menu/button where the action will appear
	 */
	public EasterEggAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.aboutness"), icon, c);
	}

	public EasterEggAction(Coma c) {
		this(c, null);
	}

	/** Displays the about screen */
	public void actionPerformed(ActionEvent actionEvent) {
		try {
			Desktop
					.getDesktop()
					.browse(
							new URI(
									"http://www.db.dk/bh/Core%20Concepts%20in%20LIS/articles%20a-z/aboutness.htm"));
		} catch (IOException err) {
			err.printStackTrace();
		} catch (URISyntaxException err) {
			err.printStackTrace();
		}
	}
}