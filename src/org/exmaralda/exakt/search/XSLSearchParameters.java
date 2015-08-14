/*
 * XSLSearchParameters.java
 *
 * Created on 6. Maerz 2007, 10:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

import org.jdom.*;
import org.jdom.transform.*;
import java.io.File;

/**
 *
 * @author thomas
 */
public class XSLSearchParameters implements SearchParametersInterface {
    
    XSLTransformer transformer;
    String filename;
    
    /** Creates a new instance of XSLSearchParameters */
    public XSLSearchParameters(File file) throws XSLTransformException {
        transformer = new XSLTransformer(file);
    }

    public int getSearchType() {
        return SearchParametersInterface.COMPONENT_SEARCH;
    }

    public String getSearchExpressionAsString() {
        return "";
    }

    public int getContextLimit() {
        return -1;
    }
    
    public void setContextLimit(int cl){
    }


    public Object[][] getAdditionalDataLocators() {
        return null;
    }
    
    public XSLTransformer getXSLTransformer(){
        return transformer;
    }

    public String getCategory() {
        return "";
    }
    
}
