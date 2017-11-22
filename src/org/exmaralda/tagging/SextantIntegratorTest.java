/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.tagging;

import java.io.IOException;
import org.exmaralda.partitureditor.jexmaralda.ListTranscription;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.sax.SegmentedTranscriptionSaxReader;
import org.exmaralda.partitureditor.jexmaralda.segment.SegmentedToListInfo;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class SextantIntegratorTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new SextantIntegratorTest().doit();
    }

    private void doit() {
        /*String SEG_TRANS = "S:\\TP-Z2\\TAGGING\\SegSexTest.exs";
        String ANN = "S:\\TP-Z2\\TAGGING\\SegSexTest.esa";
        String SEG_OUT = "S:\\TP-Z2\\TAGGING\\SegSexTest_integrated.exs";
        String LIST_OUT = "S:\\TP-Z2\\TAGGING\\SegSexTest_integrated.exl";*/
        /*String SEG_TRANS = "S:\\TP-Z2\\DATEN\\MAPTASK\\0.2\\Ali_Dimitri\\MT_091209_Dimitri_s.exs";
        String ANN = "S:\\TP-Z2\\DATEN\\MAPTASK\\0.2\\Ali_Dimitri\\MT_091209_Dimitri_st_possup.esa";
        String SEG_OUT = "S:\\TP-Z2\\DATEN\\MAPTASK\\0.2\\Ali_Dimitri\\MT_091209_Dimitri_s_integrated.exs";
        String LIST_OUT = "S:\\TP-Z2\\DATEN\\MAPTASK\\0.2\\Ali_Dimitri\\MT_091209_Dimitri_integrated.exl";*/
        String SEG_TRANS = "X:\\MINI_DEMO_CORPUS\\HIAT-segmented\\BLSA_E_00220_SE_01_T_01_s.exs";
        String ANN = "X:\\MINI_DEMO_CORPUS\\HIAT-segmented\\BLSA_E_00220_SE_01_T_01_s_POS.esa";
        String SEG_OUT = "X:\\MINI_DEMO_CORPUS\\HIAT-segmented\\BLSA_E_00220_SE_01_T_01_s_integrated.exs";
        String LIST_OUT = "X:\\MINI_DEMO_CORPUS\\HIAT-segmented\\BLSA_E_00220_SE_01_T_01_s_integrated.exl";
        try {
            SextantIntegrator si = new SextantIntegrator(SEG_TRANS);
            si.integrate(ANN);
            si.writeDocument(SEG_OUT);
            SegmentedTranscription st = new SegmentedTranscriptionSaxReader().readFromFile(SEG_OUT);
            SegmentedToListInfo stli = new SegmentedToListInfo(st, SegmentedToListInfo.HIAT_UTTERANCE_SEGMENTATION);
            ListTranscription lt = st.toListTranscription(stli, true);
            lt.getBody().sort();
            lt.writeXMLToFile(LIST_OUT, "none");
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
