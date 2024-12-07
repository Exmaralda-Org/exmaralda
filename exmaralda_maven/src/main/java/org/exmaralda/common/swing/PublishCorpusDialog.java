package org.exmaralda.common.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.exmaralda.coma.resources.ResourceHandler;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.common.helpers.JDOMDocMaker;
import org.jdom.Document;
import org.jdom.Element;

public class PublishCorpusDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static PublishCorpusDialog c;
	private HashMap<String, configSetting> options;

	public PublishCorpusDialog(Frame arg0, ExmaraldaApplication e) {
		super(arg0);
		getContentPane().setLayout(new BorderLayout());
		JTabbedPane optionsPane = new JTabbedPane();
		getContentPane().add(optionsPane, BorderLayout.CENTER);
		options = new HashMap<String, configSetting>();

		URL lfURL = new ResourceHandler().defaultHokusCorpusFileURL();
		Document configDoc = JDOMDocMaker.getDocument(lfURL);
		Element configRoot = configDoc.getRootElement();
		for (Element section : (List<Element>) configRoot
				.getChildren("section")) {
			System.out.println(section.getAttributeValue("name"));
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			JPanel rowPanel = new JPanel();
			rowPanel.setLayout(new GridLayout(0, 3));
			rowPanel.setBorder(BorderFactory.createTitledBorder(section
					.getAttributeValue("name")));
			panel.add(rowPanel, BorderLayout.NORTH);
			optionsPane.addTab(section.getAttributeValue("name"), panel);
			for (Element opt : (List<Element>) section.getChildren()) {
				configSetting cs = new configSetting("true".equals(opt
						.getAttributeValue("active")), opt.getName(), opt
						.getAttributeValue("use").equals("required"), opt
						.getText());
				options.put(opt.getName(), cs);
				JLabel label = new JLabel(cs.optionName);
				label.setOpaque(true);
				if (cs.required) {
					label.setBackground(Color.YELLOW);
				}
				rowPanel.add(cs.doit);
				rowPanel.add(label);
				rowPanel.add(cs.value);
			}
		}
		pack();

	}
	public static void main(String[] args) {
		c = new PublishCorpusDialog(null, null);
		c.setVisible(true);
	}
	class configSetting {
		JCheckBox doit;
		String optionName;
		boolean required;
		JTextField value;

		public configSetting(boolean d, String n, boolean r, String v) {
			doit = new JCheckBox();
			doit.setSelected(d);
			optionName = n;
			required = r;
			value = new JTextField(v);
		}

	}
}
