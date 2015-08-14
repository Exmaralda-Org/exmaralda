/**
 * 
 */
package org.exmaralda.coma.dialogs;

import javax.swing.JOptionPane;

/**
 * @author woerner
 * 
 */
public class ComaError {

    public ComaError(String title, String err, String debug) {
	JOptionPane.showMessageDialog(null, "<html><b>" + err + "</b><br/>"
		+ debug + "</html>", title, JOptionPane.ERROR_MESSAGE);
    }

}
