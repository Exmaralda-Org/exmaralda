/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.orthonormal.io;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.orthonormal.data.NormalizedFolkerTranscription;
import org.exmaralda.orthonormal.lexicon.SimpleXMLFileLexicon;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class TEST {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TEST().doit();
        } catch (JDOMException | IOException | SAXException | ParserConfigurationException | TransformerException | JexmaraldaException ex) {
            Logger.getLogger(TEST.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerException, TransformerConfigurationException, JexmaraldaException {
        SimpleXMLFileLexicon x = new SimpleXMLFileLexicon();
        /*NormalizedFolkerTranscription nft = XMLReaderWriter.readNormalizedFolkerTranscription(new File("C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_02_24_ISSUE_313\\FOLK_MEET_03_A01_MASK_TAGGED.fln"));
        SegmentedTranscription segmentedTranscription = nft.toSegmentedTranscription();
        segmentedTranscription.writeXMLToFile(new File("C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_02_24_ISSUE_313\\FOLK_MEET_03_A01_MASK_TAGGED_TEST.exs").getAbsolutePath(), "none");*/
    }
    
}
