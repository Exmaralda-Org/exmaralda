/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.editor;

import java.awt.Component;
import javax.swing.JList;
import org.jdom.*;

/**
 *
 * @author thomas
 */
public class ErrorListCellRenderer extends javax.swing.DefaultListCellRenderer{

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Element error = (Element)value;
        
        String text =   error.getAttributeValue("transcription") + " / "
                        + error.getAttributeValue("iu_no") + " / "
                        + error.getText();
        
        Component c = super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
        
        
        return c;
    }

}
