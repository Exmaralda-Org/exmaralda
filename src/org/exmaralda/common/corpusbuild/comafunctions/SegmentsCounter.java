/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild.comafunctions;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.transform.XSLTransformException;
import org.jdom.transform.XSLTransformer;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 * 
 * @author thomas
 */
public class SegmentsCounter {

	private Vector<SearchListenerInterface> listenerList = new Vector<SearchListenerInterface>();

	String COUNT_XSL = "/org/exmaralda/common/corpusbuild/comafunctions/CountSegments.xsl";
	Document countResultDocument = new Document(new Element("result"));
	XSLTransformer transformer;
	String currentFilename;
	Element currentElement;
	int count = 0;
	private HashSet<String> errorList = new HashSet<String>();

	public SegmentsCounter() throws JDOMException, IOException {
		Document xslDocument = new IOUtilities()
				.readDocumentFromResource(COUNT_XSL);
		transformer = new org.jdom.transform.XSLTransformer(xslDocument);
	}

	public Document getCountResultDocument() {
		return countResultDocument;
	}

	public HashSet<String> getErrorList() {
		return errorList;
	}

	public void countSegments(Document corpus, File baseDir)
			throws JDOMException, SAXException, JexmaraldaException,
			URISyntaxException, IOException {
		XPath xpath = XPath
				.newInstance("//Transcription[Description/Key[@Name='segmented']/text()='true']/NSLink");
		List transcriptionList = xpath.selectNodes(corpus);
		for (int pos = 0; pos < transcriptionList.size(); pos++) {
			Element nslink = (Element) (transcriptionList.get(pos));
			URL fileURL = new URL(baseDir.toURL(),nslink.getText());
			currentElement = nslink;
			currentFilename = fileURL.getFile();
			count++;

			fireCorpusInit((double) count / (double) transcriptionList.size(),
					nslink.getText());
			try {
				Document st = IOUtilities
				.readDocumentFromURL(fileURL);
				processDocument(st);
			} catch (Exception e) {
				errorList.add(e.getMessage());
			}
		}

	}

	public void processDocument(Document st) throws XSLTransformException {
		System.out.println(st.toString());
		Document thisResult = transformer.transform(st);
		Element communication = currentElement.getParentElement()
				.getParentElement();
		Element transcription = currentElement.getParentElement();
		thisResult.getRootElement().setAttribute("communication-id",
				communication.getAttributeValue("Id"));
		thisResult.getRootElement().setAttribute("communication-name",
				communication.getAttributeValue("Name"));
		thisResult.getRootElement().setAttribute("transcription-name",
				transcription.getChild("Name").getText());
		thisResult.getRootElement().setAttribute("transcription-file",
				new File(currentFilename).getName());
		countResultDocument.getRootElement().addContent(
				thisResult.getRootElement().detach());
	}

	public void addSearchListener(SearchListenerInterface sli) {
		listenerList.addElement(sli);
	}

	protected void fireCorpusInit(double progress, String message) {
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listenerList.size() - 1; i >= 0; i -= 1) {
			SearchEvent se = new SearchEvent(SearchEvent.CORPUS_INIT_PROGRESS,
					progress, message);
			listenerList.elementAt(i).processSearchEvent(se);
		}
	}

}
