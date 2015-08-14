/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.exmaralda.coma.helpers.GUID;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.ComaElement;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;

/**
 * coma2/org.sfb538.coma2.fileActions/newAction.java
 * 
 * @author woerner
 *  
 */
public class AddPersonAction extends ComaAction {
	public AddPersonAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.AddPerson"), icon, c);
		this.putValue(
			Action.ACCELERATOR_KEY,
			KeyStroke.getKeyStroke(
				KeyEvent.VK_N,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	public void actionPerformed(ActionEvent actionEvent) {
		coma.status("@Add Person");
		int myIndex = 0;
		myIndex = data.getDataElement().getChildren().size();
		ComaElement newSpk = new ComaElement("Speaker");
		String myId = new GUID().makeID();
		newSpk.setAttribute("Id", myId);
		Element tmpElement = new Element("Sigle");
		String input =
			(String) JOptionPane.showInputDialog(
				coma,
				Ui.getText("inputsigle"),
				Ui.getText("inputsigle"),
				JOptionPane.PLAIN_MESSAGE,
				null,
				null,
				"");
		tmpElement.setText(input);
		newSpk.addContent(tmpElement);
		data.getDataElement().addContent(newSpk);
		coma.nowSpeaker = newSpk;
		coma.newSpeaker();

		coma.status(
			"Person added in " + data.getDataElement().toString() + " @ " + myIndex);
		
//		EditDescriptionWindow edw = new EditDescriptionWindow();
	}

}
