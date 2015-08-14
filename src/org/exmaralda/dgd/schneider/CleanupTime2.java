/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.GATParser;
import org.exmaralda.folker.data.PFParser;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class CleanupTime2 extends AbstractSchneiderProcessor {

    
    Document logDocument;
    Element logRoot;
    
    

    
    public CleanupTime2(String[] args){
        inputDirectory = new File(args[0]);
        inputSuffix = args[1];
        outputDirectory = new File(args[2]);
        outputDirectory.mkdir();
        for (File f : outputDirectory.listFiles()){
            f.delete();
        }
        outputSuffix = args[3];
        
        logFile = new File(args[4]);
        
        logRoot = new Element("cleanup-time-2-log");
        logDocument = new Document(logRoot);
        
        inputFiles = inputDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(inputSuffix.toUpperCase());
            }            
        });
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            CleanupTime2 aa = new CleanupTime2(args);
            aa.processFiles();
        } catch (Exception ex) {
            Logger.getLogger(CleanupTime2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processFiles() throws UnsupportedEncodingException, FileNotFoundException, IOException, JDOMException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException {
        StringBuffer log = new StringBuffer();
        for (File inputFile : inputFiles){
            Element thisLogElement = new Element("folker-transcription");
            thisLogElement.setAttribute("file", inputFile.getName());
            logRoot.addContent(thisLogElement);
            
            System.out.println("Reading " + inputFile.getName());                        
            EventListTranscription folkerTranscript = EventListTranscriptionXMLReaderWriter.readXML(inputFile, 0);

            //File intermediate1 = new File(makeOutputPath(inputFile).replaceAll("\\.flk", "_1.flk"));
            //EventListTranscriptionXMLReaderWriter.writeXML(folkerTranscript, intermediate1, new GATParser(), 0);
            
            folkerTranscript.updateContributions();


            //File intermediate2 = new File(makeOutputPath(inputFile).replaceAll("\\.flk", "_2.flk"));
            //EventListTranscriptionXMLReaderWriter.writeXML(folkerTranscript, intermediate2, new GATParser(), 0);
                        
            Document xmlDocument = EventListTranscriptionXMLReaderWriter.toJDOMDocument(folkerTranscript, inputFile);
            
            Hashtable<String,Double> ids2Time = new Hashtable<String,Double>();
            // <timepoint timepoint-id="TLI_160" absolute-time="0.16"/>
            for (Object o : XPath.newInstance("//timepoint").selectNodes(xmlDocument)){
                Element tp = (Element)o;
                ids2Time.put(tp.getAttributeValue("timepoint-id"), new Double(tp.getAttributeValue("absolute-time")));
            }
            
            Vector<Element> toBeProcessed = getProblemContributions(xmlDocument);    
            System.out.println("#1: " + toBeProcessed.size() + " problematic contributions (out of " + xmlDocument.getRootElement().getChildren("contribution").size() + ")");
            thisLogElement.setAttribute("step1", Integer.toString(toBeProcessed.size()));            
            swapThem(toBeProcessed);
            File tempFile = File.createTempFile("flk", "tmp");
            tempFile.deleteOnExit();
            FileIO.writeDocumentToLocalFile(tempFile.getAbsolutePath(), xmlDocument);
            xmlDocument = EventListTranscriptionXMLReaderWriter.toJDOMDocument(EventListTranscriptionXMLReaderWriter.readXML(tempFile,0), tempFile);
            
            toBeProcessed = getProblemContributions(xmlDocument);    
            System.out.println("#2: " + toBeProcessed.size() + " problematic contributions (out of " + xmlDocument.getRootElement().getChildren("contribution").size() + ")");
            thisLogElement.setAttribute("step2", Integer.toString(toBeProcessed.size()));            
            swapThem(toBeProcessed);
            tempFile = File.createTempFile("flk", "tmp");
            tempFile.deleteOnExit();
            FileIO.writeDocumentToLocalFile(tempFile.getAbsolutePath(), xmlDocument);
            xmlDocument = EventListTranscriptionXMLReaderWriter.toJDOMDocument(EventListTranscriptionXMLReaderWriter.readXML(tempFile,0), tempFile);

            toBeProcessed = getProblemContributions(xmlDocument);    
            System.out.println("#3: " + toBeProcessed.size() + " problematic contributions (out of " + xmlDocument.getRootElement().getChildren("contribution").size() + ")");
            thisLogElement.setAttribute("step3", Integer.toString(toBeProcessed.size()));            
            for (Element problemContribution : toBeProcessed){
                /* Problemstruktur:
                 * <segment start-reference="TLI_2155" end-reference="TLI_2157">((1) Ja, bitte, gern geschehen. </segment>
                 * <segment start-reference="TLI_2156" end-reference="TLI_2157">((2)Lachen(2)) </segment>
                 * Lösung: 
                 * <segment start-reference="TLI_2155" end-reference="TLI_2156">((1) Ja, bitte, gern geschehen. </segment>
                 * <segment start-reference="TLI_2156" end-reference="TLI_2157">((2)Lachen(2)) </segment>
                 */
                Element previousSegment = null;
                Vector<Element> toBeRemoved = new Vector<Element>();
                for (Object o : problemContribution.getChildren("segment")){
                    Element segment = (Element)o;
                    if ((previousSegment!=null) && (previousSegment.getAttributeValue("end-reference").equals(segment.getAttributeValue("end-reference")))){
                        int startRef1 = Integer.parseInt(previousSegment.getAttributeValue("start-reference").substring(4));
                        int startRef2 = Integer.parseInt(segment.getAttributeValue("start-reference").substring(4));
                        if (startRef1+1==startRef2){
                            previousSegment.setAttribute("end-reference", segment.getAttributeValue("start-reference"));
                        }
                    }
                    previousSegment = segment;
                }                
            }
            
            toBeProcessed = getProblemContributions(xmlDocument);     
            System.out.println("#4: " + toBeProcessed.size() + " problematic contributions (out of " + xmlDocument.getRootElement().getChildren("contribution").size() + ")");
            thisLogElement.setAttribute("step4", Integer.toString(toBeProcessed.size()));            
            
            for (Element problemContribution : toBeProcessed){
                /* Problemstruktur:
                 * <segment start-reference="TLI_828" end-reference="TLI_829">Geflügel. </segment>
                 * <segment start-reference="TLI_828" end-reference="TLI_829">((2)Pause(2)) </segment>
                 * Lösung: 
                 * <segment start-reference="TLI_828" end-reference="TLI_829">Geflügel. ((2)Pause(2)) </segment>
                 */
                Element previousSegment = null;
                Vector<Element> toBeRemoved = new Vector<Element>();
                for (Object o : problemContribution.getChildren("segment")){
                    Element segment = (Element)o;
                    String start = ((Element)segment).getAttributeValue("start-reference");
                    if ((previousSegment!=null) && (previousSegment.getAttributeValue("start-reference").equals(segment.getAttributeValue("start-reference")))){
                        int endRef1 = Integer.parseInt(previousSegment.getAttributeValue("end-reference").substring(4));
                        int endRef2 = Integer.parseInt(segment.getAttributeValue("end-reference").substring(4));
                        if (endRef1==endRef2){
                            previousSegment.setText(previousSegment.getText() + segment.getText());
                            toBeRemoved.addElement(segment);
                        }
                    }
                    previousSegment = segment;
                }                
                System.out.println("About to remove " + toBeRemoved.size() + " segments");
                for (Element segment : toBeRemoved){
                    segment.detach();
                }
            }
            
            toBeProcessed = getProblemContributions(xmlDocument);     
            System.out.println("#5: " + toBeProcessed.size() + " problematic contributions (out of " + xmlDocument.getRootElement().getChildren("contribution").size() + ")");
            thisLogElement.setAttribute("step5", Integer.toString(toBeProcessed.size()));            
            
            // remove superfluous empty unparsed elements (no idea where they come from... 
            Vector<Element> superflu = new Vector<Element>();
            for(Object c : XPath.newInstance("//unparsed[not(*) and string-length(normalize-space())=0]").selectNodes(xmlDocument)){
                superflu.addElement((Element)(c));
            }
            for (Element e : superflu){
                e.detach();
            }

            
            FileIO.writeDocumentToLocalFile(makeOutputPath(inputFile), xmlDocument);            

            for (Element e : toBeProcessed){
                e.detach();
                thisLogElement.addContent(e);
            }

            
            
        }
        
        writeLogToXMLFile(logDocument);
    }


    private Vector<Element> getProblemContributions(Document xmlDocument) throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException {
        new PFParser().parseDocument(xmlDocument, 1);
        Vector<Element> toBeProcessed = new Vector<Element>();
        for (Object c : XPath.newInstance("//contribution").selectNodes(xmlDocument)){
            // <contribution parse-level="0" start-reference="TLI_178890" end-reference="TLI_200740"
            Element contribution = (Element)c;
            boolean isOK = "1".equals(contribution.getAttributeValue("parse-level"));
            if (isOK) {
                continue;
            }                
            toBeProcessed.addElement(contribution);                
        }
        return toBeProcessed;
    }

    private void swapThem(Vector<Element> toBeProcessed) {
        for (Element problemContribution : toBeProcessed){
            /* Problemstruktur:
             * <segment start-reference="TLI_261" end-reference="TLI_263">also </segment>
             * <segment start-reference="TLI_261" end-reference="TLI_262">s </segment>
             * Lösung: 
             * <segment start-reference="TLI_261" end-reference="TLI_262">s </segment>
             * <segment start-reference="TLI_262" end-reference="TLI_263">also </segment>
             */
            Element previousSegment = null;
            Vector<Element> toBeSwapped = new Vector<Element>();
            for (Object o : problemContribution.getChildren("segment")){
                Element segment = (Element)o;
                String start = ((Element)segment).getAttributeValue("start-reference");
                if ((previousSegment!=null) && (previousSegment.getAttributeValue("start-reference").equals(segment.getAttributeValue("start-reference")))){
                    int endRef1 = Integer.parseInt(previousSegment.getAttributeValue("end-reference").substring(4));
                    int endRef2 = Integer.parseInt(segment.getAttributeValue("end-reference").substring(4));
                    if (endRef1==endRef2+1){
                        previousSegment.setAttribute("start-reference", segment.getAttributeValue("end-reference"));
                        toBeSwapped.addElement(segment);
                    }
                }
                previousSegment = segment;
            }                
            System.out.println("About to swap " + toBeSwapped.size() + " segments");
            for (Element segment : toBeSwapped){
                Element c = segment.getParentElement();
                int index = c.indexOf(segment);
                segment.detach();
                c.setContent(index-1, segment);
            }
        }
    }
    
}
