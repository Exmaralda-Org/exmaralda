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
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.PFParser;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class FindOrphanedText extends AbstractSchneiderProcessor {

    
    Document logDocument;
    Element logRoot;
    
    String orphan_xp = "//text()[parent::contribution and string-length(normalize-space())>0]";
    XPath xp;
    

    
    public FindOrphanedText(String[] args){
        try {
            xp = XPath.newInstance(orphan_xp);
        } catch (JDOMException ex) {
            Logger.getLogger(FindOrphanedText.class.getName()).log(Level.SEVERE, null, ex);
        }
        inputDirectory = new File(args[0]);
        inputSuffix = args[1];       
        logFile = new File(args[2]);
        
        logRoot = new Element("orphaned-text");
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
            FindOrphanedText aa = new FindOrphanedText(args);
            aa.processFiles();
        } catch (Exception ex) {
            Logger.getLogger(FindOrphanedText.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processFiles() throws UnsupportedEncodingException, FileNotFoundException, IOException, JDOMException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException {
        StringBuffer log = new StringBuffer();
        for (File inputFile : inputFiles){
            Element thisLogElement = new Element("folker-transcription");
            thisLogElement.setAttribute("file", inputFile.getName());
            logRoot.addContent(thisLogElement);
            
            System.out.println("Reading " + inputFile.getName());                        
            Document doc = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());
            
            List l = xp.selectNodes(doc);
            for (Object o : l){
                Text text = (Text)o;
                Element textE = new Element("text");
                textE.setText(text.getText());
                thisLogElement.addContent(textE);
            }
            
        }
        
        writeLogToXMLFile(logDocument);
    }


    
}
