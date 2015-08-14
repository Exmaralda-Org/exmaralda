package org.exmaralda.sextanttagger.types;

import java.awt.event.KeyEvent;

public class KeyboardShortcut {

	public static String getLabel(String shortcutString) {
		String label = "";
		int modifier = new Integer(shortcutString.split(";")[1]).intValue();
		int skey = new Integer(shortcutString.split(";")[0]).intValue();
		if (modifier + skey > 0) {
			if (modifier < 1) {
				label = KeyEvent.getKeyText(skey);
			} else {
				label = KeyEvent.getKeyModifiersText(modifier) + "+"
						+ KeyEvent.getKeyText(skey);
			}
		}
		return label;
	}
}
