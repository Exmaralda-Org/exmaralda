/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.alignment;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Thomas_Schmidt
 */
public class TestFineAligner {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestFineAligner().doit();
        } catch (SAXException ex) {
            Logger.getLogger(TestFineAligner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(TestFineAligner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestFineAligner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(TestFineAligner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FSMException ex) {
            Logger.getLogger(TestFineAligner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws SAXException, JexmaraldaException, IOException, JDOMException, FSMException {
        BasicTranscription bt = new BasicTranscription("F:\\AGD-DATA\\dgd2_data\\exb-transcripts\\ISW\\ISW-_E_00011_SE_01_T_02_DF_01b.exb");
        bt.getHead().getMetaInformation().setReferencedFile("Y:\\media\\audio\\ISW\\ISW-_E_00011_SE_01_A_01_DF_01.WAV");
        FineAligner fa = new FineAligner(bt);
        fa.doFineAlignment();
        BasicTranscription alignedTranscription = fa.getTranscription();
        alignedTranscription.writeXMLToFile("Y:\\thomas\\ISW2FLK\\4\\TEST.exb", "none");
        
    }
    
}
