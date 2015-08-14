/*
 * Created on 21.06.2004 by woerner
 */
package org.exmaralda.coma.root;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * coma2/org.sfb538.coma2/ComaXMLOutputter.java
 * 
 * @author woerner
 *  
 */
public class ComaXMLOutputter extends XMLOutputter {

	/**
	 * only used constructor. sets utf8-pretty-format for the outputter
	 */
	public ComaXMLOutputter() {
		super();
		Format format = Format.getRawFormat();
		format.setEncoding("UTF-8");
		this.setFormat(format);
	}
}