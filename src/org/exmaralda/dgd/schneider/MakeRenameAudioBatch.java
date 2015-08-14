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
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class MakeRenameAudioBatch extends AbstractSchneiderProcessor {
    
    
    

    
    public MakeRenameAudioBatch(String[] args) throws JDOMException, IOException{
        inputDirectory = new File(args[0]);
        inputSuffix = args[1];
                
        getMappings(args[2]);
        
        logFile = new File(args[3]);
        
        
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
            MakeRenameAudioBatch aa = new MakeRenameAudioBatch(args);
            aa.processFiles();
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(MakeRenameAudioBatch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processFiles() throws UnsupportedEncodingException, FileNotFoundException, IOException, JDOMException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        StringBuffer log = new StringBuffer();
        for (File inputFile : inputFiles){
            System.out.println("Reading " + inputFile.getName());                        
            String oldName = inputFile.getAbsolutePath();
            String newName = determineAudio(old2new, inputFile);
            newName = newName.substring(newName.lastIndexOf("/")+1);
            System.out.println(oldName + " "  + newName);
            log.append("rename " + oldName + " " + newName + System.getProperty("line.separator"));                    
        }
        writeLogToTextFile(log);        
    }

    @Override
    protected String makeOutputPath(File inputFile) {
        String name = super.determineTranscriptName(old2new, inputFile);
        File f = new File(outputDirectory, name + "." + outputSuffix);
        return f.getAbsolutePath();        
    }
    
    
    
}
