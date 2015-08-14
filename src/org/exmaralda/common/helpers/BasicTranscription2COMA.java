/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.helpers;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.exmaralda.coma.helpers.GUID;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.MetaInformation;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.sound.RecordingPropertiesCalculator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 * 
 * @author thomas
 */
public class BasicTranscription2COMA {

	static String SKELETON_XSL_PATH = "/org/exmaralda/common/resources/BasicTranscription2ComaElements.xsl";

	public static Vector<Element> generateComaElements(
			File basicTranscriptionFile, File comaFile) throws JDOMException,
			IOException, SAXException, ParserConfigurationException,
			TransformerConfigurationException, TransformerException,
			JexmaraldaException {

		Vector<Element> result = new Vector<Element>();
		StylesheetFactory ssf = new StylesheetFactory();
		Document comaDocument = IOUtilities.readDocumentFromLocalFile(comaFile
				.getAbsolutePath());
		BasicTranscription bt = new BasicTranscription(basicTranscriptionFile
				.getAbsolutePath());
                // ".." in relative paths allowed now
		bt.getHead().getMetaInformation().resolveReferencedFile(basicTranscriptionFile.getAbsolutePath(), MetaInformation.NEW_METHOD);
		String skeletonString = ssf.applyInternalStylesheetToString(
				SKELETON_XSL_PATH, bt.toXML());

		Document skeletonDocument = IOUtilities
				.readDocumentFromString(skeletonString);
		for (Object o : skeletonDocument.getRootElement().getChildren()) {
			Element e = (Element) o;
			result.addElement(e);
		}

		Element communicationElement = result.elementAt(0);
		communicationElement.setAttribute("Id", new GUID().makeID());
		if (communicationElement.getAttributeValue("Name").trim().length() == 0) {
			communicationElement.setAttribute("Name", basicTranscriptionFile
					.getName());
		}
		Element transcriptionElement = communicationElement
				.getChild("Transcription");
		transcriptionElement.setAttribute("Id", new GUID().makeID());
		transcriptionElement.getChild("Filename").setText(
				basicTranscriptionFile.getName());

		URI uri1 = basicTranscriptionFile.toURI();
		URI uri2 = comaFile.getParentFile().toURI();
		URI relativeURI = uri2.relativize(uri1);
		transcriptionElement.getChild("NSLink").setText(relativeURI.toString());

		for (Object o : communicationElement.getChildren("Recording")) {
			Element recordingElement = (Element) o;
			recordingElement.setAttribute("Id", new GUID().makeID());
			Element mediaElement = recordingElement.getChild("Media");
			mediaElement.setAttribute("Id", new GUID().makeID());
			File mediaFile = new File(mediaElement.getChildText("NSLink"));
			uri1 = mediaFile.toURI();
			relativeURI = uri2.relativize(uri1);
			mediaElement.getChild("NSLink").setText(relativeURI.toString());
			mediaElement.getChild("Filename").setText(mediaFile.getName());
			double dur = RecordingPropertiesCalculator
					.getRecordingDuration(mediaFile);
			// System.out.println(">>>>" + dur);
			if (dur >= 0) {
				recordingElement.getChild("RecordingDuration").setText(
						Long.toString((long) (Math.round(dur * 1000.0))));
			}
		}

		Element settingElement = communicationElement.getChild("Setting");
		String uniqueSD = comaDocument.getRootElement().getAttributeValue(
				"uniqueSpeakerDistinction");
		System.out.println(">>" + uniqueSD);
		List sds = null;
		if (!uniqueSD.endsWith("abbreviation")) {
			Document btDocument = IOUtilities
					.readDocumentFromLocalFile(basicTranscriptionFile
							.getAbsolutePath());
			sds = org.jdom.xpath.XPath.selectNodes(btDocument, uniqueSD);
		}

		for (int pos = 1; pos < result.size(); pos++) {
			Element speakerElement = result.elementAt(pos);
			String id = new GUID().makeID();
			speakerElement.setAttribute("Id", id);
			Element personElement = new Element("Person");
			personElement.setText(id);
			settingElement.addContent(personElement);
			if (!uniqueSD.endsWith("abbreviation")) {
				Element e = (Element) (sds.get(pos - 1));
				speakerElement.getChild("Sigle").setText(e.getText());
			}
		}
		return result;
	}

	public static Object[] importBasicTranscription(
			File basicTranscriptionFile, Document comaDocument, File comaFile)
			throws JDOMException, IOException, SAXException,
			ParserConfigurationException, TransformerConfigurationException,
			TransformerException, JexmaraldaException {
		File tempFile = File.createTempFile("comatemp", "coma", comaFile.getParentFile());
		tempFile.deleteOnExit();
		IOUtilities.writeDocumentToLocalFile(tempFile.getAbsolutePath(),
				comaDocument);
		Vector<Element> v = BasicTranscription2COMA.generateComaElements(
				basicTranscriptionFile, tempFile);
		Element communicationElement = v.elementAt(0);
		int countNew = 0;
		int countExisting = 0;
		for (int pos = 1; pos < v.size(); pos++) {
			Element speakerElement = v.elementAt(pos);
			String sigle = speakerElement.getChildText("Sigle");
			String id = speakerElement.getAttributeValue("Id");
			String xp = "//Speaker[Sigle/text()='" + sigle + "']";
			Object o = XPath.newInstance(xp).selectSingleNode(comaDocument);
			if (o != null) {
				// i.e. there is already a speaker with this sigle in the coma
				// document
				Element existingSpeaker = (Element) o;
				String hisID = existingSpeaker.getAttributeValue("Id");
				String xp2 = "//Person[text()='" + id + "']";
				Element personElement = (Element) XPath.newInstance(xp2)
						.selectSingleNode(communicationElement);
				personElement.setText(hisID);
				countExisting++;
			} else {
				speakerElement.detach();
				comaDocument.getRootElement().getChild("CorpusData")
						.addContent(speakerElement);
				countNew++;
			}
		}
		communicationElement.detach();
		comaDocument.getRootElement().getChild("CorpusData").addContent(
				communicationElement);
		Object[] returnValue = new Object[3];
		returnValue[0] = communicationElement.getAttributeValue("Name");
		returnValue[1] = new Integer(countNew);
		returnValue[2] = new Integer(countExisting);
		return returnValue;
	}

}
