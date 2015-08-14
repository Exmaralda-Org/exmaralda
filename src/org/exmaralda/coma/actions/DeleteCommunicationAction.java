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
import org.exmaralda.coma.root.Ui;

/**
 * coma2/org.sfb538.coma2.fileActions/newAction.java
 * @author woerner
 * 
 */
public class DeleteCommunicationAction extends ComaAction {
	public DeleteCommunicationAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.DelCommunication"), icon,c);
		this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));        
	}

	public void actionPerformed(ActionEvent actionEvent) {
		System.out.println(">>"+data.getDataElement().toString());
		System.out.println("delete communications");
//		coma.xmlChanged=true;
	}
	
}
