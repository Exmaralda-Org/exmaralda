package org.exmaralda.common.helpers;

import java.awt.Color;

/**
 * @author kai
 *
 */
/**
 * @author kai
 *
 */
public class ColorHelper {
	
	/**
	 * creates a html-color-string for a given java-color
	 * @param color
	 * @return
	 */
	public static String getHTMLColor(Color color) {
		String colString = "#";
		colString += ((Integer.toString(color.getRed(), 16)).length() == 1 ? "0"
				+ Integer.toString(color.getRed(), 16)
				: Integer.toString(color.getRed(), 16));
		colString += ((Integer.toString(color.getGreen(), 16)).length() == 1 ? "0"
				+ Integer.toString(color.getGreen(), 16)
				: Integer.toString(color.getGreen(), 16));
		colString += ((Integer.toString(color.getBlue(), 16)).length() == 1 ? "0"
				+ Integer.toString(color.getBlue(), 16)
				: Integer.toString(color.getBlue(), 16));
		return colString;
	}

	
	/**
	 * generates a color for a given index out of a number of elements
	 * @param index
	 * @param max
	 * @return
	 */
	public static Color getColorForIndex(int index,int max) {
		return Color.getHSBColor(new Float(new Float(index)
				/ new Float(max)), 1, 1);
	}
}
