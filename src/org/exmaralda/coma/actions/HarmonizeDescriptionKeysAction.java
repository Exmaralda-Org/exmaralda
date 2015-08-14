/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.TreeMap;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

import org.exmaralda.coma.dialogs.KeyMapperDialog;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;
import org.jdom.xpath.XPath;

/** coma2/org.sfb538.coma2.fileActions/newAction.java
 * 
 * @author woerner */
public class HarmonizeDescriptionKeysAction extends ComaAction {
	Style style;
	StyledDocument doc;
	JTextPane textPane;

	public HarmonizeDescriptionKeysAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.HarmonizeDescriptionKeys"), icon, c);
		this.putValue(Action.SHORT_DESCRIPTION,
				Ui.getText("cmd.HarmonizeDescriptionKeys"));
	}

	public HarmonizeDescriptionKeysAction(Coma c) {
		this(c, null);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		TreeMap<String, String> keyMap = new TreeMap<String, String>();
		XPath xp;
		try {
			xp = XPath.newInstance("//Key[not(starts-with(@Name,'#'))]");
			// xp = XPath.newInstance("//Key]");
			List<Element> keys = xp.selectNodes(coma.getData().getDocument()
					.getRootElement());
			if (keys.size() > 0) {

				KeyMapperDialog kmd = new KeyMapperDialog(coma);
				kmd.setLocationRelativeTo(coma);
				kmd.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(coma,
						Ui.getText("err.noDescriptionKeys"));
			}

		} catch (Exception ex) {
			System.out.println("fail");
			JOptionPane.showMessageDialog(coma,
					Ui.getText("err.noDescriptionKeys"));
		}
	}

}