/**
 * 
 */
package org.exmaralda.coma.models;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * @author woerner
 *
 */
public class CCWSpeakerModel extends DefaultTableModel {

	/**
	 * 
	 */
	public CCWSpeakerModel() {
		super();

	}

	public Class<?> getColumnClass(int columnIndex) {
		return (columnIndex == 1) ? String.class : Boolean.class;
	}

	/**
	 * @return
	 */
	public Vector<String> getSelectedSpeakers() {
		Vector<String> sSpk = new Vector<String>();
		for (int i = 0; i < dataVector.size(); i++) {
			Vector v = (Vector) dataVector.get(i);
			if (((Boolean) v.get(0)) == true) {
				sSpk.add((String) v.get(1));
			}
		}
		return sSpk;

	}
}
