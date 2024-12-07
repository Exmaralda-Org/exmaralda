/*
 * NormalizeTest.java
 *
 * Created on 15. Maerz 2007, 16:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.io.IOException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class NormalizeTest {
    
    /** Creates a new instance of NormalizeTest */
    public NormalizeTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            BasicTranscription bt = new BasicTranscription("S:\\TP-Z2\\DATEN\\K5\\0.5\\K5_Oeresund\\A6M068\\A6M068.xml");
            bt.normalize();
            bt.writeXMLToFile("S:\\TP-Z2\\DATEN\\K5\\0.5\\K5_Oeresund\\A6M068\\A6M068_Normalized.xml", "none");
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
    }
    
}
