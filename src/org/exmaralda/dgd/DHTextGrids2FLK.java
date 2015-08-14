/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.GATParser;
import org.exmaralda.folker.io.EventListTranscriptionConverter;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.convert.PraatConverter;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class DHTextGrids2FLK {

    String[] FILES = {
            "AAC2_IV_k",
            /*"KOB2_IV_k",
            "ZIT4_IV_",
            "BWS2_IV_k",
            "CHE3_IV_k",
            "GLZ4_IV_k",
            "HBG2_IV_k",
            "NDH4_IV_k",
            "SMA3_IV_k",
            "STP4_IV",
            "STZ2_IV",
            "VAD2_IV_k",*/
            //"WIE2_IV"
        };
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            try {
                new DHTextGrids2FLK().doit();
            } catch (SAXException ex) {
                Logger.getLogger(DHTextGrids2FLK.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JDOMException ex) {
                Logger.getLogger(DHTextGrids2FLK.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(DHTextGrids2FLK.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerConfigurationException ex) {
                Logger.getLogger(DHTextGrids2FLK.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerException ex) {
                Logger.getLogger(DHTextGrids2FLK.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(DHTextGrids2FLK.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException, SAXException, JDOMException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        for (String file : FILES){
            String fullTextGridPath = "Z:\\IV_dh_FOLK\\" + file + ".TextGrid";
            PraatConverter praatConverter = new PraatConverter();
            BasicTranscription bt = praatConverter.readPraatFromFile(fullTextGridPath, "ISO-8859-1");
            for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
                for (int i=0; i<bt.getBody().getTierAt(pos).getNumberOfEvents(); i++){
                    String d = bt.getBody().getTierAt(pos).getEventAt(i).getDescription();
                    bt.getBody().getTierAt(pos).getEventAt(i).setDescription(d.toLowerCase().replaceAll("\\/", ""));
                }
            }
            EventListTranscription elt = EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt, 0);
            File outFile = new File(new File("Z:\\IV_dh_FOLK\\"), file + ".flk");
            EventListTranscriptionXMLReaderWriter.writeXML(elt, outFile, new GATParser(), 2);
        }
    }
}
