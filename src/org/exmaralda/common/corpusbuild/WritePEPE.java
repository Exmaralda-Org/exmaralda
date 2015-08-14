/*
 * MakePartiturFiles.java
 *
 * Created on 21. November 2006, 15:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.ListTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.fsm.FSMException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.interlinearText.*;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;
import org.jdom.*;
import org.jdom.transform.*;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public class WritePEPE extends AbstractBasicTranscriptionProcessor {
    
    String STYLESHEET = ""; //Constants.LIST2HTMLStylesheet;
    String suffix = "_ulist";        
    String toTopLevel = "../../../";
    XSLTransformer transformer;
    
    
    /** Creates a new instance of MakePartiturFiles */
    public WritePEPE(String corpusName, String s, String stylesheet) {
        super(corpusName);
        suffix = s;
        STYLESHEET = stylesheet;
        try {
            transformer = new XSLTransformer(STYLESHEET);            
            //sf = new jexmaralda.convert.StylesheetFactory();            
        } catch (XSLTransformException ex) {
            ex.printStackTrace();
        }
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            WritePEPE wul = new WritePEPE(args[0], args[2], args[3]);
            if (args.length>4){
                wul.toTopLevel = args[4];
            }            
            wul.doIt();
            wul.output(args[1]);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
    }

    public void processTranscription(BasicTranscription bt) {
        try {
            // einen Stylesheet-Prozessor initialisieren
            org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory();
            org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation hs = new org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation();
            String referencedFile = bt.getHead().getMetaInformation().getReferencedFile();
            //This line is buggy: it does not work on Linux because
            // Linux has a different file separator
            //referencedFile=referencedFile.substring(referencedFile.lastIndexOf("\\")+1);
            referencedFile = new File(referencedFile).getName();
            System.out.println("Referenced file: " + referencedFile);
            System.out.println("Referenced file: " + referencedFile);
            ListTranscription lt = hs.BasicToUtteranceList(bt);

            lt.getHead().getMetaInformation().relativizeReferencedFile(currentFilename);
            
            String listFilename =    getCurrentDirectoryname() 
                                    + System.getProperty("file.separator")
                                    + "presentation"
                                    + System.getProperty("file.separator")
                                    + getNakedFilenameWithoutSuffix()
                                    + "_ulist"
                                    + ".xml";                        
            lt.writeXMLToFile(listFilename,"none");
            String pepeXMLFilename =    getCurrentDirectoryname()
                                    + System.getProperty("file.separator")
                                    + "export"
                                    + System.getProperty("file.separator")
                                    + getNakedFilenameWithoutSuffix()
                                    + suffix
                                    + ".xml";
            //lt.writeHTMLToFile(listFilename, STYLESHEET);
            try {
                Document listXML = FileIO.readDocumentFromLocalFile(listFilename);
                listXML.getRootElement().
                        getChild("head").getChild("meta-information").
                        getChild("referenced-file").setAttribute("url", referencedFile);
                
                Document basicXML = FileIO.readDocumentFromLocalFile(currentFilename);
                Element acTier = (Element)(XPath.newInstance("//tier[@category='ac']").selectSingleNode(basicXML));
                if (acTier!=null){
                    acTier.detach();
                    listXML.getRootElement().addContent(acTier);
                }
                Element nvTier = (Element)(XPath.newInstance("//tier[@category='nv']").selectSingleNode(basicXML));
                if (nvTier!=null){
                    nvTier.detach();
                    listXML.getRootElement().addContent(nvTier);
                }

                Document pepeXML = transformer.transform(listXML);
                
                
                FileIO.writeDocumentToLocalFile(pepeXMLFilename,pepeXML);
                
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JDOMException ex) {
                ex.printStackTrace();
            }
            
            outappend(pepeXMLFilename +  " written.\n");

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (FSMException ex) {
            ex.printStackTrace();
        }          
    }
    
}
