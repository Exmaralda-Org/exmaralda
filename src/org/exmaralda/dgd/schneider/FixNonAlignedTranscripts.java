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
import java.util.List;
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
public class FixNonAlignedTranscripts extends AbstractSchneiderProcessor {


    
    public FixNonAlignedTranscripts(String[] args){
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
            FixNonAlignedTranscripts aa = new FixNonAlignedTranscripts(args);
            aa.processFiles();
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(FixNonAlignedTranscripts.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processFiles() throws UnsupportedEncodingException, FileNotFoundException, IOException, JDOMException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        StringBuffer log = new StringBuffer();
        for (File inputFile : inputFiles){
            System.out.println("Reading " + inputFile.getName());                        
            Document xmlDocument = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());
            
            boolean isTimeAligned = (XPath.newInstance("//timepoint").selectNodes(xmlDocument).size()>2);
            if (!isTimeAligned){
                log.append("Pseudo alignment for " + inputFile.getName() + System.getProperty("line.separator"));                    
                Element timeline = (Element)(XPath.newInstance("//timeline").selectSingleNode(xmlDocument));
                List contributions = XPath.newInstance("//contribution").selectNodes(xmlDocument);
                double totalTextLength = 0;
                for (Object o : contributions){
                    Element contribution = (Element)o;
                    totalTextLength+=contribution.getChildText("unparsed").length();
                }
                
                int count = 0;
                double textLengthUntilNow = 0;
                double totalLength = Double.parseDouble(((Element)(XPath.newInstance("//timepoint[position()=last()]").selectSingleNode(xmlDocument))).getAttributeValue("absolute-time"));
                
                System.out.println("Total length " + totalLength + " / totalTextlength " + totalTextLength);
                log.append("Total length " + totalLength + " / totalTextlength " + totalTextLength + System.getProperty("line.separator"));                    
                for (Object o : contributions){
                    Element contribution = (Element)o;
                    String timepointID = "TLI_" + Integer.toString(count);
                    Element timepoint = new Element("timepoint");
                    timepoint.setAttribute("timepoint-id", timepointID);
                    if (count==0){
                        timepoint.setAttribute("absolute-time", "0.0");                        
                    } else {
                        double proportionalTime = (textLengthUntilNow / totalTextLength) * totalLength;
                        timepoint.setAttribute("absolute-time", Double.toString(proportionalTime));                                                
                    }
                    textLengthUntilNow+=contribution.getChildText("unparsed").length();
                    timeline.addContent(timeline.getChildren().size()-1, timepoint);
                    contribution.setAttribute("start-reference", timepointID);
                    if (count<contributions.size()-1){
                        String endID = "TLI_" + Integer.toString(count+1);
                        contribution.setAttribute("end-reference", endID);
                    }
                    count++;
                }
                log.append("--------------------------------" + System.getProperty("line.separator"));                    
            }


            FileIO.writeDocumentToLocalFile(makeOutputPath(inputFile), xmlDocument);
        }
        
        super.writeLogToTextFile(log);
        
    }

}
