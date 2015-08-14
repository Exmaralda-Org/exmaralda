/**
 * 
 */
package org.exmaralda.teide.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import org.exmaralda.teide.HTML.HTMLSelection;

/**
 * @author woerner
 *
 */
public class ProgressDialog extends JDialog {
	private JProgressBar progressBar;

	private JEditorPane statusField;

	private String statusText = "";

	private JButton clipboardButton;

	private JButton okButton;

	private JLabel taskDisplay;

	private String tempText = "";

	private String prevLine = "";

	private String thisLine = "";

	public ProgressDialog(Frame owner, String title, int min, int max) {
		super(owner, title);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout());
		JPanel northPanel = new JPanel(new BorderLayout());
		JPanel southPanel = new JPanel(new FlowLayout());
		taskDisplay = new JLabel("Task:");
		progressBar = new JProgressBar(min, max);
		northPanel.add(taskDisplay, BorderLayout.CENTER);
		northPanel.add(progressBar, BorderLayout.EAST);
		add(northPanel, BorderLayout.NORTH);
		statusField = new JEditorPane();
		statusField.setEditable(false);
		statusField.setContentType("text/html");

		add(new JScrollPane(statusField), BorderLayout.CENTER);
		clipboardButton = new JButton(Loc
				.getText("command.copyResultToClipboard"));
		clipboardButton.setEnabled(false);
		clipboardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				HTMLSelection ss = new HTMLSelection("" + statusText + "");
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
						ss, null);
			}
		});

		okButton = new JButton(Loc.getText("command.close"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		okButton.setEnabled(false);
		southPanel.add(clipboardButton);
		southPanel.add(okButton);
		add(southPanel, BorderLayout.SOUTH);
		pack();
		setSize(500, 500);
	}

	public void addStatusText(String newText, boolean newline) {
		if (newline) {
			thisLine += newText + "<br/>";
			statusField.setText("<html>" + prevLine + thisLine + "</html>");
			statusText += thisLine;
			prevLine = thisLine;
			thisLine = "";
		} else {
			thisLine += newText;
			statusField.setText("<html>" + prevLine + thisLine + "</html>");
		}
	}

	public void setProgress(int progress) {
		progressBar.setValue(progress);
		progressBar.setString(progress + "/" + progressBar.getMaximum());
	}

	public void setMin(int min) {
		progressBar.setMinimum(min);
	}

	public void setMax(int max) {
		progressBar.setMaximum(max);
	}

	public void setDeterminable(boolean det) {
		progressBar.setIndeterminate(!det);
		progressBar.setStringPainted(det);

	}

	public void done() {
		clipboardButton.setEnabled(true);
		okButton.setEnabled(true);
		progressBar.setEnabled(false);
	}

	public String setTask(String taskDescription) {
		String old = taskDisplay.getText();
		taskDisplay.setText(taskDescription);
		return old;
	}
}
