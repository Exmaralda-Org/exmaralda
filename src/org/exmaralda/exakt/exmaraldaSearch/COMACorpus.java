/*
 * COMACorpus.java
 *
 * Created on 8. Januar 2007, 10:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch;

import java.net.*;
import java.io.*;
import org.jdom.*;
import org.jdom.xpath.*;
import java.util.*;

/**
 *
 * @author thomas
 */
//public class COMACorpus extends AbstractXMLFileListCorpus implements COMACorpusInterface {
public class COMACorpus extends AbstractCOMACorpus  {
    
    URI baseURI;    
    //Document comaDocument;
    private String corpusName = "";
    private String corpusPath = "";
    public String explicitlySetWordSegmentName = null;
    //public String uniqueSpeakerIdentifier;
        
    /** Creates a new instance of COMACorpus */
    public COMACorpus() {
    }

    public void readCorpus(File file) throws JDOMException, IOException {
        readCorpus(file,true);
    }

    
    public void readCorpus(File file, boolean index) throws JDOMException, IOException {
        corpusPath = file.getAbsolutePath();
        //System.out.println("Corpus path set to " + corpusPath);
        baseURI = file.getParentFile().toURI();
        comaDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromLocalFile(file);
        corpusName = comaDocument.getRootElement().getAttributeValue("Name");
        uniqueSpeakerIdentifier = comaDocument.getRootElement().getAttributeValue("uniqueSpeakerDistinction");  
        if (uniqueSpeakerIdentifier==null){
            throw new IOException("No speaker identifier in COMA file");
        }
        uniqueSpeakerIdentifier = uniqueSpeakerIdentifier.substring(10);
        
        fireCorpusInit(0, "Started reading " + file.getAbsolutePath() + ".");
        XPath xpath = XPath.newInstance(XPATH_TO_TRANSCRIPTION_URL);
        List transcriptionList = xpath.selectNodes(comaDocument);
        for (int pos=0; pos<transcriptionList.size(); pos++){
            Element node = (Element)(transcriptionList.get(pos));
            String transcriptionPath = node.getText();
            File fullPath = new File(baseURI.resolve(transcriptionPath));
            getFileList().addElement(fullPath);
            Element communicationElement = node.getParentElement().getParentElement();
            communicationMappings.put(fullPath.getAbsolutePath(), communicationElement);
            Element transcriptionElement = node.getParentElement();
            transcriptionMappings.put(fullPath.getAbsolutePath(), transcriptionElement);
        }
        fireCorpusInit(0.01, file.getAbsolutePath() + " read successfully.");
        if (index){
            index();
        }
        fetchSpeakerLocationAttributes();
        fetchSpeakerLanguageAttributes();
        fetchAttributes("//Speaker", speakerAttributes, FIXED_SPEAKER_ATTRIBUTES);
        fetchAttributes("//Communication", communicationAttributes, FIXED_COMMUNICATION_ATTRIBUTES);
        fetchAttributes("//Transcription", transcriptionAttributes, FIXED_TRANSCRIPTION_ATTRIBUTES);
    }
    
    public String getXPathToSearchableSegment() {
        return XPATH_TO_SEARCHABLE_SEGMENT;
    }
    
    public void setXPathToSearchableSegment(String xp){
        XPATH_TO_SEARCHABLE_SEGMENT = xp;
    }
    
    @Override
    public void index() throws JDOMException, IOException {
        Hashtable<String,Element> speakersInComaIndex = new Hashtable<String,Element>();
        String speakersInComa = "//Speaker/Sigle";
        XPath speakersInComaXPath = XPath.newInstance(speakersInComa);
        List speakersInComaList = speakersInComaXPath.selectNodes(comaDocument);
        for (Object o : speakersInComaList){
            Element sigleElement = (Element)o;
            String sigleText = sigleElement.getText();
            Element speakerElement = sigleElement.getParentElement();
            speakersInComaIndex.put(sigleText, speakerElement);
        }
        
        
        XPath xpath = XPath.newInstance(getXPathToSearchableSegment());        
        XPath speakerIDXPath = XPath.newInstance(uniqueSpeakerIdentifier);
        XPath segmentationNameXPath = XPath.newInstance(XPATH_TO_SEGMENTATION_NAMES);
        XPath annotationNameXPath = XPath.newInstance(XPATH_TO_ANNOTATION_NAMES);
        XPath descriptionNameXPath = XPath.newInstance(XPATH_TO_DESCRIPTION_NAMES);
        XPath segmentNameXPath = XPath.newInstance(XPATH_TO_SEGMENT_NAMES);
        int countSegments = 0;
        for (int pos=0; pos<getNumberOfCorpusComponents(); pos++){
            File xmlFile = getFileList().elementAt(pos);
            Document xmlDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromLocalFile(xmlFile);
            
            // get the segmentation names
            List segNames = segmentationNameXPath.selectNodes(xmlDocument);
            for (Object o : segNames){
                Attribute a = (Attribute)o;
                segmentationNames.add(a.getValue());
            }
            
            // get the annotation names
            List annNames = annotationNameXPath.selectNodes(xmlDocument);
            for (Object o : annNames){
                Attribute a = (Attribute)o;
                annotationNames.add(a.getValue());
            }
            
            // get the description names
            List descNames = descriptionNameXPath.selectNodes(xmlDocument);
            for (Object o : descNames){
                Attribute a = (Attribute)o;
                getDescriptionNames().add(a.getValue());
            }

            // get the segment names
            List segmNames = segmentNameXPath.selectNodes(xmlDocument);
            for (Object o : segmNames){
                Attribute a = (Attribute)o;
                getSegmentNames().add(a.getValue());
            }

            // get the searchable segments and count them
            List segmentList = xpath.selectNodes(xmlDocument);
            countSegments+=segmentList.size();
            
            // Make a hashtable in which a combination of 
            // transcription speaker ids and the path of the transcription file
            // is mapped to the corresponding speaker element of the CoMa file            
            XPath speakerXPath = XPath.newInstance("//speaker");
            List speakerList = speakerXPath.selectNodes(xmlDocument);
            for(Object o : speakerList){
                Element speaker = (Element)o;
                String id = speaker.getAttributeValue("id");
                Element uniqueID = (Element)(speakerIDXPath.selectSingleNode(speaker));
                if (uniqueID==null){
                    throw new JDOMException("No unique speaker ID for " + id);
                }
                String identifier = uniqueID.getText();
                Element speakerData = speakersInComaIndex.get(identifier);
                if (speakerData!=null){
                    String combinedID = xmlFile.getAbsolutePath() + "#" + id;
                    speakerMappings.put(combinedID, speakerData);                
                }
            }                        
            fireCorpusInit((double)pos/(double)getNumberOfCorpusComponents(), xmlFile.getAbsolutePath() +  " indexed.");
        }
        setNumberOfSearchableSegments(countSegments);                
    }
    
    public String getCorpusName() {
        return corpusName;
    }

    public String getCorpusPath() {
        return corpusPath;
    }
    
    


    
}
