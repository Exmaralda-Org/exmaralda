/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class TestWebLichtConnector {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestWebLichtConnector().doit();
        } catch (IOException ex) {
            Logger.getLogger(TestWebLichtConnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(TestWebLichtConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException, JDOMException {
        WebLichtConnector mc = new WebLichtConnector();
         
        File tcfFile = new File("C:\\Users\\Schmidt\\Dropbox\\IDS\\TEI_ISO\\TCF\\HelgeSchneider_TCF_EXPORT.xml");
        File chainFile = new File("C:\\Users\\Schmidt\\Dropbox\\IDS\\TEI_ISO\\TCF\\HelgeSchneider_WebLichtChain_2.xml");
        //0000014eb5f24146o1SuA2CXltlIzeD6S/JvIKN7q6i2gvp/NHsyQ05Sp3Q=
        String apiKey = "0000014eb5f5dd2b+YbuglbIJ1IJ/j4Oqq6f1C18RH5kajkrgvz2PCODPoU=";
        String result = mc.callWebLicht(tcfFile, chainFile, apiKey);
        
        System.out.println(result);
    }
}
