/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild.corpuscatalog;

import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class CalculateSizes {

    String CATALOG_FILE = "S:\\TP-Z2\\DATEN\\EXMARaLDACorpusCatalog.xml";
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
            try {
                new CalculateSizes().doit();
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

    private void doit() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        Document CATALOG_DOCUMENT = FileIO.readDocumentFromLocalFile(CATALOG_FILE);
        List l = XPath.newInstance("//resource[contains(@type,'exmaralda') and descendant::exmaralda-coma[@type='local']]").selectNodes(CATALOG_DOCUMENT);
        for (Object o : l){
            Element resourceElement = (Element)o;
            Element comaCorpusElement = (Element) XPath.newInstance("descendant::exmaralda-coma[@type='local']").selectSingleNode(resourceElement);
            String url = comaCorpusElement.getAttributeValue("url").substring(7);
            System.out.println("Processing " + url);
            Document comaDocument = FileIO.readDocumentFromLocalFile(url);
            
            // count Communications
            int numberOfCommunications = XPath.newInstance("//Communication").selectNodes(comaDocument).size();            
            System.out.println(numberOfCommunications + " Communications");
            if (XPath.newInstance("descendant::size[@unit='communications']").selectSingleNode(resourceElement)==null){
                Element sizeElement = new Element("size");
                sizeElement.setAttribute("unit", "communications");                
                int index = resourceElement.indexOf(
                        (Element) XPath.newInstance("descendant::short-description[last()]").selectSingleNode(resourceElement));
                resourceElement.addContent(index+1, sizeElement);
            }
            Element sizeCommunications = (Element) XPath.newInstance("descendant::size[@unit='communications']").selectSingleNode(resourceElement);
            sizeCommunications.setText(Integer.toString(numberOfCommunications));

            // count speakers
            int numberOfSpeakers = XPath.newInstance("//Speaker").selectNodes(comaDocument).size();            
            System.out.println(numberOfSpeakers + " Speakers");
            if (XPath.newInstance("descendant::size[@unit='speakers']").selectSingleNode(resourceElement)==null){
                Element sizeElement = new Element("size");
                sizeElement.setAttribute("unit", "speakers");                
                int index = resourceElement.indexOf(
                        (Element) XPath.newInstance("descendant::short-description[last()]").selectSingleNode(resourceElement));
                resourceElement.addContent(index+1, sizeElement);
            }
            Element sizeSpeakers = (Element) XPath.newInstance("descendant::size[@unit='speakers']").selectSingleNode(resourceElement);
            sizeSpeakers.setText(Integer.toString(numberOfSpeakers));
        
            // count transcriptions
            int numberOfTranscriptions = XPath.newInstance("//Transcription[Description/Key[@Name='segmented']='true']").selectNodes(comaDocument).size();            
            System.out.println(numberOfTranscriptions + " Transcriptions");
            if (XPath.newInstance("descendant::size[@unit='transcriptions']").selectSingleNode(resourceElement)==null){
                Element sizeElement = new Element("size");
                sizeElement.setAttribute("unit", "transcriptions");                
                int index = resourceElement.indexOf(
                        (Element) XPath.newInstance("descendant::short-description[last()]").selectSingleNode(resourceElement));
                resourceElement.addContent(index+1, sizeElement);
            }
            Element sizeTranscriptions = (Element) XPath.newInstance("descendant::size[@unit='transcriptions']").selectSingleNode(resourceElement);
            sizeTranscriptions.setText(Integer.toString(numberOfTranscriptions));
            
            // count words
            int numberOfWords = 0;
            List l2 = XPath.newInstance("//Transcription/Description/Key[starts-with(@Name,'#') and ends-with(@Name,':w')]").selectNodes(comaDocument);
            for (Object o2 : l2){
                Element e2 = (Element)o2;
                numberOfWords+=Integer.parseInt(e2.getText());
            }
            System.out.println(numberOfWords + " Words");
            if (numberOfWords>0){
                if (XPath.newInstance("descendant::size[@unit='transcribed words']").selectSingleNode(resourceElement)==null){
                    Element sizeElement = new Element("size");
                    sizeElement.setAttribute("unit", "transcribed words");                
                    int index = resourceElement.indexOf(
                            (Element) XPath.newInstance("descendant::short-description[last()]").selectSingleNode(resourceElement));
                    resourceElement.addContent(index+1, sizeElement);
                }
                Element sizeWords = (Element) XPath.newInstance("descendant::size[@unit='transcribed words']").selectSingleNode(resourceElement);
                sizeWords.setText(Integer.toString(numberOfWords));                
            }

            // count recordings
            Element mmf = resourceElement.getChild("media-master-format");
            if (mmf!=null){
                String masterSuffix = mmf.getAttributeValue("suffix");
                List l3 = XPath.newInstance("//Recording[descendant::NSLink[ends-with(text(),'" + masterSuffix + "')]]").selectNodes(comaDocument);
                int numberOfRecordings = l3.size();
                int totalRecordingDuration = 0;
                for (Object o3 : l3){
                    Element e3 = (Element)o3;
                    if (e3.getChild("RecordingDuration")!=null){
                        totalRecordingDuration+=Integer.parseInt(e3.getChildText("RecordingDuration"));
                    } else {
                        totalRecordingDuration = 0;
                        break;
                    }
                }
                System.out.println(numberOfRecordings + " Recordings");
                if (numberOfRecordings>0){
                    if (XPath.newInstance("descendant::size[@unit='recordings']").selectSingleNode(resourceElement)==null){
                        Element sizeElement = new Element("size");
                        sizeElement.setAttribute("unit", "recordings");                
                        int index = resourceElement.indexOf(
                                (Element) XPath.newInstance("descendant::short-description[last()]").selectSingleNode(resourceElement));
                        resourceElement.addContent(index+1, sizeElement);
                    }
                    Element sizeRecordings = (Element) XPath.newInstance("descendant::size[@unit='recordings']").selectSingleNode(resourceElement);
                    sizeRecordings.setText(Integer.toString(numberOfRecordings));
                }

                if (totalRecordingDuration>0){
                    if (XPath.newInstance("descendant::size[@unit='miliseconds of recording']").selectSingleNode(resourceElement)==null){
                        Element sizeElement = new Element("size");
                        sizeElement.setAttribute("unit", "miliseconds of recording");                
                        int index = resourceElement.indexOf(
                                (Element) XPath.newInstance("descendant::short-description[last()]").selectSingleNode(resourceElement));
                        resourceElement.addContent(index+1, sizeElement);
                    }
                    Element sizeDuration = (Element) XPath.newInstance("descendant::size[@unit='miliseconds of recording']").selectSingleNode(resourceElement);
                    sizeDuration.setText(Integer.toString(totalRecordingDuration));  

                }
            }
        }
        
        FileIO.writeDocumentToLocalFile(CATALOG_FILE, CATALOG_DOCUMENT);
    }

}
