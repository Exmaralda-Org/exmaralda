/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda.segment;

import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.ListTranscription;
import org.jdom.Document;

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
            BasicTranscription bt = new BasicTranscription("C:\\Users\\Schmidt\\Dropbox\\WV_JensLanwer\\DATEN\\Komplex_Beispiel3.exb");
            GATSegmentation segmenter = new org.exmaralda.partitureditor.jexmaralda.segment.GATSegmentation();
            ListTranscription lt = segmenter.BasicToIntonationUnitList(bt);     
            Document xml = GATSegmentation.toXML(lt);
            System.out.println(IOUtilities.documentToString(xml));
            //String text = GATSegmentation.toText(lt);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }

}
