/*
 * TEIMerger.java
 *
 * Created on 28. November 2006, 15:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import java.io.IOException;
import org.jdom.*;
import org.jdom.xpath.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.jexmaraldaswing.TransformationDialog;
import org.jdom.transform.*;
import org.xml.sax.SAXException;
//import net.sf.saxon.jdom.DocumentWrapper;



/**
 *
 * @author thomas
 */
public class TEIMerger {
    
    static String TEI_SKELETON_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/EXMARaLDA2TEI_Skeleton.xsl"; //Constants.BASICTRANSCRIPTION2TEISKELETONStylesheet;
    static String SC_TO_TEI_U_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/SegmentChain2TEIUtterance.xsl";; //Constants.SEGMENTCHAIN2TEIUTTERANCEStylesheet;
    static String SORT_AND_CLEAN_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/TEICleanAndSort.xsl";; //Constants.TEICLEANANDSORTStylesheet;

    static String TEI_SKELETON_STYLESHEET_NEW = "/org/exmaralda/tei/xml/EXMARaLDA2TEI_Skeleton.xsl"; //Constants.BASICTRANSCRIPTION2TEISKELETONStylesheet;
    static String SC_TO_TEI_U_STYLESHEET_NEW = "/org/exmaralda/tei/xml/SegmentChain2TEIUtterance.xsl";; //Constants.SEGMENTCHAIN2TEIUTTERANCEStylesheet;
    static String SORT_AND_CLEAN_STYLESHEET_NEW = "/org/exmaralda/tei/xml/TEICleanAndSort.xsl";; //Constants.TEICLEANANDSORTStylesheet;

    static String TEI_SKELETON_STYLESHEET_ISO = "/org/exmaralda/tei/xml/EXMARaLDA2ISOTEI_Skeleton.xsl"; //Constants.BASICTRANSCRIPTION2TEISKELETONStylesheet;
    static String SC_TO_TEI_U_STYLESHEET_ISO = "/org/exmaralda/tei/xml/SegmentChain2ISOTEIUtterance.xsl";; //Constants.SEGMENTCHAIN2TEIUTTERANCEStylesheet;
    static String SORT_AND_CLEAN_STYLESHEET_ISO = "/org/exmaralda/tei/xml/ISOTEICleanAndSort.xsl";; //Constants.TEICLEANANDSORTStylesheet;

    static String BODY_NODE = "//text";

    XSLTransformer transformer;
    XSLTransformer transformer2;
    XSLTransformer transformer3;
    
    boolean ISO = false;
    
    /** Creates a new instance of TEIMerger */
    public TEIMerger(String ss1, String ss2, String ss3) throws XSLTransformException {
        TEI_SKELETON_STYLESHEET = ss1;
        SC_TO_TEI_U_STYLESHEET = ss2;
        SORT_AND_CLEAN_STYLESHEET = ss3;
        transformer = new XSLTransformer(TEI_SKELETON_STYLESHEET);
        transformer2 = new XSLTransformer(SC_TO_TEI_U_STYLESHEET); 
        transformer3 = new XSLTransformer(SORT_AND_CLEAN_STYLESHEET);
    }
    
    public TEIMerger() throws XSLTransformException{
        java.io.InputStream is = getClass().getResourceAsStream(TEI_SKELETON_STYLESHEET);
        java.io.InputStream is2 = getClass().getResourceAsStream(SC_TO_TEI_U_STYLESHEET);
        java.io.InputStream is3 = getClass().getResourceAsStream(SORT_AND_CLEAN_STYLESHEET);
        transformer = new XSLTransformer(is);
        transformer2 = new XSLTransformer(is2);
        transformer3 = new XSLTransformer(is3);
    }

    public TEIMerger(boolean beISO) throws XSLTransformException{
        ISO = beISO;
        if (beISO){
            //java.io.InputStream is = getClass().getResourceAsStream(TEI_SKELETON_STYLESHEET_ISO);
            java.io.InputStream is2 = getClass().getResourceAsStream(SC_TO_TEI_U_STYLESHEET_ISO);
            //java.io.InputStream is3 = getClass().getResourceAsStream(SORT_AND_CLEAN_STYLESHEET_ISO);
            //transformer = new XSLTransformer(is);
            transformer2 = new XSLTransformer(is2);
            //transformer3 = new XSLTransformer(is3);
        }
    }
    

    public Document SegmentedTranscriptionToTEITranscription(Document segmentedTranscription,
                                                                    String nameOfDeepSegmentation,
                                                                    String nameOfFlatSegmentation)
                                                                    throws XSLTransformException, JDOMException, SAXException, ParserConfigurationException, IOException, TransformerException  {
        return SegmentedTranscriptionToTEITranscription(segmentedTranscription, nameOfDeepSegmentation, nameOfFlatSegmentation, false);
    }

    public Document SegmentedTranscriptionToTEITranscription(Document segmentedTranscription,
                                                             String nameOfDeepSegmentation, 
                                                             String nameOfFlatSegmentation,
                                                             boolean useNewStylesheets) throws XSLTransformException, JDOMException, SAXException, ParserConfigurationException, IOException, TransformerException  {
        return SegmentedTranscriptionToTEITranscription(segmentedTranscription, 
                nameOfDeepSegmentation, 
                nameOfFlatSegmentation, 
                useNewStylesheets, 
                false);
    }
    
    public Document SegmentedTranscriptionToTEITranscription(Document segmentedTranscription,
                                                             String nameOfDeepSegmentation, 
                                                             String nameOfFlatSegmentation,
                                                             boolean useNewStylesheets,
                                                             boolean includeFullText) throws XSLTransformException, JDOMException, SAXException, ParserConfigurationException, IOException, TransformerException {
        
        String skeleton_stylesheet = TEIMerger.TEI_SKELETON_STYLESHEET_NEW;
        if (ISO) skeleton_stylesheet = TEIMerger.TEI_SKELETON_STYLESHEET_ISO;

        String transform_stylesheet = TEIMerger.SC_TO_TEI_U_STYLESHEET_NEW;
        if (ISO) transform_stylesheet = TEIMerger.SC_TO_TEI_U_STYLESHEET_ISO;

        String sort_and_clean_stylesheet = TEIMerger.SORT_AND_CLEAN_STYLESHEET_NEW;
        if (ISO) sort_and_clean_stylesheet = TEIMerger.SORT_AND_CLEAN_STYLESHEET_ISO;        
        

        Document teiDocument = null;
        if (useNewStylesheets){
            StylesheetFactory ssf = new StylesheetFactory(true);
            String result =
                ssf.applyInternalStylesheetToString(skeleton_stylesheet, IOUtilities.documentToString(segmentedTranscription));
            teiDocument = IOUtilities.readDocumentFromString(result);
        } else {
            teiDocument = transformer.transform(segmentedTranscription);            
        }
        
        //FileIO.writeDocumentToLocalFile("C:\\Users\\Schmidt\\Desktop\\TEI\\intermediate1.xml", teiDocument);      
        //System.out.println("STEP 1 completed.");
        
        Vector uElements = TEIMerge(segmentedTranscription, nameOfDeepSegmentation, nameOfFlatSegmentation, includeFullText);
        

        XPath xp = XPath.newInstance(BODY_NODE);
        if (useNewStylesheets){
            BODY_NODE = "//tei:body";
            xp = XPath.newInstance(BODY_NODE);
            xp.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        }


        Element textNode = (Element)(xp.selectSingleNode(teiDocument));
        textNode.addContent(uElements);

        //FileIO.writeDocumentToLocalFile("C:\\Users\\thomas.schmidt\\Desktop\\DEBUG\\IT_2021\\MIKO2_TEI_MERGED.xml", teiDocument);
        //System.out.println("STEP 2 completed.");

        Document transformedDocument = null;
        if (useNewStylesheets){
            StylesheetFactory ssf = new StylesheetFactory(true);
            String result =
                ssf.applyInternalStylesheetToString(transform_stylesheet, IOUtilities.documentToString(teiDocument));
            transformedDocument = IOUtilities.readDocumentFromString(result);
            //fix for issue #89
            textNode = (Element)(xp.selectSingleNode(transformedDocument));
        } else {
            transformedDocument = transformer2.transform(teiDocument);
            textNode = (Element)(xp.selectSingleNode(transformedDocument));
        }

        //FileIO.writeDocumentToLocalFile("C:\\Users\\Schmidt\\Desktop\\TEI\\intermediate3.xml", transformedDocument);
        //System.out.println("STEP 3 completed.");

        // now take care of the events from tiers of type 'd'
        XPath xp2 = XPath.newInstance("//segmentation[@name='Event']/ats");
        List events = xp2.selectNodes(segmentedTranscription);
        for (int pos=0; pos<events.size(); pos++){
            Element exmaraldaEvent = (Element)(events.get(pos));
            String category = exmaraldaEvent.getParentElement().getParentElement().getAttributeValue("category");

            String elementName = "event";
            if (category.equals("pause")) {
                elementName ="pause";
            }
            
            Element teiEvent = new Element(elementName);
            
            String speakerID = exmaraldaEvent.getParentElement().getParentElement().getAttributeValue("speaker");
            if (speakerID!=null){
                teiEvent.setAttribute("who", speakerID);
            }
            teiEvent.setAttribute("start", exmaraldaEvent.getAttributeValue("s"));
            teiEvent.setAttribute("end", exmaraldaEvent.getAttributeValue("e"));
            if (!category.equals("pause")){
                teiEvent.setAttribute("desc", exmaraldaEvent.getText());
                teiEvent.setAttribute("type", category);
            } else {
                String duration = exmaraldaEvent.getText().replaceAll("\\(","").replaceAll("\\)","");
                teiEvent.setAttribute("dur", duration);
            }
            textNode.addContent(teiEvent);
        }

        //IOUtilities.writeDocumentToLocalFile("N:\\Workspace\\HMAT\\HAMATAC\\transcripts\\Intermediate_TEI.xml", transformedDocument);

        
        Document finalDocument = null;
        if (useNewStylesheets){
            StylesheetFactory ssf = new StylesheetFactory(true);
            String result =
                ssf.applyInternalStylesheetToString(sort_and_clean_stylesheet, IOUtilities.documentToString(transformedDocument));
            finalDocument = IOUtilities.readDocumentFromString(result);
        } else {
            finalDocument = transformer3.transform(transformedDocument);
        }


        return finalDocument;
    }
    
    public static Vector TEIMerge(Document segmentedTranscription, String nameOfDeepSegmentation, String nameOfFlatSegmentation) throws IOException {                
        return TEIMerge(segmentedTranscription, nameOfDeepSegmentation, nameOfFlatSegmentation, false);
    }
    /** this method will take the segmented transcription and, for each speaker contribution in the segmentation with
     * the name 'nameOfDeepSegmentation' will add anchors from the segmentation with the name 'nameOfFlatSegmentation'
     * such that the temporal information provided in the flat segmentation is completely represented as anchors 
     * within the deep segmentation. The typical application scenario is to give this method a segmented HIAT transcription with
     * nameOfDeepSegmentation = 'SpeakerContribution_Utterance_Word' 
     * nameOfFlatSegmentation = 'SpeakerContribution_Event'
     * @param segmentedTranscription
     * @param nameOfDeepSegmentation
     * @param nameOfFlatSegmentation
     * @param includeFullText
     * the method returns a vector of speaker-contribution elements with 'who' attributes
     * @return  */
    public static Vector TEIMerge(Document segmentedTranscription, 
            String nameOfDeepSegmentation, 
            String nameOfFlatSegmentation,
            boolean includeFullText) throws IOException {                
        try {
            
            // Make a map of the timeline
            Map timelineItems = new HashMap();
            String xpath = "//tli";
            XPath xpx = XPath.newInstance(xpath);
            List tlis = xpx.selectNodes(segmentedTranscription);
            for (int pos=0; pos<tlis.size();pos++){
                timelineItems.put(((Element)(tlis.get(pos))).getAttributeValue("id"), pos);
            }
            
            
            Vector returnValue = new Vector();
            XPath xp1 = XPath.newInstance("//segmentation[@name='" + nameOfDeepSegmentation + "']/ts");
            List segmentChains = xp1.selectNodes(segmentedTranscription);        
            // go through all top level segment chains
            for (Object segmentChain : segmentChains) {
                Element sc = (Element) (segmentChain);
                sc.setAttribute("speaker", sc.getParentElement().getParentElement().getAttributeValue("speaker"));
                String tierref = sc.getParentElement().getAttributeValue("tierref");
                String start = sc.getAttributeValue("s");
                String end = sc.getAttributeValue("e");
                String xpath2 = "//segmentation[@name='" + nameOfFlatSegmentation + "' and @tierref='" + tierref + "']"
                        + "/ts[@s='" + start + "' and @e='" + end +"']";
                XPath xp2 = XPath.newInstance(xpath2);
                Element sc2 = (Element)(xp2.selectSingleNode(segmentedTranscription));                                               
                if (sc2==null){
                    //this means that no corresponding top level
                    //element was found in the second segmentation
                    //which should not happen
                    throw new IOException(tierref + " " + start + " " + end);
                }
                // this is where the magic happens
                Element mergedElement = merge(sc,sc2);
                

                // now take care of the corresponding annotations
                int s = ((Integer)(timelineItems.get(start)));
                int e = ((Integer)(timelineItems.get(end)));
                String xpath5 = "//segmented-tier[@id='" + tierref + "']/annotation/ta";
                XPath xp5 = XPath.newInstance(xpath5);
                List annotations = xp5.selectNodes(segmentedTranscription);
                for (Object annotation1 : annotations) {
                    Element anno = (Element) (annotation1);
                    String aStart = anno.getAttributeValue("s");
                    String aEnd = anno.getAttributeValue("e");
                    int as = ((Integer)(timelineItems.get(aStart)));
                    int ae = ((Integer)(timelineItems.get(aEnd)));
                    boolean annotationBelongsToThisElement = (as>=s && as<=e) || (ae>=s && ae<=e);
                    if (annotationBelongsToThisElement){
                        Element annotationsElement = mergedElement.getChild("annotations");
                        if (annotationsElement==null){
                            annotationsElement = new Element("annotations");
                            mergedElement.addContent(annotationsElement);
                        }
                        Element annotation = new Element("annotation");
                        annotation.setAttribute("start",aStart);
                        annotation.setAttribute("end",aEnd);
                        annotation.setAttribute("level",anno.getParentElement().getAttributeValue("name"));
                        annotation.setAttribute("value", anno.getText());
                        annotationsElement.addContent(annotation);
                    }
                    
                    //System.out.println(s + "/" + e + " **** " + as + "/" + ae);
                }
                
                
                //*****************************************
                // NEW 25-04-2016
                // include full text if Daniel J. wisheth thus
                if (includeFullText){
                    Element annotation = new Element("annotation");
                    annotation.setAttribute("start", start);
                    annotation.setAttribute("end", end);
                    annotation.setAttribute("level", "full-text");
                    
                    String fullText = "";
                    List l = XPath.selectNodes(sc2, "descendant::text()");
                    for (Object o : l){
                        Text text = (Text)o;
                        fullText+=text.getText();
                    }
                    annotation.setAttribute("value", fullText);
                    
                    Element annotationsElement = mergedElement.getChild("annotations");
                    if (annotationsElement==null){
                        annotationsElement = new Element("annotations");
                        mergedElement.addContent(annotationsElement);
                    }
                    annotationsElement.addContent(annotation);
                }
                //*****************************************
                
                returnValue.addElement(mergedElement.detach());
            }
            
            // issue #89 - Now the vector contains elements only from the 
            // segmentations passed as parameters
            // in particular, it seems that tiers of type 'd' (which end up as 
            // segmentation @name='Event' are lost
            
            return returnValue;
        } catch (JDOMException ex) {
            Logger.getLogger(TEIMerger.class.getName()).log(Level.SEVERE, null, ex);     
            throw new IOException(ex);

        }
        //return null;
    }
    
    public static Element merge(Element e1, Element e2){
        
        //System.out.println("Merging " + IOUtilities.elementToString(e1) + " with " + IOUtilities.elementToString(e2));
        
        Iterator i1 = e1.getDescendants();
        Vector pcData1 = new Vector();
        while (i1.hasNext()){pcData1.addElement(i1.next());}
        
        Iterator i2 = e2.getDescendants(new TextFilter());
        Vector pcData2 = new Vector();
        while (i2.hasNext()){pcData2.addElement(i2.next());}
                  
        int charBoundary = 0;
        for (int pos=0; pos<pcData2.size()-1; pos++){
            Text eventText = (Text)(pcData2.elementAt(pos));
            Element anchor = new Element("anchor");
            Element event = eventText.getParentElement();
            String start = event.getAttributeValue("e");
            anchor.setAttribute("synch", start);

            charBoundary+= eventText.getText().length();
            // jetzt durch den anderen Baum laufen und den zugehoerigen Anker
            // an der richtigen Stelle einfuegen
            int charCount = 0;
            for (int pos2=0; pos2<pcData1.size(); pos2++){
                Object o = pcData1.elementAt(pos2);
                if (!(o instanceof Text)) continue;
                Text segmentText = (Text)o;
                int textLength = segmentText.getText().length();
                if (charCount+textLength<charBoundary){
                    charCount+=textLength;
                    continue;
                } else if (charCount+textLength==charBoundary){
                    Element parent = segmentText.getParentElement();
                    int index = parent.indexOf(segmentText);
                    Element parentOfParent = parent.getParentElement();
                    int index2 = parentOfParent.indexOf(parent);
                    parentOfParent.addContent(index2+1,anchor);
                    break;
                }
                // charCount+textLength>charBoundary
                String leftPart = segmentText.getText().substring(0, charBoundary-charCount);
                String rightPart = segmentText.getText().substring(charBoundary-charCount);
                Text leftText = new Text(leftPart);
                Text rightText = new Text(rightPart);


                // neue Sachen muessen zweimal eingefuegt werden - einmal
                // in den Vector, einmal in den Parent
                // Sachen im Vector muessen den richtigen Parent bekommen
                
                
                Element parent = segmentText.getParentElement();
                parent.removeContent(segmentText);
                parent.addContent(leftText);
                parent.addContent(anchor);
                parent.addContent(rightText);
                
                pcData1.remove(segmentText);
                pcData1.add(pos2,rightText);
                pcData1.add(pos2,anchor);
                pcData1.add(pos2,leftText);
                break;
            }
        }
        
        return e1;
    }
    
}
