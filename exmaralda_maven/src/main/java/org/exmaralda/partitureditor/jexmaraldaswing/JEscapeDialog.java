/*
 * JEscapeDialog.java
 *
 * Created on 21. November 2002, 14:23
 */

package org.exmaralda.partitureditor.jexmaraldaswing;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRootPane;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
/**
 *
 * @author  Thomas
 * @version 
 */
public class JEscapeDialog extends javax.swing.JDialog {

    public boolean change = false;;
    ActionListener actionListener;
    
    /** Creates new JEscapeDialog */
    public JEscapeDialog(java.awt.Frame parent, boolean modal) {
        super(parent,modal);
        actionListener = new ActionListener() {     
            public void actionPerformed(ActionEvent actionEvent) {
                escapePressed();
            }
        };
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        this.getRootPane().registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }


    void escapePressed(){
        change = false;
        setVisible (false);
        dispose ();    
    }

}
