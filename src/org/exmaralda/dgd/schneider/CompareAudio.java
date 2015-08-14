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
import java.util.List;
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
public class CompareAudio extends AbstractSchneiderProcessor {
    
    BASAudioPlayer player = new BASAudioPlayer();
    Document compareDocument;
    Document logDocument;
    Element logRoot;
    
    public CompareAudio(String[] args) throws JDOMException, IOException{
        inputDirectory = new File(args[0]);
        inputSuffix = args[1];
        getMappings(args[2]);
        
        compareDocument = FileIO.readDocumentFromLocalFile(args[3]);
        
        logFile = new File(args[4]);
        
        logRoot = new Element("compare-audio");
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
            CompareAudio aa = new CompareAudio(args);
            aa.processFiles();
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(CompareAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processFiles() throws UnsupportedEncodingException, FileNotFoundException, IOException, JDOMException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        StylesheetFactory sf = new StylesheetFactory(true);
        for (File inputFile : inputFiles){
            System.out.println("Reading " + inputFile.getName());                        
            Document xmlDocument = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());
            
            Element recordingElement = (Element) XPath.newInstance("//recording").selectSingleNode(xmlDocument);
            String absoluteAudioPath = "";

            String path = determineAudio(old2new, inputFile);
            recordingElement.setAttribute("path", path);
            absoluteAudioPath = RelativePath.resolveRelativePath(path, inputFile.getAbsolutePath());            
            double lengthInSeconds = -1.0;
            if (new File(absoluteAudioPath).exists()){
                player.setSoundFile(absoluteAudioPath);
                lengthInSeconds = player.getTotalLength();
            }
            
            
            String nakedName = inputFile.getName().substring(0, inputFile.getName().indexOf("TRA."));
            XPath xp = XPath.newInstance("//audio-file[starts-with(@name,'" + nakedName + "')]");
            List l = xp.selectNodes(compareDocument);
            if (l!=null){
                Element compareElement = new Element("compare");
                compareElement.setAttribute("file", inputFile.getName());
                Element audioElement1 = new Element("audio");
                audioElement1.setAttribute("file", absoluteAudioPath);
                audioElement1.setAttribute("length", Double.toString(lengthInSeconds));
                compareElement.addContent(audioElement1);
                for (Object o : l){
                    Element e = (Element)o;
                    Element audioElement2 = new Element("audio");
                    // <audio-file name="HLD02BW2.WAV" path="W:\TOENE\HL\HLD\HLD02BW2.WAV" seconds="129.62449645996094"length="02:09.62" bytes="11432924"/> 
                    audioElement2.setAttribute("file", e.getAttributeValue("path"));
                    audioElement2.setAttribute("length", e.getAttributeValue("seconds"));
                    compareElement.addContent(audioElement2);
                }
                logRoot.addContent(compareElement);
            }
            
            
        }
        
        writeLogToXMLFile(logDocument);
        
        
    }
}
