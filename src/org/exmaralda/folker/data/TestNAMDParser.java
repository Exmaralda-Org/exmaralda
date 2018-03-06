/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.folker.data;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.Document;
import org.jdom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Thomas_Schmidt
 */
public class TestNAMDParser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestNAMDParser().doit();
        } catch (SAXException ex) {
            Logger.getLogger(TestNAMDParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(TestNAMDParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestNAMDParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    String EXB_IN = "F:\\Dropbox\\IDS\\AGD\\Sprachinseln\\GOLD_STANDARD\\NAMD_E_00023_SE_03_T00_2b.exb";
    String FLK_OUT = "F:\\Dropbox\\IDS\\AGD\\Sprachinseln\\GOLD_STANDARD\\NAMD_E_00023_SE_03_T00_2.flk";
    
    
    private void doit() throws SAXException, JexmaraldaException, IOException {
        BasicTranscription exb = new BasicTranscription(EXB_IN);
        
        NAMDParser parser = new NAMDParser();
        // normalization:
        // (1) whitespace before time mark, not after
        // (2) punctuation always preceded by space
        parser.normalizeEXB(exb);
        
        EventListTranscription flk = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(exb, true);
        
        Element rootFLK = flk.toJDOMElement(new File(EXB_IN));
        Document flkDoc = new Document(rootFLK);
        parser.parseDocument(flkDoc, 1);
        parser.parseDocument(flkDoc, 2);
        
        FileIO.writeDocumentToLocalFile(new File(FLK_OUT), flkDoc);
        //org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.writeXML(elt, exportFile, new org.exmaralda.folker.data.GATParser(), 0);
    }
    
}
