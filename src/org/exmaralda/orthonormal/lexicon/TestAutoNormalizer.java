/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.orthonormal.lexicon;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.segment.GenericSegmentation;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class TestAutoNormalizer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestAutoNormalizer().doit();
        } catch (IOException | SAXException | JexmaraldaException | FSMException | JDOMException | LexiconException ex) {
            Logger.getLogger(TestAutoNormalizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException, SAXException, JexmaraldaException, FSMException, JDOMException, LexiconException {
        XMLLexicon lexicon = new XMLLexicon();
        lexicon.read("/org/exmaralda/orthonormal/lexicon/GOS_Normalization_Lexicon_MAY_2025.xml");
        AutoNormalizer autoNormalizer = new AutoNormalizer(lexicon);
        BasicTranscription bt = new BasicTranscription("C:\\Users\\bernd\\Dropbox\\work\\2023_Maribor\\2025_04_30_NORMALISATION\\Rog-Art-J-Gvecg-P580041-pog.exb");
        GenericSegmentation genericSegmentation = new GenericSegmentation();
        Tier normalizeBasicTranscriptionTier = autoNormalizer.normalizeBasicTranscriptionTier(bt, "TIE1", genericSegmentation, "SpeakerContribution_Word");
        System.out.println(normalizeBasicTranscriptionTier.toXML());
        
        
        
    }
    
}
