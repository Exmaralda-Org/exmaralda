/*
 * AbstractCorpusProcessor.java
 *
 * Created on 10. Oktober 2006, 17:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.GATParser;
import org.jdom.transform.XSLTransformException;
import org.xml.sax.*;
import org.jdom.*;
import org.exmaralda.partitureditor.jexmaralda.convert.ELANConverter;
/**
 *
 * @author thomas
 */
public class WriteFOLKERTranscriptions extends AbstractBasicTranscriptionProcessor {
    
    
    
    /** Creates a new instance of AbstractCorpusProcessor */

    public WriteFOLKERTranscriptions(String corpusName) {
        super(corpusName);
    }
    
    public static void main(String[] args){
        try {
            String comafile = "T:\\TP-E5\\VORBILD\\ENDFAS_SKOBI_GoldStandard.coma";
            if (args.length>0){
                comafile = args[0];
            }
            WriteFOLKERTranscriptions wtf = new WriteFOLKERTranscriptions(comafile);
            wtf.doIt();
            //wtf.output(args[1]);
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


    public void processTranscription(BasicTranscription bt) {
        String folkerFilename =    getCurrentDirectoryname()
                                + System.getProperty("file.separator")
                                + "export"
                                + System.getProperty("file.separator")
                                + getNakedFilenameWithoutSuffix()
                                + ".flk";
        EventListTranscription elt =
            org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt);
        System.out.println("SOOOOOOOOO VIELE: " + elt.getNumberOfContributions());
        try {
            org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.writeXML(elt, new File(folkerFilename), new GATParser(), 1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
