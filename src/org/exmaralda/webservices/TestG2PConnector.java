/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.convert.PraatConverter;
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
        } catch (IOException ex) {
            Logger.getLogger(TestG2PConnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(TestG2PConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException, JDOMException {
        G2PConnector mc = new G2PConnector();
         
        File bpfFileIn = new File("N:\\Workspace\\IS_Alignment\\NonDGD-Transkripte\\BPF\\IS--_E_00021_SE_01_T_01.par");
        
        String[][] parameters = {
            {"lng","deu"}
        };
        HashMap<String, Object> otherParameters = new HashMap<String, Object>();
        for (String[] s : parameters){
            otherParameters.put(s[0], s[1]);
        }
        
        String result = mc.callG2P(bpfFileIn, otherParameters);
        System.out.println(result);
    }
}
