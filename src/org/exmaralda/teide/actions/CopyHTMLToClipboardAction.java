/**
 * 
 */
package org.exmaralda.teide.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.exmaralda.teide.ui.Loc;
import org.exmaralda.teide.ui.TeideUI;

/**
 * @author woerner
 * 
 */
public class CopyHTMLToClipboardAction extends AbstractAction {

	/**
	 * 
	 */
	public CopyHTMLToClipboardAction(TeideUI teideUI) {
		super(Loc.getText("command.copyResultToClipboard"), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		System.out.println("html2clipboard");

	}

}
