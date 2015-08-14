/**
 *
 */
package org.exmaralda.coma.launcher;

import java.io.File;
import java.util.Date;
import java.util.prefs.Preferences;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.exmaralda.coma.resources.ResourceHandler;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;

/**
 * @author woerner
 * 
 */
public class Launcher {

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
		if (Ui.prefs.getBoolean("prefs.useNimbusLookAndFeel", false)) {
			try {
				for (LookAndFeelInfo info : UIManager
						.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) {
						UIManager.setLookAndFeel(info.getClassName());
						break;
					}
				}
			} catch (Exception e) {
				if (!Coma.os.equals("mac")) {
					try {
						UIManager.setLookAndFeel(UIManager
								.getSystemLookAndFeelClassName());
					} catch (Exception f) {
						System.err.println("failed setting system look&feel");
					}
				} else {
					try {
						UIManager
								.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
					} catch (Exception g) {
						System.err
								.println("failed setting Quaqua look&feel (don't bother if you don't use a mac).");

					}
				}

			}
		} else {
			if (!Coma.os.equals("mac")) {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (Exception f) {
					System.err.println("failed setting system look&feel");
				}
			} else {
				try {
					UIManager
							.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
				} catch (Exception g) {
					System.err
							.println("failed setting Quaqua look&feel (don't bother if you don't use a mac).");

				}
			}

		}
		checkFirstRun();
		java.net.URL pURL = new ResourceHandler().propertiesURL();
		java.util.Properties props = new java.util.Properties();
		try {
			props.load(pURL.openStream());
		} catch (Exception err) {
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
