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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.folker.data.AbstractParser;
import org.exmaralda.folker.data.DSParser;
import org.exmaralda.folker.data.FRParser;
import org.exmaralda.folker.data.ISParser;
import org.exmaralda.folker.data.PFParser;
import org.exmaralda.folker.data.ZWParser;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class Parse extends AbstractSchneiderProcessor {

    
    public Parse(String[] args){
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
            Parse aa = new Parse(args);
            aa.processFiles();
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(Parse.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    private void processFiles() throws UnsupportedEncodingException, FileNotFoundException, IOException, JDOMException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        AbstractParser parser = new PFParser();
        if (inputFiles[0].getName().startsWith("OS")){
            parser = new ZWParser();
        }
        if (inputFiles[0].getName().startsWith("ZW")){
            parser = new ZWParser();
        }
        if (inputFiles[0].getName().startsWith("IS")){
            parser = new ISParser();
        }
        if (inputFiles[0].getName().startsWith("HE")){
            parser = new ZWParser();
        }
        if (inputFiles[0].getName().startsWith("KO")){
            parser = new ZWParser();
        }
        if (inputFiles[0].getName().startsWith("HL")){
            parser = new ZWParser();
        }
        if (inputFiles[0].getName().startsWith("DS")){
            parser = new DSParser();
        }
        if (inputFiles[0].getName().startsWith("FR")){
            parser = new FRParser();
        }
        for (File inputFile : inputFiles){
            System.out.println("Reading " + inputFile.getName());                        
            Document unparsedDocument = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());
            parser.parseDocument(unparsedDocument, 2);            
            FileIO.writeDocumentToLocalFile(makeOutputPath(inputFile), unparsedDocument);
        }
    }

}
