/**
 * 
 */
package org.exmaralda.coma.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.exmaralda.coma.datatypes.Description;
import org.exmaralda.coma.dialogs.KeyMapperDialog.MyTableCellRenderer;
import org.exmaralda.coma.models.DescriptionTableModel;
import org.exmaralda.coma.models.MixedDescriptionJTable;
import org.exmaralda.coma.models.MixedDescriptionTableModel;
import org.exmaralda.coma.root.Coma;
import org.jdom.Element;

/**
 * @author woerner
 * 
 */
public class MixedDescriptionPanel extends JPanel implements TableModelListener {
	Element element;

	Coma coma;

	DescriptionTableModel dtm;

	Description description;

	private TreeMap<String, String> savedContent;

	private JTable descTable;

	private boolean context;

	private JComboBox templateCombo;

	private HashMap<String, HashSet<String>> desc;

	private MixedDescriptionTableModel tm;

	public MixedDescriptionPanel(Vector<Element> dElms) {
		super();
		desc = new HashMap<String, HashSet<String>>();
		for (Element e : dElms) { // descriptionElements
			for (Element ke : (List<Element>) e.getChildren()) {
				if (!desc.containsKey(ke.getAttributeValue("Name"))) {
					desc.put(ke.getAttributeValue("Name"),
							new HashSet<String>());
				}
				desc.get(ke.getAttributeValue("Name")).add(ke.getText());
				System.out.println("++"
						+ desc.get(ke.getAttributeValue("Name")).size());
			}
		}

		tm = new MixedDescriptionTableModel(desc);
		descTable = new MixedDescriptionJTable(tm);
		tm.addEmptyRow();
		tm.addTableModelListener(this);
		TableColumn col = descTable.getColumnModel().getColumn(1);
		col.setCellRenderer(new MyTableCellRenderer());
		System.out.println(tm.getRowCount());

		JScrollPane sp = new JScrollPane(descTable);
		this.add(sp, BorderLayout.CENTER);
	}

	public Element getDescriptionElement() {
		Element d = new Element("Description");
		for (int i = 0; i < tm.getRowCount(); i++) {
			if (tm.getValueAt(i, 1).toString().length() > 0) {
				Element ke = new Element("Key").setAttribute("Name", tm
						.getValueAt(i, 0).toString());
				ke.setText(tm.getValueAt(i, 1).toString());
				d.addContent(ke);
			}

		}
		return d;
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (descTable.getValueAt(descTable.getRowCount() - 1, 0).toString()
				.length() > 0
				&& descTable.getValueAt(descTable.getRowCount() - 1, 1)
						.toString().length() > 0) {
			// tm.addRow(new String[]{"", ""});
			int size = descTable.getRowCount();
			tm.fireTableRowsInserted(size - 1, size - 1);

		}

	}
	public class MyTableCellRenderer extends JComboBox
			implements
				TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable arg0,
				Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
			// TODO Auto-generated method stub
			return new JComboBox((String[]) arg1);
		}

	}

}
