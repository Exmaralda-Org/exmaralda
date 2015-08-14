/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.exmaralda.coma.helpers.GUID;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;

/**
 * coma2/org.sfb538.coma2.fileActions/newAction.java
 * 
 * @author woerner
 *  
 */
public class AddCommunicationAction extends ComaAction {
	public AddCommunicationAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.addCommunication"), icon, c);
		this.putValue(
			Action.ACCELERATOR_KEY,
			KeyStroke.getKeyStroke(
				KeyEvent.VK_N,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	public void actionPerformed(ActionEvent actionEvent) {
		int myIndex = 0;
		myIndex =
			(data.getDataElement()
				.getChildren()
				.indexOf(data.getDataElement().getChild("Communication"))
				+ data.getDataElement().getChildren().size());
		if (myIndex == 0) {
			myIndex =
				(data.getDataElement()
					.getChildren()
					.indexOf(data.getDataElement().getChild("Description"))
					+ 1);
			if (myIndex == 1) {
				myIndex =
					(data.getDataElement()
						.getChildren()
						.indexOf(data.getDataElement().getChild("Speaker")));
			}
		}
		if (myIndex == -1) {
			myIndex = 0;
		}
		
		Element newComm = new Element("Communication");
		String myId=new GUID().makeID();
		newComm.setAttribute("Id", myId);
		newComm.setAttribute("Name", "unnamed");
		newComm.addContent(new Element("Setting"));
		coma.status("myIndex"+myIndex);
		data.getDataElement().addContent(myIndex, newComm);
		coma.newCommunication(newComm);

		coma.status(
			"Communication added in "
				+ data.getDataElement().toString()
				+ " @ "
				+ myIndex);
	}
}
