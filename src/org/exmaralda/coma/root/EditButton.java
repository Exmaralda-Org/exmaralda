/*
 * Created on 16.03.2004 by woerner
 */
package org.exmaralda.coma.root;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * coma2/org.sfb538.coma2/EditType.java * @author woerner
 */

public class EditButton extends JButton {
	private Object userObject;


	/**
	 * 
	 */
	public EditButton() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public EditButton(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public EditButton(String arg0, Icon arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public EditButton(Action arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public EditButton(Icon arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param data
	 * @param object
	 */
	public Object getUserObject() {
		return this.userObject;
	}

	/**
	 * @param userObject The userObject to set.
	 */
	public void setUserObject(Object uO) {
		this.userObject = uO;
	}

}
