/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.tei;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.ConverterEvent;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
import static org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter.ISOTEI2EXMARaLDA_0_NORMALIZE;
import static org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter.ISOTEI2EXMARaLDA_1_ATTRIBUTES2SPANS_XSL;
import static org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter.ISOTEI2EXMARaLDA_1b_AUGMENTTIMELINE_XSL;
import static org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter.ISOTEI2EXMARaLDA_2_TOKEN2TIMEREFS_XSL;
import static org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter.ISOTEI2EXMARaLDA_2b_REMOVE_TIMEPOINTS_XSL;
import static org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter.ISOTEI2EXMARaLDA_3_DETOKENIZE_XSL;
import static org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter.ISOTEI2EXMARaLDA_4_TRANSFORM_XSL;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.transform.XSLTransformException;
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
            String transform2_b = sf.applyInternalStylesheetToString(ISOTEI2EXMARaLDA_2b_REMOVE_TIMEPOINTS_XSL, transform2);
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
    
}
