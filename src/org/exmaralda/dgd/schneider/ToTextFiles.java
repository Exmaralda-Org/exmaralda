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
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class ToTextFiles extends AbstractSchneiderProcessor {
  
    String xslPath;
    
    public ToTextFiles(String[] args){
        inputDirectory = new File(args[0]);
        inputSuffix = args[1];
        outputDirectory = new File(args[2]);
        outputDirectory.mkdir();
        for (File f : outputDirectory.listFiles()){
            f.delete();
        }
        outputSuffix = args[3];
        xslPath = args[4];
        
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
            ToTextFiles xtf = new ToTextFiles(args);
            xtf.processFiles();
        } catch (Exception ex) {
            Logger.getLogger(ToTextFiles.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    private void processFiles() throws UnsupportedEncodingException, FileNotFoundException, IOException, JDOMException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        StylesheetFactory sf = new StylesheetFactory(true);
        for (File inputFile : inputFiles){
            System.out.println("Reading " + inputFile.getName());            
            String result = sf.applyExternalStylesheetToExternalXMLFile(xslPath, inputFile.getAbsolutePath());
            System.out.println("started writing " + makeOutputPath(inputFile));
            FileOutputStream fos = new FileOutputStream(makeOutputPath(inputFile));
            fos.write(result.toString().getBytes());
            fos.close();
            System.out.println("document written.");        
        }
    }

}
