/*
 * MakePartiturFiles.java
 *
 * Created on 21. November 2006, 15:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.*;
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
public class TransformBasicTranscriptions extends AbstractCorpusProcessor {
    
    String TRANSFORM_STYLESHEET = ""; //Constants.FORMATTABLE4BASICTRANSCRIPTIONStylesheet;            
    String suffix = "_ulist";
    String toTopLevel = "../../../";

    
    XSLTransformer transformer;
    
    /** Creates a new instance of MakePartiturFiles */
    public TransformBasicTranscriptions(String corpusName, String s, String stylesheet) {
        super(corpusName);
        TRANSFORM_STYLESHEET = stylesheet;
        suffix = s;
        try {
            transformer = new XSLTransformer(TRANSFORM_STYLESHEET);            
        } catch (XSLTransformException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            TransformBasicTranscriptions wp = new TransformBasicTranscriptions(args[0], args[2], args[3]);
            if (args.length>4){
                wp.toTopLevel = args[4];
            }            
            wp.doIt();
            wp.output(args[1]);            
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }            
    }


    public void process(String filename) throws JexmaraldaException, SAXException {
            System.out.println("Transforming...");
        String htmlFilename;
        try {
            Document btdoc = FileIO.readDocumentFromLocalFile(filename);
            Document htmlDoc = transformer.transform(btdoc);
            htmlFilename = getCurrentDirectoryname() + System.getProperty("file.separator") + "presentation" + System.getProperty("file.separator") + getNakedFilenameWithoutSuffix() + suffix + ".html";
            
            String prev = toTopLevel + previousURL + suffix + ".html";
            String next = toTopLevel + nextURL + suffix + ".html";                 
            AbstractCorpusProcessor.insertPreviousAndNext(htmlDoc, prev, next);
            String s1 = "HTML";
            String s2 = "-//W3C//DTD HTML 4.01//EN";
            FileIO.writeDocumentToLocalFile(htmlFilename,htmlDoc, true, s1, s2);
            //FileIO.writeDocumentToLocalFile(htmlFilename,htmlDoc, true);
            outappend(htmlFilename +  " written.\n");        
        } catch (XSLTransformException ex) {
            JexmaraldaException je = new JexmaraldaException(0, ex.getMessage());
            throw je;
        } catch (JDOMException ex) {
            JexmaraldaException je = new JexmaraldaException(0, ex.getMessage());
            throw je;
        } catch (IOException ex) {
            JexmaraldaException je = new JexmaraldaException(0, ex.getMessage());
            throw je;
        }
            
            
        
    }

    public String getXpathToTranscriptions() {
        return super.BASIC_FILE_XPATH;
    }
    
}
