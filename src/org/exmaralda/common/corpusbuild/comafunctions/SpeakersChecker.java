/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild.comafunctions;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Vector;

import org.exmaralda.common.corpusbuild.AbstractCorpusChecker;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Speaker;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 * 
 * @author thomas
 */
public class SpeakersChecker extends AbstractCorpusChecker {

	String speakerFinder;
	public Vector<String> undefinedSpeakersSigles = new Vector<String>();

	public SpeakersChecker() {
	}

	@Override
	public void checkCorpus(Document corpus, String CORPUS_BASEDIRECTORY)
			throws JDOMException, SAXException, JexmaraldaException,
			URISyntaxException {
		String uniqueSpeakerDistinction = corpus.getRootElement()
				.getAttributeValue("uniqueSpeakerDistinction");
		// uniqueSpeakerDistinction="//speaker/ud-speaker-information/ud-information[@attribute-name=&#34;Sigle&#34;]"
		// uniqueSpeakerDistinction="//speaker/abbreviation"
		speakerFinder = "";
		if (!("//speaker/abbreviation".equals(uniqueSpeakerDistinction))) {
			int index1 = uniqueSpeakerDistinction.lastIndexOf("@");
			int index2 = uniqueSpeakerDistinction.indexOf("\"", index1);
			int index3 = uniqueSpeakerDistinction.lastIndexOf("\"");
			speakerFinder = uniqueSpeakerDistinction.substring(index2 + 1,
					index3);
		}
		System.out.println("SpeakerFinder=" + speakerFinder);
		super.checkCorpus(corpus, CORPUS_BASEDIRECTORY);
		// super!
	}

	@Override
	public void processTranscription(BasicTranscription bt,
			String currentFilename) throws URISyntaxException, SAXException {
		for (int pos = 0; pos < bt.getHead().getSpeakertable()
				.getNumberOfSpeakers(); pos++) {
			Speaker speaker = bt.getHead().getSpeakertable().getSpeakerAt(pos);
			String sigleToFind = speaker.getAbbreviation();
			if (speakerFinder.length() > 0) {
				sigleToFind = speaker.getUDSpeakerInformation()
						.getValueOfAttribute(speakerFinder);
			}
			String xp = "//Speaker[Sigle='" + sigleToFind + "']";
			boolean found = false;
			try {
				Object o = XPath.newInstance(xp).selectSingleNode(
						corpusDocument);
				found = (o != null);
			} catch (JDOMException e) {
				e.printStackTrace();
			}
			if (!found) {
				String text = "Speaker " + speaker.getAbbreviation() + " ("
						+ speaker.getID() + ") not defined in COMA";
				undefinedSpeakersSigles.addElement(sigleToFind);
				addError(currentFilename, "", "", text);
			}
		}
	}

}
