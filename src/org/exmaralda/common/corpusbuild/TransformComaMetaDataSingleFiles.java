/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.jdom.*;
import org.jdom.xpath.*;
import java.util.*;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.jdom.transform.*;
import org.xml.sax.SAXException;


/**
 *
 * @author thomas
 */
public class TransformComaMetaDataSingleFiles {

    //public static String CORPUS_FILENAME = "T:\\TP-E5\\0.9\\ENDFAS_SKOBI_Mit_Transkriptionen.coma";
    //public static String CORPUS_PATH = "T:/TP-E5/0.9/";
    //public static String OUT_DIRECTORY = "T:\\TP-E5\\0.9\\metadata\\";
    //public static String STYLESHEET = "T:\\TP-E5\\0.9\\stylesheets\\comadisplay_single.xsl";

    public static String CORPUS_FILENAME = "T:\\TP-Z2\\DATEN\\E2\\0.2\\BIPODE\\BIPODE.coma";
    public static String CORPUS_PATH = "T:/TP-Z2/DATEN/E2/0.2/BIPODE";
    public static String OUT_DIRECTORY = "T:\\TP-Z2\\DATEN\\E2\\0.2\\BIPODE\\metadata\\";
    public static String STYLESHEET = "T:\\TP-Z2\\DATEN\\E2\\0.2\\BIPODE\\stylesheets\\comadisplay_single.xsl";


    public TransformComaMetaDataSingleFiles() {
    }

    void doit() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException{
        // einen Stylesheet-Prozessor initialisieren
        org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf
                = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory(true);
        //XSLTransformer transformer = new XSLTransformer(STYLESHEET);
        Document corpus = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromLocalFile(CORPUS_FILENAME);
        XPath xp1 = XPath.newInstance("//Communication");
        List allCommunications = xp1.selectNodes(corpus);
        String prev = ((Element)allCommunications.get(allCommunications.size()-1)).getAttributeValue("Id") + ".html";
        String next = "";
        int count = 0;
        for (Object o : allCommunications){
            if (count<allCommunications.size()-1){
                next = ((Element)allCommunications.get(count+1)).getAttributeValue("Id") + ".html";
            } else {
                next = ((Element)allCommunications.get(0)).getAttributeValue("Id") + ".html";
            }
            Element communication = (Element)o;
            Document communicationDocument = new Document();
            communicationDocument.setRootElement(new Element("Corpus"));
            Element dataElement = new Element("CorpusData");
            communicationDocument.getRootElement().addContent(dataElement);
            communication.detach();
            dataElement.addContent(communication);
            System.out.println("***********************");

            XPath xp2 = XPath.newInstance("Setting/Person");
            List allPersons = xp2.selectNodes(communication);
            for (Object o2 : allPersons){
                Element person = (Element)o2;
                String id = person.getText();
                XPath xp3 = XPath.newInstance("//Speaker[@Id='" + id + "']");
                Element speaker = (Element)(xp3.selectSingleNode(corpus));
                if (speaker==null) continue;
                dataElement.addContent((Element)(speaker.clone()));
            }

            //Document htmlDoc = transformer.transform(communicationDocument);
            String xmlString = IOUtilities.documentToString(communicationDocument);
            String htmlString = sf.applyExternalStylesheetToString(STYLESHEET, xmlString);
            Document htmlDoc = IOUtilities.readDocumentFromString(htmlString);

            AbstractCorpusProcessor.insertPreviousAndNext(htmlDoc, prev, next);

            String outFilename = OUT_DIRECTORY + communication.getAttributeValue("Id") + ".html";

            System.out.println(outFilename);

            org.exmaralda.common.jdomutilities.IOUtilities.writeDocumentToLocalFile(outFilename, htmlDoc, true);

            count++;
            prev = communication.getAttributeValue("Id") + ".html";


        }
    }
    
    public static void main(String[] args){
        try {
            CORPUS_FILENAME = args[0];
            CORPUS_PATH = new File(CORPUS_FILENAME).getParent();
            OUT_DIRECTORY = args[1];
            STYLESHEET = args[2];

            new TransformComaMetaDataSingleFiles().doit();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

    

