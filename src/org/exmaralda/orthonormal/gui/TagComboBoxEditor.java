/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.gui;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import org.exmaralda.orthonormal.lexicon.Tagset;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author thomas
 */
public class TagComboBoxEditor implements javax.swing.ComboBoxEditor, FocusListener {

    JTextField textField = new JTextField();
    private static final String COMMIT_ACTION = "commit";    

    public TagComboBoxEditor() {
        try {
            ArrayList<String> tags = new Tagset().getTagList();
            // Without this, cursor always leaves text field
            textField.setFocusTraversalKeysEnabled(false);
            // Our words to complete
            Autocomplete autoComplete = new Autocomplete(textField, tags);
            textField.getDocument().addDocumentListener(autoComplete);
            // Maps the tab key to the commit action, which finishes the autocomplete
            // when given a suggestion
            textField.getInputMap().put(KeyStroke.getKeyStroke("TAB"), COMMIT_ACTION);        
            textField.getActionMap().put(COMMIT_ACTION, autoComplete.new CommitAction());
            textField.addFocusListener(this);
        } catch (JDOMException ex) {
            Logger.getLogger(TagComboBoxEditor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TagComboBoxEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    @Override
    public Component getEditorComponent() {
        return textField;
    }


    @Override
    public void setItem(Object anObject) {
        if (anObject instanceof String){
            textField.setText(anObject.toString());
        } else if (anObject instanceof Element){
            Element e = (Element)anObject;
            String tag = e.getChild("tag").getAttributeValue("name");
            textField.setText(tag);            
        }
    }

    @Override
    public Object getItem() {
        return textField.getText();
    }

    @Override
    public void selectAll() {
        textField.selectAll();
    }

    @Override
    public void addActionListener(ActionListener l) {
        textField.addActionListener(l);
    }

    @Override
    public void removeActionListener(ActionListener l) {
        textField.removeActionListener(l);
    }

    @Override
    public void focusGained(FocusEvent e) {
        textField.selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {
        // do nothing
    }
    
}
