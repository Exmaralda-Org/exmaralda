/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;

import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.Ui;

/**
 * coma2/org.sfb538.coma2.fileActions/newAction.java
 * 
 * @author woerner
 * 
 */
public class UpdateRecordingsAction extends ComaAction {
	Style style;
	StyledDocument doc;
	JTextPane textPane;

	public UpdateRecordingsAction(Coma c,
			javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.extractRecordings"), icon, c);
	}

	public UpdateRecordingsAction(Coma c) {
		this(c, null);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		coma.updateRecordings();	}

	/**
	 * @param string
	 */
	protected void changeStyle(String string) {
		StyleConstants.setFontFamily(style, string);
		textPane.setParagraphAttributes(style, true);

	}
}