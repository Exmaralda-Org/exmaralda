/*
 * AIFConverter.java
 *
 * Created on 2. Juli 2003, 13:52
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;


import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

// For write operation
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author  thomas
 */
public class AIFConverter {
    
    static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    
    static String XML_DOCTYPE_AG = "<!DOCTYPE AGSet SYSTEM \"ag.dtd\">";

    static final String AG2EX_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/ag2exmaralda.xsl";
    static final String EX2AG_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/exmaralda2ag.xsl";
    
    
    /** Creates a new instance of AIFConverter */
    public AIFConverter() {
    }
    
    
    public static String BasicTranscriptionToAIF(BasicTranscription t) throws SAXException, 
                                                                         IOException, 
                                                                         ParserConfigurationException, 
                                                                         TransformerConfigurationException, 
                                                                         TransformerException {
        t.getBody().getCommonTimeline().completeTimes();
        StylesheetFactory ssf = new StylesheetFactory();        
        String aifTrans = ssf.applyInternalStylesheetToString(EX2AG_STYLESHEET, t.toXML());      
        return aifTrans;        
    }
    
    /*public static String BasicTranscriptionToAIF(BasicTranscription t){
        StringBuffer sb = new StringBuffer();
        sb.append("<AGSet id=\"exmaralda\" version=\"1.0\" xmlns=\"http://www.ldc.upenn.edu/atlas/ag/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"");
        sb.append(" xmlns:dc=\"http://purl.org/DC/documents/rec-dces-19990702.htm\">");
        sb.append(headToAIF(t.getHead()));
        sb.append("<Timeline id=\"exmaralda:timeline1\" xlink:href=\"");
        sb.append(t.getHead().getMetaInformation().getReferencedFile());
        sb.append("\"/>");
        //sb.append("</Timeline>");
        sb.append(basicBodyToAIF(t.getBody()));
        sb.append("</AGSet>");
        return sb.toString();
    }*/

    public BasicTranscription readAIFFromFile(String filename) throws JexmaraldaException, 
                                                                       SAXException, 
                                                                       IOException, 
                                                                       ParserConfigurationException, 
                                                                       TransformerConfigurationException, 
                                                                       TransformerException {
        StylesheetFactory ssf = new StylesheetFactory(true);        
        String basicTrans = ssf.applyInternalStylesheetToExternalXMLFile(AG2EX_STYLESHEET, filename);      
        BasicTranscription bt = new BasicTranscription();
        bt.BasicTranscriptionFromString(basicTrans);
        return bt;
    }
    
    /** writes an AIF version of this BasicTranscription to a file */
    public static void writeAIFToFile(BasicTranscription t, String filename) throws IOException {
        try {
            System.out.println("started writing document...");
            FileOutputStream fos = new FileOutputStream(new File(filename));
            //fos.write(XML_HEADER.getBytes("UTF-8"));        
            //fos.write(XML_DOCTYPE_AG.getBytes("UTF-8"));
            String aifString = BasicTranscriptionToAIF(t);
            aifString = aifString.replaceAll("xmlns=\\\"\\\"", "");
            fos.write(aifString.getBytes("UTF-8"));
            fos.close();
            System.out.println("document written.");
        } catch (FileNotFoundException ex) {
            throw new IOException(ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            throw new IOException(ex.getMessage());
        } catch (TransformerConfigurationException ex) {
            throw new IOException(ex.getMessage());
        } catch (TransformerException ex) {
            throw new IOException(ex.getMessage());
        } catch (SAXException ex) {
            throw new IOException(ex.getMessage());
        } catch (ParserConfigurationException ex) {
            throw new IOException(ex.getMessage());
        } 
    }
    
    /*private static String headToAIF(Head head){
        StringBuffer sb = new StringBuffer();
        sb.append("<Metadata>");
        sb.append(metaInformationToAIF(head.getMetaInformation()));
        sb.append(speakertableToAIF(head.getSpeakertable()));
        sb.append("</Metadata>");
        return sb.toString();        
    }

    private static String metaInformationToAIF(MetaInformation mi){
        StringBuffer sb = new StringBuffer();
        sb.append("<MetadataElement name=\"dc:title\">");
        sb.append(mi.getTranscriptionName());
        sb.append("</MetadataElement>");
        sb.append("<MetadataElement name=\"exmaralda.projectName\">");
        sb.append(mi.getProjectName());
        sb.append("</MetadataElement>");
        sb.append("<MetadataElement name=\"exmaralda.transcriptionConvention\">");
        sb.append(mi.getTranscriptionConvention());
        sb.append("</MetadataElement>");
        sb.append("<MetadataElement name=\"exmaralda.comment\">");
        sb.append(mi.getComment());
        sb.append("</MetadataElement>");
        sb.append(udMetaInformationToAIF(mi.getUDMetaInformation(), "exmaralda"));
        return sb.toString();        
    }

    private static String udMetaInformationToAIF(UDInformationHashtable ud, String prefix){
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<ud.getNumberOfAttributes(); i++){
            String attribute = ud.getAttributeAt(i);
            String value = ud.getValueOfAttribute(attribute);
            sb.append("<MetadataElement name=\"");
            sb.append(prefix);
            sb.append(".");
            sb.append(attribute);
            sb.append("\">");
            sb.append(value);
            sb.append("</MetadataElement>");
        }        
        return sb.toString();        
    }
    
    
    private static String speakertableToAIF(Speakertable st){
        StringBuffer sb = new StringBuffer();
        for (int pos=0; pos<st.getNumberOfSpeakers(); pos++){
            Speaker speaker = st.getSpeakerAt(pos);
            String prefix = "exmaralda.speaker." + speaker.getID();
            sb.append("<MetadataElement name=\"");
            sb.append(prefix);
            sb.append(".abbreviation\">");
            sb.append(speaker.getAbbreviation());
            sb.append("</MetadataElement>");
            sb.append("<MetadataElement name=\"");
            sb.append(prefix);
            sb.append(".sex\">");
            sb.append(speaker.getSex());
            sb.append("</MetadataElement>");
            sb.append("<MetadataElement name=\"");
            sb.append(prefix);
            sb.append(".comment\">");
            sb.append(speaker.getComment());
            sb.append("</MetadataElement>");
            sb.append("<MetadataElement name=\"");
            sb.append(prefix);
            sb.append(".languagesUsed\">");
            sb.append(speaker.getLanguagesUsed().getLanguagesString());
            sb.append("</MetadataElement>");
            sb.append("<MetadataElement name=\"");
            sb.append(prefix);
            sb.append(".l1\">");
            sb.append(speaker.getL1().getLanguagesString());
            sb.append("</MetadataElement>");
            sb.append("<MetadataElement name=\"");
            sb.append(prefix);
            sb.append(".l2\">");
            sb.append(speaker.getL2().getLanguagesString());
            sb.append("</MetadataElement>");
            sb.append(udMetaInformationToAIF(speaker.getUDSpeakerInformation(), "exmaralda"));          
        }
        return sb.toString();        
    }
    
    private static String basicBodyToAIF(BasicBody bb){
        StringBuffer sb = new StringBuffer();
        sb.append("<AG id=\"exmaralda:ag1\" type=\"ExmaraldaBasicTranscription\" timeline=\"exmaralda:timeline1\">");
        sb.append("<Metadata>");
        for (int pos=0; pos<bb.getNumberOfTiers(); pos++){
            Tier t = bb.getTierAt(pos);
            sb.append(udMetaInformationToAIF(t.getUDTierInformation(),t.getID()));
        }
        sb.append("</Metadata>");
        sb.append(timelineToAIF(bb.getCommonTimeline()));
        for (int pos=0; pos<bb.getNumberOfTiers(); pos++){
            Tier t = bb.getTierAt(pos);
            sb.append(tierToAIF(t));
        }
        sb.append("</AG>");
        return sb.toString();        
    }
    
    private static String tierToAIF(Tier t){
        StringBuffer sb = new StringBuffer();
        for (int pos=0; pos<t.getNumberOfEvents(); pos++){
            Event e = t.getEventAt(pos);
            sb.append("<Annotation ");
            String id = t.getID() + "_" + Integer.toString(pos);
            sb.append("id=\"" + "exmaralda:ag1:" + id + "\" ");
            sb.append("type=\"" + t.getType() +"\" ");
            sb.append("start=\"exmaralda:ag1:" + e.getStart() + "\" ");
            sb.append("end=\"exmaralda:ag1:" + e.getEnd() + "\">");
            sb.append("<Feature name=\"tier\">");
            sb.append(t.getID());
            sb.append("</Feature>");
            sb.append("<Feature name=\"category\">");
            sb.append(t.getCategory());
            sb.append("</Feature>");
            if (t.getSpeaker()!=null){
                sb.append("<Feature name=\"speaker\">");
                sb.append(t.getSpeaker());
                sb.append("</Feature>");
            }
            sb.append(eventToAIF(e));
            sb.append("</Annotation>");
        }
        return sb.toString();        
    }

    private static String timelineToAIF(Timeline tl){
        StringBuffer sb = new StringBuffer();
        for (int pos=0; pos<tl.getNumberOfTimelineItems(); pos++){
            TimelineItem tli = tl.getTimelineItemAt(pos);
            sb.append("<Anchor id=\"");
            sb.append("exmaralda:ag1:" + tli.getID());
            sb.append("\"");
            if (tli.getTime()>=0){
                sb.append(" offset=\"");
                sb.append(Double.toString(tli.getTime()));
                sb.append("\" unit=\"sec\"");
            }
            sb.append("/>");            
        }
        return sb.toString();        
    }

   public static String eventToAIF(Event e){
        StringBuffer sb = new StringBuffer();
        sb.append("<Feature name=\"description\">");
        sb.append(e.getDescription());
        sb.append("</Feature>");
        if (!e.getMedium().equals("none")){
            sb.append("<Feature name=\"link-medium\">");
            sb.append(e.getMedium());
            sb.append("</Feature>");
            sb.append("<Feature name=\"link-url\">");
            sb.append(e.getURL());
            sb.append("</Feature>");
        }
/*        for (int pos=0; pos<udEventInformation.getNumberOfAttributes(); pos++){
            String attribute = udEventInformation.getAttributeAt(pos);
            String value = udEventInformation.getValueOfAttribute(attribute);
            sb.append("<Feature name=\"");
            sb.append(attribute);
            sb.append("\">");
            sb.append(value);
            sb.append("</Feature>");
        }*/
        /*return sb.toString();
    }    */
}
