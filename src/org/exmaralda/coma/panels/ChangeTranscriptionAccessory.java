/**
 *
 */
package org.exmaralda.coma.panels;

import javax.swing.JCheckBox;

import org.exmaralda.coma.root.Ui;

/**
 * @author woerner
 * 
 */
public class ChangeTranscriptionAccessory extends JCheckBox {

	/**
	 * 
	 */
	public ChangeTranscriptionAccessory() {
		super(Ui.getText("cmd.updateTranscriptionDescriptions"));
		this.setSelected(Ui.prefs.getBoolean("option.changeTranscription", true));
		this.setEnabled(false);
	}

	@Override
	public boolean isSelected() {
		return super.isSelected();
	}

}
