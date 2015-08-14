/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.Ui;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * coma2/org.sfb538.coma2.fileActions/newAction.java
 * @author woerner
 * 
 */
public class DumpCorpusAction extends ComaAction {
	Style style;
	StyledDocument doc;
	JTextPane textPane;
	public DumpCorpusAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.DumpCorpusAction"), icon, c);
		this.putValue(Action.SHORT_DESCRIPTION, Ui
				.getText("cmd.DumpCorpusAction"));
	}
	public DumpCorpusAction(Coma c) {
		this(c, null);
	}
	public void actionPerformed(ActionEvent actionEvent) {
		coma.status("@dump document");
		Document dumpDoc = coma.getData().getDocument();
		Format format = Format.getPrettyFormat();
		XMLOutputter outputter = new XMLOutputter(format);
		// Get the text pane's document
		JTextPane textPane = new JTextPane();
		doc = (StyledDocument) textPane.getDocument();
		// Create a style object and then set the style attributes
		style = doc.addStyle("StyleName", null);
		// Font family
		StyleConstants.setFontFamily(style, "Courier");
		// Font size
		StyleConstants.setFontSize(style, 12);
		// Foreground color
		StyleConstants.setForeground(style, Color.black);
		try {
			// Append to document
			doc.insertString(doc.getLength(), outputter.outputString(dumpDoc),
					style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		JFrame dumpFrame = new JFrame("XMLDump");
		dumpFrame.getContentPane().setLayout(new BorderLayout());
		dumpFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dumpFrame.getContentPane().add(new JScrollPane(textPane), BorderLayout.CENTER);
		JButton courier = new JButton("courier");
		courier.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		      	changeStyle("Courier");
		      }
		    });
		JButton arialUnicode = new JButton("arial unicode");
		courier.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		      	changeStyle("Arial Unicode MS");
		      }
		    });
		dumpFrame.getContentPane().add(arialUnicode,BorderLayout.SOUTH);
		dumpFrame.setSize(coma.getSize());
		dumpFrame.setLocationRelativeTo(coma);
		//		dumpFrame.pack();
		dumpFrame.setVisible(true);
	}
	/**
	 * @param string
	 */
	protected void changeStyle(String string) {
		StyleConstants.setFontFamily(style, string);	
		 textPane.setParagraphAttributes(style, true);
		   		
	}
}