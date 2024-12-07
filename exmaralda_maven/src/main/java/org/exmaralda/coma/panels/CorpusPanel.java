/*
 * Created on 23.07.2004 by woerner
 */
package org.exmaralda.coma.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.exmaralda.coma.datatypes.Description;
import org.exmaralda.coma.models.DescriptionTableModel;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaData;
import org.exmaralda.coma.root.IconFactory;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;

/**
 * coma2/org.sfb538.coma2.panels/CorpusPanel.java
 * 
 * @author woerner
 * 
 */
public class CorpusPanel extends JPanel implements /* TreeSelectionListener, */
TableModelListener, /* CorpusChangedListener, */ActionListener {

	private static final long serialVersionUID = 1L;
	// private ComaTableModel cptModel;
	private Coma coma;
	private ComaData data;
	private Description description;
	private JTable descTable;
	private JButton addDCButton;
	private JPanel attributePanel;
	// private JTextField corpusNameTextfield;
	private JPanel descriptionPanel;
	private DescriptionTableModel dtm;
	private JLabel usdLabel;
	private JButton publishCorpusButton;
	private JLabel corpusNameLabel;
	private JButton changeCorpusNameButton;
	private JButton changeUsdButton;

	public CorpusPanel(Coma c) {
		super();
		coma = c;
		dtm = new DescriptionTableModel(coma);
		data = c.getData();
		layoutPanel();
	}

	private void layoutPanel() {
		this.setLayout(new BorderLayout());
		this.add(getAttributePanel(), BorderLayout.NORTH);
		this.add(getDescriptionPanel(), BorderLayout.CENTER);

	}

	// @Override
	// public void paintComponent(Graphics g) {
	// g.drawImage(IconFactory.createImageIcon("splash.png").getImage(), 0, 0,
	// null);
	// }

	private JPanel getDescriptionPanel() {
		descriptionPanel = new JPanel(new BorderLayout());
		descriptionPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		if (data.getRootElement().getChild("Description") == null) {
			data.getRootElement().addContent(new Element("Description"));
		}
		descTable = new JTable(dtm);
		descTable.setAutoCreateColumnsFromModel(false);
		dtm.addTableModelListener(this);
		descTable.getColumnModel().removeColumn(
				descTable.getColumnModel().getColumn(2));
		descriptionPanel.add(new JScrollPane(descTable), BorderLayout.CENTER);
		descriptionPanel.add(getButtonPanel(), BorderLayout.WEST);
		return descriptionPanel;
	}

	private Component getAttributePanel() {
		attributePanel = new JPanel(new BorderLayout());
		attributePanel.setOpaque(true);
		corpusNameLabel = new JLabel();
		corpusNameLabel.setFont(new Font("Dialog", 1, 24));
		// corpusNameLabel.setBackground(Color.darkGray);
		// corpusNameLabel.setOpaque(true);
		changeCorpusNameButton = new JButton (IconFactory.createImageIcon("edit-icon16.png"));
		changeCorpusNameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String s = (String) JOptionPane.showInputDialog(coma,
						Ui.getText("change"), Ui.getText("corpusName"),
						JOptionPane.PLAIN_MESSAGE, null, null,
						corpusNameLabel.getText());

				if ((s != null) && (s.length() > 0)) {
					corpusNameLabel.setText(s);
					if (data.getDocument() != null) {
						data.getRootElement().setAttribute("Name", s);
						coma.updateTitle();
						coma.xmlChanged();
						coma.requestFocus();
					}
					return;
				}
			}
		});

		JPanel namePanel = new JPanel();
		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
		namePanel.add(changeCorpusNameButton);
		namePanel.add(corpusNameLabel);
		namePanel.setBorder(BorderFactory.createTitledBorder(Ui
				.getText("corpusName")));
		attributePanel.add(namePanel, BorderLayout.NORTH);
		JPanel usdPanel = new JPanel();
		usdPanel.setLayout(new BoxLayout(usdPanel, BoxLayout.X_AXIS));
		usdLabel = new JLabel();
		changeUsdButton = new JButton(IconFactory.createImageIcon("edit-icon16.png"));
		changeUsdButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String s = (String) JOptionPane.showInputDialog(coma,
						"Enter new unique speaker distinction",
						"unique speaker distinction",
						JOptionPane.PLAIN_MESSAGE, null, null,
						usdLabel.getText());
				if ((s != null) && (s.length() > 0)) {
					usdLabel.setText(s);
					if (data.getDocument() != null) {
						data.getRootElement().setAttribute(
								"uniqueSpeakerDistinction", s);
						coma.xmlChanged();
						coma.requestFocus();
					}
					return;
				}
			}
		});
		usdPanel.add(changeUsdButton);
		usdPanel.add(usdLabel);
		usdPanel.setBorder(BorderFactory
				.createTitledBorder("unique speaker distinction"));
		attributePanel.add(usdPanel);
		return attributePanel;
	}

	public final void tableChanged(TableModelEvent e) {
		data.getRootElement().getChild("Description").removeContent();
		for (Entry<String, String> es : dtm.getData().entrySet()) {
			Element el = new Element("Key");
			el.setAttribute("Name", es.getKey());
			el.setText(es.getValue());
			data.getRootElement().getChild("Description").addContent(el);
		}
		// TODO find a way to fire xmlChanged();
		// coma.xmlChanged();
	}

	private JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(getAddDCButton());
		// buttonPanel.add(getPublishCorpusButton());
		return buttonPanel;
	}

	private JButton getAddDCButton() {
		addDCButton = new JButton(IconFactory.createImageIcon("dublincore.png"));
		addDCButton.setActionCommand("addDCMetaData");
		addDCButton.setToolTipText("add dublin core metadata");
		addDCButton.addActionListener(this);
		addDCButton.putClientProperty("JButton.buttonType", "gradient");
		return addDCButton;
	}

	private JButton getPublishCorpusButton() {
		publishCorpusButton = new JButton(
				IconFactory.createImageIcon("publishCorpus.png"));
		publishCorpusButton.setActionCommand("publishCorpus");
		publishCorpusButton.setToolTipText("publish...");
		publishCorpusButton.addActionListener(this);
		publishCorpusButton.putClientProperty("JButton.buttonType", "gradient");
		publishCorpusButton.setEnabled(false);
		return publishCorpusButton;
	}

	private void showCorpusParms(Element elmt) {
		corpusNameLabel.setText(elmt.getAttributeValue("Name"));
		usdLabel.setText(elmt.getAttributeValue("uniqueSpeakerDistinction"));
		if (elmt.getChild("Description") == null) {
			elmt.addContent(new Element("Description"));
		}
		description = new Description(elmt.getChild("Description"), coma);
		dtm.setDescription(description);
		dtm.fireTableChanged(null);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("addDCMetaData")) {

			addDCMetaData();
		}
		// if (e.getSource() == corpusNameTextfield) {
		// changeHeader();
		// }
	}

	/**
	 * 
	 */
	private void addDCMetaData() {
		String[] dcKeys = { "DC:contributor", "DC:coverage", "DC:creator",
				"DC:date", "DC:description", "DC:format", "DC:identifier",
				"DC:language", "DC:publisher", "DC:relation", "DC:rights",
				"DC:source", "DC:subject", "DC:title", "DC:type" };
		for (String key : dcKeys) {
			dtm.addRow(key, null, false);
		}
		dtm.fireTableStructureChanged();
	}

	public void showValues() {
		coma.toggleTab(true, false);
		Element elmt = data.getRootElement();
		coma.updateTitle();
		showCorpusParms(elmt);
		addDCButton.setEnabled(true);
		if (elmt.getChild("CorpusData") != null) { // holds_(corpus)data
			// coma.getData().setSelectedCorpus(elmt);
			coma.updateTitle();
			Element e = elmt.getChild("CorpusData");
			data.setDataElement(e);
			coma.toggleTab(
					new String[] { Ui.getText("data"), Ui.getText("basket"), },
					true);
		}
	}

	private void changeHeader() {
		if (data.getDocument() != null) {
			data.getRootElement().setAttribute("Name",
					corpusNameLabel.getText());
			coma.xmlChanged();
			coma.requestFocus();
		}
	}
}