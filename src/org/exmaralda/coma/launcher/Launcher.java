/**
 *
 */
package org.exmaralda.coma.launcher;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.prefs.Preferences;

import javax.swing.UIManager;

import org.exmaralda.coma.resources.ResourceHandler;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.partitureditor.partiture.StringUtilities;

/**
 * @author woerner
 * 
 */
public class Launcher {

	// always change launchertemplate.java
	// another git test

	Preferences prefs = Ui.prefs;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
                // added 09-11-2022: issue #344
                System.setProperty("apple.awt.application.name", "Corpus Manager");        
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name","Corpus Manager");
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception f) {
                    System.err.println("failed setting system look&feel");
                }

		checkFirstRun();
		java.net.URL pURL = new ResourceHandler().propertiesURL();
		java.util.Properties props = new java.util.Properties();
		try {
                    props.load(pURL.openStream());
		} catch (IOException err) {
                    // can't get version number
                    err.printStackTrace();
                    System.out.println(err.getLocalizedMessage());
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
                    System.out.println(args[0] + " passed as file to open. ");
                    if (new File(args[0]).exists()) {
                        // dirty fix for #216
                        inputFile = StringUtilities.fixFilePath(args[0]);
                        System.out.println(inputFile + " set as input file. ");                        
                    }

                    if (args.length > 1) {
                        if (args[1].equals("nologging")) {
                            logging = false;
                        }
                    }
		} else {
                    System.out.println("No arguments passed to Coma.");
                }
		Coma comaInstance = new Coma(inputFile, version, revision, logging);
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
