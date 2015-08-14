/**
 * 
 */
package org.exmaralda.teide;

import java.util.HashMap;
import java.util.prefs.Preferences;

/**
 * @author woerner
 *
 */
public class Teide {
	private HashMap parms;

	private static Preferences prefs = Preferences.userRoot().node(
			"org.exmaralda.teide");

	public Teide() {

	}
}
