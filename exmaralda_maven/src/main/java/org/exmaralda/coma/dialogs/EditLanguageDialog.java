/*
 * Created on 31.03.2004 by woerner
 */
package org.exmaralda.coma.dialogs;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.exmaralda.coma.datatypes.Description;
import org.exmaralda.coma.datatypes.Language;
import org.exmaralda.coma.helpers.ComaHTML;
import org.exmaralda.coma.helpers.ISOLanguageCodeHelper;
import org.exmaralda.coma.helpers.JSuggestField;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;

public class EditLanguageDialog extends JDialog implements ActionListener {
	boolean newLanguage;

	Coma coma;

	Language lang;

	Element elmt;

	JTable myTable;

	private JTextField languageCode;

	private JTextField typeField;

	private InnerDescriptionPanel descriptionPanel;

	private JSuggestField languageNamesuggestField;

	private String oldCode;

	/**
	 * 
	 * @param c
	 *            Coma
	 * @param e
	 *            Element
	 * @param l
	 *            New Language?
	 */

	public EditLanguageDialog(Coma c, Element e, boolean n) {
		super(c, true);
		// lang = new Language(e, coma);
		newLanguage = n;
		coma = c;
		// lang = l;
		elmt = e;

		setParms();
		drawUI();
	}

	private void setParms() {
		this.setResizable(false);
		this.setLocationRelativeTo(coma);
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setTitle("Edit Language");
		oldCode = (elmt.getChildText("LanguageCode") != null ? elmt
				.getChildText("LanguageCode") : "");
	}

	private void drawUI() {
		this.getContentPane().setLayout(new BorderLayout());
		JPanel inputPanel = new JPanel();
		JPanel okCancelPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(0, 2));
		languageCode = new LowerCaseField(
				(elmt.getChildText("LanguageCode") != null ? elmt
						.getChildText("LanguageCode") : ""), 15);
		languageCode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				languageNamesuggestField.setText(ISOLanguageCodeHelper
						.getLanguageName(languageCode.getText()));
			}
		});
		typeField = new JTextField(
				(elmt.getAttributeValue("Type") != null ? elmt
						.getAttributeValue("Type") : ""));
		JButton lookupCodeButton = new JButton(Ui
				.getText("cmd.lookupLanguageCode"));
		lookupCodeButton.addActionListener(this);
		lookupCodeButton.setActionCommand("lookupCode");
		inputPanel.add(new JLabel("ISO 639-3 Code"));
		inputPanel.add(languageCode);
		languageNamesuggestField = new JSuggestField(this,
				ISOLanguageCodeHelper.getLanguageNames());
		languageNamesuggestField.addSelectionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setCode();

			}

		});
		languageNamesuggestField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setCode();

			}
		});
		inputPanel.add(new JLabel(Ui.getText("name")));
		inputPanel.add(languageNamesuggestField);

		// inputPanel.add(lookupCodeButton);
		inputPanel.add(new JLabel("Type"));
		inputPanel.add(typeField);

		descriptionPanel = new InnerDescriptionPanel(new Description(elmt
				.getChild("Description"), coma), false);

		this.getContentPane().add(inputPanel, BorderLayout.NORTH);
		this.getContentPane().add(descriptionPanel, BorderLayout.CENTER);
		this.getContentPane().add(okCancelPanel, BorderLayout.SOUTH);
		JButton okButton = new JButton(Ui.getText("OK"));
		okButton.addActionListener(this);
		okButton.setActionCommand("OK");
		JButton cancelButton = new JButton(Ui.getText("cancel"));
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("cancel");
		JButton deleteButton = new JButton(Ui.getText("cmd.deleteLanguage"));
		deleteButton.addActionListener(this);
		deleteButton.setActionCommand("delete");
		okCancelPanel.add(okButton);
		okCancelPanel.add(cancelButton);
		okCancelPanel.add(deleteButton);
		// this.getContentPane().add(new JButton("ASDF"));

		languageCode.selectAll();
		this.pack();
		this.setLocationRelativeTo(coma);
		this.setVisible(true);
	}

	protected void setCode() {
		languageCode.setText(ISOLanguageCodeHelper
				.getLanguageCode(languageNamesuggestField.getText()));
	}

	public void actionPerformed(ActionEvent evt) {
		if ("cancel".equals(evt.getActionCommand())) {
			if (newLanguage == true) {
				elmt.getParent().removeContent(elmt);
			}
			this.setVisible(false);
			this.dispose();
			coma.updateValueDisplay();
		} else if ("OK".equals(evt.getActionCommand())) {
			if (languageCode.getText().length() < 1) {
				JOptionPane.showMessageDialog(this,
						"Language code must not be empty.");
			} else {
				coma.status(elmt.getChild("LanguageCode").getText());
				elmt.getChild("LanguageCode").setText(languageCode.getText().toLowerCase());
				if (typeField.getText().length() > 0) {
					elmt.setAttribute("Type", typeField.getText());
				}

				Element panelDescription = descriptionPanel
						.getDataAsNewElement();
				if (elmt.getChild("Description") != null) {
					elmt.removeChild("Description");
				}
				elmt.addContent(panelDescription);

				this.setVisible(false);
				this.dispose();
				coma.updateValueDisplay();
			}
		} else if ("delete".equals(evt.getActionCommand())) {
			elmt.getParent().removeContent(elmt);
			this.setVisible(false);
			this.dispose();
			coma.updateValueDisplay();
		} else if ("lookupCode".equals(evt.getActionCommand())) {
			coma.status("Enthnologue");
			String url = "";
			if (languageCode.getText().length() >= 3) {
				url = ComaHTML.ENTHOLOGUE_LOOKUP
						+ languageCode.getText().substring(0, 3).toLowerCase();
			} else {
				url = ComaHTML.ENTHOLOGUE_INDEX;
			}
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException err) {
				err.printStackTrace();
			} catch (URISyntaxException err) {
				err.printStackTrace();
			}
		} else if ("delete".equals(evt.getActionCommand())) {
			// hä?
		} else if ("editDescription".equals(evt.getActionCommand())) {
			/*
			 * if (lang.getDescription() == null) { lang.setDescription(new
			 * Description(coma)); } if (elmt.getChild("Description") == null) {
			 * Element newDesc = new Element("Description");
			 * elmt.addContent(elmt.getContentSize(), newDesc);
			 * coma.status("keine description in der language, depp!"); }
			 * coma.status(lang); coma.status(lang.getDescription());
			 * EditDescriptionDialog edl = new EditDescriptionDialog(coma, lang
			 * .getDescription());
			 */}
	}
}

/**
 * UpperCaseField: A JTextField that can only hold uppercase characters
 * 
 * @author woerner
 * 
 */
class LowerCaseField extends JTextField {
	public LowerCaseField(int cols) {
		super(cols);
	}

	@Override
	protected Document createDefaultModel() {
		return new UpperCaseDocument();
	}

	static class UpperCaseDocument extends PlainDocument {
		@Override
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			if (str == null) {
				return;
			}
			char[] upper = str.toCharArray();
			for (int i = 0; i < upper.length; i++) {
				upper[i] = Character.toLowerCase(upper[i]);
			}
			super.insertString(offs, new String(upper), a);
		}
	}

	public LowerCaseField() {
		super();
	}

	public LowerCaseField(String arg0) {
		super(arg0);
	}

	public LowerCaseField(String arg0, int arg1) {
		super(arg0, arg1);
	}

	public LowerCaseField(Document arg0, String arg1, int arg2) {
		super(arg0, arg1, arg2);
	}
}