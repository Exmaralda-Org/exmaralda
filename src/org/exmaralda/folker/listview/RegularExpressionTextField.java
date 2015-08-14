/*
 * RegularExpressionTextField.java
 *
 * Created on 15. April 2008, 13:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.listview;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JTextField;
import java.util.regex.*;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;

/**
 *
 * @author thomas
 */
public class RegularExpressionTextField extends JTextField implements DocumentListener {
    
    private Pattern pattern;
    private Matcher matcher;
    
    java.awt.Color OK_COLOR = super.getForeground();
    java.awt.Color ERROR_COLOR = java.awt.Color.RED;
    
    /** Creates a new instance of RegularExpressionTextField */
    public RegularExpressionTextField() {
        this(".*");
    }
    
    public RegularExpressionTextField(String regexPattern) throws PatternSyntaxException {
        getDocument().addDocumentListener(this);
        setPattern(regexPattern);
        setFont(getFont().deriveFont(13.0f));
        java.awt.Insets insets = getMargin();
        java.awt.Insets newInsets = (java.awt.Insets)(insets.clone());
        newInsets.left=0;
        this.setMargin(newInsets);

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
        matcher.reset(text);
        boolean matches = matcher.matches();
        if (!matches){
            setForeground(ERROR_COLOR);
            //String ttt = "Fehler";
            String ttt = FOLKERInternationalizer.getString("misc.error");
            setToolTipText(ttt);
            return;
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
