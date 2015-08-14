/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.exakt.exmaraldaSearch;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import org.exmaralda.exakt.search.AbstractXMLFileListCorpus;
import org.exmaralda.partitureditor.jexmaralda.StringUtilities;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public abstract class AbstractCOMACorpus extends AbstractXMLFileListCorpus implements COMACorpusInterface {
    

    Document comaDocument;
    
    static String UNDEFINED = "#undefined";
    static String XPATH_TO_TRANSCRIPTION_URL = "//Transcription[Description/Key[@Name='segmented']/text()='true']/NSLink";
    static String XPATH_TO_SEGMENTATION_NAMES = "//segmented-tier[@type='t']/segmentation/@name";
    static String XPATH_TO_ANNOTATION_NAMES = "//annotation/@name";
    static String XPATH_TO_DESCRIPTION_NAMES = "//segmented-tier[@type='d']/@category";
    String XPATH_TO_SEARCHABLE_SEGMENT = "//segmentation[@name='SpeakerContribution_Event']/ts";
    static String XPATH_TO_SEGMENT_NAMES = "//*[self::ts or self::ats or self::nts]/@n";
    
    Hashtable<String,Element> speakerMappings = new Hashtable<String,Element>();
    Hashtable<String,Element> communicationMappings = new Hashtable<String,Element>();
    Hashtable<String,Element> transcriptionMappings = new Hashtable<String,Element>();
    
    Hashtable<String,XPath> speakerAttributes = new Hashtable<String,XPath>();
    Hashtable<String,XPath> communicationAttributes = new Hashtable<String,XPath>();
    Hashtable<String,XPath> transcriptionAttributes = new Hashtable<String,XPath>();        
    
    static String[][] FIXED_SPEAKER_ATTRIBUTES = {
                                                        {"Sigle*","Sigle/text()"},
                                                        {"KnownHuman*","KnownHuman/text()"},
                                                        {"Pseudo*","Pseudo/text()"},
                                                        {"Sex*","Sex/text()"}
                                                    };
    static String[][] FIXED_COMMUNICATION_ATTRIBUTES = {
                                                        {"Name*","@Name"},
                                                        {"City*", "Location/City/text()"},
                                                        {"Country*", "Location/Country/text()"},
                                                        {"Date*", "Location/Period/PeriodStart/text()"},
                                                        {"Duration*", "Location/Period/Duration/text()"},
                                                    };
    static String[][] FIXED_TRANSCRIPTION_ATTRIBUTES = {
                                                        {"Name*","Name/text()"},
                                                        {"Filename*","Filename/text()"}
                                                    };
    
    HashSet<String> segmentationNames = new HashSet<String>();
    HashSet<String> annotationNames = new HashSet<String>();
    HashSet<String> descriptionNames = new HashSet<String>();
    HashSet<String> segmentNames = new HashSet<String>();

    void fetchAttributes(String xpathToStartElement, Hashtable<String, XPath> index, String[][] fixedAttributes) {
        String searchString = xpathToStartElement + "/Description/Key/@Name";
        try {
            XPath xp = XPath.newInstance(searchString);
            List<Attribute> allAttributes = xp.selectNodes(comaDocument);
            for (Attribute a : allAttributes) {
                String attributeName = a.getValue();
                if (!index.containsKey(attributeName)) {
                    // changed 07-06-2011: need to escape the attribute
                    String an = StringUtilities.toXMLString(attributeName);
                    String xpathToValueString = "Description/Key[@Name='" + an + "']/text()";
                    if (an.contains("'")) {
                        xpathToValueString = "Description/Key[@Name=\"" + an + "\"]/text()";
                    }
                    XPath xpathToValue = XPath.newInstance(xpathToValueString);
                    index.put(attributeName, xpathToValue);
                }
            }
            for (String[] fa : fixedAttributes) {
                XPath xp2 = XPath.newInstance(fa[1]);
                index.put(fa[0], xp2);
            }
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
    }

    void fetchSpeakerLanguageAttributes() {
        try {
            String xpathString = "//Speaker/Language/@type";
            List l = XPath.newInstance(xpathString).selectNodes(comaDocument);
            HashSet<String> allLanguageTypeNames = new HashSet<String>();
            for (Object o : l) {
                Attribute a = (Attribute) o;
                allLanguageTypeNames.add(a.getValue());
            }
            xpathString = "//Speaker/Language/@Type";
            l = XPath.newInstance(xpathString).selectNodes(comaDocument);
            for (Object o : l) {
                Attribute a = (Attribute) o;
                allLanguageTypeNames.add(a.getValue());
            }

            
            for (String languageTypeName : allLanguageTypeNames) {
                // changed 07-06-2011: need to escape the attribute
                String ltn = StringUtilities.toXMLString(languageTypeName);
                String locationPath = "Language[@type='" + ltn + "' or @Type='" + ltn + "']/";
                if (ltn.contains("'")) {
                    locationPath = "Language[@type=\"" + ltn + "\" or @Type=\"" + ltn + "\"]/";
                }
                String languageCodeString = locationPath + "LanguageCode/text()";
                XPath languagePath = XPath.newInstance(languageCodeString);
                speakerAttributes.put(languageTypeName + ":Code", languagePath);
                
                // added 16-02-2012: also take care of descriptions underneath languages
                String keyPath = locationPath + "/Description/Key/@Name";
                System.out.println("KeyPath: " + keyPath);
                XPath xp = XPath.newInstance("//Speaker/" + keyPath);
                HashSet<String> allDescriptionNames = new HashSet<String>();
                for (Object o : xp.selectNodes(comaDocument)){
                        Attribute a = (Attribute) o;
                        allDescriptionNames.add(a.getValue());
                }
                for (String descriptionName : allDescriptionNames){
                    XPath descriptionPath = XPath.newInstance(locationPath + "/Description/Key[@Name='" + descriptionName + "']/text()");
                    speakerAttributes.put(languageTypeName + ":" + descriptionName, descriptionPath);
                }
            }
            
            
            
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
    }

    void fetchSpeakerLocationAttributes() {
        try {
            // Find out what types there are - we need both Type and type
            // because Kai and me are honks
            String xpathString = "//Speaker/Location/@type";
            List l = XPath.newInstance(xpathString).selectNodes(comaDocument);
            HashSet<String> allLocationTypeNames = new HashSet<String>();
            for (Object o : l) {
                Attribute a = (Attribute) o;
                allLocationTypeNames.add(a.getValue());
            }
            xpathString = "//Speaker/Location/@Type";
            l = XPath.newInstance(xpathString).selectNodes(comaDocument);            
            for (Object o : l) {
                Attribute a = (Attribute) o;
                allLocationTypeNames.add(a.getValue());
            }

            for (String locationTypeName : allLocationTypeNames) {
                // changed 07-06-2011: need to escape the attribute
                // changed 16-02-2012: type and Type
                String ltn = StringUtilities.toXMLString(locationTypeName);
                String locationPath = "Location[@type='" + ltn + "' or @Type='" + ltn + "']/";
                if (ltn.contains("'")) {
                    locationPath = "Location[@type=\"" + ltn + "\" or @Type=\"" + ltn + "\"]/";
                }
                String cityPathString = locationPath + "City/text()";
                XPath cityPath = XPath.newInstance(cityPathString);
                speakerAttributes.put(locationTypeName + ":City", cityPath);
                String countryPathString = locationPath + "Country/text()";
                XPath countryPath = XPath.newInstance(countryPathString);
                speakerAttributes.put(locationTypeName + ":Country", countryPath);
                String datePathString = locationPath + "Period/PeriodStart/text()";
                XPath datePath = XPath.newInstance(datePathString);
                speakerAttributes.put(locationTypeName + ":Date", datePath);
                String durationPathString = locationPath + "Period/Duration/text()";
                XPath durationPath = XPath.newInstance(durationPathString);
                speakerAttributes.put(locationTypeName + ":Duration", durationPath);
                
                // added 16-02-2012: also take care of desceriptions underneath languages
                String keyPath = locationPath + "/Description/Key/@Name";
                System.out.println("KeyPath: " + keyPath);
                XPath xp = XPath.newInstance("//Speaker/" + keyPath);
                HashSet<String> allDescriptionNames = new HashSet<String>();
                for (Object o : xp.selectNodes(comaDocument)){
                        Attribute a = (Attribute) o;
                        allDescriptionNames.add(a.getValue());
                }
                for (String descriptionName : allDescriptionNames){
                    XPath descriptionPath = XPath.newInstance(locationPath + "/Description/Key[@Name='" + descriptionName + "']/text()");
                    speakerAttributes.put(locationTypeName + ":" + descriptionName, descriptionPath);
                }
                
            }
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
    }
    
    public String uniqueSpeakerIdentifier;

    @Override
    public HashSet<String> getAnnotationNames() {
        return annotationNames;
    }

    @Override
    public Set<String> getCommunicationAttributes() {
        return communicationAttributes.keySet();
    }

    @Override
    public String getCommunicationData(String transcriptionLocator, String attributeName) {
        XPath xpath = communicationAttributes.get(attributeName);
        if (xpath == null) {
            return UNDEFINED;
        }
        Element communicationElement = communicationMappings.get(transcriptionLocator);
        if (communicationElement == null) {
            return UNDEFINED;
        }
        String returnValue = UNDEFINED;
        try {
            // changed 16-02-2012
            /*Object o = xpath.selectSingleNode(communicationElement);
            if (o == null) {
                return UNDEFINED;
            }
            if (o instanceof Text) {
                returnValue = ((Text) o).getText();
            } else if (o instanceof Attribute) {
                returnValue = ((Attribute) o).getValue();
            }*/
            List l = xpath.selectNodes(communicationElement);
            if (l.isEmpty()){
                return UNDEFINED;
            }
            returnValue = "";
            int count=0;
            for (Object o : l){
                if (o instanceof Text) {
                    returnValue+= ((Text) o).getText();
                } else if (o instanceof Attribute) {
                    returnValue+= ((Attribute) o).getValue();
                }
                // this one is very suspicious
                // changed 17-10-2013, bugfix Anne-Sophie Ghyselen
                if (count<l.size()-1){
                    returnValue+=" ";
                }
                count++;
            }
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
        return returnValue;
    }

    @Override
    public HashSet<String> getDescriptionNames() {
        return descriptionNames;
    }
    
    @Override
    public int getNumberOfSegmentChains() {
        return getNumberOfSearchableSegments();
    }

    @Override
    public int getNumberOfTranscriptions() {
        return getNumberOfCorpusComponents();
    }

    public HashSet<String> getSegmentNames() {
        return segmentNames;
    }

    @Override
    public HashSet<String> getSegmentationNames() {
        return segmentationNames;
    }

    @Override
    public Set<String> getSpeakerAttributes() {
        return speakerAttributes.keySet();
    }

    public Element getSpeakerData() {
        return null;
    }

    @Override
    public String getSpeakerData(String transcriptionLocator, String speakerID, String attributeName) {
        XPath xpath = speakerAttributes.get(attributeName);
        if (xpath == null) {
            return UNDEFINED;
        }
        String combinedID = transcriptionLocator + "#" + speakerID;
        Element speakerElement = speakerMappings.get(combinedID);
        if (speakerElement == null) {
            return UNDEFINED;
        }
        String returnValue = UNDEFINED;
        try {
            /*Text text = (Text) (xpath.selectSingleNode(speakerElement));
            if (text == null) {
                return UNDEFINED;
            }
            returnValue = text.getText();*/
            List l = xpath.selectNodes(speakerElement);
            if (l.isEmpty()){
                return UNDEFINED;
            }
            returnValue = "";
            int count=0;
            for (Object o : l){
                if (o instanceof Text) {
                    returnValue+= ((Text) o).getText();
                } else if (o instanceof Attribute) {
                    returnValue+= ((Attribute) o).getValue();
                }
                // this one is very suspicious
                // changed 15-10-2013, bugfix Anne-Sophie Ghyselen
                if (count<l.size()-1){
                    returnValue+=" ";
                }
                count++;
            }
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
        
        
        return returnValue;
    }

    @Override
    public Set<String> getTranscriptionAttributes() {
        return transcriptionAttributes.keySet();
    }

    @Override
    public String getTranscriptionData(String transcriptionLocator, String attributeName) {
        XPath xpath = transcriptionAttributes.get(attributeName);
        if (xpath == null) {
            return UNDEFINED;
        }
        Element transcriptionElement = transcriptionMappings.get(transcriptionLocator);
        if (transcriptionElement == null) {
            return UNDEFINED;
        }
        String returnValue = UNDEFINED;
        try {
            Text text = (Text) (xpath.selectSingleNode(transcriptionElement));
            if (text == null) {
                return UNDEFINED;
            }
            returnValue = text.getText();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
        return returnValue;
    }

    @Override
    public String getUniqueSpeakerIdentifier() {
        return uniqueSpeakerIdentifier;
    }

    @Override
    public String getWordSegmentName() {
        for (String s : segmentNames) {
            if (s.endsWith(":w")) {
                return s;
            }
        }
        return null;
    }

    @Override
    public boolean isWordSegmented() {
        for (String s : segmentNames) {
            if (s.endsWith(":w")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setXPathToSearchableSegment(String xp) {
        XPATH_TO_SEARCHABLE_SEGMENT = xp;
    }

    public void setWordSegmentName(String wordSegmentName) {
        segmentNames.add(wordSegmentName);
    }


}
