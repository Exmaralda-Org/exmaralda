/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class TestG2PConnector {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestG2PConnector().doit();
        } catch (IOException | JDOMException ex) {
            Logger.getLogger(TestG2PConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException, JDOMException {
        G2PConnector mc = new G2PConnector();
         
        File inFile = new File("C:\\Users\\bernd\\OneDrive\\Desktop\\neu_27.txt");
        
        String[][] parameters = {
            {"lng","eng"},
            {"iform","list"},
            {"oform","tab"},
            {"outsym","x-sampa"},
            {"syl","yes"},
            {"stress","yes"}
        };
        HashMap<String, Object> otherParameters = new HashMap<>();
        for (String[] s : parameters){
            otherParameters.put(s[0], s[1]);
        }
        
        String result = mc.callG2P(inFile, otherParameters);
        System.out.println(result);
    }
}
