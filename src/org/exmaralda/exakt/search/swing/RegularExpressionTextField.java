/*
 * RegularExpressionTextField.java
 *
 * Created on 10. Januar 2007, 16:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search.swing;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.event.DocumentEvent;
import java.util.regex.*;
/**
 *
 * @author thomas
 */
public class RegularExpressionTextField extends javax.swing.JTextField 
                                        implements javax.swing.event.DocumentListener,
                                                   javax.swing.ComboBoxEditor,
                                                   MouseListener {
    
    RegularExpressionPopupMenu popupMenu = new RegularExpressionPopupMenu(this);

    /** Creates a new instance of RegularExpressionTextField */
    public RegularExpressionTextField() {
        getDocument().addDocumentListener(this);
        addMouseListener(this);
    }

    public void removeUpdate(DocumentEvent e) {
        checkExpression();
    }

    public void insertUpdate(DocumentEvent e) {
        checkExpression();
    }

    public void changedUpdate(DocumentEvent e) {
        checkExpression();
    }
    
    private void checkExpression(){
        String text = getText();
        try {
            Pattern.compile(text);
            setToolTipText("Regular expression OK");
            setForeground(java.awt.Color.BLACK);            
        } catch (PatternSyntaxException pse){
            String message = pse.getMessage();
            setToolTipText(message);
            setForeground(java.awt.Color.GRAY);            
        }
    }

    public void setItem(Object anObject) {
        String[] historyItem = (String[])anObject;
        setText(historyItem[0]);
    }

    public Component getEditorComponent() {
        return this;
    }

    public Object getItem() {
        String[] returnValue = {getText(), "?"};
        return returnValue;
    }

    public void escapeRegex(){
        String selection = getSelectedText();
        if (selection!=null){
            //System.out.println("Esacping!");
            String escaped = Pattern.quote(selection);
            replaceSelection(escaped);
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()){
            popupMenu.show(this, e.getX(), e.getY());

        }
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()){
            popupMenu.show(this, e.getX(), e.getY());
        }
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }
    
}
