/*
 * SpeakerCellEditor.java
 *
 * Created on 7. Mai 2008, 17:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.listview;

import java.awt.Component;
import java.util.Vector;
import javax.swing.*;
import org.exmaralda.folker.data.Speaker;
import org.exmaralda.folker.data.Speakerlist;

/**
 *
 * @author thomas
 */
public class SpeakerCellEditor extends javax.swing.DefaultCellEditor {
    
    Speakerlist speakerlist;
    DefaultComboBoxModel model;
    SpeakerListCellRenderer cellRenderer = new SpeakerListCellRenderer();
    
    /** Creates a new instance of SpeakerCellEditor */
    public SpeakerCellEditor(Speakerlist s) {
        super(new JComboBox());
        speakerlist = s;        
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component retValue;
        
        retValue = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        JComboBox jcb = (JComboBox)retValue;
        Vector<Speaker> splist = (Vector<Speaker>)(speakerlist.getSpeakers().clone());
        splist.addElement(null);
        model = new DefaultComboBoxModel(splist);
        jcb.setModel(model);
        jcb.setRenderer(cellRenderer);
        jcb.setSelectedItem(value);
        //TODO
        return jcb;
    }
    
}
