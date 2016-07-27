/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.gui;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class TagCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        /* <category name="Partikel bei Adjektiv oder Adverb">
            <tag name="PTKA"/>
            <description>am [schönsten], zu [schnell]</description>
        </category> */
        Element e = (Element)value;
        String tag = e.getChild("tag").getAttributeValue("name");
        String tip = e.getAttributeValue("name") + " / z.B.: " + e.getChildText("description");
        
        JLabel c = (JLabel)(super.getListCellRendererComponent(list, tag, index, isSelected, cellHasFocus));
        c.setToolTipText(tip);
        return c;
    }

    
    
}
