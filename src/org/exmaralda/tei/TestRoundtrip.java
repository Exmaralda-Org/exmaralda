/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.tei;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.transform.XSLTransformException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class TestRoundtrip {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestRoundtrip().doit();
        } catch (SAXException | JexmaraldaException | FSMException | JDOMException | IOException | ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(TestRoundtrip.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


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


    private void doit() throws SAXException, JexmaraldaException, FSMException, JDOMException, XSLTransformException, IOException, ParserConfigurationException, TransformerException {
        
        reRead(new File("C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_03_04_ISSUE_375\\DNAM_E_00003_SE_01_T_01_DF_01.xml"));
        System.exit(0);
        
        
        TEIConverter teiConverter = new TEIConverter();
        
        String[] HIAT_FILES = {
            "C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_02_11_ISSUE_367\\HIAT\\HIAT_IN.exb",
            "C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_02_11_ISSUE_367\\HIAT_BECKHAMS\\HIAT_IN.exb",            
            "C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_02_11_ISSUE_367\\HIAT_ANNEWILL\\HIAT_IN.exb",             
            "C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_02_11_ISSUE_367\\HIAT_HAMATAC\\HIAT_IN.exb"            
        };
        
        for (String path : HIAT_FILES){
            File inFile = new File(path);
            BasicTranscription bt = new BasicTranscription(inFile.getAbsolutePath());
            File outFile = new File(inFile.getParentFile(), "0_HIAT_OUT.xml");
            teiConverter.writeHIATISOTEIToFile(bt, outFile.getAbsolutePath());
            reRead(outFile);
        }

        String[] CGAT_FILES = {
            "C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_02_11_ISSUE_367\\cGAT_EXB_FOLK_56\\cGAT_IN.exb",
        };
        
        for (String path : CGAT_FILES){
            File inFile = new File(path);
            BasicTranscription bt = new BasicTranscription(inFile.getAbsolutePath());
            File outFile = new File(inFile.getParentFile(), "0_CGAT_OUT.xml");
            teiConverter.writeCGATMINIMALISOTEIToFile(bt, outFile.getAbsolutePath(), null);
            reRead(outFile);
        }


        String[] EVENT_FILES = {
            "C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_02_11_ISSUE_367\\EVENT_FOLK_56\\EVENT_IN.exb",
        };
        
        for (String path : EVENT_FILES){
            File inFile = new File(path);
            BasicTranscription bt = new BasicTranscription(inFile.getAbsolutePath());
            File outFile = new File(inFile.getParentFile(), "0_EVENT_OUT.xml");
            teiConverter.writeEventTokenISOTEIToFile(bt, outFile.getAbsolutePath());
            reRead(outFile);
        }


        String[] GENERIC_FILES = {
            "C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_02_11_ISSUE_367\\GENERIC\\GENERIC_IN.exb",

        };
        
        for (String path : GENERIC_FILES){
            File inFile = new File(path);
            BasicTranscription bt = new BasicTranscription(inFile.getAbsolutePath());
            File outFile = new File(inFile.getParentFile(), "0_GENERIC_OUT.xml");
            teiConverter.writeGenericSegmentedISOTEIToFile(bt, outFile.getAbsolutePath(), null);
            reRead(outFile);
        }


        String[] FLN_FILES = {
            "C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2023_02_11_ISSUE_367\\FLN_FOLK_56\\FLN_IN.fln",
        };
        
        for (String path : FLN_FILES){
            File inFile = new File(path);
            Document flnDoc = FileIO.readDocumentFromLocalFile(inFile);
            teiConverter.setLanguage("de");
            File outFile = new File(inFile.getParentFile(), "0_FLN_OUT.xml");
            teiConverter.writeFOLKERISOTEIToFile(flnDoc, outFile.getAbsolutePath()); 
            reRead(outFile);
        }


    }
    
    
    private void reRead(File exportedFile) throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerException, JexmaraldaException{
            StylesheetFactory sf = new StylesheetFactory(true);
            Document doc = FileIO.readDocumentFromLocalFile(exportedFile);
            String docString = IOUtilities.documentToString(doc);           
            String nakedFilename = exportedFile.getName().substring(2, exportedFile.getName().indexOf("."));
            // new 10-03-2021
            String transform0 = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_0_NORMALIZE, docString);
            FileIO.writeDocumentToLocalFile(new File(exportedFile.getParentFile(), "1_" + nakedFilename + "_NORMALIZED.xml"), IOUtilities.readDocumentFromString(transform0));


            String transform1 = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_1_ATTRIBUTES2SPANS_XSL, transform0);
            FileIO.writeDocumentToLocalFile(new File(exportedFile.getParentFile(), "2_" + nakedFilename + "_ATTRIBUTES2SPANS.xml"), IOUtilities.readDocumentFromString(transform1));
            
            // new 09-03-2021
            String transform1_b = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_1b_AUGMENTTIMELINE_XSL, transform1);
            FileIO.writeDocumentToLocalFile(new File(exportedFile.getParentFile(), "3_" + nakedFilename + "_AUGMENTTIMELINE.xml"), IOUtilities.readDocumentFromString(transform1_b));
                        
            String transform2 = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_2_TOKEN2TIMEREFS_XSL, transform1_b);
            FileIO.writeDocumentToLocalFile(new File(exportedFile.getParentFile(), "4_" + nakedFilename + "_TOKEN2TIMEREFS.xml"), IOUtilities.readDocumentFromString(transform2));

            // new 10-03-2021
            //String transform2_b = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_2b_REMOVE_TIMEPOINTS_XSL, transform2);
            String transform2_b = removeTimepoints(transform2);
            FileIO.writeDocumentToLocalFile(new File(exportedFile.getParentFile(), "5_" + nakedFilename + "_REMOVE_TIMEPOINTS.xml"), IOUtilities.readDocumentFromString(transform2_b));
            
            String transform2_c = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_2c_DESEGMENT_XSL, transform2_b);
            FileIO.writeDocumentToLocalFile(new File(exportedFile.getParentFile(), "6_" + nakedFilename + "_DESEGMENTED.xml"), IOUtilities.readDocumentFromString(transform2_c));

            String transform3 = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_3_DETOKENIZE_XSL, transform2_c);
            FileIO.writeDocumentToLocalFile(new File(exportedFile.getParentFile(), "7_" + nakedFilename + "_DETOKENIZED.xml"), IOUtilities.readDocumentFromString(transform3));

            String transform3_b = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_3b_AUGMENT_FINAL_XSL, transform3);
            FileIO.writeDocumentToLocalFile(new File(exportedFile.getParentFile(), "8_" + nakedFilename + "_AUGMENT_FINAL.xml"), IOUtilities.readDocumentFromString(transform3_b));

            String transform3_c = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_3c_REMOVE_STRAY_ANCHORS_XSL, transform3_b);
            FileIO.writeDocumentToLocalFile(new File(exportedFile.getParentFile(), "9_" + nakedFilename + "_NO_STRAY_ANCHORS.xml"), IOUtilities.readDocumentFromString(transform3_c));


            String exbString = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_4_TRANSFORM_XSL, transform3_c);
            FileIO.writeDocumentToLocalFile(new File(exportedFile.getParentFile(), "10_" + nakedFilename + "_TRANSFORMED.exb"), IOUtilities.readDocumentFromString(exbString));

            BasicTranscription bt = new BasicTranscription();
            bt.BasicTranscriptionFromString(exbString);
        
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

    
    
}
