package org.exmaralda.coma.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.exmaralda.coma.helpers.ComaXML;
import org.exmaralda.coma.models.HarmonizeTableModel;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.helpers.LevenshteinDistance;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 * @author kai released = the last released major version (i.e. 1.6) latest =
 *         the latest released (possibly preview) version (i.e. 1.6.115)
 * 
 */
public class KeyMapperDialog extends JDialog {

	private JButton okButton;
	private boolean updateAvailable;
	private JButton disposeButton;
	private HarmonizeTableModel model;
	private Coma coma;
	private JTable keysTable;
	private TreeMap<String, String> keyMap;
	private String keyXPath = "//Key";

	public KeyMapperDialog(Coma owner) {
		super(owner);
		coma = owner;
		keyMap = new TreeMap<String, String>();

		this.setLayout(new BorderLayout());
		String[] showStrings = { "All", "Communications", "Speakers" };
		JComboBox selectionBox = new JComboBox(showStrings);
		selectionBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				int showWhat = cb.getSelectedIndex();
				updateXPath(showWhat);
			}
		});
		this.add(selectionBox, BorderLayout.NORTH);
		this.setTitle(Ui.getText("keyMapper.windowTitle"));
		model = new HarmonizeTableModel(keyMap);
		// for (String k : keyMap.keySet()) {
		// model.addRow(new Object[] { k, keyMap.get(k) });
		// }
		keysTable = new JTable(model);
		keysTable.getColumnModel().getColumn(0)
				.setCellRenderer(new MyTableCellRenderer());
		keysTable.getColumnModel().getColumn(1)
				.setCellRenderer(new MyTableCellRenderer());
		this.add(new JScrollPane(keysTable), BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		this.add(buttonPanel, BorderLayout.SOUTH);
		disposeButton = new JButton(Ui.getText("cancel"));
		disposeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		okButton = new JButton(Ui.getText("OK"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				harmonize();
			}
		});

		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(disposeButton);
		pack();
		selectionBox.setSelectedIndex(0);
	}

	protected void updateXPath(int what) {
		switch (what) {
		case 0:
			changeXPath("//Key[not(starts-with(@Name,'#'))]");
			break;
		case 1:
			changeXPath("//Communication//Key[not(starts-with(@Name,'#'))]");
			break;
		case 2:
			changeXPath("//Speaker//Key[not(starts-with(@Name,'#'))]");
			break;
		}
	}

	protected void harmonize() {
		if (keysTable.getCellEditor() != null) {
			keysTable.getCellEditor().stopCellEditing();
		}
		int changes = 0;
		HashMap<String, String> tableMap = new HashMap<String, String>();
		HashSet<Element> conflicts = new HashSet<Element>();
		tableMap = model.getDataAsMap();
		XPath xp;
		for (String key : tableMap.keySet()) {
			if (tableMap.get(key).length() == 0) {
				System.out.println("removing key " + key);
				try {
					xp = XPath.newInstance("//Key[@Name='" + key + "']");
					List<Element> keys = xp.selectNodes(coma.getData()
							.getDocument().getRootElement());
					for (Element e : keys) {
						e.getParent().removeContent(e);
						changes++;
					}
				} catch (JDOMException e1) {
					e1.printStackTrace();
				}

			}
		}
		for (String tm : tableMap.keySet()) {
			System.out.println(">>" + tm);
		}

		try {
			xp = XPath.newInstance(keyXPath);
			List<Element> keys = xp.selectNodes(coma.getData().getDocument()
					.getRootElement());
			for (Element e : keys) {
				if (!tableMap.get(e.getAttributeValue("Name")).equals(
						e.getAttributeValue("Name"))) { // value changed
					Element theDescription = e.getParentElement();
					HashSet<String> keyNames = new HashSet<String>();
					for (Element dc : (List<Element>) theDescription
							.getChildren()) {
						keyNames.add(dc.getAttributeValue("Name"));
					}
					if (keyNames.contains(e.getAttributeValue("Name"))
							&& keyNames.contains(tableMap.get(e
									.getAttributeValue("Name")))) {
						conflicts.add(e);
					} else {
						// change key!
						e.setAttribute("Name",
								tableMap.get(e.getAttributeValue("Name")));
						changes++;
					}

				}

			}
		} catch (Exception ex) {
			System.out.println("xpath doof!");
			ex.printStackTrace();
		}
		coma.getData().disableCommFilters();
		coma.getData().disableSpeakerFilters();
		HashSet<Element> conflictContexts = new HashSet<Element>();
		for (Element e : conflicts) {
			conflictContexts.add(ComaXML.getContextElement(e));
		}
		coma.dataPanel.updateLists();
		coma.dataPanel.setSelectionFromElements(conflictContexts);
		JOptionPane
				.showMessageDialog(
						this,
						changes + " " + Ui.getText("changesMade") + "\n"
								+ conflicts.size() + " "
								+ Ui.getText("conflictsFound"));
		dispose();
	}

	private void changeXPath(String xPath) {
		keyMap.clear();
		XPath xp;
		keyXPath = xPath;
		try {
			xp = XPath.newInstance(xPath);
			List<Element> keys = xp.selectNodes(coma.getData().getDocument()
					.getRootElement());
			for (Element e : keys) {
				keyMap.put(e.getAttributeValue("Name"),
						e.getAttributeValue("Name"));
			}
		} catch (Exception ex) {
			System.out.println("fail");
		}
		model.setData(keyMap);

	}

	public boolean isUpdateAvailable() {
		return updateAvailable;
	}

	public class MyTableCellRenderer extends JLabel implements
			TableCellRenderer {
		// This method is called each time a cell in a column
		// using this renderer needs to be rendered.
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus,
				int rowIndex, int vColIndex) {
			// 'value' is value contained in the cell located at
			// (rowIndex, vColIndex)
			this.setOpaque(true);
			String s1 = (rowIndex > 0) ? table.getValueAt(rowIndex - 1,
					vColIndex).toString() : "";
			String s2 = value.toString();
			String s3 = (rowIndex < table.getRowCount() - 1) ? table
					.getValueAt(rowIndex + 1, vColIndex).toString() : "";
			this.setBackground(Color.WHITE);
			if (rowIndex > 0) {
				if (new LevenshteinDistance().levenshteinDistance(s1, s2) < 2) {
					this.setBackground(Color.YELLOW);
				}
			}
			if (rowIndex != table.getRowCount() - 1) {
				if (new LevenshteinDistance().levenshteinDistance(s2, s3) < 2) {
					this.setBackground(Color.YELLOW);
				}
			}
			if (vColIndex == 1) {
				if (!value.toString().equals(
						table.getValueAt(rowIndex, 0).toString())) {
					this.setBackground(Color.RED);
				} else {
					this.setBackground(Color.WHITE);
				}
			}
			if (isSelected) {

				// cell (and perhaps other cells) are selected
			}

			if (hasFocus) {
				// this cell is the anchor and the table has the focus
			}

			// Configure the component with the specified value
			setText(value.toString());

			// Set tool tip if desired
			setToolTipText((String) value);

			// Since the renderer is a component, return itself
			return this;
		}

		// The following methods override the defaults for performance reasons
		public void validate() {
		}

		public void revalidate() {
		}

		protected void firePropertyChange(String propertyName, Object oldValue,
				Object newValue) {
		}

		public void firePropertyChange(String propertyName, boolean oldValue,
				boolean newValue) {
		}

	}

}