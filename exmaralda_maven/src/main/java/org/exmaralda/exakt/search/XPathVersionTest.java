/*
 * XPathVersionTest.java
 *
 * Created on 22. Februar 2007, 12:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

import java.net.MalformedURLException;
import org.jdom.JDOMException;
import org.jdom.xpath.*;
import org.jdom.transform.*;

/**
 *
 * @author thomas
 */
public class XPathVersionTest {
    
    /** Creates a new instance of XPathVersionTest */
    public XPathVersionTest() {
    }
    
    
    public void doit(){
        try {
            String SEGMENTED_STYLESHEET = "/org/exmaralda/exakt/resources/ST2HTML.xsl";
            //String SEGMENTED_STYLESHEET = "/org/exmaralda/exakt/resources/HIAT_FormatTable4BasicTranscription_Squirrel.xsl";            
            java.io.InputStream is = getClass().getResourceAsStream(SEGMENTED_STYLESHEET);
            XSLTransformer transformer = new XSLTransformer(is);        
            
        } catch (XSLTransformException ex) {
            ex.printStackTrace();
        }
        try {
            java.net.URL page = new java.net.URL("http://www.kicktionary.de/index#x27");
            String ref = page.getRef();
            System.out.println(ref);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new XPathVersionTest().doit();
        // TODO code application logic here
        /*String pathString = "string-join(//*)";
        try {
            XPath.newInstance(pathString);
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }*/
    }
    
}
