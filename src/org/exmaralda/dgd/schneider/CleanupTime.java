/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class CleanupTime extends AbstractSchneiderProcessor {
    
    

    
    public CleanupTime(String[] args){
        inputDirectory = new File(args[0]);
        inputSuffix = args[1];
        outputDirectory = new File(args[2]);
        outputDirectory.mkdir();
        for (File f : outputDirectory.listFiles()){
            f.delete();
        }
        outputSuffix = args[3];
        
        logFile = new File(args[4]);
        
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
            CleanupTime aa = new CleanupTime(args);
            aa.processFiles();
        } catch (Exception ex) {
            Logger.getLogger(CleanupTime.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    private void processFiles() throws UnsupportedEncodingException, FileNotFoundException, IOException, JDOMException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        StringBuffer log = new StringBuffer();
        for (File inputFile : inputFiles){
            System.out.println("Reading " + inputFile.getName());                        
            Document xmlDocument = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());
            
            Hashtable<String,Double> ids2Time = new Hashtable<String,Double>();
            // <timepoint timepoint-id="TLI_160" absolute-time="0.16"/>
            for (Object o : XPath.newInstance("//timepoint").selectNodes(xmlDocument)){
                Element tp = (Element)o;
                ids2Time.put(tp.getAttributeValue("timepoint-id"), new Double(tp.getAttributeValue("absolute-time")));
            }
            
            Vector<Element> toBeRemoved = new Vector<Element>();
            for (Object c : XPath.newInstance("//contribution").selectNodes(xmlDocument)){
                // <contribution parse-level="0" start-reference="TLI_178890" end-reference="TLI_200740"
                Element contribution = (Element)c;
                double cStart = ids2Time.get(contribution.getAttributeValue("start-reference")).doubleValue();
                double cEnd = ids2Time.get(contribution.getAttributeValue("end-reference")).doubleValue();
                double cNow = cStart;
                for (Object t : contribution.getChild("unparsed").getChildren("time")){
                    // <time timepoint-reference="TLI_179230"/>
                    Element time = (Element)t;
                    double thisTime = ids2Time.get(time.getAttributeValue("timepoint-reference"));
                    if ((thisTime<=cNow) || (thisTime>cEnd)){
                        toBeRemoved.addElement(time);
                        log.append(inputFile.getName() + "\t" + time.getAttributeValue("timepoint-reference") + "\n");                    
                    } else if (thisTime==cStart) {
                        toBeRemoved.addElement(time);                        
                    }else {
                        cNow = thisTime;
                    }
                }
                for (Element time : toBeRemoved){
                    time.detach();
                }
            }
            
            FileIO.writeDocumentToLocalFile(makeOutputPath(inputFile), xmlDocument);            
            
            
        }
        
        writeLogToTextFile(log);
        
    }


}
