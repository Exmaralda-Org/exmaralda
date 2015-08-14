/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.utilities;

import java.util.Iterator;
import org.jdom.Element;
import org.jdom.filter.AbstractFilter;

/**
 *
 * @author thomas
 */
public class WordUtilities {
    
    static AbstractFilter textFilter = new AbstractFilter(){
        @Override
        public boolean matches(Object o) {
            return ((o instanceof org.jdom.Text) && ((org.jdom.Text)o).getTextTrim().length()>0);
        }
    };

    static AbstractFilter literalTextFilter = new AbstractFilter(){
        @Override
        public boolean matches(Object o) {
            return ((o instanceof org.jdom.Text));
        }
    };

    public static String getWordText(Element wordElement){        
        Iterator i = wordElement.getDescendants(textFilter);
        StringBuilder result = new StringBuilder();
        while (i.hasNext()){
            result.append(((org.jdom.Text)(i.next())).getText());
        }
        return result.toString();
    }    

    public static String getLiteralWordText(Element wordElement){        
        Iterator i = wordElement.getDescendants(literalTextFilter);
        StringBuilder result = new StringBuilder();
        while (i.hasNext()){
            result.append(((org.jdom.Text)(i.next())).getText());
        }
        return result.toString();
    }    

}
