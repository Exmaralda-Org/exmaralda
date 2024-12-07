/**
 * 
 */
package org.exmaralda.coma.dialogs;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.swing.RegularExpressionTextField;

/**
 * @author woerner
 * 
 */
public class TranscriptionSearchAndReplaceDialog extends JDialog {

	private int result;
	private JTextField replaceEx;
	private RegularExpressionTextField searchEx;
	private JLabel rLabel;
	private JLabel sLabel;
	private JCheckBox createBackupsCheckbox;
	private JTextField exResult;
	private JTextField exInput;
	private JTextField xpath;

	public TranscriptionSearchAndReplaceDialog(Coma coma) {
		super(coma, true);
		setAlwaysOnTop(true);
		setTitle("S&R");
		this.getRootPane().setLayout(new BorderLayout());
		JLabel descLabel = new JLabel(
				Ui.getText("msg.TransSearchAndReplaceDescription"));
		descLabel.setFont(new Font("Dialog", 1, 14));
		this.getRootPane().add(descLabel, BorderLayout.NORTH);
		JPanel srPanel = new JPanel();
		srPanel.setLayout(new GridLayout(0, 2));
		srPanel.add(new JLabel());
		srPanel.add(new JLabel());
		sLabel = new JLabel(Ui.getText("search"));
		srPanel.add(sLabel);
		searchEx = new RegularExpressionTextField();
		srPanel.add(searchEx);
		rLabel = new JLabel(Ui.getText("replace"));
		srPanel.add(rLabel);
		replaceEx = new RegularExpressionTextField();
		srPanel.add(replaceEx);
		srPanel.add(new JLabel());
		srPanel.add(new JLabel());
		srPanel.add(new JLabel("example"));
		exInput = new JTextField();
		srPanel.add(exInput);
		JButton evalButton = new JButton("evaluate");
		exResult = new JTextField();
		exResult.setEditable(false);
		evalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exResult.setText(exInput.getText().replaceAll(
						searchEx.getText(), replaceEx.getText()));
			}
		});
		srPanel.add(evalButton);
		srPanel.add(exResult);
		srPanel.add(new JLabel(Ui.getText("option.createBackups")));
		createBackupsCheckbox = new JCheckBox();
		srPanel.add(createBackupsCheckbox);
		final JCheckBox safetyCheckbox = new JCheckBox(
				Ui.getText("cmd.XPathedit"));
		srPanel.add(safetyCheckbox);
		safetyCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				xpath.setEnabled(safetyCheckbox.isSelected());
			}

		});
		xpath = new JTextField("//event");
		srPanel.add(xpath);
		xpath.setEnabled(false);

		srPanel.add(new JLabel());
		srPanel.add(new JLabel());
		JButton okButton = new JButton(Ui.getText("OK"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!searchEx.checkExpression()) {
					searchEx.requestFocusInWindow();
					searchEx.setSelectionStart(0);
					searchEx.setSelectionEnd(searchEx.getText().length());
					Toolkit.getDefaultToolkit().beep();
					return;
				} else {
					ok();
				}

			}

		});
		JButton cancelButton = new JButton(Ui.getText("cancel"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				result = JOptionPane.CANCEL_OPTION;
				dispose();
			}
		});
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cancel();
			}
		});

		srPanel.add(okButton, BorderLayout.SOUTH);
		srPanel.add(cancelButton, BorderLayout.SOUTH);
		this.getRootPane().add(srPanel);
		pack();
		setLocationRelativeTo(coma);
		setVisible(true);
	}
	public int getResult() {
		return result;
	}

	public String getSearchExpression() {
		return searchEx.getText();
	}

	public String getReplaceExpression() {
		return replaceEx.getText();
	}

	public String getSearchString() {
		return searchEx.getText();
	}
	private void cancel() {
		result = JOptionPane.CANCEL_OPTION;
		dispose();
	}
	private void ok() {
		result = JOptionPane.OK_OPTION;
		dispose();
	}

	public String getXPath() {
		return xpath.getText();
	}

	public String getReplaceString() {
		return replaceEx.getText();
	}

	public boolean getCreateBackupFlag() {
		return createBackupsCheckbox.isSelected();
	}
}
