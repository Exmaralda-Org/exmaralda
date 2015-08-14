/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tei;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.transform.XSLTransformException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class MergeTeiSpeakersCOMA {

    Namespace TEI_NAMESPACE = Namespace.getNamespace("tei", "http://www.tei-c.org/ns/1.0");
    String XSL = "/org/exmaralda/tei/xml/ComaSpeakerProperties2TEISpanGrps.xsl";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //Document teiDoc = FileIO.readDocumentFromLocalFile(new File("C:\\Users\\Schmidt\\Dropbox\\HAMATAC\\ALI_TEI.xml"));
            //Document comaDoc = FileIO.readDocumentFromLocalFile(new File("C:\\Users\\Schmidt\\Dropbox\\HAMATAC\\Korpus\\hamatac.coma"));
            //File comaFile = new File("C:\\Users\\Schmidt\\Dropbox\\HAMATAC\\Korpus\\hamatac.coma");
            //File outDir = new File("S:\\Korpora\\ISO-TEI\\HAMATAC");
            File comaFile = new File("S:\\Korpora\\HaCASpa\\0.2\\hacaspa.coma");
            File outDir = new File("S:\\Korpora\\ISO-TEI\\HaCASpa");
            
            //File comaFile = new File("S:\\Korpora\\HamCoPoliG\\0.2\\hamcopolig.coma");
            //File outDir = new File("S:\\Korpora\\ISO-TEI\\HamCoPoliG");
            MergeTeiSpeakersCOMA x = new MergeTeiSpeakersCOMA();
            x.mergeCorpus(comaFile, outDir);
            //x.merge(teiDoc, comaDoc);
            //FileIO.writeDocumentToLocalFile(new File("C:\\Users\\Schmidt\\Dropbox\\HAMATAC\\ALI_TEI_OUT.xml"), teiDoc);
        } catch (JDOMException ex) {
            Logger.getLogger(MergeTeiSpeakersCOMA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MergeTeiSpeakersCOMA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(MergeTeiSpeakersCOMA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(MergeTeiSpeakersCOMA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(MergeTeiSpeakersCOMA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(MergeTeiSpeakersCOMA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(MergeTeiSpeakersCOMA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FSMException ex) {
            Logger.getLogger(MergeTeiSpeakersCOMA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MergeTeiSpeakersCOMA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void mergeCorpus(File comaFile, File outputDirectory) throws JDOMException, IOException, SAXException, JexmaraldaException, FSMException, XSLTransformException, Exception{
        TEIConverter teiConverter = new TEIConverter();
        Document comaDocument = FileIO.readDocumentFromLocalFile(comaFile);
        XPath xpath = XPath.newInstance("//Transcription[Description/Key[@Name='segmented']/text()='false']/NSLink");
        List transcriptionList = xpath.selectNodes(comaDocument);
        outputDirectory.mkdir();
        for (Object o : transcriptionList){
            Element nslink = (Element)o;            
            String fullTranscriptionName = comaFile.getParent() + System.getProperty("file.separator", "/") + nslink.getText();
            BasicTranscription bt = new BasicTranscription(fullTranscriptionName);
            System.out.println(fullTranscriptionName + " read.");
            File outputFile = new File(outputDirectory, new File(fullTranscriptionName).getName().replaceAll("\\.exb", "_TEI.xml"));
            teiConverter.writeHIATISOTEIToFile(bt, outputFile.getAbsolutePath());
            System.out.println(outputFile.getAbsolutePath() + " written.");
            Document teiDocument = FileIO.readDocumentFromLocalFile(outputFile);
            merge(teiDocument, comaDocument);
            System.out.println("Merged. ");
            FileIO.writeDocumentToLocalFile(outputFile, teiDocument);
            
        }
    }
    
    public void merge(Document teiDocument, Document comaDocument) throws JDOMException, SAXException, ParserConfigurationException, IOException, TransformerConfigurationException, TransformerException{
        // 1. get the speakers from the TEI document and 
        // 2. match them to the speakers in the COMA document
        //HashMap<String,Element> speakerAbb2ComaSpeaker = new HashMap<String,Element>();
        //HashMap<String,String> speakerAbb2TEIID = new HashMap<String,String>();        
        StylesheetFactory ssf = new StylesheetFactory(true);
        XPath xp1 = XPath.newInstance("//tei:person");
        xp1.addNamespace(TEI_NAMESPACE);
        List personList = xp1.selectNodes(teiDocument);
        for (Object o : personList){
            // <person xml:id="DIS_4" n="DIS 4">

            Element personElement = (Element)o;
            String abbreviation = personElement.getAttributeValue("n");
            String id = personElement.getAttributeValue("id", Namespace.XML_NAMESPACE);
            
            System.out.println("TEI speaker: " + abbreviation + " / " + id);
            
            Element comaSpeaker = (Element) XPath.selectSingleNode(comaDocument, "//Speaker[Sigle='" + abbreviation + "']");
            String spanGrpString = ssf.applyInternalStylesheetToString(XSL, IOUtilities.elementToString(comaSpeaker));
            
            XPath xp2 = XPath.newInstance("//standoff:annotationGrp[@who='#" + id + "']");
            xp2.addNamespace("standoff", "http://standoff.proposal");
            List annotationGrpList = xp2.selectNodes(teiDocument);
            ArrayList<Element> theOtherList = new ArrayList<Element>();
            for (Object o2: annotationGrpList){
                theOtherList.add((Element) o2);
            }
            
            
            for (Element annotationGrpElement: theOtherList){
                Document dummyDoc = IOUtilities.readDocumentFromString(spanGrpString);
                List spanGrps = dummyDoc.getRootElement().getChildren("spanGrp");
                ArrayList<Element> thisHereList = new ArrayList<Element>();
                for (Object o3: spanGrps){
                    thisHereList.add((Element) o3);
                }

                for (Element spanGrpElement : thisHereList){
                    //Element spanGrpElement = (Element)o3;
                    spanGrpElement.detach();
                    spanGrpElement.setNamespace(TEI_NAMESPACE);
                    spanGrpElement.getChild("span").setAttribute("from", annotationGrpElement.getAttributeValue("start"));
                    spanGrpElement.getChild("span").setAttribute("to", annotationGrpElement.getAttributeValue("end"));
                    spanGrpElement.getChild("span").setNamespace(TEI_NAMESPACE);
                    annotationGrpElement.addContent(spanGrpElement);
                }
            }
            
        }
    }
}
