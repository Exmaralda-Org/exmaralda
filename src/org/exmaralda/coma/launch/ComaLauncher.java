/**
 *
 */
package org.exmaralda.coma.launch;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.prefs.Preferences;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.exmaralda.coma.resources.ResourceHandler;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;

/**
 * @author woerner
 * 
 */
public class ComaLauncher {

	// always change launchertemplate.java

	private static Coma comaInstance;
	Preferences prefs = Ui.prefs;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name",
				"Corpus Manager");
		if (!Coma.os.equals("mac")) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException f) {
				System.err.println("failed setting system look&feel");
			}
		} else {
			try {
				UIManager
						.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException g) {
				System.err
						.println("failed setting Quaqua look&feel (don't bother if you don't use a mac).");

			}

		}
		checkFirstRun();
		java.net.URL pURL = new ResourceHandler().propertiesURL();
		java.util.Properties props = new java.util.Properties();
		try {
			props.load(pURL.openStream());
		} catch (IOException err) {
			// can't get version number
			err.printStackTrace();
		}
		String version = props.getProperty("comaversion");
		System.out.println(version);
		String revision;
		if (version.split("\\.").length > 2) {
			revision = version.substring(version.lastIndexOf(".") + 1);
		} else {
			revision = "0";
		}
		System.out.println("Coma" + version + "(" + revision + ")");

		String inputFile = "";
		boolean logging = true;
		if (args.length > 0) {
			if (new File(args[0]).exists()) {
				inputFile = args[0];
			}

			if (args.length > 1) {

				if (args[1].equals("nologging")) {
					logging = false;

				}
			}
		}
		comaInstance = new Coma(inputFile, version, revision, logging);
	}

	/**
	 * 
	 */
	private static void checkFirstRun() {
		Preferences prefs = Ui.prefs;
		System.out.println("last run:" + prefs.get("lastRun", "never"));
		if (prefs.get("lastRun", "never").equals("never")) {
			prefs.put("lastRun", new Date().toLocaleString());
			prefs.put("uiLanguage", "eng");
			prefs.put("cmd.copyOnlySegmentedTranscriptions", "true");
		}
		if (prefs.get("uiLanguage", "ger").equals("ger")) {
			prefs.put("uiLanguage", "deu");
		}

	}
}
