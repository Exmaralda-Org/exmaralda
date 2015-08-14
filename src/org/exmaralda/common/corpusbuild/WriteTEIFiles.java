/*
 * AbstractCorpusProcessor.java
 *
 * Created on 10. Oktober 2006, 17:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import org.jdom.transform.XSLTransformException;
import org.xml.sax.*;
import org.jdom.*;
/**
 *
 * @author thomas
 */
public class WriteTEIFiles extends AbstractCorpusProcessor {
    
    
    public String currentFilename;
    TEIMerger teiMerger;
    String suffix = "_TEI";
    String segmented_suffix = "_s";
    String nameOfDeepSegmentation = "SpeakerContribution_Utterance_Word";
    
    /** Creates a new instance of AbstractCorpusProcessor */

    public WriteTEIFiles(   String corpusName,
                            String s,
                            String tei_skeleton_xsl,
                            String sc_to_tei_xsl,
                            String sort_and_clean_xsl) throws XSLTransformException {
        super(corpusName);
        suffix = s;
        teiMerger = new TEIMerger(tei_skeleton_xsl, sc_to_tei_xsl, sort_and_clean_xsl);

    }
    
    public static void main(String[] args){
        try {
            WriteTEIFiles wtf = new WriteTEIFiles(args[0], args[2], args[3], args[4], args[5]);
            if (args.length==7){
                wtf.segmented_suffix = args[6];
            }
            if (args.length==8){
                wtf.nameOfDeepSegmentation = args[7];
            }
            wtf.doIt();
            wtf.output(args[1]);
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void process(String filename) throws JexmaraldaException, SAXException {
        try {
            Document st = FileIO.readDocumentFromLocalFile(filename);
            /*Document teiDoc = teiMerger.SegmentedTranscriptionToTEITranscription(st,
                                                                            "SpeakerContribution_Utterance_Word",
                                                                            "SpeakerContribution_Event");*/
            Document teiDoc = teiMerger.SegmentedTranscriptionToTEITranscription(st,
                                                                            nameOfDeepSegmentation,
                                                                            "SpeakerContribution_Event");
            int index = getNakedFilenameWithoutSuffix().lastIndexOf(segmented_suffix);
            String teiFilename =    getCurrentDirectoryname() 
                                    + System.getProperty("file.separator")
                                    + "export"
                                    + System.getProperty("file.separator")
                                    + getNakedFilenameWithoutSuffix().substring(0,index)
                                    + suffix
                                    + ".xml";                        
            FileIO.writeDocumentToLocalFile(teiFilename, teiDoc);
            outappend(teiFilename +  " written.\n");   
        } catch (XSLTransformException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }               
    }

    public String getXpathToTranscriptions() {
        return SEGMENTED_FILE_XPATH;
    }
    
}
