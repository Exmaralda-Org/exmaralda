/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.convert.PraatConverter;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class TestMAUSConnector {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestMAUSConnector().doit();
        } catch (IOException | JDOMException ex) {
            Logger.getLogger(TestMAUSConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException, JDOMException {
        MAUSConnector mc = new MAUSConnector(false);
         
        File textFile = new File("C:\\Users\\bernd\\OneDrive\\Desktop\\TEST_MAUS\\TEST.TXT");
        File signalFile = new File("C:\\Users\\bernd\\OneDrive\\Desktop\\TEST_MAUS\\TEST.WAV");
        
        String[][] parameters = {
            {"LANGUAGE","deu"}
        };
        
        String result = mc.callMAUS(textFile, signalFile, null);
        File temp = File.createTempFile("MAUSRESULT", ".textGrid");
        temp.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(temp);
        fos.write(result.getBytes("UTF-8"));
        fos.close();                    
        
        
        PraatConverter pc = new PraatConverter();
        BasicTranscription bt = pc.readPraatFromFile(temp.getAbsolutePath(), "UTF-8");
        bt.writeXMLToFile("C:\\Users\\bernd\\OneDrive\\Desktop\\TEST_MAUS\\MAUS_TEST.exb", "none");
        
        System.out.println(result);
    }
}
