/*
 * RegularSearchExpression.java
 *
 * Created on 14. November 2006, 12:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.kwicSearch;

import java.util.regex.*;

/**
 *
 * @author thomas
 */
public class RegularSearchExpression implements SearchExpression {
    
    private Pattern pattern;
    
    
    /** Creates a new instance of RegularSearchExpression */
    public RegularSearchExpression(String regEx) {
        pattern = Pattern.compile(regEx);
    }


    public String getStringRepresentation() {
        if (pattern!=null){
            return pattern.toString();
        }
        return "Invalid Regular Expression";
    }

    public Object getSearchExpressionObject() {
        return pattern;
    }
    
}
