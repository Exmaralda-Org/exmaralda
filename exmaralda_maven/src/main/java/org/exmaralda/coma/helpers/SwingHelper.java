/**
 * 
 */
package org.exmaralda.coma.helpers;

import java.awt.Dimension;

import javax.swing.JComponent;

/**
 * @author woerner
 *
 */
public class SwingHelper {

	public static void sizeIt(JComponent c, int width, int height) {
		if (height<0) {
			height = c.getPreferredSize().height;
		}
		Dimension myDimension = new Dimension(width, height);
		c.setMaximumSize(myDimension);
		c.setMinimumSize(myDimension);
		c.setPreferredSize(myDimension);
	}
}
