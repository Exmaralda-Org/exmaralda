/*
 * RegularExpressionTextField.java
 *
 * Created on 15. April 2008, 13:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.experiment;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JTextField;
import java.util.regex.*;

/**
 *
 * @author thomas
 */
public class TokenizingRegularExpressionTextField extends JTextField implements DocumentListener {
    
    private Pattern pattern;
    private Matcher matcher;
    
    java.awt.Color OK_COLOR = super.getForeground();
    java.awt.Color ERROR_COLOR = java.awt.Color.RED;
    
    /** Creates a new instance of RegularExpressionTextField */
    public TokenizingRegularExpressionTextField() {
        this(".*");
    }
    
    public TokenizingRegularExpressionTextField(String regexPattern) throws PatternSyntaxException {
        getDocument().addDocumentListener(this);
        setPattern(regexPattern);
    }

    public void changedUpdate(DocumentEvent e) {
        checkExpression();
    }

    public void insertUpdate(DocumentEvent e) {
        checkExpression();
    }

    public void removeUpdate(DocumentEvent e) {        
        checkExpression();
    }
    
    void checkExpression(){
        String text = getText();
        String[] elements = text.split(" ");
        int count=0;
        for (String element : elements){
            count++;
            matcher.reset(element);
            boolean matches = matcher.matches();
            if (!matches){
                setForeground(ERROR_COLOR);
                String ttt = "Fehler an Position " + Integer.toString(count) + ": " + element;
                setToolTipText(ttt);
                return;
            }
        }
        setForeground(OK_COLOR);
        setToolTipText("OK!");
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern p) {
        pattern = p;
        matcher = pattern.matcher("");
    }
    
    public void setPattern(String patternString) throws PatternSyntaxException {
         setPattern(Pattern.compile(patternString));
    }
    
}
