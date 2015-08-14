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
import javax.swing.event.DocumentEvent;
import org.jdom.xpath.*;
/**
 *
 * @author thomas
 */
public class XPathExpressionTextField extends javax.swing.JTextField 
                                        implements javax.swing.event.DocumentListener {
    
    /** Creates a new instance of RegularExpressionTextField */
    public XPathExpressionTextField() {
        getDocument().addDocumentListener(this);
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
            XPath.newInstance(text);
            setToolTipText("XPath expression OK");
            setForeground(java.awt.Color.BLACK);            
        } catch (org.jdom.JDOMException jde){
            String message = jde.getMessage();
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
    
}
