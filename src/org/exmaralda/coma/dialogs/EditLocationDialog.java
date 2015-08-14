/**
 * 
 */
package org.exmaralda.coma.dialogs;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.exmaralda.coma.datatypes.Description;
import org.exmaralda.coma.helpers.ComaDateTime;
import org.exmaralda.coma.helpers.ComaXML;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.helpers.DurationHelper;
import org.exmaralda.common.helpers.DurationInputter;
import org.jdom.Element;

import com.toedter.calendar.JDateChooser;

/**
 * @author woerner
 * 
 */
public class EditLocationDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Element editingElement;

	private Coma coma;

	private Element element;

	private boolean context; // communication = true, speaker = false

	private JTextField streetTF;

	private JTextField cityTF;

	private JTextField postalTF;

	private JTextField countryTF;

	private JDateChooser periodStart;

	private DurationInputter periodDuration;

	private InnerDescriptionPanel descriptionPanel;

	private JComboBox templateCombo;

	private JTextField typeTF;

	private JCheckBox periodExactCheckBox;

	public EditLocationDialog(Coma c, Element locationElement, boolean isNew) {
		super(c, true);
		element = locationElement;
		if (element.getChild("Description") == null) {
			element.addContent(new Element("Description"));
		}
		editingElement = (Element) locationElement.clone();
		context = ComaXML.getContext(locationElement);
		coma = c;
		this.setTitle(Ui.getText("dialog.editLocation"));
		this.getRootPane().putClientProperty("Window.style", "small");
		layoutPanel();
		fillForm(editingElement);
		this.pack();
		this.setLocationRelativeTo(coma);
		this.setVisible(true);
	}

	private void layoutPanel() {
		this.getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		JPanel templatePanel = initTemplatePanel();
		JPanel valuesPanel = initValuesPanel();
		descriptionPanel = new InnerDescriptionPanel(new Description(
				element.getChild("Description"), coma), false);
		this.add(templatePanel);
		this.add(valuesPanel);
		this.add(descriptionPanel);
		this.add(getButtonsPanel());

	}

	/**
	 * @return
	 */
	private JPanel getButtonsPanel() {
		JPanel bp = new JPanel(new FlowLayout());
		JButton okButton = new JButton(Ui.getText("OK"));
		okButton.addActionListener(this);
		okButton.setActionCommand("OK");
		JButton cancelButton = new JButton(Ui.getText("cancel"));
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("cancel");
		JButton deleteButton = new JButton(Ui.getText("cmd.deleteLocation"));
		deleteButton.addActionListener(this);
		deleteButton.setActionCommand("delete");
		bp.add(okButton);
		bp.add(cancelButton);
		bp.add(deleteButton);
		return bp;
	}

	/**
	 * @return
	 */
	private JPanel initValuesPanel() {
		JPanel vp = new JPanel(new GridLayout(0, 2));
		vp.add(new JLabel("Type"));
		vp.add(typeTF = new JTextField());
		vp.add(new JLabel("Street"));
		vp.add(streetTF = new JTextField());
		vp.add(new JLabel("City"));
		vp.add(cityTF = new JTextField());
		vp.add(new JLabel("PostalCode"));
		vp.add(postalTF = new JTextField());
		vp.add(new JLabel("Country"));
		vp.add(countryTF = new JTextField());
		vp.add(new JLabel(Ui.getText("PeriodStart")));
		periodStart = new JDateChooser();
		periodStart.setDateFormatString("dd.MM.yyyy HH:mm");
		JPanel pstart = new JPanel();
		pstart.setLayout(new BoxLayout(pstart, BoxLayout.X_AXIS));
		pstart.setAlignmentX(JComponent.LEFT_ALIGNMENT);

		pstart.add(periodStart);
		vp.add(pstart);
		vp.add(new JLabel(Ui.getText("PeriodExact")));
		vp.add(periodExactCheckBox = new JCheckBox());
		vp.add(new JLabel(Ui.getText("PeriodDuration")));

		// PeriodDuration
		periodDuration = new DurationInputter();
		periodDuration.setToolTipText(DurationHelper.FORMAT_STRING);
		vp.add(periodDuration);
		return vp;
	}

	/**
	 * @return
	 */
	private JPanel initTemplatePanel() {
		JPanel templatePanel = new JPanel(new FlowLayout());
		templatePanel.add(new JLabel(Ui.getText("templates")));
		templateCombo = coma.getData().getTemplates()
				.getTemplatesCombo(element);
		templateCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (((JComboBox) e.getSource()).getSelectedIndex() > 0) {
					applyTemplate(coma
							.getData()
							.getTemplates()
							.getTemplateElement(
									context,
									(String) ((JComboBox) e.getSource())
											.getSelectedItem()));
				}
			}

		});
		templatePanel.add(templateCombo);
		JButton templateAddButton = coma.getData().getTemplates()
				.getAddTemplatesButton();
		templateAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createTemplate();
			}

		});
		templatePanel.add(templateAddButton);
		JButton templateRemoveButton = coma.getData().getTemplates()
				.getRemoveTemplateButton();
		templateRemoveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeTemplate();
			}

		});
		templatePanel.add(templateRemoveButton);
		return templatePanel;
	}

	/**
	 * 
	 */
	protected void removeTemplate() {
		if (templateCombo.getSelectedIndex() > 0) {
			coma.getData().getTemplates()
					.removeTemplate(templateCombo.getSelectedItem(), context);
			int index = templateCombo.getSelectedIndex();
			templateCombo.setSelectedIndex(0);
			templateCombo.removeItemAt(index);
		}
	}

	/**
	 * 
	 */
	protected void createTemplate() {
		makeElement();
		coma.getData().getTemplates()
				.addTemplate(editingElement, coma, context);
	}

	private void applyTemplate(Element templateElement) {
		fillForm(templateElement);
	}

	/**
	 * @param templateElement
	 */
	private void fillForm(Element fe) {
		if (fe.getAttributeValue("Type") != null) {
			typeTF.setText(fe.getAttributeValue("Type"));
		}
		streetTF.setText(fe.getChildText("Street"));
		cityTF.setText(fe.getChildText("City"));
		postalTF.setText(fe.getChildText("PostalCode"));
		countryTF.setText(fe.getChildText("Country"));
		if (fe.getChild("Period") == null) {
			fe.addContent(new Element("Period"));
		}
		Element period = fe.getChild("Period");
		if (period.getChildText("PeriodStart") != null) {

			periodStart.setDate(ComaDateTime.dateFromXSDateTime(period
					.getChildText("PeriodStart")));
		} else {
			periodStart.setDate(null);
		}
		if (period.getChild("PeriodExact") != null) {
			periodExactCheckBox.setSelected(period.getChildText("PeriodExact")
					.equals("true"));
		}
		long showDuration;
		try {
			showDuration = new Long(
					period.getChildText("PeriodDuration") == null ? "0"
							: period.getChildText("PeriodDuration"));
		} catch (NumberFormatException ex) {
			showDuration = new Long("0");
		}
		periodDuration.setText(showDuration);
		descriptionPanel.updateTableModel(new Description(fe
				.getChild("Description"), coma));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		if ("cancel".equals(evt.getActionCommand())) {
			cancel();
		} else if ("OK".equals(evt.getActionCommand())) {
			makeElement();
			element.getParentElement().addContent(editingElement);
			element.getParentElement().removeContent(element);
			// todo
			coma.xmlChanged();
			this.setVisible(false);
			this.dispose();
			coma.updateValueDisplay();
		}

		else if ("delete".equals(evt.getActionCommand())) {
			element.getParentElement().removeContent(element);
			coma.xmlChanged();
			this.setVisible(false);
			this.dispose();
			coma.updateValueDisplay();
		}
	}

	/**
	 * 
	 */
	private void makeElement() {
		if (typeTF.getText().length() > 0) {
			editingElement.setAttribute("Type", typeTF.getText());
		} else {
			editingElement.removeAttribute("Type");
		}
		if (streetTF.getText().length() > 0) {
			if (editingElement.getChild("Street") == null) {
				editingElement.addContent(new Element("Street"));
			}
			editingElement.getChild("Street").setText(streetTF.getText());
		} else {
			editingElement.removeChild("Street");
		}
		if (cityTF.getText().length() > 0) {
			if (editingElement.getChild("City") == null) {
				editingElement.addContent(new Element("City"));
			}
			editingElement.getChild("City").setText(cityTF.getText());
		} else {
			editingElement.removeChild("City");
		}
		if (postalTF.getText().length() > 0) {
			if (editingElement.getChild("PostalCode") == null) {
				editingElement.addContent(new Element("PostalCode"));
			}
			editingElement.getChild("PostalCode").setText(postalTF.getText());
		} else {
			editingElement.removeChild("PostalCode");
		}
		if (countryTF.getText().length() > 0) {
			if (editingElement.getChild("Country") == null) {
				editingElement.addContent(new Element("Country"));
			}
			editingElement.getChild("Country").setText(countryTF.getText());
		} else {
			editingElement.removeChild("Country");
		}
		Element periodE;

		if ((periodStart.getDate() != null) || (periodDuration.hasDuration())) {
			if (editingElement.getChild("Period") == null) {
				periodE = new Element("Period");
			} else {
				periodE = editingElement.getChild("Period");
			}
			if (periodStart.getDate() != null) {
				if (periodE.getChild("PeriodStart") == null) {
					periodE.addContent(new Element("PeriodStart"));
				}
				periodE.getChild("PeriodStart").setText(
						ComaDateTime.xsDateTimeFromDate(periodStart.getDate()));
			}

			if (periodE.getChild("PeriodExact") == null) {
				periodE.addContent(new Element("PeriodExact"));
			}
			// periodE.getChild("PeriodExact").setText("true");
			periodE.getChild("PeriodExact").setText(
					periodExactCheckBox.isSelected() ? "true" : "false");
			System.out.println("setting it "
					+ (periodExactCheckBox.isSelected() ? "true" : "false"));

			if (periodE.getChild("PeriodDuration") == null) {
				periodE.addContent(new Element("PeriodDuration"));
			}
			periodE.getChild("PeriodDuration").setText(
					"" + periodDuration.getDurationInMilliseconds());

		} else {
			if (editingElement.getChild("Period") != null) {
				editingElement.removeChild("Period");
			}

		}

		Element panelDescription = descriptionPanel.getDataAsNewElement();
		if (editingElement.getChild("Description") != null) {
			editingElement.removeChild("Description");
		}
		editingElement.addContent(panelDescription);

	}

	/**
	 * closes the dialog
	 */
	private void cancel() {
		this.setVisible(false);
		this.dispose();
	}

}
