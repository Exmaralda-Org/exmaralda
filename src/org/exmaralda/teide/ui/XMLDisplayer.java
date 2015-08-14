/**
 *
 */
package org.exmaralda.teide.ui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

//import org.bounce.text.xml.XMLDocument;
import org.bounce.text.xml.XMLEditorKit;

/**
 * @author woerner
 * 
 */
public class XMLDisplayer extends JPanel {

	private XMLEditorKit dkit;

	private JEditorPane editor;

	private JCheckBox showXML;

	public XMLDisplayer() {
		super(new BorderLayout());

		editor = new JEditorPane();
		dkit = new XMLEditorKit();
		// KAPUTT!
		// dkit.setLineWrappingEnabled(true);
		// dkit.setWrapStyleWord(true);
		editor.setEditorKit(dkit);
		editor.setFont(new Font("Arial Unicode", Font.PLAIN, 12));
		// KAPUTT!
		// editor.getDocument().putProperty(XMLDocument.TAG_COMPLETION_ATTRIBUTE,
		// new Boolean(true));
		// editor.getDocument().putProperty(XMLDocument.AUTO_INDENTATION_ATTRIBUTE,
		// new Boolean(true));
		editor.setEditable(true);
		this.add(new JScrollPane(editor), BorderLayout.CENTER);
		showXML = new JCheckBox("XML zeigen");
		this.add(showXML, BorderLayout.NORTH);
	}

	public void setText(String xml) {
		editor.setText(xml);
	}

	public boolean showXML() {
		return showXML.isSelected();
	}
}
