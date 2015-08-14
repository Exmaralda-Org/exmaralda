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
import org.exmaralda.exakt.search.CorpusComponentInterface;
import org.exmaralda.exakt.search.SearchableSegmentLocatorInterface;
import org.jdom.*;
import org.jdom.xpath.*;
import java.util.*;
import org.exmaralda.exakt.search.XMLCorpusComponent;
import org.exmaralda.exakt.search.XMLSearchableSegmentLocator;

/**
 *
 * @author thomas
 */
//
public class COMARemoteCorpus extends AbstractCOMACorpus {
    

    URI baseURI;    
    private String corpusName = "";
    private String corpusPath = "";
    
    public Vector<URI> uriList = new Vector<URI>();

    /** Creates a new instance of COMACorpus */
    public COMARemoteCorpus() {
    }
    
    public void readCorpus(URL url) throws JDOMException, IOException, URISyntaxException {
        corpusPath = url.toString();
        //System.out.println("Corpus path set to " + corpusPath);
        //baseURI = new URI(url.toString().substring(0,url.toString().lastIndexOf("/")));
        baseURI = new URI(url.toString());
        //System.out.println("BASE: " + baseURI.toString());
        comaDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromURL(corpusPath);
        corpusName = comaDocument.getRootElement().getAttributeValue("Name");
        uniqueSpeakerIdentifier = comaDocument.getRootElement().getAttributeValue("uniqueSpeakerDistinction");             
        if (uniqueSpeakerIdentifier==null){
            throw new IOException("No speaker identifier in COMA file");
        }
        uniqueSpeakerIdentifier = uniqueSpeakerIdentifier.substring(10);
        
        fireCorpusInit(0, "Started reading " + corpusPath + ".");
        XPath xpath = XPath.newInstance(XPATH_TO_TRANSCRIPTION_URL);
        List transcriptionList = xpath.selectNodes(comaDocument);
        for (int pos=0; pos<transcriptionList.size(); pos++){
            Element node = (Element)(transcriptionList.get(pos));
            String transcriptionPath = node.getText();
            URI fullURI = baseURI.resolve(transcriptionPath);
            //System.out.println("FULL: " + fullURI.toString());
            uriList.add(fullURI);
            //File fullPath = new File(baseURI.resolve(transcriptionPath));
            //getFileList().addElement(fullPath);
            Element communicationElement = node.getParentElement().getParentElement();
            communicationMappings.put(fullURI.toString(), communicationElement);
            Element transcriptionElement = node.getParentElement();
            transcriptionMappings.put(fullURI.toString(), transcriptionElement);
        }
        //fireCorpusInit(0.01, file.getAbsolutePath() + " read successfully.");
        index();
        fetchSpeakerLocationAttributes();
        fetchSpeakerLanguageAttributes();
        fetchAttributes("//Speaker", speakerAttributes, FIXED_SPEAKER_ATTRIBUTES);
        fetchAttributes("//Communication", communicationAttributes, FIXED_COMMUNICATION_ATTRIBUTES);
        fetchAttributes("//Transcription", transcriptionAttributes, FIXED_TRANSCRIPTION_ATTRIBUTES);
    }
    
    public int getNumberOfCorpusComponents() {
        return uriList.size();
    }
    
    
    public String getXPathToSearchableSegment() {
        return XPATH_TO_SEARCHABLE_SEGMENT;
    }
    
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
            //File xmlFile = getFileList().elementAt(pos);
            URI xmlURI = uriList.elementAt(pos);
            Document xmlDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromURL(xmlURI.toString());
            
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
                    String combinedID = xmlURI.toString() + "#" + id;
                    speakerMappings.put(combinedID, speakerData);                
                }
            }                        
            fireCorpusInit((double)pos/(double)getNumberOfCorpusComponents(), xmlURI.toString() +  " indexed.");
        }
        setNumberOfSearchableSegments(countSegments);                
    }

    public String getCorpusName() {
        return corpusName;
    }

    public String getCorpusPath() {
        return corpusPath;
    }


    public CorpusComponentInterface next() {
        XMLCorpusComponent returnValue = new XMLCorpusComponent(getXPathToSearchableSegment());
        //File xmlFile = getFileList().elementAt(counter);
        URI xmlURI = uriList.elementAt(counter);
        try {
            returnValue.readCorpusComponent(xmlURI.toURL());
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
        counter++;
        return returnValue;
    }

    public CorpusComponentInterface getCorpusComponent(SearchableSegmentLocatorInterface id) {
        XMLSearchableSegmentLocator locator = (XMLSearchableSegmentLocator)id;
        String corpusComponentPath = (String)(locator.getCorpusComponentLocator());
        //File transcriptionFile = new File(corpusComponentPath);
        //System.out.println("CORPUS-COMPONENT-PATH: " + corpusComponentPath);
        XMLCorpusComponent returnValue = new XMLCorpusComponent(getXPathToSearchableSegment());
        try {
            //returnValue.readCorpusComponent(transcriptionFile);
            returnValue.readCorpusComponent(new URL(corpusComponentPath));
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
        return returnValue;
    }

    @Override
    public void readCorpus(File file) throws JDOMException, IOException {
        // we don't want this. It is only there because the class hierarchy is shite
    }



    
}
