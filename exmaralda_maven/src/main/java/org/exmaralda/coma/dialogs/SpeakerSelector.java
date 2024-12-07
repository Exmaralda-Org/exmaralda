package org.exmaralda.coma.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.exmaralda.coma.root.Ui;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

public class SpeakerSelector extends JDialog {

    private JButton visitButton;
    private String dlFile;
    private JButton getFileButton;
    private JButton disposeButton;
    private boolean updateAvailable;
    private JList list;
    private HashMap<String, Element> speakerList;
    private HashSet<Element> speakers;

    public SpeakerSelector(Frame owner, Document doc) {
	super(owner, true);
	speakers = new HashSet<Element>();
	System.out.println("speakerSelector");
	this.setLayout(new BorderLayout());
	speakerList = new HashMap<String, Element>();
	Iterator i = doc.getDescendants(new ElementFilter("Speaker"));
	while (i.hasNext()) {
	    String s = "";
	    Element e = (Element) i.next();
	    s = e.getChild("Sigle").getText() + " (" + e.getChildText("Pseudo")
		    + ")";
	    if (speakerList.containsKey(s)) {
		s += "'";
	    }
	    speakerList.put(s, e);
	}
	this.setTitle(Ui.getText("speakerImport.speakerSelection"));
	list = new JList(speakerList.keySet().toArray());
	list
		.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

	this.add(new JScrollPane(list), BorderLayout.CENTER);

	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
	this.add(buttonPanel, BorderLayout.SOUTH);
	JButton importButton = new JButton(Ui.getText("import"));
	importButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		importSpeakers();
	    }
	});

	JButton cancelButton = new JButton(Ui.getText("cancel"));
	cancelButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		dispose();
	    }
	});
	buttonPanel.add(importButton);
	buttonPanel.add(cancelButton);
	this.pack();
    }

    protected void importSpeakers() {
	for (int i = 0; i < list.getSelectedValues().length; i++) {
	    if (speakerList.containsKey(list.getSelectedValues()[i])) {
		speakers.add(speakerList.get(list.getSelectedValues()[i]));

	    }
	}
	dispose();
    }

    public HashSet<Element> getSpeakers() {
	return speakers;
    }
}
