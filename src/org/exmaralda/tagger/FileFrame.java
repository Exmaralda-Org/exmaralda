package org.exmaralda.tagger;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

/*
 * Created on 11.08.2004 by woerner
 */
/**
 * Ludger//FileFrame.java
 * @author woerner
 * 
 */
public class FileFrame extends JFrame {
	static Preferences prefs = Preferences.userRoot().node(
			"org.sfb538.teitagger");
	private JPanel jContentPane;
	private JPanel filePanel;
	private JTextField inputFileTextField;
	private JTextField outputFileTextField;
	private JTextField wordlistFileTextField;
	private JButton chooseInputFileButton;
	private JButton chooseOutputFileButton;
	private JButton chooseWordlistFileButton;
	private JButton editWordlistButton;
	private JButton newWordlistButton;
	private JButton startTaggingButton;
	private TEITagger tagger;
	public FileFrame() {
		super();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			System.setProperty("apple.awt.brushMetalLook", "true");
		} catch (Exception e) {
			System.err.println("failed setting SystemLookAndFeel");
		}
		initialize();
	}
	private void initialize() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("[z2tagger]");
		this.setResizable(false);
		this.setContentPane(getFilePanel());
	}
	private JPanel getFilePanel() {
		if (filePanel == null) {
			filePanel = new JPanel();
			filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
		}
		final JFileChooser fc = new JFileChooser(prefs.get("recentDir",null));
		inputFileTextField = new JTextField(prefs.get("inputFile", ""));
		resize(inputFileTextField, 400, 20);
		wordlistFileTextField = new JTextField(prefs.get("wordlistFile", ""));
		chooseInputFileButton = new JButton("...");
		chooseInputFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(filePanel);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					inputFileTextField.setText(file.getPath());
					prefs.put("recentDir",file.getParent());
				} else {
					// nix
				}
			}
		});
		chooseOutputFileButton = new JButton("...");
		chooseOutputFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(filePanel);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					outputFileTextField.setText(file.getPath());
				} else {
					// nix
				}
			}
		});
		chooseWordlistFileButton = new JButton("...");
		chooseWordlistFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(filePanel);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					wordlistFileTextField.setText(file.getPath());
					prefs.put("recentDir",file.getParent());

				} else {
					// nix
				}
			}
		});
		editWordlistButton = new JButton("edit");
		editWordlistButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WordListEditor wle = new WordListEditor(wordlistFileTextField
						.getText(), wordlistFileTextField);
				wle.show();
			}
		});
		newWordlistButton = new JButton("new");
		newWordlistButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WordListEditor wle = new WordListEditor("",
						wordlistFileTextField);
				wle.show();
			}
		});
		startTaggingButton = new JButton("start tagging!");
		startTaggingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startTagging();
			}
		});
		JPanel ifPanel = new JPanel();
		ifPanel.setLayout(new BoxLayout(ifPanel, BoxLayout.X_AXIS));
		ifPanel.add(new JLabel(" Input file:    "));
		ifPanel.add(inputFileTextField);
		ifPanel.add(chooseInputFileButton);
		JPanel wfPanel = new JPanel();
		wfPanel.setLayout(new BoxLayout(wfPanel, BoxLayout.X_AXIS));
		wfPanel.add(new JLabel(" Wordlist file: "));
		wfPanel.add(wordlistFileTextField);
		wfPanel.add(editWordlistButton);
		wfPanel.add(newWordlistButton);
		wfPanel.add(chooseWordlistFileButton);
		filePanel.add(ifPanel);
		//filePanel.add(ofPanel);
		filePanel.add(wfPanel);
		filePanel.add(startTaggingButton);
		return filePanel;
	}
	private void startTagging() {
		if ((new File(inputFileTextField.getText()).exists())
				&& (new File(wordlistFileTextField.getText()).exists())) {
			String ipF = inputFileTextField.getText();
			String wlF = wordlistFileTextField.getText();
			prefs.put("inputFile", ipF);
			prefs.put("wordlistFile", wlF);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			tagger = new TEITagger(ipF, wlF);
			TaggerFrame tf = new TaggerFrame(tagger);
			tf.show();
			this.dispose();
		} else {
			JOptionPane.showMessageDialog(this, "File not found!");
		}
	}
	private void resize(JComponent c, int w, int h) {
		Dimension d = new Dimension(w, h);
		c.setPreferredSize(d);
		c.setMinimumSize(d);
		c.setMaximumSize(d);
	}
}