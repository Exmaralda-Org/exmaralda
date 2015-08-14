/**
 *
 */
package org.exmaralda.teide;

import org.exmaralda.teide.ui.TeideUI;

/**
 * @author woerner
 * 
 */
public class Launcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Teide t = new Teide();
		TeideUI teide = new TeideUI();
		teide.setVisible(true);

	}

}
