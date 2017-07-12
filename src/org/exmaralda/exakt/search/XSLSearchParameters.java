/*
 * XSLSearchParameters.java
 *
 * Created on 6. Maerz 2007, 10:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

import java.io.File;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author thomas
 */
public class XSLSearchParameters implements SearchParametersInterface {
    
    Transformer transformer;
    String filename;
    
    /** Creates a new instance of XSLSearchParameters
     * @param file
     * @throws javax.xml.transform.TransformerConfigurationException */
    public XSLSearchParameters(File file) throws TransformerConfigurationException {
        StreamSource xsltSource = new StreamSource(file);
        TransformerFactory tf = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
        transformer = tf.newTransformer(xsltSource);        
    }

    @Override
    public int getSearchType() {
        return SearchParametersInterface.COMPONENT_SEARCH;
    }

    @Override
    public String getSearchExpressionAsString() {
        return "";
    }

    @Override
    public int getContextLimit() {
        return -1;
    }
    
    @Override
    public void setContextLimit(int cl){
    }


    @Override
    public Object[][] getAdditionalDataLocators() {
        return null;
    }
    
    public Transformer getXSLTransformer(){
        return transformer;
    }

    @Override
    public String getCategory() {
        return "";
    }
    
}
