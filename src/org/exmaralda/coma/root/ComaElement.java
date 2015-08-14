/*
 * Created on 10.03.2004 by woerner
 */
package org.exmaralda.coma.root;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * coma2/org.sfb538.coma2/ComaElement.java
 * 
 * @author woerner
 *  
 */
public class ComaElement extends Element {

	/**
	 *  
	 */
	public ComaElement() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public ComaElement(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ComaElement(String arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public ComaElement(String arg0, String arg1, String arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ComaElement(String arg0, Namespace arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		if (this.name=="Speaker") {
			return (this.getChild("Sigle").getText());
		} else {
			return this.name;
		}
	}

}
