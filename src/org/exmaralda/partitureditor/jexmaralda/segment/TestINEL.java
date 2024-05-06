/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda.segment;

import java.io.IOException;
import java.util.Vector;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.jdom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class TestINEL {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            BasicTranscription bt = new BasicTranscription("G:\\Meine Ablage\\INEL_DOLGAN\\flk\\AkEE_19900810_GirlAnys_flk\\AkEE_19900810_GirlAnys_flk.exb");
            InelEventBasedSegmentation segmenter = new org.exmaralda.partitureditor.jexmaralda.segment.InelEventBasedSegmentation();
            SegmentedTranscription st = segmenter.BasicToSegmented(bt);
            st.writeXMLToFile("C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2024_04_24_INEL_SEGMENTATION\\AkEE_19900810_GirlAnys_flk.exs", "none");
            Vector<Element> wordList = st.getBody().getWordList();
            for (Element e : wordList){
                System.out.println(e);
            }
        } catch (IOException | FSMException | JexmaraldaException | SAXException ex) {
            ex.printStackTrace();
        } 
    }

}
