/*
 * Created on 29.03.2004 by woerner
 */
package org.exmaralda.coma.root;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;
/**
 * coma2/org.sfb538.coma2/ComaCellRenderer.java
 * 
 * @author woerner
 *  
 */
public class ComaCellRenderer extends JLabel
		implements
			ListCellRenderer,
			TableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *  
	 */
	public ComaCellRenderer() {
		super();
		// TODO Auto-generated constructor stub
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
	 *          java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(JList arg0, Object arg1,
			int arg2, boolean arg3, boolean arg4) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
	 *          java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int col) {
		if (value instanceof JComponent) {
			return (JComponent) value;
		} else {
			return this;
		}
	}
	
	

}