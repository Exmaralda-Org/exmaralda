/*
 * Created on 18.03.2004 by woerner
 */
package org.exmaralda.coma.root;

import java.awt.Component;
import java.io.Serializable;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeCellEditor;

/**
 * coma2/org.sfb538.coma2/ComaTableCellEditor.java * @author woerner
 */

public class ComaTableCellEditor implements TableCellEditor, TreeCellEditor,
	Serializable {

		protected EventListenerList listenerList = new EventListenerList();

		transient protected ChangeEvent changeEvent = null;

		protected JComponent editorComponent = null;

		protected JComponent container = null; // Can be tree or table

		
		
		public Component getComponent() {
			return editorComponent;
		}
		
		
		public Object getCellEditorValue() {
			return editorComponent;
		}
		
		public boolean isCellEditable(EventObject anEvent) {
			return true;
		}
		
		public boolean shouldSelectCell(EventObject anEvent) {
			return false;
		}
		
		public boolean stopCellEditing() {
			fireEditingStopped();
			return true;
		}
		
		public void cancelCellEditing() {
			fireEditingCanceled();
		}
		
		public void addCellEditorListener(CellEditorListener l) {
			listenerList.add(CellEditorListener.class, l);
		}
		
		public void removeCellEditorListener(CellEditorListener l) {
			listenerList.remove(CellEditorListener.class, l);
		}
		
		protected void fireEditingStopped() {
			Object[] listeners = listenerList.getListenerList();
			// Process the listeners last to first, notifying
			// those that are interested in this event
			for (int i = listeners.length-2; i>=0; i-=2) {
				if (listeners[i]==CellEditorListener.class) {
					// Lazily create the event:
					if (changeEvent == null)
						changeEvent = new ChangeEvent(this);
					((CellEditorListener)listeners[i+1]).editingStopped(changeEvent);
				}	       
			}
		}
		
		protected void fireEditingCanceled() {
			// Guaranteed to return a non-null array
			Object[] listeners = listenerList.getListenerList();
			// Process the listeners last to first, notifying
			// those that are interested in this event
			for (int i = listeners.length-2; i>=0; i-=2) {
				if (listeners[i]==CellEditorListener.class) {
					// Lazily create the event:
					if (changeEvent == null)
						changeEvent = new ChangeEvent(this);
					((CellEditorListener)listeners[i+1]).editingCanceled(changeEvent);
				}	       
			}
		}
		
		// implements javax.swing.tree.TreeCellEditor
		public Component getTreeCellEditorComponent(JTree tree, Object value,
													boolean isSelected, boolean expanded, boolean leaf, int row) {
			String         stringValue = tree.convertValueToText(value, isSelected,
					expanded, leaf, row, false);
			
			editorComponent = (JComponent)value;
			container = tree;
			return editorComponent;
		}
		
		// implements javax.swing.table.TableCellEditor
		public Component getTableCellEditorComponent(JTable table, Object value,
													 boolean isSelected, int row, int column) {
			
			editorComponent = (JComponent)value;
			container = table;
			return editorComponent;
		}
		
	} // End of class JComponentCellEditor

