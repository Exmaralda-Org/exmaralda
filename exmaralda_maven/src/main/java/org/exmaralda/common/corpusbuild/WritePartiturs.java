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
import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.interlinearText.*;
import org.xml.sax.SAXException;
import org.jdom.*;
import org.jdom.filter.*;
import java.util.*;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.jdom.xpath.*;
import org.jdom.transform.*;

/**
 *
 * @author thomas
 */
public class WritePartiturs extends AbstractBasicTranscriptionProcessor {
    
    String PARTITUR_FORMAT_STYLESHEET = ""; //Constants.FORMATTABLE4BASICTRANSCRIPTIONStylesheet;            
    String IT_TRANSFORM_STYLESHEET = ""; //Constants.PARTITUR2HTMLStylesheet;
    String RTF_STYLESHEET = "";
    String suffix = "_partiture";
    String toTopLevel = "../../../";
    double HTML_WIDTH = 800.0;

    
    //XSLTransformer transformer;
    org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf;
    
    /** Creates a new instance of MakePartiturFiles */
    public WritePartiturs(String corpusName, String s, String pfStylesheet, String itStylesheet, String rtfStylesheet) {
        super(corpusName);
        PARTITUR_FORMAT_STYLESHEET = pfStylesheet;
        IT_TRANSFORM_STYLESHEET = itStylesheet;
        RTF_STYLESHEET = rtfStylesheet;
        suffix = s;
        sf = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory(true);
        /*try {
            transformer = new XSLTransformer(IT_TRANSFORM_STYLESHEET);            
        } catch (XSLTransformException ex) {
            ex.printStackTrace();
        }*/
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            WritePartiturs wp = new WritePartiturs(args[0], args[2], args[3], args[4], args[5]);
            if (args.length>6){
                wp.toTopLevel = args[6];
            }           
            if (args.length>7){
                wp.HTML_WIDTH = Double.parseDouble(args[7]);
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

    public void processTranscription(BasicTranscription bt) {
        try {

            // **************************************************
            // *******************  STEP 1   ********************
            // **************************************************
            // Generate an Interlinear Text Document
            // den StylesheetProzessor 
            // ein externes Stylesheet 
            // auf die XML-String-Repraesentation der Transkription (table.getModel().getTranscription().toXML())
            // anwenden lassen. Das Ergebnis (formatTableString)
            // ist eine XML-String-Repraesentation einer Formatierungstabelle
            String formatTableString = sf.applyExternalStylesheetToString(PARTITUR_FORMAT_STYLESHEET, bt.toXML());
            // eine neue Formatierungstabelle initialisieren
            TierFormatTable tft = new TierFormatTable();
            // die Formatierungstabelle aus einer XML-String-Repraesentation einlesen
            tft.TierFormatTableFromString(formatTableString);
            // die Formatierungstabelle des Partitur-Editors auf die neue Formatierungstabelle setzen
            // (dadurch wird automatisch die Partitur neu formatiert
            
            org.exmaralda.partitureditor.interlinearText.InterlinearText it = ItConverter.BasicTranscriptionToInterlinearText(bt, tft);
            outappend(getCurrentFilename() +  " converted to interlinear text.\n");        
            
            HTMLParameters param = new HTMLParameters();
            
            param.setWidth(HTML_WIDTH);
            param.stretchFactor = 1.5;
            param.smoothRightBoundary = true;
            param.includeSyncPoints = true;
            param.putSyncPointsOutside = true;
            param.outputAnchors = true;
            param.frame = "lrtb";
            param.frameStyle = "Solid";
            param.setFrameColor(new java.awt.Color(153,153,153));
            
            it.trim(param);
            
            // **************************************************
            // *******************  STEP 2   ********************
            // **************************************************
            // Write the Interlinear Text Document
            String xmlFilename =    getCurrentDirectoryname() 
                                    + System.getProperty("file.separator")
                                    + "presentation"
                                    + System.getProperty("file.separator")
                                    + getNakedFilenameWithoutSuffix()
                                    + suffix
                                    + ".xml";

                           
            it.writeXMLToFile(xmlFilename);
            outappend(xmlFilename +  " written.\n");        
            
            
            // **************************************************
            // *******************  STEP 3   ********************
            // **************************************************
            final Document itDocument = FileIO.readDocumentFromLocalFile(xmlFilename);
            Document btDocument = FileIO.readDocumentFromLocalFile(currentFilename);
            
            // remove "line" elements (die stoeren nur)
            Iterator i = itDocument.getRootElement().getDescendants(new ElementFilter("line"));
            Vector toBeRemoved = new Vector();
            while (i.hasNext()){
                toBeRemoved.addElement(i.next());
            }
            for (int pos=0; pos<toBeRemoved.size(); pos++){
                Element e = (Element)(toBeRemoved.elementAt(pos));
                e.detach();
            }
            
            XPath xpath1 = XPath.newInstance("//common-timeline");
            Element timeline = (Element)(xpath1.selectSingleNode(btDocument));
            timeline.detach();
            
            XPath xpath2 = XPath.newInstance("//head");
            Element head = (Element)(xpath2.selectSingleNode(btDocument));
            head.detach();
            
            XPath xpath3 = XPath.newInstance("//tier");
            List tiers = xpath3.selectNodes(btDocument);
            Element tiersElement = new Element("tiers");
            for (int pos=0; pos<tiers.size(); pos++){
                Element t = (Element)(tiers.get(pos));
                t.detach();
                t.removeContent();
                tiersElement.addContent(t);
            }

            /*int i1 = xmlFilename.lastIndexOf("\\");
            int i2 = xmlFilename.lastIndexOf("_");
            String filename = xmlFilename.substring(i1+1,i2);*/
            String nameOnly = new File(xmlFilename).getName();
            String filename = nameOnly.substring(0, nameOnly.lastIndexOf("_"));

            Element nameElement = new Element("name");
            nameElement.setAttribute("name", filename);

            Element tableWidthElement = new Element("table-width");
            tableWidthElement.setAttribute("table-width", Long.toString(Math.round(param.getWidth())));
            
            Element btElement = new Element("basic-transcription");
            btElement.addContent(nameElement);
            btElement.addContent(tableWidthElement);
            btElement.addContent(head);
            btElement.addContent(timeline);
            btElement.addContent(tiersElement);
                       
            itDocument.getRootElement().addContent(btElement);
            
            FileIO.writeDocumentToLocalFile(xmlFilename,itDocument);
            //FileIO.writeDocumentToLocalFile("S:\\TP-Z2\\DATEN\\HTML5\\Rudi\\presentation\\inter.xml",itDocument);
            
            final String htmlFilename =    getCurrentDirectoryname()
                                    + System.getProperty("file.separator")
                                    + "presentation"
                                    + System.getProperty("file.separator")
                                    + getNakedFilenameWithoutSuffix()
                                    + suffix
                                    + ".html";

            final String prev = toTopLevel + previousURL + suffix + ".html";
            final String next = toTopLevel + nextURL + suffix + ".html";
            final String f_xmlFilename = xmlFilename;
            
            Runnable theRunnable = new Runnable(){

                public void run() {
                    try {
                        System.out.println("Transforming...");
                        //Document htmlPartiturDoc = transformer.transform(itDocument);
                        String transDoc = sf.applyExternalStylesheetToExternalXMLFile(IT_TRANSFORM_STYLESHEET, f_xmlFilename);
                        Document htmlPartiturDoc = IOUtilities.readDocumentFromString(transDoc);
                        AbstractCorpusProcessor.insertPreviousAndNext(htmlPartiturDoc, prev, next);
                        FileIO.writeDocumentToLocalFile(htmlFilename, htmlPartiturDoc, true);
                        outappend(htmlFilename + " written.\n");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            };

            new Thread(theRunnable).start();

            
            
            // **************************************************
            // *******************  STEP 4   ********************
            // **************************************************
            RTFParameters rtfParam = new RTFParameters();
            
            rtfParam.smoothRightBoundary = true;
            rtfParam.includeSyncPoints = false;
            rtfParam.putSyncPointsOutside = true;
            rtfParam.saveSpace = true;
            rtfParam.useClFitText = true;
            rtfParam.makePageBreaks = false;
            rtfParam.frame = "lrtb";
            rtfParam.frameStyle = "Solid";
            
            String formatTableString2 = sf.applyExternalStylesheetToString(RTF_STYLESHEET, bt.toXML());
            TierFormatTable tft2 = new TierFormatTable();
            tft2.TierFormatTableFromString(formatTableString2);
            it = ItConverter.BasicTranscriptionToInterlinearText(bt, tft2);
            it.trim(rtfParam);

            int index = currentFilename.lastIndexOf(".");           
            String rtfFilename =    getCurrentDirectoryname() 
                                    + System.getProperty("file.separator")
                                    + "presentation"
                                    + System.getProperty("file.separator")
                                    + getNakedFilenameWithoutSuffix()
                                    + suffix
                                    + ".rtf";                        
            it.writeTestRTF(rtfFilename, rtfParam);
            outappend(rtfFilename +  " written.\n");   
            
            // **************************************************
            // *******************  STEP 5   ********************
            // **************************************************
            //it.writeXMLToFile(xmlFilename);
            //outappend(xmlFilename +  " written again.\n");
            
            
            
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
        
    }
    
}
