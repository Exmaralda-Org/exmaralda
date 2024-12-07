/**
 * 
 */
package org.exmaralda.coma.datatypes;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

/**
 * @author woerner
 *
 */
public class ComaTemplate {
	HashMap<String, String> data;

	String name;

	String xPath;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6336901013229876439L;

	public ComaTemplate(String name, String xPath, Vector<String> data) {
		this.name = name;
		this.xPath = xPath;
		for (String s : data) {
			this.data.put(s, "");
		}
	}

	public ComaTemplate(String name, String xPath, HashMap<String, String> data) {
		this.name = name;
		this.xPath = xPath;
		this.data = data;

	}

	public TreeMap<String, String> getData() {
		return new TreeMap<String, String>(data);
	}

}
