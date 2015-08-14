/*
 * Created on 05.02.2004 by woerner
 */
package org.exmaralda.coma.root;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;

import org.exmaralda.coma.resources.ResourceHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * coma2/org.sfb538.coma2/Ui.java
 * 
 * @author woerner
 * 
 */
public class Ui {
	private static final HashMap<String, String> UiElements = new HashMap<String, String>();

	public static final Preferences prefs = Preferences.userRoot().node("org.exmaralda.coma");

	private static String languageCode="";

	/** Returns localized UI-Element-names */
	public static String getText(String key) {
		String value;
		if (UiElements.get(key) != null) {
			value = UiElements.get(key);
		} else {
			value = "* " + key;
		}
		return value;
	}
	
	public static String getLanguageCode() {
		return languageCode;
	}
	
	public static Vector<File> getHelpFiles() {
		Vector<File> helpFiles = new Vector<File>();

		return helpFiles;
	}

	static {

		SAXBuilder builder = new SAXBuilder();
		languageCode = prefs.get("uiLanguage", "deu");

		java.net.URL lfURL = new ResourceHandler().languageFileURL();

		try {
			Document doc = builder.build(lfURL);
			Element root = doc.getRootElement();
			List allElements = root.getChild("uiItems").getChildren();
			Iterator iterator = allElements.iterator();
			while (iterator.hasNext()) {
				Element myElement = (Element) iterator.next();
				if (myElement.getName() == "uiItem" && myElement.getAttributeValue("name") != null && myElement.getAttributeValue("value") != null && myElement.getAttributeValue("lang").equals(languageCode)) {
					UiElements.put(myElement.getAttributeValue("name"), myElement.getAttributeValue("value"));
				} else {
					//					System.err.println("uiElement in Languagefile invalid!");
				}
			}

		} catch (JDOMException e) {
			System.err.println("Languagefile invalid!");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Languagefile missing!");
		}

	}
}