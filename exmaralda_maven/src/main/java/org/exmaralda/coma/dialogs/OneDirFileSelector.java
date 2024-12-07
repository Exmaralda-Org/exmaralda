package org.exmaralda.coma.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.helpers.ExmaraldaFileFilter;

public class OneDirFileSelector extends JDialog
		implements
			ListSelectionListener {

	private JTextField fileNameTF;
	private JList fileList;
	private String extension;
	private File dir;
	boolean result = false;
	private JPanel fileSelectionPane;
	private JTextField fileNameDirLabel;
	private File selectedFile;
	private File corpusFile;
	private File defaultFile;

	public boolean showOneDirFileSelector() {
		this.setVisible(true);
		return result;
	}

	public OneDirFileSelector(Coma c, String title, File dir, File f, String ex) {

		super(c, title);
		corpusFile = c.getData().getOpenFile();
		defaultFile = f;
		selectedFile = f;
		this.setModal(true);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cancel();
			}
		});
		fileSelectionPane = new JPanel();
		fileSelectionPane.setLayout(new BoxLayout(fileSelectionPane,
				BoxLayout.X_AXIS));
		extension = ex;
		this.dir = dir;

		fileNameDirLabel = new JTextField(selectedFile.getParentFile()
				.getAbsolutePath());
		fileNameDirLabel.setEditable(false);
		fileNameTF = new JTextField(selectedFile.getName());
		fileNameTF.setSelectionStart(0);
		fileNameTF.setSelectionEnd(selectedFile.getName().lastIndexOf("."));

		fileSelectionPane.setBorder(BorderFactory.createTitledBorder(Ui
				.getText("prompt.enterBasketFilename")));
		fileSelectionPane.add(new JLabel("File"));
		fileSelectionPane.add(fileNameDirLabel);
		fileSelectionPane.add(fileNameTF);
		fileSelectionPane.add(Box.createHorizontalGlue());
		JButton fileSelectButton = new JButton(Ui.getText("browse"));
		fileSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File tmpFile = selectFile();
				if (tmpFile != null) {
					if (tmpFile.getAbsolutePath().equals(
							corpusFile.getAbsolutePath())) {
						selectedFile = defaultFile;
					} else {
						selectedFile = tmpFile;
					}
					fileNameTF.setText(selectedFile.getName());
					fileNameDirLabel.setText(selectedFile.getParent());
				}
			}
		});
		fileSelectionPane.add(fileSelectButton);
		String error = "";
		if (!dir.isDirectory()) {
			error = dir + " is not a directory";
		}
		DefaultListModel lm = new DefaultListModel();
		this.getRootPane().setLayout(
				new BoxLayout(this.getRootPane(), BoxLayout.Y_AXIS));
		this.getRootPane().add(fileSelectionPane);
		this.getRootPane().add(Box.createVerticalGlue());
		this.getRootPane().add(getButtonPane());
		this.pack();
		this.setLocationRelativeTo(c);
		fileNameTF.setSelectionStart(0);
		fileNameTF.setSelectionEnd(selectedFile.getName().lastIndexOf("."));
		fileNameTF.requestFocus();
	}

	private Component getButtonPane() {
		JPanel bp = new JPanel();
		JButton okBtn = new JButton(Ui.getText("OK"));
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		JButton cancelBtn = new JButton(Ui.getText("cancel"));
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		bp.add(okBtn);
		bp.add(cancelBtn);
		return bp;
	}

	protected void ok() {
		result = true;
		this.dispose();

	}

	protected void cancel() {
		result = false;
		this.dispose();
	}

	public File getSelectedFile() {
		String fileName;
		if (!fileNameTF.getText().endsWith(extension)) {
			fileName = fileNameTF.getText() + extension;

		} else {
			fileName = fileNameTF.getText();
		}
		File f = new File(dir.getAbsolutePath() + File.separator + fileName);
		return f;
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		fileNameTF.setText(((File) fileList.getSelectedValue()).getName());

	}

	protected File selectFile() {
		File file = new File("");
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(selectedFile.getParentFile());
		fc.addChoosableFileFilter(new ExmaraldaFileFilter("Coma-Corpus",
				new String[]{"coma", "xml"}, true));
		int returnVal = fc.showSaveDialog(this);
		if (fc.getSelectedFile() != null) {
			file = fc.getSelectedFile();

		}
		return file;

	}
}