/*
 * Created on 31.03.2004 by woerner
 */
package org.exmaralda.coma.dialogs;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.exmaralda.coma.helpers.ComaDateTime;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;

import com.toedter.calendar.JDateChooser;

//import discarded.LocationTableModel;

/**
 * coma2/org.sfb538.coma2.personActions/EditLocationDialog.java
 * 
 * @author woerner
 */

public class EditPeriodDialog extends JDialog implements ActionListener {
	boolean newLanguage;

	Coma coma;

	Element elmt;

	//LocationTableModel ltm;

	JTable myTable;

	private JTextField languageCode;

	private JDateChooser periodStart;

	private JTextField periodExact;

	private JTextField periodDuration;

	private JLabel psLabel;

	private JLabel peLabel;

	private JLabel pdLabel;

	public EditPeriodDialog(Coma c, Element e, boolean l) {
		super(c, true);
		System.out.println("EDIT PERIOD DIALOG!");
		newLanguage = l;
		coma = c;
		elmt = e;
		setParms();
		drawUI();
	}

	private void setParms() {
		this.setResizable(false);
		this.setLocationRelativeTo(coma);
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setTitle(Ui.getText("cmd.editPeriod"));
	}

	private void drawUI() {
		this.getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		JPanel inputPanel = new JPanel();
		JPanel okCancelPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(3, 2));
		psLabel = new JLabel(Ui.getText("PeriodStart"));
		peLabel = new JLabel(Ui.getText("PeriodExact"));
		pdLabel = new JLabel(Ui.getText("PeriodDuration"));
		if (elmt.getChildText("PeriodStart") != null) {
			periodStart = new JDateChooser(ComaDateTime.dateFromXSDateTime(elmt.getChildText("PeriodStart")));
			periodStart.setDateFormatString("dd.MM.yyyy HH:mm");
		} else {
			periodStart = new JDateChooser();
		}
		periodExact = new JTextField((elmt.getChildText("PeriodExact") != null ? elmt.getChildText("PeriodExact") : ""), 15);
		periodDuration = new JTextField((elmt.getChildText("PeriodDuration") != null ? elmt.getChildText("PeriodDuration") : ""), 15);

		// ****************************************************************************************
		if (elmt.getChildText("PeriodStart") != null) {
			DateFormat df = DateFormat.getDateInstance();
			try {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				Date theDate = dateFormat.parse(elmt.getChildText("PeriodStart"));
				System.out.println(theDate.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		// ******************************************************************************************

		inputPanel.setBorder(BorderFactory.createTitledBorder("Period"));
		inputPanel.add(psLabel);
		inputPanel.add(periodStart);
		inputPanel.add(peLabel);
		inputPanel.add(periodExact);
		inputPanel.add(pdLabel);
		inputPanel.add(periodDuration);
		this.getContentPane().add(inputPanel);
		this.getContentPane().add(Box.createVerticalGlue());
		this.getContentPane().add(okCancelPanel);
		JButton okButton = new JButton(Ui.getText("OK"));
		okButton.addActionListener(this);
		okButton.setActionCommand("OK");
		JButton cancelButton = new JButton(Ui.getText("cancel"));
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("cancel");
		JButton deleteButton = new JButton(Ui.getText("cmd.deletePeriod"));
		deleteButton.addActionListener(this);
		deleteButton.setActionCommand("delete");
		okCancelPanel.add(okButton);
		okCancelPanel.add(cancelButton);
		okCancelPanel.add(deleteButton);
		//		this.getContentPane().add(new JButton("ASDF"));
		this.pack();
		this.setVisible(true);
	}

	//				EditDescriptionDialog edl = new EditDescriptionDialog(coma,
	//	(Element) userObject);
	public void actionPerformed(ActionEvent evt) {
		if ("cancel".equals(evt.getActionCommand())) {
			if (newLanguage == true) {
				elmt.getParent().removeContent(elmt);
			}
			this.setVisible(false);
			this.dispose();
			coma.updateValueDisplay();
		} else if ("OK".equals(evt.getActionCommand())) {
			if (periodStart.getDate() == null) {
				JOptionPane.showMessageDialog(this, "PeriodStart must not be empty.");
			} else {
				if (elmt.getChild("PeriodStart") == null) {
					elmt.addContent(new Element("PeriodStart"));

				}
				elmt.getChild("PeriodStart").setText(ComaDateTime.xsDateTimeFromDate(periodStart.getDate()));
				if (elmt.getChild("PeriodExact") == null) {
					elmt.addContent(new Element("PeriodExact"));
				}
				if (periodExact.getText() != "true") {
					elmt.getChild("PeriodExact").setText("false");
				} else {
					elmt.getChild("PeriodExact").setText("true");
				}
				if (periodDuration.getText().length() > 0) {
					if (elmt.getChild("PeriodDuration") == null) {
						elmt.addContent(new Element("PeriodDuration"));
					}
					elmt.getChild("PeriodDuration").setText(periodDuration.getText());

				}

				this.setVisible(false);
				this.dispose();
				coma.updateValueDisplay();
			}
		} else if ("delete".equals(evt.getActionCommand())) {
			elmt.getParent().removeContent(elmt);
			this.setVisible(false);
			this.dispose();
			coma.updateValueDisplay();
		} else if ("delete".equals(evt.getActionCommand())) {
		}
	}
}
