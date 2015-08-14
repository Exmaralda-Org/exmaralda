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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class HTMLOutput extends AbstractSchneiderProcessor {

    
    public static String CONTRIBUTIONS2HTML_AUDIO_STYLESHEET = "/org/exmaralda/folker/data/unparsedFolker2HTMLContributionListAudio.xsl";
    

    
    public HTMLOutput(String[] args){
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
            HTMLOutput xtf = new HTMLOutput(args);
            xtf.processFiles();
        } catch (Exception ex) {
            Logger.getLogger(HTMLOutput.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    private void processFiles() throws UnsupportedEncodingException, FileNotFoundException, IOException, JDOMException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException {
        StylesheetFactory sf = new StylesheetFactory(true);
        for (File inputFile : inputFiles){
            System.out.println("Reading " + inputFile.getName());      
            EventListTranscription elt = EventListTranscriptionXMLReaderWriter.readXML(inputFile,0);
            Document flkDoc = EventListTranscriptionXMLReaderWriter.toJDOMDocument(elt, inputFile);
            
            
            /*Document flkDoc = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());
            Element recordingElement = (Element) XPath.newInstance("//recording").selectSingleNode(flkDoc);
            String relativePath = recordingElement.getAttributeValue("path");
            String absoluteAudioPath = RelativePath.resolveRelativePath(relativePath, inputFile.getAbsolutePath());
            recordingElement.setAttribute("path", new File(absoluteAudioPath).toURI().toString());*/
            
            String result = sf.applyInternalStylesheetToString(CONTRIBUTIONS2HTML_AUDIO_STYLESHEET, IOUtilities.documentToString(flkDoc));
            FileOutputStream fos = new FileOutputStream(new File(makeOutputPath(inputFile)));
            fos.write(result.getBytes("UTF-8"));
            fos.close();                    
            
        }
    }

}
