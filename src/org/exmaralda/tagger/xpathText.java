/*
 * Created on 16.08.2004 by woerner
 */
package org.exmaralda.tagger;

import java.util.Iterator;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 * Ludger/z2tagger/xpathText.java
 * @author woerner
 * 
 */
public class xpathText {
	private static Document doc;
	private static Element root;
	private static XPath xpath;
	public static void main(String[] args) {
		SAXBuilder parser = new SAXBuilder();
		try {
			doc = parser.build("test.xml");
			root = doc.getRootElement();
			xpath = XPath.newInstance("//w");
			List words = xpath.selectNodes(doc);
			Iterator i = words.iterator();
			while (i.hasNext()) {
				System.out.println(((Element)i.next()).getAttributeValue("no"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}