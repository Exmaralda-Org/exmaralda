/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;
import java.util.prefs.BackingStoreException;

import javax.swing.JDialog;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.Logger;

/**
 * coma2/org.sfb538.coma2/AboutAction.java
 * 
 * @author woerner
 * 
 */
public class CopyDebugInfoAction extends ComaAction {
	private JDialog aboutDialog;

	/**
	 * Abstract action to display an about screen.
	 * 
	 * @param c
	 *            the Coma-instance
	 * @param icon
	 *            an icon for the menu/button where the action will appear
	 */
	public CopyDebugInfoAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.copyDebugInfo"), icon, c);
	}

	public CopyDebugInfoAction(Coma c) {
		this(c, null);
	}

	/** Displays the about screen */
	public void actionPerformed(ActionEvent actionEvent) {
		String debugInfo = "COMA DEBUG INFO\n\n";
		debugInfo += ("Date : " + new java.util.Date().toString()) + "\n";
		debugInfo += ("Operating system : " + System.getProperty("os.name"))
				+ "\n";
		debugInfo += ("OS version : " + System.getProperty("os.version"))
				+ "\n";
		debugInfo += ("JRE version : " + System.getProperty("java.version"))
				+ "\n";
		debugInfo += ("Application name:" + coma.getApplicationName()) + "\n";
		debugInfo += ("Application version:" + coma.getVersion()) + "\n";
		debugInfo += ("Build time:" + org.exmaralda.common.EXMARaLDAConstants.BUILD_TIME)
				+ "\n\n\n";
		debugInfo += "Coma Preferences\n\n";

		try {
			for (String key : Ui.prefs.keys()) {
				debugInfo += key + " = " + Ui.prefs.get(key, "*undefined*")
						+ "\n";
			}
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		debugInfo += "\n\n" + Logger.getLog(coma);
		java.awt.datatransfer.StringSelection ss = new java.awt.datatransfer.StringSelection(
				debugInfo);
		coma.getToolkit().getSystemClipboard().setContents(ss, ss);
		coma.status(debugInfo);
	}

}