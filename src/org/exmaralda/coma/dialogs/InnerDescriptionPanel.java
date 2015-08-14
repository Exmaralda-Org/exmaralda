/**
 * 
 */
package org.exmaralda.coma.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.exmaralda.coma.datatypes.Description;
import org.exmaralda.coma.helpers.ComaXML;
import org.exmaralda.coma.models.DescriptionTableModel;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;

/**
 * @author woerner
 * 
 */
public class InnerDescriptionPanel extends JPanel {
	Element element;

	Coma coma;

	DescriptionTableModel dtm;

	Description description;

	private TreeMap<String, String> savedContent;

	private JTable descTable;

	private boolean context;

	private JComboBox templateCombo;

	public InnerDescriptionPanel(Description d, boolean showTemplates) {
		super(new BorderLayout());
		if (d != null) {
			context = ComaXML.getContext(d.getElement());
			description = d;
			savedContent = d.getDataAsTreeMap();
			updateTableModel(d);
		}
		// this.setBorder(BorderFactory.createTitledBorder("Description"));
		JPanel templatePanel = new JPanel();
		// System.out.println(element);
		templateCombo = coma.getData().getTemplates()
				.getTemplatesCombo(element);
		templateCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyTemplate((String) ((JComboBox) e.getSource())
						.getSelectedItem());
			}

		});
		JButton templateAddButton = coma.getData().getTemplates()
				.getAddTemplatesButton();
		templateAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Element elmtParent = element.getParentElement();
				Element el = (Element) element.clone();
				coma.getData().getTemplates()
						.addTemplate(element, coma, context);
				elmtParent.removeContent(element);
				elmtParent.addContent(el);
			}
		});
		JButton templateRemoveButton = coma.getData().getTemplates()
				.getRemoveTemplateButton();
		templateRemoveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeTemplate();
			}
		});
		templatePanel.add(new JLabel("Templates"));
		templatePanel.add(templateCombo);
		templatePanel.add(templateAddButton);
		templatePanel.add(templateRemoveButton);
		if (showTemplates) {
			this.add(templatePanel, BorderLayout.NORTH);
		}
		dtm = new DescriptionTableModel(d.getComa(), d);
		descTable = new JTable(dtm);

		TableColumnModel cmodel = descTable.getColumnModel();
		DescriptionValueRenderer textAreaRenderer = new DescriptionValueRenderer();
		ButtonRenderer buttonRenderer = new ButtonRenderer();

		cmodel.getColumn(1).setCellRenderer(textAreaRenderer);
		cmodel.getColumn(2).setCellRenderer(buttonRenderer);
		cmodel.getColumn(2).setCellEditor(new ButtonEditor());
		cmodel.getColumn(2).setMinWidth(25);
		cmodel.getColumn(2).setMaxWidth(25);
		JScrollPane sp = new JScrollPane(descTable);
		// SwingHelper.sizeIt(sp, 500, 300);
		this.add(sp, BorderLayout.CENTER);

	}

	public void resetDescription() {
		description.setContent(savedContent);
	}

	protected void removeTemplate() {
		if (templateCombo.getSelectedIndex() > 0) {
			coma.getData().getTemplates()
					.removeTemplate(templateCombo.getSelectedItem(), context);
			int index = templateCombo.getSelectedIndex();
			templateCombo.setSelectedIndex(0);
			templateCombo.removeItemAt(index);
		}
	}

	private void applyTemplate(String templateName) {
		if (coma.getData().getTemplates()
				.getTemplateElement(context, templateName) == null) {
			return;
		}

		dtm.setDataFromElement(coma.getData().getTemplates()
				.getTemplateElement(context, (templateName)));
		if (!ComaXML.getContext(element)) {
			System.out.println("speaker");
		} else {
			System.out.println("comm");

		}
	}

	/**
	 * @param description
	 */
	public void updateTableModel(Description description) {
		coma = description.getComa();
		element = description.getElement();
		if (dtm == null) {
			dtm = new DescriptionTableModel(coma, description);
		} else {
			dtm.setDescription(description);
		}
	}

	/**
	 * 
	 */
	public void updateDescription() {
		if (descTable.isEditing()) {
			descTable.getCellEditor().stopCellEditing();
		}

		description.setContent(dtm.getData());
	}

	public Description getDescription() {
		return description;
	}

	public Element getDataAsNewElement() {
		stopEditing();
		Element de = new Element("Description");
		TreeMap<String, String> tm = new TreeMap<String, String>(
				dtm.getDataAsHashMap());
		for (String key : dtm.getKeys()) {
			if (key.length() > 0 && tm.get(key).length() > 0) {
				Element k = new Element("Key");
				k.setAttribute("Name", key);
				k.setText(tm.get(key));
				de.addContent(k);
			}
		}
		return de;
	}

	public TreeMap<String, String> getDataAsTreemap() {
		return dtm.getData();
	}

	public class DescriptionValueRenderer extends JTextField implements
			TableCellRenderer {
		private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();

		public Component getTableCellRendererComponent(JTable table,
				Object obj, boolean isSelected, boolean hasFocus, int row,
				int column) {
			adaptee.getTableCellRendererComponent(table, obj, isSelected,
					hasFocus, row, column);
			setForeground(adaptee.getForeground());
			setBackground(adaptee.getBackground());
			setBorder(adaptee.getBorder());
			setFont(adaptee.getFont());
			if (adaptee.getText().indexOf(14) > 0) {
				setText(adaptee.getText().substring(0, 29) + "(\u00B6)");
			} else if (adaptee.getText().length() > 30) {
				setText(adaptee.getText().substring(0, 29) + "(...)");
			} else {
				setText(adaptee.getText());
			}
			return this;
		}
	}

	public class ButtonRenderer extends JButton implements TableCellRenderer {

		public ButtonRenderer() {
			setOpaque(true);
			putClientProperty("JButton.buttonType", "square");
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(UIManager.getColor("Button.background"));
			}
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	public class ButtonEditor extends DefaultCellEditor {
		protected JButton button;
		private String label;
		private boolean isPushed;
		private String editValue;
		private int editRow;

		public ButtonEditor() {
			super(new JCheckBox());
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			});
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			if (isSelected) {
				button.setForeground(table.getSelectionForeground());
				button.setBackground(table.getSelectionBackground());
			} else {
				button.setForeground(table.getForeground());
				button.setBackground(table.getBackground());
			}
			// editValue = (String) table.getValueAt(row, 1);
			editRow = row;
			label = (value == null) ? "" : value.toString();
			button.setText("...");
			isPushed = true;
			return button;
		}

		public Object getCellEditorValue() {
			if (isPushed) {
				editValueLarge(editRow);
				// JOptionPane.showMessageDialog(button, editValue);

			}
			isPushed = false;
			return new String("...");
		}

		public boolean stopCellEditing() {
			isPushed = false;
			return super.stopCellEditing();
		}

		protected void fireEditingStopped() {
			super.fireEditingStopped();
		}
	}

	public void stopEditing() {
		if (descTable.getCellEditor() != null) {
			descTable.getCellEditor().stopCellEditing();
		}
	}

	public void editValueLarge(final int editRow) {
		final JDialog editFrame = new JDialog();
		editFrame.setTitle((String) descTable.getValueAt(editRow, 0));
		editFrame.getContentPane().setLayout(new BorderLayout());
		final JTextArea area = new JTextArea();
		area.setLineWrap(true);
		area.setText((String) descTable.getValueAt(editRow, 1));
		editFrame.getContentPane().add(new JScrollPane(area),
				BorderLayout.CENTER);

		JButton okEButton = new JButton(Ui.getText("OK"));
		okEButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				descTable.setValueAt(area.getText(), editRow, 1);
				editFrame.dispose();
			}

		});
		JButton cancelEButton = new JButton(Ui.getText("cancel"));
		cancelEButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editFrame.dispose();
			}

		});
		JPanel okcPanel = new JPanel(new FlowLayout());
		editFrame.getContentPane().add(okcPanel, BorderLayout.SOUTH);
		okcPanel.add(okEButton);
		okcPanel.add(cancelEButton);

		editFrame.pack();
		editFrame.setModal(true);
		editFrame.setLocation(MouseInfo.getPointerInfo().getLocation());
		editFrame.setSize(new Dimension(300, 400));
		editFrame.setVisible(true);
	}

	public void setDescription(Description d) {
		context = ComaXML.getContext(d.getElement());
		description = d;
		savedContent = d.getDataAsTreeMap();
		updateTableModel(d);
	}

}
