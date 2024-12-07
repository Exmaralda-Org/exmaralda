/**
 * 
 */
package org.exmaralda.coma.helpers;

import java.awt.Container;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.exmaralda.coma.root.ComaXMLOutputter;
import org.exmaralda.coma.root.IconFactory;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.helpers.ExmaraldaFileFilter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * @author woerner
 * 
 */
public class ComaTemplates {
	private HashMap<String, Element> communicationTemplates;

	private HashMap<String, Element> speakerTemplates;

	private File templatesFile;

	public ComaTemplates() {
		this(null);
	}

	public ComaTemplates(File templateFile) {
		communicationTemplates = new HashMap();
		speakerTemplates = new HashMap();
		if (templateFile != null) {
			loadTemplates(templateFile);
		}
	}

	/**
	 * @param file
	 */
	public boolean loadTemplates(File file) {
		SAXBuilder builder = new SAXBuilder(
				"org.apache.xerces.parsers.SAXParser");
		boolean read = false;
		Document templateDoc = null;
		try {
			templateDoc = builder.build(file);
			read = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (read) {
			templatesFile = file;
			parse(templateDoc);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	private void parse(Document d) {
		List<Element> ct = d.getRootElement().getChild(
				"communication-templates").getChildren();
		List<Element> st = d.getRootElement().getChild("speaker-templates")
				.getChildren();
		System.out.println("children:" + st.size());
		for (Element e : ct) {
			String name = e.getAttributeValue("coma_template_name");
			e.removeAttribute("coma_template_name");
			communicationTemplates.put(name, e);
		}
		for (Element e : st) {
			String name = e.getAttributeValue("coma_template_name");
			e.removeAttribute("coma_template_name");
			speakerTemplates.put(name, e);
		}

	}

	public JButton getAddTemplatesButton() {
		JButton jb = new JButton(IconFactory.createImageIcon("list-add-16.png"));
		jb.setToolTipText(Ui.getText("cmd.addTemplate"));
		return jb;
	}

	public JButton getRemoveTemplateButton() {
		JButton jb = new JButton(IconFactory
				.createImageIcon("list-remove-16.png"));
		jb.setToolTipText(Ui.getText("cmd.rmvTemplate"));

		return jb;
	}

	public void addTemplate(Element e, Container jc, boolean context) {
		System.out.println(e);
		String s = JOptionPane.showInputDialog(jc, Ui.getText("templateName"),
				(context ? "Communication" : "Speaker") + ".");

		if (context) {
			communicationTemplates.put(s, e);
		} else {
			speakerTemplates.put(s, e);
		}

	}

	public JComboBox getTemplatesCombo(Element editedElement) {
		JComboBox jc = new JComboBox();
		jc.addItem("");
		jc.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXX");
		if (ComaXML.getContext(editedElement)) {
			for (String s : communicationTemplates.keySet()) {
				if (communicationTemplates.get(s).getName().equals(
						editedElement.getName())) {
					jc.addItem(s);
				}
			}
		}
		if (!ComaXML.getContext(editedElement)) {
			for (String s : speakerTemplates.keySet()) {
				if (speakerTemplates.get(s).getName().equals(
						editedElement.getName())) {
					jc.addItem(s);
				}
			}
		}
		// jc.setEnabled(jc.getItemCount() > 1);
		return jc;
	}

	public Element getCommunicationTemplate(String name) {
		return communicationTemplates.get(name);
	}

	public Element getTemplateElement(boolean context, String name) {
		return (context) ? communicationTemplates.get(name) : speakerTemplates
				.get(name);
	}

	public Element getSpeakerTemplate(String name) {
		return speakerTemplates.get(name);
	}

	/**
	 * 
	 */
	public void saveTemplates(boolean saveAs, Container container) {
		Element root = new Element("coma-template");
		Document templateDoc = new Document(root);
		Element cte = new Element("communication-templates");
		Element ste = new Element("speaker-templates");
		root.addContent(cte);
		root.addContent(ste);
		for (String key : communicationTemplates.keySet()) {
			Element addElement = (Element) communicationTemplates.get(key)
					.clone();
			addElement.setAttribute("coma_template_name", key);
			addElement.removeAttribute("Id");
			cte.addContent(addElement);
		}

		for (String key : speakerTemplates.keySet()) {
			Element addElement = (Element) speakerTemplates.get(key).clone();
			addElement.setAttribute("coma_template_name", key);
			addElement.removeAttribute("Id");
			ste.addContent(addElement);
		}

		if ((templatesFile == null) || (saveAs)) {

			File file = null;

			JFileChooser fc = new JFileChooser();
			fc.addChoosableFileFilter(new ExmaraldaFileFilter("Coma-Templates",
					new String[]{"ctf"}, true));
			int returnVal = fc.showSaveDialog(container);
			if (fc.getSelectedFile() != null) {
				file = fc.getSelectedFile();

			}

			if (file != null) {
				if ((file.getName().toLowerCase().endsWith(".ctf"))) {
					templatesFile = file;
				} else {
					templatesFile = new File(file.getPath() + ".ctf");
				}

			}
		}
		ComaXMLOutputter outputter = new ComaXMLOutputter();
		try {
			FileOutputStream out = new FileOutputStream(templatesFile);
			outputter.output(templateDoc, out);
			out.close();
			System.out.println("ganz:" + templatesFile.getPath());
		} catch (IOException e) {
			System.out.println("kaputt");
			// catch me if you can
		}

	}

	/**
	 * @param f
	 */
	public void setFile(File f) {
		templatesFile = f;
	}

	/**
	 * @param selectedItem
	 */
	public void removeTemplate(Object selectedItem, boolean context) {
		if (((String) selectedItem).length() > 0) {
			if (context) {
				communicationTemplates.remove(selectedItem);
			} else {
				speakerTemplates.remove(selectedItem);
			}
		}
	}

}
