/*
 * FileIOTest.java
 *
 * Created on 20. November 2006, 16:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.utilities;

import java.io.IOException;
import org.jdom.JDOMException;
import org.jdom.*;

/**
 *
 * @author thomas
 */
public class FileIOTest {
    
    /** Creates a new instance of FileIOTest */
    public FileIOTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            java.net.Authenticator.setDefault(new KicktionaryAuthenticator());
            Document doc = FileIO.readDocumentFromURL("http://www.kicktionary.de/protectedData/KWIC_Experiment/DE/UEFA/CL/report_1965_79005.xml");
            System.out.println(doc.getRootElement().getName());
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
    }
    
}
