/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.root;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Action;
import javax.swing.KeyStroke;

/**
 * coma2/org.sfb538.coma2.fileActions/newAction.java
 * 
 * @author woerner
 * 
 */
public class HelpAction extends ComaAction {

	public HelpAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.help"), icon, c);
		this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_F1, 0));
	}

	public HelpAction(Coma c) {
		this(c, null);
	}

	public void actionPerformed(ActionEvent actionEvent) {

		try {
			Desktop.getDesktop().browse(new URI("http://www.exmaralda.org/files/comadoku.pdf"));
		} catch (IOException err) {
			err.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}