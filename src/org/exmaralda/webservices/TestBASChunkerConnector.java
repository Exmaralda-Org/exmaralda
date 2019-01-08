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
public class TestBASChunkerConnector {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestBASChunkerConnector().doit();
        } catch (IOException ex) {
            Logger.getLogger(TestBASChunkerConnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(TestBASChunkerConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException, JDOMException {
        BASChunkerConnector mc = new BASChunkerConnector();
         
        File bpfFileIn = new File("N:\\Workspace\\IS_Alignment\\NonDGD-Transkripte\\BPF-G2P\\IS--_E_00021_SE_01_T_01.par");
        File wavFileIn = new File("N:\\Workspace\\IS_Alignment\\NonDGD-Transkripte\\Audio_16kHz_Mono\\IS--_E_00021_SE_01_A_01_DF_01.WAV");
        
        String[][] parameters = {
            {"language","deu-DE"}
        };
        HashMap<String, Object> otherParameters = new HashMap<String, Object>();
        for (String[] s : parameters){
            otherParameters.put(s[0], s[1]);
        }
        
        String result = mc.callChunker(bpfFileIn, wavFileIn, otherParameters);
        System.out.println(result);
    }
}
