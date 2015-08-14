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
public class PublishCorpusAction extends AbstractAction {
	TeideUI teide;

	/**
	 *
	 */
	public PublishCorpusAction(TeideUI teide) {
		super("**" + Loc.getText("command.publishCorpus"), null);
		this.teide = teide;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		teide.publishCorpusX();

	}

}
