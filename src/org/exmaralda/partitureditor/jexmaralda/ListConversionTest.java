/*
 * ListConversionTest.java
 *
 * Created on 27. August 2002, 16:52
 */

package org.exmaralda.partitureditor.jexmaralda;

import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.segment.*;

/**
 *
 * @author  Thomas
 */
public class ListConversionTest {
    
    /** Creates a new instance of ListConversionTest */
    public ListConversionTest() {
    }
    
    static String[] testFiles = {"a1","a2","a4_asien","a4_ha","a4_he","a4_mormonen","a4_trans","a4_ullaeli","a5","a6","b2_dufde1","b2_lapsus_utt&ann","b2_lapsus_wrd&ann","b5","babel","deuchar","gat","perdue","siebtersinn","tropfsteinhoehle","babel"};
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       try{
//           SegmentedTranscriptionSaxReader str = new SegmentedTranscriptionSaxReader();
//           SegmentedTranscription st = str.readFromFile("e:\\segmentation\\tropf_out4");
           for (int pos=0; pos<testFiles.length; pos++){
            String filename = testFiles[pos];
            System.out.println("------------------------------------------------------------------");
            System.out.println("Processing " + filename + "...");
            System.out.println("------------------------------------------------------------------");
            BasicTranscription bt = new BasicTranscription("E:\\AAA_Beispiele\\testset\\" + filename +".xml");
            SegmentedTranscription st = bt.toSegmentedTranscription();
            SegmentedToListInfo info = new SegmentedToListInfo(st, SegmentedToListInfo.TURN_SEGMENTATION);
           /*info.addMain("SPK0","TIE0","Turn_Utterance_Word","u");
           info.addDependent("SPK0", "TIE4", "Event");
           info.addAnnotation("SPK0", "TIE0", "eng");
           info.addAnnotation("SPK0", "TIE0", "fra");
           info.addMain("SPK1","TIE1","Turn_Utterance_Word","u");
           info.addDependent("SPK1", "TIE3", "Event");
           info.addAnnotation("SPK1", "TIE1", "eng");*/
           
           ListTranscription lt = st.toListTranscription(info);
           lt.writeXMLToFile("e:\\segmentation\\" + filename + "_list.xml","none");
           TierFormatTable tft = new TierFormatTable(bt);
           lt.writeHTMLToFile("e:\\segmentation\\" + filename + "_list.html", tft);
           }
       } catch (Throwable t){
           t.printStackTrace();
       }
    }
    
}
