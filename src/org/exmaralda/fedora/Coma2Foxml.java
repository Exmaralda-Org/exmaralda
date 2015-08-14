/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.fedora;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.helpers.RelativePath;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class Coma2Foxml {
    
    static String CORPUS_SYTLESHEET = "/org/exmaralda/fedora/Corpus2FOXML.xsl";
    static String COMMUNICATION_SYTLESHEET = "/org/exmaralda/fedora/Communication2FOXML.xsl";
    static String TRANSCRIPTION_SYTLESHEET = "/org/exmaralda/fedora/Transcription2FOXML.xsl";
    static String RECORDING_SYTLESHEET = "/org/exmaralda/fedora/Recording2FOXML.xsl";
    static String ADDITIONAL_FILE_SYTLESHEET = "/org/exmaralda/fedora/AdditionalFile2FOXML.xsl";
    static String SPEAKER_SYTLESHEET = "/org/exmaralda/fedora/Speaker2FOXML.xsl";
    
    String targetFolder;
    String comaPath;
    String cmdiPath;
    String corpusNamespace;
    
    public Coma2Foxml(String comaPath, String cmdiPath, String targetFolder, String corpusNamespace) {
        this.targetFolder = targetFolder;
        this.comaPath = comaPath;
        this.cmdiPath = cmdiPath;
        this.corpusNamespace = corpusNamespace;
    }
    
    public void transform() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException{
        StylesheetFactory ssf = new StylesheetFactory(true);
        File targetFolderFile = new File(targetFolder);
        
        Document comaDocument = IOUtilities.readDocumentFromLocalFile(comaPath);
        Document cmdiDocument = IOUtilities.readDocumentFromLocalFile(cmdiPath);
        Element CMDI_HEADER = (Element) XPath.newInstance("//*[local-name()='Header']").selectSingleNode(cmdiDocument);            
        CMDI_HEADER.detach();
        resolvePersons(comaDocument);
        
        // The corpus as a whole
        System.out.println("***** GENERATING CORPUS FOXML ******");
        String resultFoxmlX = 
                ssf.applyInternalStylesheetToExternalXMLFile(CORPUS_SYTLESHEET, comaPath);
        Document resultFoxmlDocumentX = 
                IOUtilities.readDocumentFromString(resultFoxmlX);
        resolveReferences(resultFoxmlDocumentX);  
        String filenameX = "cor_" + corpusNamespace + ".xml";
        File outputFileX = new File(targetFolderFile,filenameX);
        IOUtilities.writeDocumentToLocalFile(outputFileX.getAbsolutePath(), resultFoxmlDocumentX);      
        System.out.println(outputFileX.getAbsolutePath() + " written.");
        
        String[][] xslParameters = {
            {"CORPUS_NAMESPACE", corpusNamespace}
        };
        
        System.out.println("***** GENERATING COMMUNICATION FOXML ******");
        List communicationsList = XPath.newInstance("//Communication").selectNodes(comaDocument);
        for (Object o : communicationsList){
            Element communication = (Element)o;
            String communicationID = communication.getAttributeValue("Id");
            
            // 0. Communications
            String resultFoxml0 = 
                    ssf.applyInternalStylesheetToString(COMMUNICATION_SYTLESHEET, IOUtilities.elementToString(communication), xslParameters);
            Document resultFoxmlDocument0 = 
                    IOUtilities.readDocumentFromString(resultFoxml0);
            String filename0 = "com_" + corpusNamespace + "_" + communication.getAttributeValue("Name") + ".xml";
            new File(targetFolderFile, "com").mkdir();
            
            
            // Retrieve and insert CMDI into COMMUNICATION FOXML
            Element CMDI_FOR_COMMUNICATION = (Element) XPath.newInstance("//*[local-name()='HZSKCommunication'][*[local-name()='GUID']='" + communicationID + "']").selectSingleNode(cmdiDocument);
            Element CMDI_DUMMY = (Element) XPath.newInstance("//*[namespace-uri()='http://www.clarin.eu/cmd/' and local-name()='INSERT_CMDI_DATA']").selectSingleNode(resultFoxmlDocument0);
            CMDI_DUMMY.getParentElement().addContent(CMDI_HEADER);
            CMDI_FOR_COMMUNICATION.detach();
            CMDI_HEADER.detach();
            CMDI_DUMMY.getParentElement().addContent(CMDI_FOR_COMMUNICATION);
            CMDI_DUMMY.detach();

            File outputFile0 = new File(new File(targetFolderFile, "com"),filename0);
            IOUtilities.writeDocumentToLocalFile(outputFile0.getAbsolutePath(), resultFoxmlDocument0);
            System.out.println(outputFile0.getAbsolutePath() + " written.");

            
            String communicationXML = IOUtilities.elementToString(communication);
            
            // 1. Transcriptions
            List l = XPath.newInstance("descendant::Transcription[ends-with(NSLink, '.exb')]").selectNodes(communication);
            for (int pos=0; pos<l.size(); pos++){
                String[][] xslParameters2 = {
                    {"CORPUS_NAMESPACE", corpusNamespace},
                    {"TRANSCRIPTION_INDEX", Integer.toString(pos+1)}
                };
                String resultFoxml = 
                        ssf.applyInternalStylesheetToString(TRANSCRIPTION_SYTLESHEET, communicationXML, xslParameters2);
                Document resultFoxmlDocument = 
                        IOUtilities.readDocumentFromString(resultFoxml);
                resolveReferences(resultFoxmlDocument);                  
                String filename = "tra_" + corpusNamespace + "_" + communication.getAttributeValue("Name") + ".xml";
                if (pos>0){
                    filename = "tra_" + corpusNamespace + "_" + communication.getAttributeValue("Name") + "_" + Integer.toString(pos+1) + ".xml";                    
                }
                new File(targetFolderFile, "tra").mkdir();
                File outputFile = new File(new File(targetFolderFile, "tra"),filename);
                IOUtilities.writeDocumentToLocalFile(outputFile.getAbsolutePath(), resultFoxmlDocument);
                System.out.println(outputFile.getAbsolutePath() + " written.");
            }

            // 2. Recordings
            String resultFoxml2 = 
                    ssf.applyInternalStylesheetToString(RECORDING_SYTLESHEET, communicationXML, xslParameters);
            Document resultFoxmlDocument2 = 
                    IOUtilities.readDocumentFromString(resultFoxml2);
            resolveReferences(resultFoxmlDocument2);  
            String filename2 = "rec_" + corpusNamespace + "_" + communication.getAttributeValue("Name") + ".xml";
            new File(targetFolderFile, "rec").mkdir();
            File outputFile2 = new File(new File(targetFolderFile, "rec"),filename2);
            IOUtilities.writeDocumentToLocalFile(outputFile2.getAbsolutePath(), resultFoxmlDocument2);
            System.out.println(outputFile2.getAbsolutePath() + " written.");

            // 3. Additional data
            String resultFoxml3 = 
                    ssf.applyInternalStylesheetToString(ADDITIONAL_FILE_SYTLESHEET, communicationXML, xslParameters);
            Document resultFoxmlDocument3 = 
                    IOUtilities.readDocumentFromString(resultFoxml3);
            resolveReferences(resultFoxmlDocument3);  
            String filename3 = "add_" + corpusNamespace + "_" + communication.getAttributeValue("Name") + ".xml";
            new File(targetFolderFile, "add").mkdir();
            File outputFile3 = new File(new File(targetFolderFile, "add"),filename3);
            IOUtilities.writeDocumentToLocalFile(outputFile3.getAbsolutePath(), resultFoxmlDocument3);
            System.out.println(outputFile3.getAbsolutePath() + " written.");
            System.out.println("------------------------");
        }
        
        
        System.out.println("***** GENERATING SPEAKER FOXML ******");
        List speakersList = XPath.newInstance("//Speaker").selectNodes(comaDocument);
        for (Object o : speakersList){
            String xmlString = "<speakerWithHisCommunications>";
            
            Element speaker = (Element)o;
            xmlString+=IOUtilities.elementToString(speaker);
            
            String speakerID = speaker.getAttributeValue("Id");
            List comms = XPath.newInstance("//Communication[descendant::Person[text()='" + speakerID  + "']]").selectNodes(comaDocument);
            for (Object o2 : comms){
                Element comm = (Element)o2;
                xmlString+=IOUtilities.elementToString(comm);
            }
            
            xmlString+="</speakerWithHisCommunications>";
            
            String resultFoxml = 
                    ssf.applyInternalStylesheetToString(SPEAKER_SYTLESHEET, xmlString, xslParameters);
                    
            Document resultFoxmlDocument = 
                    IOUtilities.readDocumentFromString(resultFoxml);
            String escapedSigle = speaker.getChildText("Sigle")
                    .replaceAll("ü", "u").replaceAll("ä", "a").replaceAll("ö", "o")
                    .replaceAll("Ü", "U").replaceAll("Ä", "A").replaceAll("Ö", "O")
                    .replaceAll("ß", "ss").replaceAll("[^A-Za-z0-9]", "x");                    
            String filename = "spk_" + corpusNamespace + "_" + escapedSigle + ".xml";
            new File(targetFolderFile, "spk").mkdir();
            File outputFile = new File(new File(targetFolderFile, "spk"),filename);
            IOUtilities.writeDocumentToLocalFile(outputFile.getAbsolutePath(), resultFoxmlDocument);            
            System.out.println(outputFile.getAbsolutePath() + " written.");
        }
        
    }

    private void resolveReferences(Document resultFoxmlDocument) throws JDOMException, MalformedURLException {
        List refs = XPath.newInstance("//*[@resolve]").selectNodes(resultFoxmlDocument);
        for (Object o : refs){
            Element e = (Element)o;
            String what = e.getAttributeValue("resolve");
            if (what.startsWith("@")){
                Attribute a = e.getAttribute(what.substring(1));
                String relativePath = a.getValue();
                if (relativePath.length()>0){
                    String absolutePath = RelativePath.resolveRelativePath(relativePath, comaPath);
                    String url = new File(absolutePath).toURI().toURL().toString();
                    a.setValue(url);
                } else {
                    String url = new File(comaPath).toURI().toURL().toString();
                    a.setValue(url);                    
                }
            }
            
            e.removeAttribute("resolve");
        }
    }
    
    public static void main(String[] args){
        /*String targetFolder = "S:\\TP-Z2\\FEDORA\\HAMATAC_FOXML_TEST";
        String comaPath = "S:\\TP-Z2\\DATEN\\MAPTASK\\0.3\\MAPTASK.coma";
        String corpusNamespace = "hamatac";*/
        if (args.length!=4){
            System.out.println("Usage: Coma2Foxml input-file.coma input-file.cmdi corpus-namespace output-path");
            System.out.println("Example: Coma2Foxml S:\\TP-Z2\\DATEN\\MAPTASK\\0.3\\MAPTASK.coma S:\\TP-Z2\\DATEN\\MAPTASK\\0.3\\MAPTASK.cmdi hamatac S:\\TP-Z2\\FEDORA\\HAMATAC_FOXML_TEST");
            System.exit(0);
        } 
        String comaPath = args[0];
        String cmdiPath = args[1];
        String corpusNamespace = args[2];
        String targetFolder = args[3];
        Coma2Foxml cfx = new Coma2Foxml(comaPath, cmdiPath, targetFolder, corpusNamespace);
        try {
            cfx.transform();
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
        }
    }

    private void insertXSI(Document resultFoxmlDocument) {
        try {
            XPath xp = XPath.newInstance("//oai_dc:dc");
            xp.addNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
            Element dcElement = (Element) xp.selectSingleNode(resultFoxmlDocument);
            System.out.println(dcElement);
            Namespace namespace = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
            dcElement.addNamespaceDeclaration(namespace);
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
    }

    private void resolvePersons(Document comaDocument) throws JDOMException {
        List l = XPath.newInstance("//Person").selectNodes(comaDocument);
        for (Object o : l){
            Element personElement = (Element) o;
            Element speaker = (Element) XPath.newInstance("//Speaker[@Id='" + personElement.getText() + "']").selectSingleNode(comaDocument);
            personElement.setAttribute("Sigle", speaker.getChildText("Sigle"));
        }
    }
    
    
    
    
}
