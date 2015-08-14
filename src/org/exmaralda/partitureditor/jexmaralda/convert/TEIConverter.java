/*
 * TEIConverter.java
 *
 * Created on 12. August 2004, 17:24
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.*;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.segment.SegmentedToListInfo;

import org.jdom.JDOMException;
import org.jdom.transform.XSLTransformException;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

import javax.xml.parsers.ParserConfigurationException;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.common.corpusbuild.TEIMerger;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.GATParser;
import org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.cGATMinimalSegmentation;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;

/**
 *
 * @author  thomas
 */
public class TEIConverter {
    
    static String STYLESHEET_PATH = "/org/exmaralda/partitureditor/jexmaralda/xsl/EXMARaLDA2TEI.xsl";

    public static final int GENERIC_METHOD = 0;
    public static final int AZM_METHOD = 1;
    public static final int MODENA_METHOD = 2;
    public static final int HIAT_METHOD = 3;
    public static final int CGAT_METHOD = 4;
    public static final int HIAT_NEW_METHOD = 5;
    public static final int ISO_GENERIC_METHOD = 6;
    public static final int HIAT_ISO_METHOD = 7;
    public static final int CGAT_MINIMAL_ISO_METHOD = 8;

    
    /** Creates a new instance of TEIConverter */
    public TEIConverter() {
    }
    
    /** Creates a new instance of TEIConverter */
    public TEIConverter(String ss) {
        STYLESHEET_PATH = ss;
    }

    public BasicTranscription readTEIFromFile(String path) throws SAXException, JDOMException, IOException { 
        Document doc = IOUtilities.readDocumentFromLocalFile(path);
        if ("TEI.2".equals(doc.getRootElement().getName())){
            // i.e. this must be a TEI document like the one defined in the AzM paper
            TEISaxReader reader = new TEISaxReader();
            return reader.readFromFile(path);
        }
        StylesheetFactory ssf = new StylesheetFactory(true);
        try {
            String result = ssf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/tei2exmaralda.xsl", IOUtilities.documentToString(doc));
            BasicTranscription bt = new BasicTranscription();
            bt.BasicTranscriptionFromString(result);
            return bt;
        } catch (JexmaraldaException ex) {
            throw new IOException(ex);
        } catch (ParserConfigurationException ex) {
            throw new IOException(ex);
        } catch (TransformerConfigurationException ex) {
            throw new IOException(ex);
        } catch (TransformerException ex) {
            throw new IOException(ex);
        }
    }
    
    public String BasicTranscriptionToTEI(BasicTranscription bt) throws SAXException,
                                                                        IOException,
                                                                        ParserConfigurationException,
                                                                        TransformerConfigurationException,
                                                                        TransformerException,
                                                                        JDOMException {
        return BasicTranscriptionToTEI(bt, false);
    }

    public String BasicTranscriptionToTEI(BasicTranscription bt, boolean callBracketReplacer) throws SAXException,
                                                                                                     IOException,
                                                                                                     ParserConfigurationException,
                                                                                                     TransformerConfigurationException,
                                                                                                     TransformerException,
                                                                                                     JDOMException {
        SegmentedTranscription st = bt.toSegmentedTranscription();
        ListTranscription lt = st.toListTranscription(new SegmentedToListInfo(st, SegmentedToListInfo.TURN_SEGMENTATION));
        // CHANGED FOR VERSION 1.3.3, 20-10-2006
        lt.getBody().sort();
        // CHANGED 30-08-2010: use XSLT2
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString(STYLESHEET_PATH, lt.toXML());
        if (callBracketReplacer){
            return replaceBrackets(result);
        }
        return result;
    }
    
    public void writeTEIToFile(BasicTranscription bt, String path, int method) throws SAXException, 
                                                                                      IOException, 
                                                                                      ParserConfigurationException, 
                                                                                      TransformerConfigurationException, 
                                                                                      TransformerException, 
                                                                                      JDOMException, 
                                                                                      FSMException, 
                                                                                      XSLTransformException, 
                                                                                      Exception{
        switch (method){
            case GENERIC_METHOD :
                writeGenericTEIToFile(bt,path);
            case AZM_METHOD :
                writeTEIToFile(bt, path);
                break;
            case MODENA_METHOD :
                writeModenaTEIToFile(bt, path);
                break;
            case HIAT_METHOD :
                writeHIATTEIToFile(bt, path);
                break;
        }
    }


    
    public void writeGenericISOTEIToFile(BasicTranscription bt, String path) throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/exmaralda2isotei.xsl", bt.toXML());
        Document d = IOUtilities.readDocumentFromString(result);
        IOUtilities.writeDocumentToLocalFile(path, d);
    }

    public void writeHIATISOTEIToFile(BasicTranscription bt, String filename) throws SAXException,
                                                                              FSMException,
                                                                              XSLTransformException,
                                                                              JDOMException,
                                                                              IOException,
                                                                              Exception {
        // added 13-12-2013
        BasicTranscription copyBT = bt.makeCopy();
        copyBT.normalize();        
        System.out.println("started writing document...");
        HIATSegmentation segmentation = new HIATSegmentation();
        SegmentedTranscription st = segmentation.BasicToSegmented(copyBT);
        System.out.println("Segmented transcription created");
        String nameOfDeepSegmentation = "SpeakerContribution_Utterance_Word";
        TEIMerger teiMerger = new TEIMerger(true);
        Document stdoc = FileIO.readDocumentFromString(st.toXML());
        Document teiDoc = teiMerger.SegmentedTranscriptionToTEITranscription(stdoc, nameOfDeepSegmentation, "SpeakerContribution_Event", true);
        System.out.println("Merged");
        generateWordIDs(teiDoc);
        FileIO.writeDocumentToLocalFile(filename, teiDoc);
        System.out.println("document written.");        
    }
    
    public void writeCGATMINIMALISOTEIToFile(BasicTranscription bt, String filename, String customFSM) throws SAXException,
                                                                              FSMException,
                                                                              XSLTransformException,
                                                                              JDOMException,
                                                                              IOException,
                                                                              Exception {
        // added 13-12-2013
        BasicTranscription copyBT = bt.makeCopy();
        copyBT.normalize();        
        System.out.println("started writing document...");
        cGATMinimalSegmentation segmentation;
        if (customFSM==null || customFSM.length()==0){
            segmentation = new cGATMinimalSegmentation();
        } else {
            segmentation = new cGATMinimalSegmentation(customFSM);
        }
        SegmentedTranscription st = segmentation.BasicToSegmented(copyBT);
        System.out.println("Segmented transcription created");
        String nameOfDeepSegmentation = "SpeakerContribution_Word";
        TEIMerger teiMerger = new TEIMerger(true);
        Document stdoc = FileIO.readDocumentFromString(st.toXML());
        Document teiDoc = teiMerger.SegmentedTranscriptionToTEITranscription(stdoc, nameOfDeepSegmentation, "SpeakerContribution_Event", true);
        System.out.println("Merged");
        generateWordIDs(teiDoc);
        FileIO.writeDocumentToLocalFile(filename, teiDoc);
        System.out.println("document written.");        
    }

    public void writeFOLKERISOTEIToFile(BasicTranscription bt, String path) throws SAXException, 
                                                                                ParserConfigurationException, 
                                                                                IOException, 
                                                                                TransformerConfigurationException, 
                                                                                TransformerException, 
                                                                                JDOMException{
        EventListTranscription elt = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt);
        elt.updateContributions();
        Element e = elt.toJDOMElement(new File(path));
        Document doc = new Document(e);
        GATParser parser = new GATParser(org.exmaralda.folker.utilities.Constants.getAlphabetLanguage());
        for (int level=1; level<=2; level++){
            parser.parseDocument(doc, level);
        }
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/folker2isotei.xsl", IOUtilities.documentToString(doc));
        Document d = IOUtilities.readDocumentFromString(result);
        generateWordIDs(d);
        IOUtilities.writeDocumentToLocalFile(path, d);                
    }
    
    public void writeFOLKERISOTEIToFile(Document flnDoc, String absolutePath) throws SAXException, ParserConfigurationException, IOException, TransformerConfigurationException, TransformerException, JDOMException {
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/folker2isotei.xsl", IOUtilities.documentToString(flnDoc));
        Document d = IOUtilities.readDocumentFromString(result);
        generateWordIDs(d);
        IOUtilities.writeDocumentToLocalFile(absolutePath, d);                
    }
    
    
    
    //////////////////////////////////////////////////////////////////
    //////////////// OLDER METHODS, NOT NEEDED SINCE ISO /////////////
    //////////////////////////////////////////////////////////////////
    
    /** transforms the basic transcription and writes a TEI file to path
     * according to the method described in Schmidt 2005 */
    public void writeTEIToFile(BasicTranscription bt, String path) throws SAXException,
                                                                          IOException, 
                                                                          ParserConfigurationException, 
                                                                          TransformerConfigurationException, 
                                                                          TransformerException,
                                                                          JDOMException {
        String toBeWritten = BasicTranscriptionToTEI(bt);
        System.out.println("started writing document...");
        java.io.FileOutputStream fos = new java.io.FileOutputStream(new java.io.File(path));
        fos.write(toBeWritten.getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");        
    }

    /** transforms the basic transcription and writes a TEI file to path
     * according to the method described by Natacha Niemants (Modena) */
    public void writeModenaTEIToFile(BasicTranscription bt, String path) throws SAXException,
                                                                                IOException,
                                                                                ParserConfigurationException,
                                                                                TransformerConfigurationException,
                                                                                TransformerException,
                                                                                JDOMException {
        String toBeWritten = BasicTranscriptionToTEI(bt, true);
        System.out.println("started writing document...");
        java.io.FileOutputStream fos = new java.io.FileOutputStream(new java.io.File(path));
        fos.write(toBeWritten.getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");
    }

    public void writeHIATTEIToFile(BasicTranscription bt, String path) throws SAXException, 
                                                                              FSMException, 
                                                                              XSLTransformException, 
                                                                              JDOMException, 
                                                                              IOException, 
                                                                              Exception {
        // added 13-12-2013
        BasicTranscription copyBT = bt.makeCopy();
        copyBT.normalize();

        System.out.println("started writing document...");
        HIATSegmentation segmentation = new HIATSegmentation();
        SegmentedTranscription st = segmentation.BasicToSegmented(copyBT);
        String nameOfDeepSegmentation = "SpeakerContribution_Utterance_Word";
        TEIMerger teiMerger = new TEIMerger();
        Document stdoc = FileIO.readDocumentFromString(st.toXML());
        Document teiDoc = teiMerger.SegmentedTranscriptionToTEITranscription(stdoc, nameOfDeepSegmentation, "SpeakerContribution_Event");
        FileIO.writeDocumentToLocalFile(path, teiDoc);
        System.out.println("document written.");                
    }

    public void writeNewHIATTEIToFile(BasicTranscription bt, String filename) throws SAXException,
                                                                              FSMException,
                                                                              XSLTransformException,
                                                                              JDOMException,
                                                                              IOException,
                                                                              Exception {
        writeNewHIATTEIToFile(bt, filename, true);
    }

    public void writeNewHIATTEIToFile(BasicTranscription bt, String filename, boolean generateWordIDs) throws SAXException,
                                                                              FSMException,
                                                                              XSLTransformException,
                                                                              JDOMException,
                                                                              IOException,
                                                                              Exception {

        // added 13-12-2013
        BasicTranscription copyBT = bt.makeCopy();
        copyBT.normalize();
        
        System.out.println("started writing document...");
        HIATSegmentation segmentation = new HIATSegmentation();
        SegmentedTranscription st = segmentation.BasicToSegmented(copyBT);
        String nameOfDeepSegmentation = "SpeakerContribution_Utterance_Word";
        TEIMerger teiMerger = new TEIMerger();
        Document stdoc = FileIO.readDocumentFromString(st.toXML());
        Document teiDoc = teiMerger.SegmentedTranscriptionToTEITranscription(stdoc, nameOfDeepSegmentation, "SpeakerContribution_Event", true);

        /*StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/transformhiattei.xsl", IOUtilities.documentToString(teiDoc));
        Document transformedTEIDoc = IOUtilities.readDocumentFromString(result);
        FileIO.writeDocumentToLocalFile(filename, transformedTEIDoc);*/
        if (generateWordIDs){
            generateWordIDs(teiDoc);
        }
        FileIO.writeDocumentToLocalFile(filename, teiDoc);
        System.out.println("document written.");

    }

    
    public void writeFOLKERTEIToFile(BasicTranscription bt, String path) throws SAXException, 
                                                                                ParserConfigurationException, 
                                                                                IOException, 
                                                                                TransformerConfigurationException, 
                                                                                TransformerException, 
                                                                                JDOMException{
        EventListTranscription elt = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt);
        elt.updateContributions();
        Element e = elt.toJDOMElement(new File(path));
        Document doc = new Document(e);
        GATParser parser = new GATParser(org.exmaralda.folker.utilities.Constants.getAlphabetLanguage());
        for (int level=1; level<=2; level++){
            parser.parseDocument(doc, level);
        }
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/folker2tei.xsl", IOUtilities.documentToString(doc));
        Document d = IOUtilities.readDocumentFromString(result);
        generateWordIDs(d);
        IOUtilities.writeDocumentToLocalFile(path, d);                
    }

    public void writeGenericTEIToFile(BasicTranscription bt, String path) throws SAXException,
                                                                                ParserConfigurationException,
                                                                                JDOMException,
                                                                                IOException,
                                                                                TransformerConfigurationException,
                                                                                TransformerException {
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/exmaralda2tei.xsl", bt.toXML());
        Document d = IOUtilities.readDocumentFromString(result);
        IOUtilities.writeDocumentToLocalFile(path, d);
    }



    //****************************************************
    //********* private processing methods ***************
    //****************************************************

    private void generateWordIDs(Document document) throws JDOMException{
        XPath wordXPath = XPath.newInstance("//tei:w"); 
        wordXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        
        List words = wordXPath.selectNodes(document);
        int count=0;
        for (Object o : words){
            count++;
            Element word = (Element)o;
            String wordID = "w" + Integer.toString(count);
            //System.out.println("*** " + wordID);
            word.setAttribute("id", wordID, Namespace.XML_NAMESPACE);
        }
        
        // new 02-12-2014
        XPath pcXPath = XPath.newInstance("//tei:pc"); 
        pcXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        
        List pcs = pcXPath.selectNodes(document);
        count=0;
        for (Object o : pcs){
            count++;
            Element pc = (Element)o;
            String pcID = "pc" + Integer.toString(count);
            //System.out.println("*** " + wordID);
            pc.setAttribute("id", pcID, Namespace.XML_NAMESPACE);
        }
        
    }
    
    private Element makeNonphoElement(String matchedText) {
        Element returnValue = new Element("x");
        String content = matchedText.substring(1,matchedText.length()-1);
        if (matchedText.startsWith("[")){
            returnValue.setName("vocal");
            returnValue.setAttribute("desc", content);
        } else if (matchedText.startsWith("{")){
            returnValue.setName("event");
            returnValue.setAttribute("desc", content);
        } else if (matchedText.startsWith("(")){
            returnValue.setName("kinesic");
            returnValue.setAttribute("desc", content);
        } else if (matchedText.startsWith("<")){
            returnValue.setName("pause");
            if (content.startsWith("dur=")){
               returnValue.setAttribute("dur" , content.substring(content.indexOf("=")+1));
            } else {
                returnValue.setAttribute("dur", content);
            }
        }
        return returnValue;
    }

    private String replaceBrackets(String input) throws JDOMException, IOException {
        Document doc = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString(input);

        String regex_no_bracket = "[^\\(\\)\\[\\]\\{\\}\\<\\>]+";
        String regex_round = "\\(" + regex_no_bracket + "\\)";
        String regex_square = "\\[" + regex_no_bracket + "\\]";
        String regex_curly = "\\{" + regex_no_bracket + "\\}";
        String regex_angle = "\\<" + regex_no_bracket + "\\>";
        String regex = "(" + regex_round + "|" + regex_square + "|" + regex_curly + "|" + regex_angle +  ")";
        Pattern pattern = Pattern.compile(regex);

        Vector<Element> allSegments = new Vector<Element>();
        Iterator i = doc.getDescendants(new ElementFilter("seg"));
        while (i.hasNext()){
            Element seg = (Element)(i.next());
            allSegments.add(seg);
        }
        for (Element seg : allSegments){
            if (!(seg.getAttributeValue("type").equals("segmental"))) continue;
            Vector<Content> newContent = new Vector<Content>();
            for (Object o : seg.getContent()){
                Content c = (Content)o;
                if (!(c instanceof Text)) {
                    newContent.add(c);
                    continue;
                }
                Text t = (Text)c;
                String originalText = t.getText();
                //System.out.println("==>" + originalText);
                Matcher m = pattern.matcher(originalText);
                int lastStart = 0;
                
                while (m.find()){
                    int s = m.start();
                    int e = m.end();
                    String matchedText = originalText.substring(s, e);
                    //System.out.println("**** " + s + " / " + e + " / " + matchedText);
                    if (lastStart<s){
                        Text text = new Text(originalText.substring(lastStart, s));
                        newContent.add(text);
                    }
                    Element nonpho = makeNonphoElement(matchedText);
                    newContent.add(nonpho);
                    //lastStart = e+1;
                    lastStart = e;
                }
                if (lastStart<originalText.length()-1){
                    Text text = new Text(originalText.substring(lastStart));
                    newContent.add(text);
                }
            }
            seg.removeContent();
            seg.setContent(newContent);

        }

        return org.exmaralda.common.jdomutilities.IOUtilities.documentToString(doc,false);
    }




    
}
