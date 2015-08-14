/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.IconFactory;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;

/**
 * coma2/org.sfb538.coma2.fileActions/newAction.java
 * @author woerner
 *
 */
public class AnonymizeAction extends ComaAction {
	public AnonymizeAction(Coma c) {
		super(Ui.getText("cmd.Anonymize"), IconFactory
				.createImageIcon("images/anonym.png"), c);
		this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_N, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
	}

	public void actionPerformed(ActionEvent actionEvent) {
		System.out.println(">>" + data.getDataElement().toString());
		System.out.println("delete person");
		String listElmtToRemove = coma.speakerElmt.getChild("Sigle").getText();
		coma.status("@" + listElmtToRemove);
		Element elmt = coma.speakerElmt;
		data.getDataElement().removeContent(coma.speakerElmt);
		//coma.speakerRemoved(elmt);
		//		coma.xmlChanged=true;
	}

}
