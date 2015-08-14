/**
 *
 */
package org.exmaralda.teide.ui;

import java.io.File;
import java.io.FileFilter;
import java.util.TreeSet;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/**
 * @author woerner
 *
 */
public class TEIDEFileFilter implements FileFilter {
	boolean onlyXML = true;

	boolean onlyHighest;

	String rootElement = "";

	/**
	 *
	 */
	public TEIDEFileFilter() {
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		if (!(f.isDirectory())) {
			boolean a = true;
			if (onlyXML) {
				if (!(f.getName().toLowerCase().endsWith(".xml")))
					return false;
			}
			if (onlyHighest) {
				String myName = f.getName();
				int lastDot = myName.lastIndexOf(".");
				if (lastDot == 0)
					lastDot = myName.length();
				TreeSet<String> brothers = new TreeSet<String>();
				for (String brother : f.getParentFile().list()) {
					if (brother.length() == myName.length()) {
						if (brother.substring(0, lastDot - 2).equals(
								myName.substring(0, lastDot - 2))) {
							brothers.add(brother);
						}
					}
				}
				String highest = brothers.last();
				if (!highest.equals(myName)) {
					return false;
				}
			}
			if (rootElement.length() > 0) {
				try {
					SAXBuilder builder = new SAXBuilder(
							"org.apache.xerces.parsers.SAXParser");
					builder
							.setFeature(
									"http://xml.org/sax/features/external-parameter-entities",
									true);
					builder
							.setFeature(
									"http://xml.org/sax/features/external-general-entities",
									true);

					Document xml = builder.build(f);
					Element root = xml.getRootElement();
					Namespace ns = root.getNamespace();
					if (!root.getName().matches(rootElement)) {
						return false; // root element doesn't match
					}
				} catch (Exception e) {
					return false; //xml error
				}

			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
		return "Accepts " + (onlyHighest ? "only highest" : "all versions")
				+ ", " + (onlyXML ? "only XML-files" : "all files") + ".";
	}

	public boolean setOnlyHighest(boolean oh) {
		boolean roh = onlyHighest;
		onlyHighest = oh;
		return roh;
	}

	public boolean setOnlyXML(boolean ox) {
		boolean rox = onlyXML;
		onlyXML = ox;
		return rox;
	}

	public String setRootElement(String newRoot) {
		String oR = rootElement;
		rootElement = newRoot;
		return oR;
	}

}
