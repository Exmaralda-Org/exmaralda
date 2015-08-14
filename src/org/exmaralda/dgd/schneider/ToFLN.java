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
import org.exmaralda.orthonormal.data.NormalizedFolkerTranscription;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class ToFLN extends AbstractSchneiderProcessor {
    
    
    

    
    public ToFLN(String[] args) throws JDOMException, IOException{
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
            ToFLN aa = new ToFLN(args);
            aa.processFiles();
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(ToFLN.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processFiles() throws UnsupportedEncodingException, FileNotFoundException, IOException, JDOMException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        for (File inputFile : inputFiles){
            System.out.println("Reading " + inputFile.getName());                        
            NormalizedFolkerTranscription nft = org.exmaralda.orthonormal.io.XMLReaderWriter.readFolkerTranscription(inputFile);            
            FileIO.writeDocumentToLocalFile(makeOutputPath(inputFile), nft.getDocument());
        }
    }

    
    
    
}
