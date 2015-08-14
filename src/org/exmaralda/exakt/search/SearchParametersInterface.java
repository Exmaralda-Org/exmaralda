/*
 * SearchParametersInterface.java
 *
 * Created on 5. Januar 2007, 17:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

/**
 *
 * @author thomas
 */
public interface SearchParametersInterface {
       
    public static final int DEFAULT_SEARCH = 0;
    public static final int COMPONENT_SEARCH = 1;
    public static final int ANNOTATION_SEARCH = 2;
    
    public String getSearchExpressionAsString();
    
    public Object[][] getAdditionalDataLocators();
    
    public int getContextLimit();
    
    public void setContextLimit(int cl);
    
    public int getSearchType();

    public String getCategory();
    
}
