/*
 * RegularExpressionSearchParameters.java
 *
 * Created on 8. Januar 2007, 14:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

import java.util.regex.*;
import org.jdom.xpath.*;
import org.jdom.*;
/**
 *
 * @author thomas
 */
public class XPathSearchParameters implements SearchParametersInterface {
    
    String xpathString;
    XPath xpath;
    Object[][] additionalDataLocators;
    int contextLimit = -1;
    String category = "v";
    
    
    /** Creates a new instance of RegularExpressionSearchParameters */
    public XPathSearchParameters(String xp, String[][] adl) 
            throws JDOMException {
        setXPath(xp);
        setAdditionalDataLocators(adl);
    }
    
    public void setXPath(String xp) throws JDOMException {
        xpath = XPath.newInstance(xp);
        xpathString = xp;
    }
    
    public void setAdditionalDataLocators(String[][] adl) throws JDOMException{
        additionalDataLocators = new Object[adl.length][2];
        int index = 0;
        for (String[] a : adl){
            additionalDataLocators[index][0] = a[0];
            additionalDataLocators[index][1] = XPath.newInstance(a[1]);
            index++;
        }        
    }
    
    public XPath getXPath(){
        return xpath;
    }

    public String getSearchExpressionAsString() {
        return xpathString;
    }

    public Object[][] getAdditionalDataLocators() {
        return additionalDataLocators;
    }

    public int getContextLimit() {
        return contextLimit;
    }
    
    public void setContextLimit(int cl){
        contextLimit=cl;
    }
    
    public int getSearchType() {
        return SearchParametersInterface.DEFAULT_SEARCH;
    }

    public String getCategory() {
        return "";
    }
    

    
    
    
}
