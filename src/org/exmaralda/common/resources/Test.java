/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.resources;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.helpers.BasicTranscription2COMA;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
            String COMA_FILE = "T:\\TP-Z2\\DATEN\\EXMARaLDA_DemoKorpus\\EXMARaLDA_DemoKorpus.coma";
        try {
            integrate("T:\\TP-Z2\\DATEN\\EXMARaLDA_DemoKorpus\\AnneWill\\AnneWill.exb", COMA_FILE);
            integrate("T:\\TP-Z2\\DATEN\\EXMARaLDA_DemoKorpus\\Gasperini\\Gasperini.exb", COMA_FILE);
            integrate("T:\\TP-Z2\\DATEN\\EXMARaLDA_DemoKorpus\\NguyenNgocNgan\\NguyenNgocNgan.exb", COMA_FILE);
            integrate("T:\\TP-Z2\\DATEN\\EXMARaLDA_DemoKorpus\\TeliaTelenor\\TeliaTelenor.exb", COMA_FILE);
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

    private static void integrate(String EXB_FILE, String COMA_FILE) throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException {
         Vector<Element> v = BasicTranscription2COMA.generateComaElements(new File(EXB_FILE), new File(COMA_FILE));
         Document comaDoc = IOUtilities.readDocumentFromLocalFile(COMA_FILE);
         for (Element e : v){
             e.detach();
             comaDoc.getRootElement().getChild("CorpusData").addContent(e);
         }
         IOUtilities.writeDocumentToLocalFile(COMA_FILE, comaDoc);
    }

}
