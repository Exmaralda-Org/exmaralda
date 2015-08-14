/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import org.exmaralda.dgd.schneider.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.common.helpers.RelativePath;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.sound.BASAudioPlayer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class FOLKCheckEndTime extends AbstractSchneiderProcessor {
    
    BASAudioPlayer player = new BASAudioPlayer();
    
    

    
    public FOLKCheckEndTime(String[] args) throws JDOMException, IOException{
        inputDirectory = new File(args[0]);
        inputSuffix = args[1];
        outputDirectory = new File(args[2]);
        outputDirectory.mkdir();
        for (File f : outputDirectory.listFiles()){
            f.delete();
        }
        outputSuffix = args[3];
        
        
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
            String[] myArgs = {
                "C:\\Users\\Schmidt\\Desktop\\FOLK_Werkstatt\\10", "fln",
                "C:\\Users\\Schmidt\\Desktop\\FOLK_Werkstatt\\11", "fln"
            };
            FOLKCheckEndTime aa = new FOLKCheckEndTime(myArgs);
            aa.processFiles();
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(FOLKCheckEndTime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processFiles() throws UnsupportedEncodingException, FileNotFoundException, IOException, JDOMException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        StylesheetFactory sf = new StylesheetFactory(true);
        int count=0;
        for (File inputFile : inputFiles){
            System.out.println("Reading " + inputFile.getName());                        
            Document xmlDocument = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());
                        
            Element recordingElement = (Element) XPath.newInstance("//recording").selectSingleNode(xmlDocument);
            Element lastTimePoint = (Element) XPath.newInstance("//timepoint[last()]").selectSingleNode(xmlDocument);

            String absoluteAudioPath = "Y:\\media\\audio\\FOLK\\" + recordingElement.getAttributeValue("dgd-id") + ".WAV";           
            if (new File(absoluteAudioPath).exists()){
                player.setSoundFile(absoluteAudioPath);
                double lengthInSeconds = player.getTotalLength();
                double latestTime = Double.parseDouble(lastTimePoint.getAttributeValue("absolute-time"));
                if (latestTime>lengthInSeconds + 1.0){
                    System.out.println("ORANGE ALERT!!!! " + latestTime + " > " + lengthInSeconds);
                    count++;
                    lastTimePoint.detach();
                }
            } else {
                System.out.println("RED ALERT!!!! " + absoluteAudioPath + " existeth not!!!!");
            }
            

            FileIO.writeDocumentToLocalFile(makeOutputPath(inputFile), xmlDocument);
            
            
            
        }
        
        System.out.println(count + " ORANGE ALERTS");
    }
}
