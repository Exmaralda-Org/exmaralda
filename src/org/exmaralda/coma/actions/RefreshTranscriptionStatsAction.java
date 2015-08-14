/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.Ui;

/**
 * coma2/org.sfb538.coma2.fileActions/newAction.java
 * 
 * @author woerner
 * 
 */
public class RefreshTranscriptionStatsAction extends ComaAction {
	public RefreshTranscriptionStatsAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.updateTranscriptionDescriptions"), icon, c);
	}

	public RefreshTranscriptionStatsAction(Coma c) {
		this(c, null);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		coma.refreshTranscriptionStats();
	}

}
