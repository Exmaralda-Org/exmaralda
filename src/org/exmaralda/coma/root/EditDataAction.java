/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.root;

import java.awt.event.ActionEvent;

import org.exmaralda.coma.dialogs.EditLanguageDialog;
import org.exmaralda.coma.dialogs.EditLocationDialog;
import org.exmaralda.coma.dialogs.EditPeriodDialog;
import org.jdom.Element;

/**
 * @author woerner
 */

public class EditDataAction extends ComaAction {

	Coma coma;

	Object userObject;

	public EditDataAction(Coma c, Object object) {
		super(((Element) object).getName() + " " + Ui.getText("cmd.edit"), null, c);
		userObject = object;
		coma = c;
	}

	public void actionPerformed(ActionEvent e) {
		coma.status("@EditDataAction actionPerformed");
		if (userObject instanceof Element) {
			if (((Element) userObject).getName() == "Location") {
				System.out.println("***" + userObject);
				EditLocationDialog edl = new EditLocationDialog(coma, (Element) userObject, false);
			} else if (((Element) userObject).getName() == "Description") {
				//				EditDescriptionDialog edl = new EditDescriptionDialog(coma,
				//						(Element) userObject);
			} else if (((Element) userObject).getName() == "Language") {
				EditLanguageDialog edl = new EditLanguageDialog(coma, (Element) userObject, false);

			} else if (((Element) userObject).getName() == "Period") {
				EditPeriodDialog edl = new EditPeriodDialog(coma, (Element) userObject, false);
			}

		}
	}
}