/*
 * Created on 25.03.2004 by woerner
 */
package org.exmaralda.coma.root;
import java.awt.Component;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 * coma2/org.sfb538.coma2/ComaListRenderer.java *  * @author woerner
 */

public class ComaListRenderer extends JLabel implements ListCellRenderer {

	private String strOut = "";

	private XPath commXPath;

	

	/**
	 *  
	 */
	public ComaListRenderer() {
		super();
		try {
			commXPath = XPath
			.newInstance("Description/Key[@Name='DArt']");	
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
	 *      java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean hasFocus) {
		setOpaque(true);
		// TODO Auto-generated method stub
		if (value instanceof Element) {
			if (((Element) value).getName() == "Speaker") {
				strOut = (((Element) value).getChild("Sigle") == null
						? "#ERROR#"
						: ((Element) value).getChild("Sigle").getText());
				strOut += (((Element) value).getChild("Pseudo") != null
						? " (" + ((Element) value).getChild("Pseudo").getText()
								+ ")"
						: "");
			} else if (((Element) value).getName() == "Communication") {
				strOut = ((Element) value).getAttributeValue("Name");
				try {
//					commXPath = XPath
	//						.newInstance("Description/Key[@Name='DArt']");
					List results = commXPath.selectNodes(value);
					if (results.size() > 0) {
						strOut += " (" + ((Element) results.get(0)).getText()
								+ ")";
					} else {strOut+=" (?)";}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				strOut = (((Element) value).getName());
			}
		} else if (value instanceof Attribute) {
			strOut = ((Attribute) value).getName();
		} else {
			//			strOut="ASDF";
			strOut = (value.toString());
		}
		setText(strOut);
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		return this;
	}
	/**
	 * @return
	 */
	public String getDescXPath() {
		return commXPath.getXPath();
	}
	public void setDescXPath(String descName)  {
		descName = "Description/Key[@Name='"+descName+"']";
		try {
			commXPath = XPath.newInstance(descName);
		} catch (JDOMException e) {
			e.printStackTrace();
		}
	}
}