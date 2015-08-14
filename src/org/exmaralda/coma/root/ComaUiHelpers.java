/*
 * Created on 29.06.2004 by woerner
 */
package org.exmaralda.coma.root;

import java.awt.Dimension;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

/**
 * coma2/org.sfb538.coma2/ComaUiHelpers.java
 * 
 * @author woerner
 * 
 */
public class ComaUiHelpers {
	static Preferences prefs = Ui.prefs;

	/**
	 * 
	 */
	public ComaUiHelpers() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void sizeIt(JComponent c, int width, int heigth) {
		Dimension myDimension = new Dimension(width, heigth);
		c.setMaximumSize(myDimension);
		c.setMinimumSize(myDimension);
		c.setPreferredSize(myDimension);
	}

	public static void btnSize(JButton[] btn, int width, int heigth) {
		for (int i = 0; i < btn.length; i++) {
			sizeIt(btn[i], width, heigth);
			btn[i].setHorizontalAlignment(SwingConstants.LEFT);
			btn[i].setToolTipText(btn[i].getLabel());
		}
	}

	/**
	 * @param xmlFile
	 */

	/**
	 * public static void setRecent(File xmlFile) {
	 * prefs.put("lastopened",xmlFile.getAbsolutePath()); }
	 * 
	 * public static String getRecent() { return prefs.get("lastopened",null); }
	 */
}
