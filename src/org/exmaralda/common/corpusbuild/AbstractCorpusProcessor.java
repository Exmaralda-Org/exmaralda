/*
 * AbstractCorpusProcessor.java
 *
 * Created on 10. Oktober 2006, 17:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.jdom.*;
import org.jdom.xpath.XPath;

/**
 * 
 * @author thomas
 */
public abstract class AbstractCorpusProcessor {

	public String CORPUS_FILENAME = "";
	public String CORPUS_BASEDIRECTORY = "";

	public static String SEGMENTED_FILE_XPATH = "//Transcription[Description/Key[@Name='segmented']/text()='true']/NSLink";
	public static String BASIC_FILE_XPATH = "//Transcription[Description/Key[@Name='segmented']/text()='false']/NSLink";
	public static String ALL_FILE_XPATH = "//Transcription/NSLink";

	public String currentFilename;

	public Document corpus;
	public Element currentElement;

	Vector<String> allFilenames;
	public String previousURL;
	public String nextURL;
	public int count = 0;

	java.text.SimpleDateFormat sdf;
	Calendar cal;

	StringBuffer out = new StringBuffer();

	/** Creates a new instance of AbstractCorpusProcessor */
	public AbstractCorpusProcessor(String corpusPath) {
		CORPUS_FILENAME = corpusPath;
		CORPUS_BASEDIRECTORY = new File(corpusPath).getParent();
		/*
		 * * on some JDK, the default TimeZone is wrong* we must set the
		 * TimeZone manually!!!* Calendar cal =
		 * Calendar.getInstance(TimeZone.getTimeZone("EST"));
		 */
		cal = Calendar.getInstance(TimeZone.getDefault());

		String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
		sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
		/*
		 * * on some JDK, the default TimeZone is wrong* we must set the
		 * TimeZone manually!!!* sdf.setTimeZone(TimeZone.getTimeZone("EST"));
		 */
		sdf.setTimeZone(TimeZone.getDefault());

	}

	public abstract String getXpathToTranscriptions();

	public String getCurrentFilename() {
		return currentFilename;
	}

	public String getCurrentDirectoryname() {
		return new File(getCurrentFilename()).getParent();
	}

	public Element getCurrentCorpusNode() {
		return currentElement;
	}

	public String getNakedFilename() {
		return new File(getCurrentFilename()).getName();
	}

	public String getNakedFilenameWithoutSuffix() {
		String with = new File(getCurrentFilename()).getName();
		int index = with.lastIndexOf('.');
		String without = with.substring(0, index);
		return without;
	}

	public Vector<String> getAllFilenames() {
		try {
			Vector<String> result = new Vector<String>();
			Document c = org.exmaralda.common.jdomutilities.IOUtilities
					.readDocumentFromLocalFile(CORPUS_FILENAME);
			XPath xpath = XPath.newInstance(getXpathToTranscriptions());
			List transcriptionList = xpath.selectNodes(c);
			for (int pos = 0; pos < transcriptionList.size(); pos++) {
				Element nslink = (Element) (transcriptionList.get(pos));
				// currentElement = nslink;
				// String fullTranscriptionName = CORPUS_BASEDIRECTORY + "\\" +
				// nslink.getText();
				result.add(nslink.getText());
			}
			return result;
		} catch (JDOMException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void doIt() throws IOException, JDOMException, SAXException,
			JexmaraldaException {
		corpus = org.exmaralda.common.jdomutilities.IOUtilities
				.readDocumentFromLocalFile(CORPUS_FILENAME);
		allFilenames = getAllFilenames();
		System.out.println(CORPUS_FILENAME + " read successfully.");
		XPath xpath = XPath.newInstance(getXpathToTranscriptions());
		List transcriptionList = xpath.selectNodes(corpus);
		for (int pos = 0; pos < transcriptionList.size(); pos++) {
			System.out
					.println("#####" + pos + " / " + transcriptionList.size());
			Element nslink = (Element) (transcriptionList.get(pos));
			currentElement = nslink;
			final String fullTranscriptionName = CORPUS_BASEDIRECTORY
					+ System.getProperty("file.separator", "/")
					+ nslink.getText();
			System.out.println("Reading " + fullTranscriptionName + "...");
			currentFilename = fullTranscriptionName;

			if (count == 0) {
				previousURL = (String) (allFilenames.elementAt(allFilenames
						.size() - 1));
			} else {
				previousURL = (String) (allFilenames.elementAt(count - 1));
			}
			String previousCode = previousURL.substring(
					previousURL.lastIndexOf('/') + 1,
					previousURL.lastIndexOf('.'));
			previousURL = previousURL.substring(0,
					previousURL.lastIndexOf('/') + 1)
					+ "presentation/"
					+ previousCode;

			if (count == allFilenames.size() - 1) {
				nextURL = (String) (allFilenames.elementAt(0));
			} else {
				nextURL = (String) (allFilenames.elementAt(count + 1));
			}
			String nextCode = nextURL.substring(nextURL.lastIndexOf('/') + 1,
					nextURL.lastIndexOf('.'));
			nextURL = nextURL.substring(0, nextURL.lastIndexOf('/') + 1)
					+ "presentation/" + nextCode;

			count++;

			process(fullTranscriptionName);

		}
	}

	public abstract void process(String filename) throws JexmaraldaException,
			SAXException;

	void output(String filename) throws IOException {
		System.out.println("started writing document...");
		outappend("============================\n");
		FileOutputStream fos = new FileOutputStream(new File(filename));
		fos.write(out.toString().getBytes());
		fos.close();
		System.out.println("Document written...");
	}

	public List get(String xp) {
		try {
			XPath xpath = XPath.newInstance(xp);
			return xpath.selectNodes(corpus);
		} catch (JDOMException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public List get(String xp, Content c) {
		try {
			XPath xpath = XPath.newInstance(xp);
			return xpath.selectNodes(c);
		} catch (JDOMException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Object getSingle(String xp) {
		try {
			XPath xpath = XPath.newInstance(xp);
			return xpath.selectSingleNode(corpus);
		} catch (JDOMException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Object getSingle(String xp, Content c) {
		try {
			XPath xpath = XPath.newInstance(xp);
			return xpath.selectSingleNode(c);
		} catch (JDOMException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void outappend(String a) {
		cal = Calendar.getInstance(TimeZone.getDefault());
		String time = sdf.format(cal.getTime());
		out.append("[" + time + "] ");
		out.append(a);
	}

	static void insertPreviousAndNext(Document d, String prev, String next) {
		if ((prev != null) && (next != null)) {
			try {
				/*
				 * <span id="previous-doc"
				 * title="Click to display Heidelberg 09"> <a href="">[Prev]</a>
				 * </span>
				 */
				XPath p = XPath.newInstance("//span[@id='previous-doc']");
				Object o = p.selectSingleNode(d);
				if (o!=null){
                                    Element span = (Element) o;
                                    span.setAttribute("title", "Click to display " + prev);
                                    Element a = span.getChild("a");
                                    a.setAttribute("href", prev);
                                }

				XPath p2 = XPath.newInstance("//span[@id='next-doc']");
                                Object o2 = p2.selectSingleNode(d);
				if (o2!=null){
                                    Element span2 = (Element) o2;
                                    span2.setAttribute("title", "Click to display " + next);
                                    Element a2 = span2.getChild("a");
                                    a2.setAttribute("href", next);
                                }

			} catch (JDOMException ex) {
				ex.printStackTrace();
			}
		} else {
			return;
			
		}
	}

}
