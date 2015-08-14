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
public class RegularExpressionSearchParameters implements SearchParametersInterface {
    
    String regularExpression;
    Pattern pattern;
    Object[][] additionalDataLocators;
    int contextLimit = -1;
    int searchType = SearchParametersInterface.DEFAULT_SEARCH;
    // Added 21-03-2011: this can be used for annotation searches
    String category = "v";
    
    /** Creates a new instance of RegularExpressionSearchParameters */
    public RegularExpressionSearchParameters(String regEx, String[][] adl) 
            throws PatternSyntaxException, JDOMException {
        setRegularExpression(regEx);
        setAdditionalDataLocators(adl);
    }
    
    /** Creates a new instance of RegularExpressionSearchParameters */
    public RegularExpressionSearchParameters(String regEx, String[][] adl, int st) 
            throws PatternSyntaxException, JDOMException {
        setRegularExpression(regEx);
        setAdditionalDataLocators(adl);
        searchType = st;
    }

    /** Creates a new instance of RegularExpressionSearchParameters */
    public RegularExpressionSearchParameters(String regEx, String[][] adl, int st, String cat)
            throws PatternSyntaxException, JDOMException {
        setRegularExpression(regEx);
        setAdditionalDataLocators(adl);
        searchType = st;
        category = cat;
    }

    public String getCategory() {
        return category;
    }


    public void setRegularExpression(String regEx) throws PatternSyntaxException {
        pattern = Pattern.compile(regEx);
        regularExpression = regEx;
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
    
    public Pattern getPattern(){
        return pattern;
    }

    public String getSearchExpressionAsString() {
        return regularExpression;
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
        return searchType;
    }

    
    
    
    
}
