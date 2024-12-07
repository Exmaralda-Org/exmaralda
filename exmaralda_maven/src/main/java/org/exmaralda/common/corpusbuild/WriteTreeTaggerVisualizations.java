/*
 * MakePartiturFiles.java
 *
 * Created on 21. November 2006, 15:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.segment.SegmentedToListInfo;
import org.xml.sax.SAXException;
import org.jdom.*;
import org.jdom.transform.*;

/**
 *
 * @author thomas
 */
public class WriteTreeTaggerVisualizations extends AbstractSegmentedTranscriptionProcessor {
    
    String STYLESHEET = ""; //Constants.LIST2HTMLStylesheet;
    String suffix = "_TT";
    String toTopLevel = "../../../";
    
    
    /** Creates a new instance of MakePartiturFiles */
    public WriteTreeTaggerVisualizations(String corpusName, String s, String stylesheet) {
        super(corpusName);
        suffix = s;
        STYLESHEET = stylesheet;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            WriteTreeTaggerVisualizations wul = new WriteTreeTaggerVisualizations(args[0], args[2], args[3]);
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

    public void processTranscription(SegmentedTranscription st) {
        try {
            // einen Stylesheet-Prozessor initialisieren
            final org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory(true);
            SegmentedToListInfo info = new SegmentedToListInfo(st, SegmentedToListInfo.HIAT_UTTERANCE_SEGMENTATION);
            ListTranscription lt = st.toListTranscription(info);

            lt.getHead().getMetaInformation().relativizeReferencedFile(currentFilename);
            
            String listFilename =    getCurrentDirectoryname() 
                                    + System.getProperty("file.separator")
                                    + "presentation"
                                    + System.getProperty("file.separator")
                                    + getNakedFilenameWithoutSuffix()
                                    + suffix
                                    + ".xml";                        
            lt.writeXMLToFile(listFilename,"none");
            final String listHTMLFilename =    getCurrentDirectoryname()
                                    + System.getProperty("file.separator")
                                    + "presentation"
                                    + System.getProperty("file.separator")
                                    + getNakedFilenameWithoutSuffix()
                                    + suffix
                                    + ".html";                        
            //lt.writeHTMLToFile(listFilename, STYLESHEET);
            try {
                final Document listXML = FileIO.readDocumentFromLocalFile(listFilename);
                final String prev = toTopLevel + previousURL + suffix + ".html";
                final String next = toTopLevel + nextURL + suffix + ".html";
                FileIO.writeDocumentToLocalFile(listFilename, listXML);
                final String f_listFilename = listFilename;

                Runnable theRunnable = new Runnable(){

                    public void run() {
                        try {
                            String transDoc = sf.applyExternalStylesheetToExternalXMLFile(STYLESHEET, f_listFilename);
                            Document listHTML = IOUtilities.readDocumentFromString(transDoc);
                            AbstractCorpusProcessor.insertPreviousAndNext(listHTML, prev, next);
                            String s1 = "HTML";
                            String s2 = "-//W3C//DTD HTML 4.01//EN";
                            FileIO.writeDocumentToLocalFile(listHTMLFilename, listHTML, true, s1, s2);
                        } catch (SAXException ex) {
                            ex.printStackTrace();
                        } catch (ParserConfigurationException ex) {
                            ex.printStackTrace();
                        } catch (TransformerConfigurationException ex) {
                            ex.printStackTrace();
                        } catch (TransformerException ex) {
                            ex.printStackTrace();
                        } catch (JDOMException ex) {
                            ex.printStackTrace();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }                    
                };
                
                new Thread(theRunnable).start();

                
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JDOMException ex) {
                ex.printStackTrace();
            }
            
            outappend(listFilename +  " written.\n");   

        } catch (IOException ex) {
            ex.printStackTrace();
        }          
    }
    
}
