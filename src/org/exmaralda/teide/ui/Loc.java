/*
 * Created on 05.02.2004 by woerner
 */
package org.exmaralda.teide.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import org.exmaralda.teide.Launcher;
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
public class Loc {
	public static final HashMap UiElements = new HashMap();

	private String language = "deu";

	/** Returns localized UI-Element-names */
	public static String getText(String key) {
		String value;
		if (UiElements.get(key) != null) {
			value = UiElements.get(key).toString();
		} else {
			value = "* " + key;
		}
		return value;
	}

	static {

		SAXBuilder builder = new SAXBuilder();
		Preferences myPrefs = Preferences.userRoot()
				.node("org.exmaralda.teide");
		String langFilePath = "/org/exmaralda/teide/resources/languages.xml";
		String languageCode = myPrefs.get("uiLanguage", "deu");
		myPrefs = null;

		java.net.URL lfURL = Launcher.class.getResource(langFilePath);

		try {
			Document doc = builder.build(lfURL);
			Element root = doc.getRootElement();
			List allElements = root.getChild("uiItems").getChildren();
			Iterator iterator = allElements.iterator();
			while (iterator.hasNext()) {
				Element myElement = (Element) iterator.next();
				if (myElement.getName() == "uiItem"
						&& myElement.getAttributeValue("name") != null
						&& myElement.getAttributeValue("value") != null
						&& myElement.getAttributeValue("lang").equals(
								languageCode)) {
					UiElements.put(myElement.getAttributeValue("name"),
							myElement.getAttributeValue("value"));
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