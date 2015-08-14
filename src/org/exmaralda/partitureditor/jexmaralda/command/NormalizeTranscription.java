/*
 * NormalizeTranscription.java
 *
 * Created on 16. April 2007, 15:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda.command;

import java.io.IOException;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class NormalizeTranscription {
    
    /** Creates a new instance of NormalizeTranscription */
    public NormalizeTranscription() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String input = args[0];
            String output = args[1];
            BasicTranscription bt = new BasicTranscription(input);
            bt.normalize();
            bt.writeXMLToFile(output,"none");
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
    }
    
}
