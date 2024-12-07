package org.exmaralda.common.helpers;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class JDOMDocMaker {

	public static Document getDocument(File f) {
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(f);
			Element root = doc.getRootElement();
			return doc;

		} catch (JDOMException e) {
			System.err.println("Languagefile invalid!");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.err.println("Languagefile missing!");
			return null;
		}
	}
	public static Document getDocument(URL u) {
		SAXBuilder builder = new SAXBuilder();
		try {
			Document doc = builder.build(u);
			Element root = doc.getRootElement();
			return doc;

		} catch (JDOMException e) {
			System.err.println("Languagefile invalid!");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.err.println("Languagefile missing!");
			return null;
		}
	}
}
