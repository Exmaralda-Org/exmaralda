/**
 * 
 */
package org.exmaralda.coma.helpers;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import org.exmaralda.coma.resources.ResourceHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * @author woerner
 * 
 */
public class ComaXML {

	/**
	 * @param text
	 * @param commElmt
	 * @return
	 */
	public static Element createAndValidateElement(String text, Element commElmt)
			throws JDOMException {
		URL url = new ResourceHandler().schemaURL();
		System.out.println(url.toString());
		System.out.println("validate");

		Element elm = null;
		String front = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<?xml-stylesheet type=\"text/xsl\" href=\"http://xml.exmaralda.org/comacorpus.xsl\"?><Corpus xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Id=\"tmp\" Name=\"tmp\" xsi:noNamespaceSchemaLocation=\"comacorpus.xsd\"><CorpusData>";
		String end = "</CorpusData></Corpus>";
		if (commElmt.getName().equals("Communication")) {
			Format format = Format.getRawFormat();
			XMLOutputter outputter = new XMLOutputter(format);
			text = text
					+ outputter.outputString(commElmt.getParentElement()
							.getChildren("Speaker"));
		}
		text = front + text + end;

		Document jdoc = null;

		SAXBuilder builder = new SAXBuilder(
				"org.apache.xerces.parsers.SAXParser", true);
		builder.setFeature("http://apache.org/xml/features/validation/schema",
				true);
		if (url.toString().indexOf(".jar!") > 0) {
			builder.setProperty(
					"http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
					// "file://org.sfb538.coma2.comacorpus.xsd");
					"jar:" + url.getFile());
		} else {
			builder.setProperty(
					"http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
					// "file://org.sfb538.coma2.comacorpus.xsd");
					url.getFile());

		}

		// builder.setEntityResolver(new SchemaLoader());

		// builder
		// .setProperty(
		// "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
		// "http://xml.exmaralda.org/comacorpus.xsd");

		System.out.println(text);
		try {
			jdoc = builder.build(new StringReader(text));
		} catch (IOException err) {
			err.printStackTrace();
		}
		elm = (Element) jdoc.getRootElement().getChild("CorpusData")
				.getChildren().get(0);
		elm.detach();

		return elm;
	}

	/**
	 * checks if an element is inside a communication or a speaker
	 * 
	 * @param e
	 *            the element to investigate
	 * @return
	 */
	public static boolean getContext(Element e) {
		if (e.getDocument() == null) { // das ist wohl ein geklontes element...
			return false;
		} else {
			Element pe = e.getParentElement();
			while ((pe.getName() != "Communication")
					&& (pe.getName() != "Speaker") && (pe != null)) {
				if (pe != pe.getDocument().getRootElement()) {
					pe = pe.getParentElement();
				} else {
					break;
				}
			}
			return pe.getName().equals("Communication");
		}
	}

	public static Element getContextElement(Element e) {
		while ((e.getName() != "Communication") && (e.getName() != "Speaker")) {
			if (e != e.getDocument().getRootElement()) {
				e = e.getParentElement();
			} else {
				break;
			}
		}
		return e;
	}
}
