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
public class AssignAudioAndEndTime extends AbstractSchneiderProcessor {
    
    BASAudioPlayer player = new BASAudioPlayer();
    
    

    
    public AssignAudioAndEndTime(String[] args) throws JDOMException, IOException{
        inputDirectory = new File(args[0]);
        inputSuffix = args[1];
        outputDirectory = new File(args[2]);
        outputDirectory.mkdir();
        for (File f : outputDirectory.listFiles()){
            f.delete();
        }
        outputSuffix = args[3];
        
        if (args.length>4){
            getMappings(args[4]);
        }
        
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
            AssignAudioAndEndTime aa = new AssignAudioAndEndTime(args);
            aa.processFiles();
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(AssignAudioAndEndTime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processFiles() throws UnsupportedEncodingException, FileNotFoundException, IOException, JDOMException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        StylesheetFactory sf = new StylesheetFactory(true);
        for (File inputFile : inputFiles){
            System.out.println("Reading " + inputFile.getName());                        
            Document xmlDocument = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());
            
            Element firstContribution = xmlDocument.getRootElement().getChild("contribution");
            if (firstContribution.getAttributeValue("start-reference").equals("TLI_")){
                Element timeline = (Element)(XPath.newInstance("//timeline").selectSingleNode(xmlDocument));
                Element startTimepoint = new Element("timepoint");
                startTimepoint.setAttribute("timepoint-id", "TLI_START");
                startTimepoint.setAttribute("absolute-time", "0.0");
                timeline.addContent(0, startTimepoint);
                firstContribution.setAttribute("start-reference", "TLI_START");
            }
            
            Element recordingElement = (Element) XPath.newInstance("//recording").selectSingleNode(xmlDocument);
            Element lastTimePoint = (Element) XPath.newInstance("//timepoint[@timepoint-id='TLI_END']").selectSingleNode(xmlDocument);

            String path = determineAudio(old2new, inputFile);
            recordingElement.setAttribute("path", path);
            String absoluteAudioPath = RelativePath.resolveRelativePath(path, inputFile.getAbsolutePath());            
            if (new File(absoluteAudioPath).exists()){
                player.setSoundFile(absoluteAudioPath);
                double lengthInSeconds = player.getTotalLength();
                lastTimePoint.setAttribute("absolute-time", Double.toString(lengthInSeconds));
            }

            FileIO.writeDocumentToLocalFile(makeOutputPath(inputFile), xmlDocument);
            
            
            
        }
    }
}
