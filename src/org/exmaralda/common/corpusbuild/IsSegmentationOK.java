/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class IsSegmentationOK {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String pathToFile = args[0];
            BasicTranscription bt = new BasicTranscription(pathToFile);
            HIATSegmentation hs = new HIATSegmentation();
            hs.BasicToSegmented(bt);
        } catch (FSMException ex) {
            System.out.println("Error (Segmentation)");
            System.exit(1);
        } catch (SAXException ex) {
            System.out.println("Error (SAXException)");
            System.exit(2);
        } catch (JexmaraldaException ex) {
            System.out.println("Error (JexmaraldaException)");
            System.exit(1);
        }
        System.out.println("OK");
        System.exit(0);
    }

}
