/*
 * Created on 31.03.2004 by woerner
 */
package org.exmaralda.coma.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.exmaralda.coma.datatypes.Description;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;

/**
 * coma2/org.sfb538.coma2.personActions/EditLocationDialog.java
 * 
 * @author woerner
 */
public class EditDescriptionDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5871060263566840029L;

	String popupKey;

	String popupValue;

	Coma coma;

	Element elmt;

	JTable myTable;

	private Description description;

	protected String largeKey;

	private InnerDescriptionPanel dp;

	public EditDescriptionDialog(Coma c, Description d) {
		super(c, true);
		this.setTitle((d.getElement().getParentElement() == null)
				? "Description"
				: d.getElement().getParentElement().getName() + "/Description");
		coma = c;
		description = d;
		JPanel p = new JPanel(new BorderLayout());
		dp = new InnerDescriptionPanel(d, true);
		p.add(dp, BorderLayout.CENTER);
		p.add(getOKPanel(), BorderLayout.SOUTH);
		add(p);
		this.pack();
		this.setLocationRelativeTo(c);
		this.setVisible(true);
	}

	/**
	 * b
	 * 
	 * @return
	 */
	private JPanel getOKPanel() {
		JPanel okp = new JPanel();
		JButton okButton = new JButton(Ui.getText("OK"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				done(true);
			}
		});
		JButton cancelButton = new JButton(Ui.getText("cancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				done(false);
			}
		});
		okp.add(okButton);
		okp.add(cancelButton);
		return okp;
	}

	private void done(boolean save) {
		if (save) {
			dp.stopEditing();
			description.setContent(dp.getDataAsTreemap());
			coma.xmlChanged();
		}
		this.setVisible(false);
		this.dispose();
	}

}