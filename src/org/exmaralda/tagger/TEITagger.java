package org.exmaralda.tagger;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.filter.ContentFilter;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/*
 * Created on 06.08.2004 by woerner
 */
/**
 * Ludger//TEITagger.java
 * 
 * @author woerner
 * 
 */
public class TEITagger {
	private int itemsBefore;

	private int itemsAfter;

	private List searchedElements;

	private boolean direction; // true = fwd, false = backwd

	private Element actualElement;

	private Element root;

	private Document doc;

	private ProcessingInstruction stylesheet;

	private String header;

	private Vector words;

	private Vector options;

	String outputFile;

	private String inputFileName;

	private String xpath;

	int findings;

	int finding;

	private String searchXPath;

	private ListIterator searchIterator;

	private String attribute;

	private String colorString;

	private int searchIndex;

	private boolean showAtts;

	private boolean showTags;

	private boolean ignoreTagged;

	private boolean docChanged;

	private boolean showFirst;

	static Preferences prefs = Preferences.userRoot().node(
			"org.sfb538.teitagger");

	private boolean docLoaded;

	private boolean wordlistExists;

	private boolean xmlLoaded;

	private HashMap ids;

	private int resultCount;

	private Vector tags;

	private Element fRoot;

	private HashSet foundElements;

	private String wordlistFileName;

	private Namespace namespace;

	/**
	 * 
	 */
	public TEITagger() {
		foundElements = new HashSet();
		docLoaded = false;
		wordlistExists = false;
		words = new Vector();
		LinkedList asdf = new LinkedList();
		searchedElements = (List) asdf;
		options = new Vector();
		xpath = "";
		options.clear();
	}

	public TEITagger(String inputFile, String wordlistFile) {
		itemsBefore = 0;
		itemsAfter = 0;
		inputFileName = inputFile;
		// setFormsFromFile(wordlistFile);
		// setInputFile(inputFile);
		docChanged = false;
	}

	public String getModifiedFileName(boolean path) {
		String myString;
		int dot = inputFileName.lastIndexOf(".");
		int slash = inputFileName.lastIndexOf("\\");
		if (path == true) {
			myString = inputFileName.substring(0, dot) + "_tagged"
					+ inputFileName.substring(dot);
		} else {
			myString = inputFileName.substring(slash + 1, dot) + "_tagged"
					+ inputFileName.substring(dot);
		}
		return myString;
	}

	public String getWordlistFile(File inputFile) {
		SAXBuilder parser = new SAXBuilder();
		try {
			Document formsDoc = parser.build(inputFile);
			if (formsDoc.getRootElement().getChild("forms") != null) {
				extractForms(formsDoc);
				search();
				wordlistFileName = inputFile.getName();
				return "ok";
			} else {
				return inputFile.getName()
						+ " is not a wordlist-file. (no forms-elements)";
			}
		} catch (JDOMException e) {
			System.err.println("failed building XML from input file");
			e.printStackTrace();
			return inputFile.getName() + " is not a valid XML-file."
					+ e.getMessage();
		} catch (IOException e) {
			System.err.println("failed loading input file");
			return inputFile.getName() + " not found.";
		}
	}

	/**
	 * checks whether xml and wordlist exist, builds search results, fires
	 * ui-update
	 */
	private void search() {
		System.out.println("search0");
		int beforeIndex = searchIndex;
		resultCount = 0;
		if ((xmlLoaded) && (wordlistExists)) {
			fireTaggerEvent("WAIT");
			fireTaggerEvent("STATUS:Searching...");
			System.out.println("all exists");
			searchedElements.clear();
			System.out.println("root element:" + root.toString());
			System.out.println(root.getNamespaceURI());
			foundElements.clear();
			XPath x;
			try {
				System.out.println(xpath);
				x = XPath.newInstance(xpath);
				// Namespace nsConfig = Namespace.getNamespace("z2", root
				// .getNamespaceURI());
				// x.addNamespace(nsConfig);
				// !!!
				// x.addNamespace(doc.getRootElement().getNamespace());
				List l = x.selectNodes(doc);
				System.out.println(l.size());
				foundElements = new HashSet(l);
				System.out.println("foundElements:" + foundElements.size());
				fireTaggerEvent("XPathValid");
			} catch (JDOMException e) {
				foundElements.clear();
				fireTaggerEvent("ERR:invalidXPath");
				// e.printStackTrace();
			}
			getSearchedElements(root);
			searchIterator = searchedElements.listIterator();
			if (searchIterator.hasNext()) {
				fireTaggerEvent("resultsExist");
				fireTaggerEvent("OK: Search complete. "
						+ searchedElements.size() + " results, " + resultCount
						+ " qualify.");
			} else {
				fireTaggerEvent("STATUS: Search complete. No results found.");
			}
			setIndex(beforeIndex);
			fireTaggerEvent("RESUME");
		}
	}

	/**
	 * @param doc2
	 */
	public void extractForms(Document d) {
		fRoot = d.getRootElement();
		if ((fRoot.getChild("forms") != null)
				&& (fRoot.getChild("xpath") != null)
				&& (fRoot.getChild("options") != null)) {
			List forms = fRoot.getChild("forms").getChildren();
			Iterator fi = forms.iterator();
			while (fi.hasNext()) {
				Element myElement = (Element) fi.next();
				if (myElement.getText() != null) {
					words.add(myElement.getText());
					System.out.println(myElement.getText());
				}
			}
			xpath = fRoot.getChildText("xpath");
			attribute = fRoot.getChild("options").getChildText("attribute");
			List opts = fRoot.getChild("options").getChildren("option");
			Iterator oi = opts.iterator();
			while (oi.hasNext()) {
				Element myElement = (Element) oi.next();
				if (myElement.getText() != null) {
					words.add(myElement.getText());
					System.out.println(myElement.getText());
				}
			}
			Iterator opI = opts.iterator();
			while (opI.hasNext()) {
				options.add(new TagOption((Element) opI.next()));
			}
			wordlistExists = true;
			fireTaggerEvent("wordlistLoaded");
		} else {
			words = null;
			wordlistExists = false;
		}
	}

	/**
	 * @param string
	 */
	public String setTEIFile(File f) {
		SAXBuilder parser = new SAXBuilder();
		try {
			doc = parser.build(f);

			List pis = doc.getContent(new ContentFilter(16));
			Iterator pisi = pis.iterator();
			while (pisi.hasNext()) {
				ProcessingInstruction pi = (ProcessingInstruction) pisi.next();
				if (pi.getTarget() == "xml-stylesheet") {
					stylesheet = pi;
				}
			}
			root = doc.getRootElement();
			System.out.println("Namespace:" + root.getNamespace());
			namespace = root.getNamespace();
			System.out.println("xml loaded OK1");
			System.out.println("removing Namespaces...");
			removeNamespace(doc);

			if (true) {
				// if ((root.getChild("teiHeader") != null) &&
				// (root.getChild("text") != null)) {
				// header = getHTMLHeader();
				header = "header";
				System.out.println("xml loaded OK2");
				xmlLoaded = true;
				if (root.getNamespaceURI().length() > 0) {
					fireTaggerEvent("hasNamespace");
				} else {
					fireTaggerEvent("hasNoNamespace");
				}
				fireTaggerEvent("xmlLoaded:" + f.getName());
				search();
				inputFileName = f.getPath();
				return "ok";
			} else {
				System.out.println("---");
				System.out.println(root.getName());
				for (Element e : (List<Element>) root.getChildren()) {
					System.out.println(e.getName());
				}
				return f.getName()
						+ " is not a valid TEI-file (no teiHeader or text-Elements fround).";
			}
		} catch (JDOMException e) {
			System.err.println("failed building XML from input file");
			e.printStackTrace();
			return f.getName() + " is not a valid XML-file.";
		} catch (IOException e) {
			System.err.println("failed opening input file");
			return f.getName() + " not found.";
		}
	}

	private void removeNamespace(Document doc2) {
		Iterator i = doc2.getRootElement().getDescendants();
		while (i.hasNext()) {
			Content c = (Content) i.next();
			if (c.getClass().equals(Element.class)) {
				((Element) c).setNamespace(null);
				for (Attribute a : (List<Attribute>) ((Element) c)
						.getAttributes()) {
					a.setNamespace(null);
				}
			}

		}
		// TODO Auto-generated method stub

	}
	private void reapplyNamespace(Document d) {
		Iterator i = d.getRootElement().getDescendants();
		while (i.hasNext()) {
			Content c = (Content) i.next();
			if (c.getClass().equals(Element.class)) {
				((Element) c).setNamespace(namespace);
				for (Attribute a : (List<Attribute>) ((Element) c)
						.getAttributes()) {
					try {
						a.setNamespace(namespace);
						
					} catch 					(org.jdom.IllegalNameException e) {
						System.out.println(e.getLocalizedMessage());
					}

				}
			}

		}
		// TODO Auto-generated method stub

	}
	

	/**
	 * @param root2
	 * @return
	 */
	public String getHTMLHeader() {

		System.out.println("Getting HTML-Header");
		String header = "<table cellpadding=0 cellspacing=0 border=0>";
		try {
			XPath teiHeaderXPath = XPath.newInstance("//teiHeader");
			Element teiHeader = (Element) teiHeaderXPath.selectSingleNode(doc);
			Iterator i = teiHeader.getDescendants(new ElementFilter());

			while (i.hasNext()) { // if (i.getClass() == Element.class) {
				// Element
				Element headerItem = (Element) i.next();
				header += "<tr><td align=right valign=top><font size=2>"
						+ headerItem.getName() + ":"
						+ "</font></td><td valign=top><font	  size=2><b>"
						+ headerItem.getText().replaceAll("\\<.*?	  \\>", "")
						+ "</b></font></td></tr>";
			}
		} catch (JDOMException e) {
			// asdf

		} catch (NullPointerException npe) {
			header += "<tr><td align=right colspan=2>No teiHeader teiHeader	  found</td></tr>";
		}
		header += "<tr><td	  align=right>Stylesheet:</td><td><b>";
		header += (stylesheet != null ? stylesheet
				.getPseudoAttributeValue("href") : "none");
		header += "</b></td></tr>";
		header += "</table>";
		return header;

	}

	/**
	 * gets the Element at the current index
	 * 
	 * @param i
	 * @return
	 */
	public Element getCurrentForm(int i) {
		if (searchIterator.hasNext()) {
			searchIterator.next();
			return (Element) searchIterator.previous();
		} else if (searchIterator.hasPrevious()) {
			searchIterator.previous();
			return (Element) searchIterator.next();
		} else {
			return null;
		}
	}

	public Element getPreviousForm() {
		if (direction == true) {
			if (searchIterator.hasPrevious()) {
				searchIterator.previous();
			}
		}
		direction = false;
		while (searchIterator.hasPrevious()) {
			Element myElement = (Element) searchIterator.previous();
			if (qualifies(myElement)) {
				searchIndex = searchedElements.indexOf(myElement);
				return actualElement = myElement;
			}
		}
		return null;
	}

	public Element getNextForm() {
		if (!direction) {
			searchIterator.next();
		}
		direction = true;
		while (searchIterator.hasNext()) {
			Element myElement = (Element) searchIterator.next();
			if (qualifies(myElement)) {
				searchIndex = searchedElements.indexOf(myElement);
				return actualElement = myElement;
			}
		}
		return null;
	}

	public Element getPreviousFormOld() {
		boolean found = false;
		if (direction == true) {
			searchIterator.previous();
		}
		direction = false; // es geht zur�ck!
		while (!found) {
			if (searchIterator.hasPrevious()) {
				Element myElement = (Element) searchIterator.previous();
				if (useWordlist) {
					Iterator formsIterator = words.iterator();
					while (formsIterator.hasNext()) {
						Element myFormElement = (Element) formsIterator.next();
						if (myElement.getText().equals(myFormElement.getText())) {
							searchIndex = searchedElements.indexOf(myElement);
							actualElement = myElement;
							if (ignoreTagged) {
								Iterator oI = options.iterator();
								boolean optionFound = false;
								while (oI.hasNext()) {
									if (myElement.getAttributeValue(attribute) == ((Element) oI
											.next()).getText()) {
										optionFound = true;
										break;
									}
								}
								if (optionFound == false) {
									found = true;
									return myElement;
								}
							} else if (showFirst) {
								Element mP = (Element) myElement.getParent();
								if (mP.getChildren().indexOf(myElement) == 0) {
									found = true;
									return myElement;
								}
							} else {
								found = true;
								return myElement;
							}
						}
					}
				} else {
					found = true;
					return myElement;
				}
			} else {
				return null;
			}
		}
		return null;
	}

	public String outputPreviousForm() {
		Element pf = getPreviousForm();
		return (pf == null ? "no (more) results." : parentFromFoundElement(pf));
	}

	public String outputNextForm() {
		Element nf = getNextForm();
		return (nf == null ? "no (more) results." : parentFromFoundElement(nf));
	}

	private String parentFromFoundElement(Element myElement) {
		int idCount = 0;
		ids = new HashMap();
		String htmlString = "";
		Element myParent = (Element) myElement.getParent();
		// Items before
		if ((myParent.getParent() != null) && (itemsBefore > 0)) {
			Element myGrandParent = (Element) myParent.getParent();
			List myAunties = myGrandParent.getChildren();
			int myParentsPosition = myAunties.indexOf(myParent);
			for (int i = 0; i < itemsBefore; i++) {
				try {
					Element nowAuntie = (Element) myAunties
							.get(myParentsPosition - itemsBefore + i);
					if (nowAuntie != null) {
						htmlString += "<font color=#999999>";
						// List myCousins = nowAuntie.getChildren();
						Iterator cousinIterator = nowAuntie
								.getDescendants(new ElementFilter());
						// Iterator cousinIterator = myCousins.iterator();
						while (cousinIterator.hasNext()) {
							Element temp = (Element) cousinIterator.next();
							// htmlString += "|" + temp.getName();
							htmlString += "<a href=# id=" + idCount + ">"
									+ handlePunctuation(temp.getText())
									+ "</a>";
							ids.put(new Integer(idCount).toString(), temp);
							idCount++;
						}
					}
				} catch (IndexOutOfBoundsException e) {
					htmlString += "<font color=#999999>no sentence before";
				}
				htmlString += "</font><br>";
			}
		}
		// found item
		htmlString += "<p style=\"font-size:14px\"><br><font color=#000000>";
		if (showTags) {
			htmlString += "<b>&lt;" + myParent.getName() + "&gt;</b>&nbsp;";
		}
		// List mySiblings = myParent.getChildren();
		// Iterator mySiblingIterator = mySiblings.iterator();
		Iterator mySiblingIterator = myParent
				.getDescendants(new ElementFilter());
		while (mySiblingIterator.hasNext()) {
			Element mySibling = (Element) mySiblingIterator.next();
			if (mySibling == myElement) {
				int myCounter = 0;
				int myIndex = 0;
				boolean attMatch = false;
				Iterator ix = options.iterator();
				while (ix.hasNext()) {
					TagOption myOption = ((TagOption) ix.next());
					if (myElement.getAttributeValue(myOption.getAttName()) == null) {
						attMatch = true;
						myIndex = myCounter;

					} else {
						if (myElement.getAttributeValue(myOption.getAttName())
								.indexOf(myOption.getNewVal()) > -1) {
							attMatch = true;
							myIndex = myCounter;
						}
					}
					/*
					 * String regex = myOption.getNewVal(); Pattern pattern =
					 * Pattern.compile(regex); Matcher matcher =
					 * pattern.matcher(myElement
					 * .getAttributeValue(myOption.getAttName())); if
					 * (matcher.matches()) { attMatch = true; myIndex =
					 * myCounter; }
					 */
					myCounter++;
				}
				if (attMatch == true) {
					colorString = getHTMLColor(Helpers.colors[myIndex]);
				} else {
					colorString = "black";
				}
				htmlString += " <a href='#' id='" + idCount + "'><font color="
						+ colorString + "><b><u>"
						+ getElementHTML(mySibling, true)
						+ "</u></b></font></a>";
				ids.put(new Integer(idCount).toString(), mySibling);
				idCount++;
				// htmlString+="<div style=\"background-color:"+colorString+"; font-weight:bold; font-decoration:underline;\">"+mySibling.getText()+"</div>";
				if (showAtts) {
					htmlString += " [" + myElement.getAttributeValue(attribute)
							+ "]";

				}
			} else {
				htmlString += "<a href='#' id='" + idCount + "'>"
						+ handlePunctuation(mySibling.getText()) + "</a>";
				ids.put(new Integer(idCount).toString(), mySibling);
				idCount++;
			}
		}
		if (showTags) {
			htmlString += "<b>&lt;/" + myParent.getName() + "&gt;</b>&nbsp;";
		}
		htmlString += "<br></p>";
		// Items after
		if ((myParent.getParent() != null) && (itemsAfter > 0)) {
			Element myGrandParent = (Element) myParent.getParent();
			List myAunties = myGrandParent.getChildren();
			int myParentsPosition = myAunties.indexOf(myParent);
			for (int i = 1; i < itemsAfter + 1; i++) {
				try {
					Element nowAuntie = (Element) myAunties
							.get(myParentsPosition + i);
					if (nowAuntie != null) {
						htmlString += "<font color=#999999>";
						// List myCousins = nowAuntie.getChildren();
						Iterator cousinIterator = nowAuntie
								.getDescendants(new ElementFilter());
						while (cousinIterator.hasNext()) {
							String addString = ((Element) cousinIterator.next())
									.getText();
							htmlString += handlePunctuation(addString);
						}
					}
				} catch (IndexOutOfBoundsException e) {
					htmlString += "<font color=#999999>no sentence behind";
				}
				htmlString += "</font><br>";
			}
		}
		return htmlString;
	}

	private String getElementHTML(Element mySibling, boolean b) {
		String s = "";
		for (Content c : (List<Content>) mySibling.getContent()) {
			if (c.getClass().equals(Element.class)) {
				s += "[" + ((Element) c).getName();
				if (((Element) c).getAttributes().size() > 0) {
					s += "(";
					for (Attribute a : (List<Attribute>) ((Element) c)
							.getAttributes()) {
						s += a.getName() + "=" + a.getValue();
					}
					s += ")";
				}
				s+="]";
			} else {
				s += "" + c.getValue();
			}
		}

		return s;
	}

	/**
	 * @param addString
	 * @return
	 */
	private String handlePunctuation(String addString) {
		if (addString.equals(",") || addString.equals(".")
				|| addString.equals(":") || addString.equals(";")) {
			return addString;
		} else {
			return " " + addString;
		}
	}

	/**
	 * @param color
	 * @return
	 */
	private String getHTMLColor(Color color) {
		String colString = "#";
		colString += Integer.toString(color.getRed(), 16);
		colString += Integer.toString(color.getGreen(), 16);
		colString += Integer.toString(color.getBlue(), 16);
		return colString;
	}

	/**
	 * @return
	 */
	public int getIndex() {
		return searchIndex;
	}

	public void setIndex(int targetIndex) {
		while (searchIndex != targetIndex) {
			if (searchIndex > targetIndex) {
				searchIterator.previous();
				searchIndex--;
			} else {
				searchIterator.next();
				searchIndex++;
			}
		}
	}

	public void save(String fileName) {
		if (fileName != null) {
			inputFileName = fileName;
		}
		if (namespace!=null) {
			if (namespace!=Namespace.NO_NAMESPACE) {
				reapplyNamespace(doc);
			}
		}
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		XMLOutputter op = new XMLOutputter();
		op.setFormat(format);
		try {
			FileOutputStream out = new FileOutputStream(new File(inputFileName));
			op.output(doc, out);
			out.close();
		} catch (Exception e) {
			fireTaggerEvent("ERR:" + "Error saving file.");
			e.printStackTrace();
		}
	}

	public String getWordlist() {
		String wordlist = "";
		Iterator i = words.iterator();
		while (i.hasNext()) {
			wordlist += ((String) i.next()) + "\n";
		}
		return wordlist;
	}

	/**
	 * @param wordlist
	 */
	/*
	 * public void setWordlist(List w) { words = w; }
	 */
	/**
	 * @return
	 */
	public String getXpath() {
		return xpath;
	}

	/**
	 * @return
	 */
	public Vector getOptions() {
		return options;
	}

	/**
	 * @return
	 */
	public int getSearchCount() {
		if (searchedElements != null) {
			return searchedElements.size();
		} else {
			return 0;
		}
	}

	public void tag(String myOption) {
		System.out.println(myOption);
		TagOption to = (TagOption) options
				.get(new Integer(myOption).intValue());
		if (to.isReplace()) {
			System.out.println(to.getAttName() + "=" + to.getNewVal());
			actualElement.setAttribute(to.getAttName(), to.getNewVal());
		} else {
			if ((actualElement.getAttributeValue(to.getAttName()) == "")
					|| (actualElement.getAttribute(to.getAttName()) == null)) {
			} else { // attribute exists already
				System.out.print(to.getAttName() + " war: "
						+ actualElement.getAttributeValue(to.getAttName()));
				String newValue = actualElement.getAttributeValue(
						to.getAttName()).replaceAll(to.getReplaceVal(),
						to.getNewVal());
				System.out.println(" wird:" + newValue);
				actualElement.setAttribute(to.getAttName(), newValue);
			}
		}
		docChanged = true;
	}

	public void newAtt(String att, String val) {
		if (att.length() > 0 && val.length() > 0) {
			actualElement.setAttribute(att, val);
			docChanged = true;
		}
	}

	public void setItemsAfter(int itemsAfter) {
		this.itemsAfter = itemsAfter;
	}

	public void setItemsBefore(int itemsBefore) {
		this.itemsBefore = itemsBefore;
	}

	/**
	 * do some seroious searching!
	 * 
	 * @param elm
	 *            the root element
	 */
	private void getSearchedElements(Element elm) {
		System.out.print("-|");
		if (foundElements.contains(elm)) {
			// if (elm.getName().equals(xpath)) {
			searchedElements.add(elm);
			if (qualifies(elm)) {
				resultCount++;
			}
		}
		List myChildren = elm.getChildren();
		Iterator i = myChildren.iterator();
		while (i.hasNext()) {
			Element child = (Element) i.next();
			getSearchedElements(child);
		}
	}

	/**
	 * checks wheter the element matches additional search criteria
	 * 
	 * @param elm
	 * @return true/false
	 */
	private boolean qualifies(Element myElement) {
		if (!useWordlist) {
			return true;
		}
		Iterator formsIterator = words.iterator();
		while (formsIterator.hasNext()) {
			String regex = (String) formsIterator.next();
			try {
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(myElement.getText());
				// System.out.print("does "+myElement.getText()+" match "+regex+"?");
				if (matcher.matches()) {
					// System.out.println(" Yes!");
					return true;
				}
			} catch (Exception e) {
				fireTaggerEvent("ERR:" + regex
						+ " is not a valid regular expression.");
				break;
				// TODO: handle exception
			}
		}
		// System.out.println(" No!");
		return false;
	}

	/*
	 * old: String myFormElement = (Element) formsIterator.next(); if
	 * (myElement.getText().equals(myFormElement.getText())) { searchIndex =
	 * searchedElements.indexOf(myElement); actualElement = myElement; if
	 * (ignoreTagged) { Iterator oI = options.iterator(); boolean optionFound =
	 * false; while (oI.hasNext()) { if (myElement.getAttributeValue(attribute)
	 * == ((Element) oI .next()).getText()) { optionFound = true; break; } } if
	 * (optionFound == false) { found = true; return myElement; } } else if
	 * (showFirst) { Element mP = (Element) myElement.getParent(); if
	 * (mP.getChildren().indexOf(myElement) == 0) { found = true; return
	 * myElement; } } else { found = true; return myElement; } } }
	 */
	/**
	 * @return
	 */
	public String[][] getParentAttributes() {
		if (actualElement != null) {
			List pAttributes = ((Element) actualElement.getParent())
					.getAttributes();
			String[][] atts = new String[pAttributes.size()][2];
			Iterator aI = pAttributes.iterator();
			int count = 0;
			while (aI.hasNext()) {
				Attribute myAtt = (Attribute) aI.next();
				atts[count][0] = myAtt.getName();
				atts[count][1] = myAtt.getValue();
				count++;
			}
			return atts;
		} else {
			return null;
		}
	}

	public Object[][] getCurrentAttributes() {
		if (actualElement != null) {
			List pAttributes = actualElement.getAttributes();
			String[][] atts = new String[pAttributes.size()][2];
			Iterator aI = pAttributes.iterator();
			int count = 0;
			while (aI.hasNext()) {
				Attribute myAtt = (Attribute) aI.next();
				atts[count][0] = myAtt.getName();
				atts[count][1] = myAtt.getValue();
				count++;
			}
			return atts;
		} else {
			return null;
		}
	}

	public void updateOptions(boolean a, boolean t, boolean i, boolean f) {
		showAtts = a;
		showTags = t;
		ignoreTagged = i;
		showFirst = f;
	}

	/**
	 * @param name
	 */
	public void setXSL(String name) {
		docChanged = true;
		if (new File(name).getParent().equals(
				new File(inputFileName).getParent())) {
			String shortName = name.substring(new File(name).getParent()
					.length() + 1);
			name = shortName;
		}
		if (stylesheet != null) {
			stylesheet.setPseudoAttribute("href", name);
		} else {
			stylesheet = new ProcessingInstruction("xml-stylesheet",
					"type=\"text/xsl\" href=\"" + name + "\"");
			doc.getContent().add(0, stylesheet);
		}
	}

	/**
	 * @return Returns the docChanged.
	 */
	public boolean isDocChanged() {
		return docChanged;
	}

	/**
	 * @return
	 */
	public int getResultCount() {
		return getSearchCount();
	}

	public Set listeners = Collections.synchronizedSet(new HashSet());

	private JDomFSM fsm;

	private boolean useWordlist = true;

	public void addTaggerEventListener(TaggerEventListener t) {
		listeners.add(t);
	}

	public void removeTaggerEventListener(TaggerEventListener t) {
		listeners.remove(t);
	}

	public void fireTaggerEvent(String message) {
		TaggerEvent e = new TaggerEvent(this, message);
		listeners.remove(null);
		synchronized (listeners) {
			Iterator li = listeners.iterator();
			while (li.hasNext()) {
				((TaggerEventListener) li.next()).TaggerEventHappened(e);
			}
		}
	}

	/**
	 * @param text
	 */
	public void updateWordlist(String text) {
		System.out.println(text);
		wordlistExists = false;
		StringTokenizer st = new StringTokenizer(text, "\n\r\f");
		words.clear();
		while (st.hasMoreTokens()) {
			String myToken = st.nextToken();
			if (myToken.length() > 0) {
				wordlistExists = true;
				words.add(myToken);
			}
		}
		search();
	}

	public void optionsChanged() {
		System.out.println("optionsChanged");
		search();
	}

	/**
	 * changes the tagname that's been searched for
	 * 
	 * @param text
	 *            the name of the xpath
	 * @param search
	 */
	public void changeTag(String text, boolean search) {
		if (!xpath.equals(text)) {
			xpath = text;
			System.out.println("Tag changed to:" + xpath);
			if (search)
				search();
		} else {
			System.out.println("Tag NOT changed:" + xpath);
		}
	}

	/**
	 * @return Returns the xmlLoaded.
	 */
	public boolean isXmlLoaded() {
		return xmlLoaded;
	}

	/**
	 * @param xmlLoaded
	 *            The xmlLoaded to set.
	 */
	public void setXmlLoaded(boolean xmlLoaded) {
		this.xmlLoaded = xmlLoaded;
	}

	/*
	 * private static List choppedList(Text text) { List l = new ArrayList();
	 * StringTokenizer st = new StringTokenizer(text.getText(),"
	 * \t\n\r\f,.:",true); while (st.hasMoreTokens()) { String myToken =
	 * st.nextToken(); if (myToken.equals(".")) { } else if
	 * (myToken.equals(",")) { } Element w = new Element("w");
	 * w.setText(st.nextToken()); l.add(w); } return l; }
	 */
	private void chopElement(Element element) {
		System.out.println(element);
		List content = new ArrayList();
		for (Iterator i = element.removeContent().iterator(); i.hasNext();) {
			Object o = i.next();
			// System.out.println(o);
			if (o instanceof Element) {
				Element e = (Element) o;
				if (!fsm.tagsToIgnore().contains(e.getName())) {
					chopElement(e);
					content.add(e);
				} else {
					content.add(e);
				}
			} else if (o instanceof Text) {
				content.addAll(fsm.getOutput((Text) o));
			} else {
				content.add(o);
			}
		}
		element.setContent(content);
	}

	private void restoreIgnores(Element element) {
		List content = new ArrayList();
		for (Iterator i = element.removeContent().iterator(); i.hasNext();) {
			Object o = i.next();
			// System.out.println(o);
			if (o instanceof Element) {
				Element e = (Element) o;
				if (!fsm.tagsToIgnore().contains(e.getName())) {
					chopElement(e);
					content.add(e);
				} else {
					content.add(e);
				}
			} else if (o instanceof Text) {
				content.addAll(fsm.getOutput((Text) o));
			} else {
				content.add(o);
			}
		}
		element.setContent(content);
	}

	/**
	 * @param string
	 * 
	 */
	public void chopWords(String string) {
		try {
			fsm = new JDomFSM(new File(string));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(doc);
		for (Element e : (List<Element>) doc.getRootElement().getChildren()) {
			System.out.println(e);
		}

		System.out.println(doc.getRootElement());
		// System.out.println(doc);
		chopElement(doc.getRootElement().getChild("text"));

		restoreIgnores(doc.getRootElement().getChild("text"));
		search();
	}

	/**
	 * @param s
	 */
	public void number(String s) {
		String numTag = s.substring(0, s.indexOf(","));
		String attName = s.substring(s.indexOf(",") + 1, s.lastIndexOf(","));
		String numNum = s.substring(s.lastIndexOf(",") + 1);
		if ((numTag.length() > 0) && (numNum.length() > 0)
				&& (attName.length() > 0)) {
			System.out.println("-->" + numTag + "/" + numNum);
			int count = new Integer(numNum).intValue();
			String pattern = "";
			for (int i = 0; i < numNum.length(); i++) {
				pattern += "0";
			}
			DecimalFormat myFormatter = new DecimalFormat(pattern);
			Iterator ci = doc.getDescendants(new ElementFilter());
			while (ci.hasNext()) {
				Element myElement = (Element) ci.next();
				if (myElement.getName().equals(numTag)) {
					myElement.setAttribute(attName, myFormatter.format(count));
					System.out.println(myFormatter.format(count) + ":"
							+ myElement.getText());
					count++;
				}
			}
		}
	}

	/**
	 * @param s
	 */
	public void toolsSetAtt(String s) {
		String attTag = s.substring(0, s.indexOf(","));
		String attName = s.substring(s.indexOf(",") + 1, s.lastIndexOf(","));
		String attVal = s.substring(s.lastIndexOf(",") + 1);
		if ((attTag.length() > 0) && (attName.length() > 0)) {
			System.out.println("-->" + attTag + "/" + attName + "/" + attVal);
			Iterator ci = doc.getDescendants(new ElementFilter());
			while (ci.hasNext()) {
				Element myElement = (Element) ci.next();
				if (myElement.getName().equals(attTag)) {
					if ((attVal.equals(" ")) || (attVal == null)) {
						myElement.removeAttribute(attName);
					} else {
						myElement.setAttribute(attName, attVal);
						System.out.println(myElement.getText());
					}
				}
			}
		}
	}

	/**
	 * @param theId
	 * @return
	 */
	public Element getElementForId(String theId) {
		return (Element) ids.get(theId);
	}

	/**
	 * @param name
	 */
	public void addOption(TagOption t) {
		System.out.println("adding an option");
		options.add(t);
	}

	public void clearOptions() {
		options.clear();
	}

	/**
	 * @return Returns the wordlistFileName.
	 */
	public String getWordlistFileName() {
		return wordlistFileName;
	}

	/**
	 * @param name
	 */
	public void saveOptions(String name) {
		Document newDoc = new Document();
		newDoc.addContent(new Element("z2tagger"));
		Element root = newDoc.getRootElement();
		root.addContent(new Element("forms"));
		Iterator wi = words.iterator();
		while (wi.hasNext()) {
			Element form = new Element("form");
			form.setText((String) wi.next());
			root.getChild("forms").addContent(form);
		}
		Element tagE = new Element("xpath");
		tagE.setText(xpath);
		root.addContent(tagE);
		Element opts = new Element("options");
		Iterator oi = options.iterator();
		while (oi.hasNext()) {
			TagOption myTO = (TagOption) oi.next();
			Element op = new Element("option");
			op.setAttribute("att", myTO.getAttName());
			op.setAttribute("key", myTO.getKey());
			op.setAttribute("newVal", myTO.getNewVal());
			op.setAttribute("replaceVal", myTO.getReplaceVal());
			op.setAttribute("replace", (myTO.isReplace() ? "true" : "false"));
			opts.addContent(op);
		}
		root.addContent(opts);
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		XMLOutputter xo = new XMLOutputter();
		xo.setFormat(format);
		try {
			FileOutputStream fos = new FileOutputStream(new File(name));
			xo.output(newDoc, fos);
			fos.close();
		} catch (Exception e) {
			System.out.println("error saving");
		}
	}

	/**
	 * @param e
	 * @return
	 */
	public String outputForm(Element e) {
		actualElement = e;
		return (e == null ? "no (more) results." : parentFromFoundElement(e));
	}

	/**
	 * @return
	 */
	public String outputCurrentForm() {
		return (parentFromFoundElement(actualElement));
	}

	public void setWordlistEnabled(boolean b) {
		useWordlist = b;
		wordlistExists = (b ? (words.size() > 0) : true);

	}
}