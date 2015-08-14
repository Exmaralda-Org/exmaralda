/*
 * Created on 03.03.2004 by woerner
 */
package org.exmaralda.coma.root;
import java.awt.Color;

import javax.swing.table.DefaultTableCellRenderer;

import org.jdom.Attribute;
import org.jdom.Element;
/**
 * coma2/org.sfb538.coma2/ComaTableRenderer.java
 * 
 * @author woerner
 *  
 */
public class ComaTableStringRenderer extends DefaultTableCellRenderer {
	/**
	 *  
	 */
	static Color descriptionColor = new Color(200, 200, 255);
	static Color noChangeColor = new Color(255, 200, 200);
	static Color otherTypeColor = new Color(210, 210, 210);
	static Color attributeColor = new Color(0,240,240);
	static Color emptyFieldColor = new Color (255,100,100);
	public ComaTableStringRenderer() {
		super();
	}
	@Override
	public void setValue(Object value) {
		if (value instanceof Attribute) {
			setBackground(attributeColor);
			super.setValue(((Attribute) value).getName());
			System.out.println("Attribute:"+((Attribute) value).getName());
		} else if (value instanceof Element) {
			value = ((Element)value).getName();
			if (((String) value).equals("Id")
					| ((String) value).equals("Parent")) {
				setBackground(noChangeColor);
				super.setValue(value);
			} else if (((String) value).equals("Description")) {
				setBackground(descriptionColor);
				super.setValue(value);
			} else if (((String) value).startsWith("Language")
					| ((String) value).equals("Location")) {
				setBackground(otherTypeColor);
				super.setValue(value);
			} else {
				setBackground(Color.WHITE);
				super.setValue(value);
			}
		} else {
			value = value.toString();
			if (((String) value).equals("Id")
					| ((String) value).equals("Parent")) {
				setBackground(noChangeColor);
				super.setValue(value);
			} else if (((String) value).equals("Description")) {
				setBackground(descriptionColor);
				super.setValue(value);
			} else if (((String) value).startsWith("Language")
					| ((String) value).equals("Location")) {
				setBackground(otherTypeColor);
				super.setValue(value);
			} else if ((String) value == "") {
				setBackground(emptyFieldColor);
				super.setValue(value);
			} else {
				setBackground(Color.WHITE);
				super.setValue(value);
			}
		}
	}
}