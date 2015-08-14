package org.exmaralda.tagger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/*
 * Created on 10.08.2004 by woerner
 */
/**
 * Ludger//WordListEditor.java
 * @author woerner
 * 
 */
public class WordListEditor extends JFrame {
	private AbstractButton doneButton;
	private JTextPane formstp;
	private List wordlist;
	private TEITagger tagger;
	private List forms;
	private List options;
	private Element tag;
	private Element att;
	private JTextPane optionstp;
	private JTextField tagtf;
	private JTextField attributetf;
	private JPanel rightPanel;
	private String filename;
	final JFileChooser fc;
	private AbstractButton saveasButton;
	private JTextField tf;
	/**
	 * listener
	 * @param tf
	 * @param tagger
	 * @throws java.awt.HeadlessException
	 */
	public WordListEditor(String s, JTextField textfield) {
		super();
		this.setTitle("z2tagger wordlist-editor");
		tf = textfield;
		filename = s;
		fc = new JFileChooser();
		this.setSize(400, 500);
		getContentPane().setLayout(new BorderLayout());
		StyleContext sc = new StyleContext();
		Style plain = sc.addStyle("Plain", null);
		StyleConstants.setFontFamily(plain, "Arial Unicode MS");
		StyleConstants.setAlignment(plain, StyleConstants.ALIGN_LEFT);
		formstp = new JTextPane();
		optionstp = new JTextPane();
		formstp.setLogicalStyle(plain);
		optionstp.setLogicalStyle(plain);
		tagtf = new JTextField();
		tagtf.setBorder(BorderFactory.createTitledBorder("tag name"));
		resize(tagtf, 200, 40);
		attributetf = new JTextField();
		attributetf.setBorder(BorderFactory
				.createTitledBorder("attribute to set"));
		resize(attributetf, 200, 40);
		rightPanel = new JPanel();
		resize(rightPanel, 200, 40);
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.add(tagtf);
		rightPanel.add(attributetf);
		JScrollPane sp2 = new JScrollPane(optionstp);
		sp2.setBorder(BorderFactory
				.createTitledBorder("attribute options(key)"));
		rightPanel.add(sp2, BorderLayout.EAST);
		JScrollPane sp1 = new JScrollPane(formstp);
		sp1.setBorder(BorderFactory.createTitledBorder("forms to search for"));
		getContentPane().add(sp1, BorderLayout.CENTER);
		getContentPane().add(rightPanel, BorderLayout.EAST);
		String wordsOut = "";
		if ((new File(filename).exists())) {
			SAXBuilder parser = new SAXBuilder();
			try {
				Document wordListDoc = parser.build(s);
				createLists(wordListDoc);
				formstp.setText(getWords(forms, "form"));
				optionstp.setText(getWords(options, "option"));
				if (att != null) {
					attributetf.setText(att.getText());
				}
				tagtf.setText(tag.getText());
			} catch (JDOMException e) {
				JOptionPane.showMessageDialog(this, "XML Error in " + s);
			} catch (IOException e) {
				JOptionPane
						.showMessageDialog(this, "File " + s + " not found!");
			}
		}
		/*		if (t != null) {
		 tagger = t;
		 wordlist = tagger.getWordlist();
		 Iterator i = wordlist.iterator();
		 while (i.hasNext()) {
		 wordsOut += ((Element) i.next()).getText() + "\n";
		 }
		 words.setText(wordsOut);
		 } */
		doneButton = new JButton("save");
		doneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateWordlist();
			}
		});
		saveasButton = new JButton("saveas");
		saveasButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filename = "";
				updateWordlist();
			}
		});
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
		btnPanel.add(doneButton);
		btnPanel.add(saveasButton);
		getContentPane().add(btnPanel, BorderLayout.SOUTH);
	}
	/**
	 * @param forms2
	 * @return
	 */
	private String getWords(List l, String fs) {
		String s = "";
		Iterator i = l.iterator();
		while (i.hasNext()) {
			Element e = (Element) i.next();
			if (fs == "option") {
				s += e.getText() + "(" + e.getAttributeValue("key") + ")\n";
			} else {
				s += e.getText() + "\n";
			}
		}
		return s;
	}
	private void createLists(Document d) {
		forms = d.getRootElement().getChild("forms").getChildren();
		options = d.getRootElement().getChild("options").getChildren("option");
		att = d.getRootElement().getChild("options").getChild("attribute");
		tag = d.getRootElement().getChild("tag");
	}
	private void updateWordlist() {
		String editedWordList = formstp.getText();
		StringTokenizer st = new StringTokenizer(editedWordList, "\n\r\f");
		Document newDoc = new Document();
		newDoc.addContent(new Element("z2tagger"));
		Element root = newDoc.getRootElement();
		root.addContent(new Element("forms"));
		while (st.hasMoreTokens()) {
			String myToken = st.nextToken();
			if (myToken.length() > 0) {
				Element form = new Element("form");
				form.setText(myToken);
				root.getChild("forms").addContent(form);
			}
		}
		Element tag = new Element("tag");
		tag.setText(tagtf.getText());
		root.addContent(tag);
		Element options = new Element("options");
		Element attE = new Element("attribute");
		attE.setText(attributetf.getText());
		options.addContent(attE);
		st = new StringTokenizer(optionstp.getText(), "\n");
		while (st.hasMoreTokens()) {
			String myToken = st.nextToken();
			if (myToken.length() > 0) {
				Element option = new Element("option");
				option.setText(myToken.substring(0, myToken.indexOf("(")));
				option.setAttribute("key", myToken.substring(myToken
						.indexOf("(") + 1, myToken.indexOf(")")));
				options.addContent(option);
			}
		}
		root.addContent(options);
		saveDoc(newDoc);
		this.dispose();
	}
	/**
	 * @param newDoc
	 */
	private void saveDoc(Document newDoc) {
		if (filename.length() > 4) {
			String myString;
			int dot = filename.lastIndexOf(".");
			String backupname = filename.substring(0, dot) + ".bak";
			File oldFile = new File(filename);
			File newFile = new File(backupname);
			if (oldFile.exists()) {
				boolean success = oldFile.renameTo(newFile);
				if (!success) {
					filename = filename.substring(0, dot) + "_.xml";
				}
			}
		} else {
			int returnVal = fc.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				String name = file.getPath();
				if (!name.endsWith(".xml")) {
					name += ".xml";
				}
				filename = name;
			}
		}
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		XMLOutputter op = new XMLOutputter();
		op.setFormat(format);
		try {
			FileOutputStream out = new FileOutputStream(new File(filename));
			op.output(newDoc, out);
			out.close();
			tf.setText(filename);
		} catch (Exception e) {
			System.out.println("error saving");
		}
	}
	private void resize(JComponent c, int w, int h) {
		Dimension d = new Dimension(w, h);
		c.setPreferredSize(d);
		c.setMinimumSize(d);
		c.setMaximumSize(d);
	}
}