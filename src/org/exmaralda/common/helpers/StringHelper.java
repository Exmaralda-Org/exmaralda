package org.exmaralda.common.helpers;

/**
 * @author kai
 *
 */
public class StringHelper {
	/**
	 * strips a given Character from a String
	 * 
	 * @param s	The input string
	 * @param charToStrip	the character to strip (as a String)
	 * @return	the string without the stripped character
	 */
	public static String strip(String s, String charToStrip) {
		String result = "";
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != charToStrip.charAt(0)) {
				result += s.charAt(i);
			}
		}
		return result;
	}

}
