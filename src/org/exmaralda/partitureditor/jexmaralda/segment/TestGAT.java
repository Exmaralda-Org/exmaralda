/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda.segment;

import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class TestGAT {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            BasicTranscription bt = new BasicTranscription("C:\\Dokumente und Einstellungen\\thomas\\Desktop\\cMini\\Thomas_Willis_SE_Final.exb");
            cGATMinimalSegmentation gms = new cGATMinimalSegmentation();

            SegmentedTranscription st = gms.BasicToSegmented(bt);
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }

}
