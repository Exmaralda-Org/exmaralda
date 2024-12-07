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
import java.io.*;
import org.xml.sax.*;
import org.jdom.*;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.interlinearText.*;
import org.xml.sax.SAXException;
import org.jdom.*;
import org.jdom.filter.*;
import java.util.*;
import org.jdom.xpath.*;
import org.jdom.transform.*;

/**
 *
 * @author thomas
 */
public class WriteHeadFiles extends AbstractCorpusProcessor {
    
    String STYLESHEET = ""; //Constants.HEAD2HTMLStylesheet; 
    XSLTransformer transformer;
    
    String toTopLevel = "../../../";
    
    public String currentFilename;
    
    String suffix = "_meta";
    

    
    /** Creates a new instance of AbstractCorpusProcessor */
    public WriteHeadFiles(String corpusName, String s, String xsl) {
        super(corpusName);
        System.out.println("--------------------------------------");
        System.out.println("WRITE HEAD FILES");
        System.out.println("--------------------------------------");
        System.out.println("CORPUS NAME: " + corpusName);
        System.out.println("SUFFIX: " + s);
        System.out.println("STYLESHEET: " + xsl);
        System.out.println("--------------------------------------");
        suffix = s;
        STYLESHEET = xsl;
        /*try {
            STYLESHEET = xsl;
            transformer = new XSLTransformer(STYLESHEET);    
            suffix = s;
        } catch (XSLTransformException ex) {
            ex.printStackTrace();
        } */
    }
    
    
    public static void main(String[] args) {
        try {
            WriteHeadFiles fcs = new WriteHeadFiles(args[0], args[2], args[3]);
            if (args.length>4){
                fcs.toTopLevel = args[4];
            }
            fcs.doIt();
            fcs.output(args[1]);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        }
    }

    public void process(String filename) throws JexmaraldaException, SAXException {
            org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf
                    = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory(true);

            String fullTranscriptionName = filename;
            
            
            currentFilename = fullTranscriptionName;
            //Document doc;
            try {
                //doc = FileIO.readDocumentFromLocalFile(fullTranscriptionName);
                
                int index = currentFilename.lastIndexOf(".");
                String htmlFilename =    getCurrentDirectoryname() 
                                            + System.getProperty("file.separator")
                                            + "presentation"
                                            + System.getProperty("file.separator")
                                            + getNakedFilenameWithoutSuffix()
                                            + suffix
                                            + ".html";                        

                 //Document htmlDoc = transformer.transform(doc);
                 String transDoc = sf.applyExternalStylesheetToExternalXMLFile(STYLESHEET, fullTranscriptionName);
                 Document htmlDoc = FileIO.readDocumentFromString(transDoc);
                 
                 String prev = toTopLevel + previousURL + suffix + ".html";
                 String next = toTopLevel + nextURL + suffix + ".html";                 
                 AbstractCorpusProcessor.insertPreviousAndNext(htmlDoc, prev, next);

                 FileIO.writeDocumentToLocalFile(htmlFilename,htmlDoc,true);
                 outappend(htmlFilename + " written.\n");
                
                
            } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
            } catch (TransformerConfigurationException ex) {
                ex.printStackTrace();
            } catch (TransformerException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JDOMException ex) {
                ex.printStackTrace();
            } 
    }

    public String getXpathToTranscriptions() {
        return BASIC_FILE_XPATH;
    }
    
}
