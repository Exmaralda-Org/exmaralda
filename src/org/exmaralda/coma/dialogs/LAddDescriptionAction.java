/*
 * Created on 29.04.2004 by woerner
 */
package org.exmaralda.coma.dialogs;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;

/**
 * coma2/org.sfb538.coma2.personActions/PAddLocationAction.java * @author woerner
 */

public class LAddDescriptionAction extends ComaAction {

	Element elmt;

	public LAddDescriptionAction(Coma c, ImageIcon icon, Element e) {
		super(Ui.getText("cmd.addDescription"), icon, c);
		coma = c;
		elmt = e;
	}
	/**
	 * @param coma
	 * @param object
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		coma.status("@LAddDescriptionnAction");
		coma.addDescriptionToLocation(elmt);
	}}
