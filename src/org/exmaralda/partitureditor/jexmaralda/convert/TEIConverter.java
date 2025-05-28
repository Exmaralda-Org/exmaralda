/*
 * TEIConverter.java
 *
 * Created on 12. August 2004, 17:24
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.exmaralda.orthonormal.data.NormalizedFolkerTranscription;
import org.exmaralda.orthonormal.io.XMLReaderWriter;
import org.exmaralda.partitureditor.jexmaralda.segment.GenericSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.InelEventBasedSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.cGATMinimalSegmentation;
import org.jdom.Attribute;
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
public class TEIConverter extends AbstractConverter {
    
    
    public static String EXMARaLDA2GENERIC_ISO_TEI_XSL = "/org/exmaralda/tei/xml/exmaralda2isotei.xsl";
    public static String EXMARaLDA2EVENT_TOKEN_ISO_TEI_XSL = "/org/exmaralda/tei/xml/exmaralda2isotei_eventToken.xsl";
    
    public static String FOLKER2ISO_TEI_XSL = "/org/exmaralda/tei/xml/folker2isotei.xsl";
    public static String INEL_SEGMENTED_ISO_TEI_XSL = "/org/exmaralda/tei/xml/exmaraldaEXS2ISOTEI_InelEventBased.xsl";
    
    /* 
        Converting from ISO/TEI to EXB is done in several steps:
        (1) move <w> attributes to <span> elements : attributes2spans.xsl
        (2) transform token references in <span> to time references : token2timeSpanReferences.xsl
        (3) turn explicit token markup into implicit charachter markup : detokenize.xsl
        (4) transform this TEI document to EXB : isotei2exmaralda.xsl
        The assumption is that the input to (1) conforms to ZuMult's ISO/TEI schema
        Intermediate steps will still conform to ISO/TEI in general.
    */
    
    public static String ISOTEI2EXMARaLDA_0_NORMALIZE = "/org/exmaralda/tei/xml/normalize.xsl";
    
    public static String ISOTEI2EXMARaLDA_1_ATTRIBUTES2SPANS_XSL = "/org/exmaralda/tei/xml/attributes2spans.xsl";
    public static String ISOTEI2EXMARaLDA_1b_AUGMENTTIMELINE_XSL = "/org/exmaralda/tei/xml/augmentTimeline.xsl";
    
    //public static String ISOTEI2EXMARaLDA_2_TOKEN2TIMEREFS_XSL = "/org/exmaralda/tei/xml/token2timeSpanReferences.xsl";
    public static String ISOTEI2EXMARaLDA_2_TOKEN2TIMEREFS_XSL = "/org/exmaralda/tei/xml/token2timeSpanReferences_optimized.xsl";
    public static String ISOTEI2EXMARaLDA_2b_REMOVE_TIMEPOINTS_XSL = "/org/exmaralda/tei/xml/removeTimepointsWithoutAbsolute.xsl";

    public static String ISOTEI2EXMARaLDA_2c_DESEGMENT_XSL = "/org/exmaralda/tei/xml/desegment.xsl";

    public static String ISOTEI2EXMARaLDA_3_DETOKENIZE_XSL = "/org/exmaralda/tei/xml/detokenize.xsl";
    public static String ISOTEI2EXMARaLDA_3b_AUGMENT_FINAL_XSL = "/org/exmaralda/tei/xml/augmentTimeline_final.xsl";
    public static String ISOTEI2EXMARaLDA_3c_REMOVE_STRAY_ANCHORS_XSL = "/org/exmaralda/tei/xml/removeStrayAnchors.xsl";

    public static String ISOTEI2EXMARaLDA_4_TRANSFORM_XSL = "/org/exmaralda/tei/xml/isotei2exmaralda.xsl";
    
    public static String ISOTEI2FOLKER_1_SPANS2ATTRIBUTES_XSL = "/org/exmaralda/tei/xml/attributes2spans.xsl";
    public static String ISOTEI2FOLKER_2_TRANSFORM_XSL = "/org/exmaralda/tei/xml/isotei2folker.xsl";
    
    public static final int ISO_NON_SEGMENTED_METHOD = 6;
    public static final int HIAT_ISO_METHOD = 7;
    public static final int CGAT_MINIMAL_ISO_METHOD = 8;
    public static final int ISO_EVENT_TOKEN_METHOD = 9;
    public static final int ISO_GENERIC_METHOD = 10;
    public static final int ISO_INEL_METHOD = 11;

    
    String language = "en";
    
    /** Creates a new instance of TEIConverter */
    public TEIConverter() {
    }
    
    /** Creates a new instance of TEIConverter
     * @param ss */
    public TEIConverter(String ss) {
        EXMARaLDA2TEI_XSL = ss;
    }
    
    
    public void setLanguage(String l){
        language = l;
        System.out.println("[TEIConverter] Language of converter set to " + language);
    }

    
    // *********************
    // NO SEGMENTATION 
    // *********************

    public void writeNonSegmentedISOTEIToFile(BasicTranscription bt, String path) throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString(EXMARaLDA2GENERIC_ISO_TEI_XSL, bt.toXML());
        Document teiDoc = IOUtilities.readDocumentFromString(result);
        setDocLanguage(teiDoc, language);        
        setTranscriptionDesc(teiDoc, "unspecified", "unspecified");
        IOUtilities.writeDocumentToLocalFile(path, teiDoc);
        System.out.println("[TEIConverter] Non segmented ISO TEI File written to " + path);
    }
    
    // *********************
    // EVENT = TOKEN
    // *********************

    public void writeEventTokenISOTEIToFile(BasicTranscription bt, String path)  throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString(EXMARaLDA2EVENT_TOKEN_ISO_TEI_XSL, bt.toXML());
        Document teiDoc = IOUtilities.readDocumentFromString(result);
        setDocLanguage(teiDoc, language);     
        setTranscriptionDesc(teiDoc, "EVENT_TOKEN", "1.0");
        IOUtilities.writeDocumentToLocalFile(path, teiDoc);
    }
    

    // *********************
    // INEL EVENT BASED
    // *********************

    public void writeINELEventBasedISOTEIToFile(BasicTranscription bt, String path)  throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, FSMException {
        BasicTranscription copyBT = bt.makeCopy();
        copyBT.normalize();              
        InelEventBasedSegmentation segmentation = new InelEventBasedSegmentation();
        SegmentedTranscription st = segmentation.BasicToSegmented(copyBT);        
        System.out.println("[TEIConverter] Segmented transcription created");
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString(TEIConverter.INEL_SEGMENTED_ISO_TEI_XSL, st.toXML());
        Document teiDoc = IOUtilities.readDocumentFromString(result);
        setDocLanguage(teiDoc, language);     
        IOUtilities.writeDocumentToLocalFile(path, teiDoc);
    }


    // *********************
    // HIAT
    // *********************

    public void writeHIATISOTEIToFile(BasicTranscription bt, String filename) throws SAXException, FSMException, XSLTransformException, JDOMException, IOException, ParserConfigurationException, TransformerException {
        writeHIATISOTEIToFile(bt, filename, false);
    }
    

    public void writeHIATISOTEIToFile(BasicTranscription bt, String filename, boolean includeFullText) throws SAXException, FSMException, XSLTransformException, JDOMException, IOException, ParserConfigurationException, TransformerException {
         writeHIATISOTEIToFile(bt, filename, null, includeFullText);
    }


    public void writeHIATISOTEIToFile(BasicTranscription bt, String filename, String customFSM, boolean includeFullText) throws SAXException,
                                                                              FSMException,
                                                                              XSLTransformException,
                                                                              JDOMException,
                                                                              IOException,
                                                                              ParserConfigurationException,
                                                                              TransformerException
                                                                              {
        // added 13-12-2013
        BasicTranscription copyBT = bt.makeCopy();
        copyBT.normalize();      
        
        // added 25-01-2021
        //copyBT.getBody().getCommonTimeline().completeTimes();
        
        
        //HIATSegmentation segmentation = new HIATSegmentation();
        // issue #152
        HIATSegmentation segmentation;
        if (customFSM==null || customFSM.length()==0){
            segmentation = new HIATSegmentation();
        } else {
            segmentation = new HIATSegmentation(customFSM);
        }
        
        SegmentedTranscription st = segmentation.BasicToSegmented(copyBT);        
        System.out.println("[TEIConverter] Segmented transcription created");
        // added 12-02-2023 to see what happens...
        //st.getBody().augmentCommonTimeline();
        String nameOfDeepSegmentation = "SpeakerContribution_Utterance_Word";
        TEIMerger teiMerger = new TEIMerger(true);
        Document stdoc = FileIO.readDocumentFromString(st.toXML());
        Document teiDoc = teiMerger.SegmentedTranscriptionToTEITranscription(stdoc, 
                nameOfDeepSegmentation, 
                "SpeakerContribution_Event", 
                true,
                includeFullText);
        System.out.println("[TEIConverter] Merged");
        generateWordIDs(teiDoc);
        setDocLanguage(teiDoc, language);
        setTranscriptionDesc(teiDoc, "HIAT", "2004");
        
        // changed for #340
        //Format prettyFormat = Format.getPrettyFormat();
        //prettyFormat.setTextMode(Format.TextMode.TRIM_FULL_WHITE);                
        //IOUtilities.writeDocumentToLocalFile(filename, teiDoc, prettyFormat);
        System.out.println("[TEIConverter] Started writing document " + filename + "...");
        IOUtilities.writeDocumentToLocalFile(filename, teiDoc);
        System.out.println("[TEIConverter] document " + filename + " written.");        
    }
    
    // *********************
    // cGAT MINIMAL
    // *********************

    public void writeCGATMINIMALISOTEIToFile(BasicTranscription bt, String filename, String customFSM) throws SAXException,
                                                                              FSMException,
                                                                              XSLTransformException,
                                                                              JDOMException,
                                                                              IOException,
                                                                              ParserConfigurationException,
                                                                              TransformerException
                                                                              {
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
        setDocLanguage(teiDoc, language);   
        setTranscriptionDesc(teiDoc, "cGAT-Minimal", "2017");
        // changed for #340
        //Format prettyFormat = Format.getPrettyFormat();
        //prettyFormat.setTextMode(Format.TextMode.TRIM_FULL_WHITE);                
        //IOUtilities.writeDocumentToLocalFile(filename, teiDoc, prettyFormat);
        IOUtilities.writeDocumentToLocalFile(filename, teiDoc);
        System.out.println("document written.");        
    }
    
    // *********************
    // GENERIC 
    // *********************

    public void writeGenericSegmentedISOTEIToFile(BasicTranscription bt, String filename, String customFSM) throws SAXException,
                                                                              FSMException,
                                                                              XSLTransformException,
                                                                              JDOMException,
                                                                              IOException,
                                                                              ParserConfigurationException,
                                                                              TransformerException
                                                                              {
        // added 13-12-2013
        BasicTranscription copyBT = bt.makeCopy();
        copyBT.normalize();        
        System.out.println("started writing document...");
        GenericSegmentation segmentation;
        if (customFSM==null || customFSM.length()==0){
            segmentation = new GenericSegmentation();
        } else {
            segmentation = new GenericSegmentation(customFSM);
        }
        SegmentedTranscription st = segmentation.BasicToSegmented(copyBT);
        System.out.println("Segmented transcription created");
        String nameOfDeepSegmentation = "SpeakerContribution_Word";
        TEIMerger teiMerger = new TEIMerger(true);
        Document stdoc = FileIO.readDocumentFromString(st.toXML());
        Document teiDoc = teiMerger.SegmentedTranscriptionToTEITranscription(stdoc, nameOfDeepSegmentation, "SpeakerContribution_Event", true);
        System.out.println("Merged");
        generateWordIDs(teiDoc);
        setDocLanguage(teiDoc, language);        
        setTranscriptionDesc(teiDoc, "GENERIC", "1.0");
        IOUtilities.writeDocumentToLocalFile(filename, teiDoc);
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
        String result = sf.applyInternalStylesheetToString(FOLKER2ISO_TEI_XSL, IOUtilities.documentToString(doc));
        Document teiDoc = IOUtilities.readDocumentFromString(result);
        generateWordIDs(teiDoc);
        setDocLanguage(teiDoc, language);       
        setTranscriptionDesc(teiDoc, "cGAT-Minimal", "2017");        
        IOUtilities.writeDocumentToLocalFile(path, teiDoc);
    }
    
    public void writeFOLKERISOTEIToFile(Document flnDoc, String absolutePath) throws SAXException, ParserConfigurationException, IOException, TransformerConfigurationException, TransformerException, JDOMException {
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString(FOLKER2ISO_TEI_XSL, IOUtilities.documentToString(flnDoc));
        Document teiDoc = IOUtilities.readDocumentFromString(result);
        generateWordIDs(teiDoc);
        setDocLanguage(teiDoc, language);                
        setTranscriptionDesc(teiDoc, "cGAT-Minimal", "2017");
        IOUtilities.writeDocumentToLocalFile(absolutePath, teiDoc);
    }
    
    
    // ******************************************************************
    // READ METHODS
    // ******************************************************************
    
    // issue #215
    public BasicTranscription readISOTEIFromFile(String path) throws IOException{
        try {
            StylesheetFactory sf = new StylesheetFactory(true);
            fireConverterEvent(new ConverterEvent("Reading " + new File(path).getName(), 0.05));
            Document doc = IOUtilities.readDocumentFromLocalFile(path);
            String docString = IOUtilities.documentToString(doc);
            fireConverterEvent(new ConverterEvent(new File(path).getName() + " read, now normalizing...", 0.1));
            
            // new 10-03-2021
            String transform0 = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_0_NORMALIZE, docString);
            fireConverterEvent(new ConverterEvent("Normalized, now transforming attributes to spans...", 0.15));            


            String transform1 = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_1_ATTRIBUTES2SPANS_XSL, transform0);
            fireConverterEvent(new ConverterEvent("Transformed attributes to spans, now augmenting timeline..", 0.25));            
            
            // new 09-03-2021
            String transform1_b = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_1b_AUGMENTTIMELINE_XSL, transform1);
            fireConverterEvent(new ConverterEvent("Augmented timeline, now transforming token to time references...", 0.35));            
                        
            
            String transform2 = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_2_TOKEN2TIMEREFS_XSL, transform1_b);
            fireConverterEvent(new ConverterEvent("Transformed token references to time references, now removing unnecessary timepoints...", 0.7));            

            // new 10-03-2021
            // this is what takes so long, don't really understand why
            //String transform2_b = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_2b_REMOVE_TIMEPOINTS_XSL, transform2);
            String transform2_b = removeTimepoints(transform2);
            fireConverterEvent(new ConverterEvent("Unnecessary timepoints removed, now desegmenting...", 0.75));            

            // new 14-02-2023 : issue #367
            String transform2_c = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_2c_DESEGMENT_XSL, transform2_b);
            fireConverterEvent(new ConverterEvent("Desegmented, now detokenizing...", 0.8));            

            String transform3 = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_3_DETOKENIZE_XSL, transform2_c);
            fireConverterEvent(new ConverterEvent("Detokenzied, now final augmenting timeline...", 0.85));            

            // new 14-02-2023 : issue #367
            String transform3_b = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_3b_AUGMENT_FINAL_XSL, transform3);
            fireConverterEvent(new ConverterEvent("Augmented timeline, now removing stray anchors...", 0.9));            

            // new 14-02-2023 : issue #367
            String transform3_c = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_3c_REMOVE_STRAY_ANCHORS_XSL, transform3_b);
            fireConverterEvent(new ConverterEvent("Stray anchors removed, now transforming to EXB...", 0.95));            
            
            String exbString = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_4_TRANSFORM_XSL, transform3_c);
            fireConverterEvent(new ConverterEvent("Transformed to EXB", 0.99));            
            BasicTranscription bt = new BasicTranscription();
            bt.BasicTranscriptionFromString(exbString);
            
            bt.getBody().removeUnusedTimelineItems();
            
            Vector<String> correctedReferencedFiles = new Vector<>();
            for (String referencedFile : bt.getHead().getMetaInformation().getReferencedFiles()){
                if (referencedFile.startsWith("file:/")){
                    correctedReferencedFiles.add(referencedFile.replaceAll("file\\:\\/+", ""));
                } else {
                    correctedReferencedFiles.add(referencedFile);
                }
            }
            bt.getHead().getMetaInformation().setReferencedFiles(correctedReferencedFiles);
            
            // new 10-12-2024
            String[] canonicalTierOrder = bt.getBody().makeCanonicalTierOrder(bt.getHead().getSpeakertable().getAllSpeakerIDs());
            bt.getBody().reorderTiers(canonicalTierOrder);
            
            return bt;
        } catch (JDOMException | SAXException | JexmaraldaException | ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(TEIConverter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        }        
    }
    
    private String removeTimepoints(String in) throws SAXException, ParserConfigurationException, IOException, TransformerException, JDOMException {
        
        // THAT WAS THE OLD WAY
        /*StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_2b_REMOVE_TIMEPOINTS_XSL, in);
        return result;*/
        
        System.out.println("Started");
        
        Document inDoc = IOUtilities.readDocumentFromString(in);
        
        Set<String> referredIDs = new HashSet<>();
        List l1 = XPath.selectNodes(inDoc, "//@*[name()='start' or name()='end' or name()='from' or name()='to' or name()='corresp']");
        for (Object o : l1){
            Attribute a = (Attribute)o;
            String value = a.getValue();
            referredIDs.addAll(Arrays.asList(value.split(" ")));
        }
        
        System.out.println("Referred IDs finished");

        Map<String,Integer> anchorCounts = new HashMap<>();
        XPath anchorXPath = XPath.newInstance("//tei:anchor"); 
        anchorXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        List anchors = anchorXPath.selectNodes(inDoc);
        for (Object o : anchors){
            Element anchor = (Element)o;
            String synch = anchor.getAttributeValue("synch");
            if (!(anchorCounts.containsKey(synch))){
                anchorCounts.put(synch, 0);
            }
            anchorCounts.put(synch, anchorCounts.get(synch) + 1);
        }
        
        System.out.println("Anchor counts finished");

        XPath timelineXPath = XPath.newInstance("//tei:timeline"); 
        timelineXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        Element timeline = (Element)(timelineXPath.selectSingleNode(inDoc));
        Set<String> sinces = new HashSet<>();
        Set<String> remaining = new HashSet<>();
        List l2 = timeline.getChildren();
        List<Element> toBeDetached = new ArrayList<>();
        for (Object o : l2){
            Element whenElement = (Element)o;
            String id = whenElement.getAttributeValue("id", Namespace.XML_NAMESPACE);
            if (whenElement.getAttribute("since")!=null) {
                sinces.add(whenElement.getAttributeValue("since"));
                remaining.add(id);
                continue;
            }
            if (whenElement.getAttribute("interval")!=null) {
                remaining.add(id);
                continue;
            }
            if (anchorCounts.containsKey(id) && anchorCounts.get(id) > 1) {
                remaining.add(id);
                continue;
            }
            if (referredIDs.contains(id)) {
                remaining.add(id);
                continue;
            }
            if (sinces.contains(id)) {
                remaining.add(id);
                continue;
            }
            
            toBeDetached.add(whenElement);
        }
        
        System.out.println("Timeline finished");
        
        for (Object o : anchors){
            Element anchor = (Element)o;
            String synch = anchor.getAttributeValue("synch");
            if (remaining.contains(synch)) continue;
            if (anchorCounts.containsKey(synch) && anchorCounts.get(synch) > 1) continue;
            if (referredIDs.contains(synch)) continue;
            
            toBeDetached.add(anchor);
        }        
        
        for (Element e : toBeDetached){
            e.detach();
        }
        
        
        return IOUtilities.documentToString(inDoc);
    }

    
    
    // issue #222
    public NormalizedFolkerTranscription readFOLKERISOTEIFromFile(String path) throws IOException {
        try {
            StylesheetFactory sf = new StylesheetFactory(true);
            Document doc = IOUtilities.readDocumentFromLocalFile(path);
            String docString = IOUtilities.documentToString(doc);
            String transform1 = sf.applyInternalStylesheetToString(ISOTEI2FOLKER_1_SPANS2ATTRIBUTES_XSL, docString);
            String flnString = sf.applyInternalStylesheetToString(ISOTEI2FOLKER_2_TRANSFORM_XSL, transform1);
            NormalizedFolkerTranscription nft = XMLReaderWriter.readNormalizedFolkerTranscription(flnString);
            
            // set the media path correctly
            String resolvedPath = XMLReaderWriter.resolveMediaPath(nft.getDocument(), new File(path));
            
            nft.setMediaPath(resolvedPath);
            return nft;
        } catch (JDOMException | IOException | SAXException | ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(TEIConverter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        }
    }
    
    
    
    

    //****************************************************
    //********* private processing methods ***************
    //****************************************************


    HashSet<String> allExistingIDs = new HashSet<>();
    
    // new 30-03-2016
    private void generateWordIDs(Document document) throws JDOMException{
        // added 30-03-2016
        XPath idXPath = XPath.newInstance("//tei:*[@xml:id]"); 
        idXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        idXPath.addNamespace(Namespace.XML_NAMESPACE);
        List idElements = idXPath.selectNodes(document);
        for (Object o : idElements){
            Element e = (Element)o;
            allExistingIDs.add(e.getAttributeValue("id", Namespace.XML_NAMESPACE));
        }
        
        
        // changed 30-03-2016
        XPath wordXPath = XPath.newInstance("//tei:w[not(@xml:id)]"); 
        wordXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        wordXPath.addNamespace(Namespace.XML_NAMESPACE);
        
        List words = wordXPath.selectNodes(document);
        int count=1;
        for (Object o : words){
            Element word = (Element)o;
            while(allExistingIDs.contains("w" + Integer.toString(count))){
                count++;
            }

            String wordID = "w" + Integer.toString(count);
            allExistingIDs.add(wordID);
            //System.out.println("*** " + wordID);
            word.setAttribute("id", wordID, Namespace.XML_NAMESPACE);
        }
        
        
        // new 02-12-2014
        XPath pcXPath = XPath.newInstance("//tei:pc[not(@xml:id)]"); 
        pcXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        pcXPath.addNamespace(Namespace.XML_NAMESPACE);
        
        List pcs = pcXPath.selectNodes(document);
        count=1;
        for (Object o : pcs){
            Element pc = (Element)o;
            while(allExistingIDs.contains("pc" + Integer.toString(count))){
                count++;
            }
            
            String pcID = "pc" + Integer.toString(count);
            allExistingIDs.add(pcID);
            //System.out.println("*** " + wordID);
            pc.setAttribute("id", pcID, Namespace.XML_NAMESPACE);
        }

        // new 19-11-2019
        XPath vocXPath = XPath.newInstance("//tei:vocal[not(@xml:id)]"); 
        vocXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        vocXPath.addNamespace(Namespace.XML_NAMESPACE);        
        List vocs = vocXPath.selectNodes(document);
        count=1;
        for (Object o : vocs){
            Element voc = (Element)o;
            while(allExistingIDs.contains("v" + Integer.toString(count))){
                count++;
            }
            
            String pcID = "v" + Integer.toString(count);
            allExistingIDs.add(pcID);
            //System.out.println("*** " + wordID);
            voc.setAttribute("id", pcID, Namespace.XML_NAMESPACE);
        }

        // new 19-11-2019
        XPath incXPath = XPath.newInstance("//tei:incident[not(@xml:id)]"); 
        incXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        incXPath.addNamespace(Namespace.XML_NAMESPACE);        
        List incs = incXPath.selectNodes(document);
        count=1;
        for (Object o : incs){
            Element inc = (Element)o;
            while(allExistingIDs.contains("i" + Integer.toString(count))){
                count++;
            }
            
            String pcID = "i" + Integer.toString(count);
            allExistingIDs.add(pcID);
            //System.out.println("*** " + wordID);
            inc.setAttribute("id", pcID, Namespace.XML_NAMESPACE);
        }

        // new 19-11-2019
        XPath pauseXPath = XPath.newInstance("//tei:pause[not(@xml:id)]"); 
        pauseXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        pauseXPath.addNamespace(Namespace.XML_NAMESPACE);        
        List pauses = pauseXPath.selectNodes(document);
        count=1;
        for (Object o : pauses){
            Element pause = (Element)o;
            while(allExistingIDs.contains("p" + Integer.toString(count))){
                count++;
            }
            
            String pcID = "p" + Integer.toString(count);
            allExistingIDs.add(pcID);
            //System.out.println("*** " + wordID);
            pause.setAttribute("id", pcID, Namespace.XML_NAMESPACE);
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

        List<Element> allSegments = new ArrayList<>();
        Iterator i = doc.getDescendants(new ElementFilter("seg"));
        while (i.hasNext()){
            Element seg = (Element)(i.next());
            allSegments.add(seg);
        }
        for (Element seg : allSegments){
            if (!(seg.getAttributeValue("type").equals("segmental"))) continue;
            List<Content> newContent = new ArrayList<>();
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

    private void setDocLanguage(Document teiDoc, String language) throws JDOMException {
        // /TEI/text[1]/@*[namespace-uri()='http://www.w3.org/XML/1998/namespace' and local-name()='lang']
        XPath xpathToLangAttribute = XPath.newInstance("//tei:text/@xml:lang");
        xpathToLangAttribute.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        xpathToLangAttribute.addNamespace(Namespace.XML_NAMESPACE);
        Attribute langAtt = (Attribute) xpathToLangAttribute.selectSingleNode(teiDoc);
        if (langAtt!=null){
            langAtt.setValue(language);
        } else {
            XPath xpathToTextElement = XPath.newInstance("//tei:text");
            xpathToTextElement.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
            xpathToTextElement.addNamespace(Namespace.XML_NAMESPACE);
            Element textEl = (Element) xpathToTextElement.selectSingleNode(teiDoc);
            textEl.setAttribute("lang", language, Namespace.XML_NAMESPACE);
        }
        System.out.println("[TEIConverter] Language of document set to " + language);        
    }
    
    private void setTranscriptionDesc(Document teiDoc, String ident, String version) throws JDOMException {
        // /tei:TEI/tei:teiHeader[1]/tei:encodingDesc[1]/tei:transcriptionDesc[1]
        XPath xpathToTranscriptionDesc = XPath.newInstance("//tei:transcriptionDesc[1]");
        xpathToTranscriptionDesc.addNamespace("tei", "http://www.tei-c.org/ns/1.0");        
        Element transcriptionDesc = (Element) xpathToTranscriptionDesc.selectSingleNode(teiDoc);
        transcriptionDesc.setAttribute("ident", ident);
        transcriptionDesc.setAttribute("version", version);
    }
    

    /*********************************************/
    /** DEPRECATED METHODS, COMPLICATED SHADOWS **/
    /*********************************************/

    /**
     * Reads a TEI file like the one defined in https://nbn-resolving.org/urn:nbn:de:bsz:mh39-22729
     * 
     * @param path
     * @return
     * @throws IOException 
     */
    @Deprecated
    public BasicTranscription readTEIFromFile(String path) throws IOException { 
        try {
            Document doc = IOUtilities.readDocumentFromLocalFile(path);
            if ("TEI.2".equals(doc.getRootElement().getName())){
                // i.e. this must be a TEI document like the one defined in the AzM paper
                TEISaxReader reader = new TEISaxReader();
                return reader.readFromFile(path);
            }
            StylesheetFactory ssf = new StylesheetFactory(true);
            String result = ssf.applyInternalStylesheetToString(TEI2EXMARaLDA_XSL, IOUtilities.documentToString(doc));
            BasicTranscription bt = new BasicTranscription();
            bt.BasicTranscriptionFromString(result);
            return bt;
        } catch (JexmaraldaException | ParserConfigurationException | TransformerException | SAXException | IOException | JDOMException ex) {
            Logger.getLogger(TEIConverter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        } 
    }

    @Deprecated
    public String BasicTranscriptionToTEI(BasicTranscription bt) throws SAXException,
                                                                        IOException,
                                                                        ParserConfigurationException,
                                                                        TransformerConfigurationException,
                                                                        TransformerException,
                                                                        JDOMException {
        return BasicTranscriptionToTEI(bt, false);
    }

    @Deprecated
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
        String result = sf.applyInternalStylesheetToString(EXMARaLDA2TEI_XSL, lt.toXML());
        if (callBracketReplacer){
            return replaceBrackets(result);
        }
        return result;
    }
    
    @Deprecated
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

    //////////////////////////////////////////////////////////////////
    //////////////// OLDER METHODS, NOT NEEDED SINCE ISO /////////////
    //////////////////////////////////////////////////////////////////
    
    /** transforms the basic transcription and writes a TEI file to path
     * according to the method described in Schmidt 2005 */
    @Deprecated
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
    @Deprecated
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

    @Deprecated
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

    @Deprecated
    public void writeNewHIATTEIToFile(BasicTranscription bt, String filename) throws SAXException,
                                                                              FSMException,
                                                                              XSLTransformException,
                                                                              JDOMException,
                                                                              IOException,
                                                                              Exception {
        writeNewHIATTEIToFile(bt, filename, true);
    }

    @Deprecated
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

    
    @Deprecated
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

    @Deprecated
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
    
    // I think these are rather deprecated
    public static String EXMARaLDA2TEI_XSL = "/org/exmaralda/partitureditor/jexmaralda/xsl/EXMARaLDA2TEI.xsl";
    public static String TEI2EXMARaLDA_XSL = "/org/exmaralda/tei/xml/tei2exmaralda.xsl";
    
    
    public static final int GENERIC_METHOD = 0;
    public static final int AZM_METHOD = 1;
    public static final int MODENA_METHOD = 2;
    public static final int HIAT_METHOD = 3;
    public static final int CGAT_METHOD = 4;
    public static final int HIAT_NEW_METHOD = 5;


    




    




    
}
