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
import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.exmaralda.orthonormal.lexicon.AutoNormalizer;
import org.exmaralda.orthonormal.lexicon.LexiconException;
import org.exmaralda.orthonormal.lexicon.RDBLexicon;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class AutoNormalize extends AbstractSchneiderProcessor implements SearchListenerInterface {

    AutoNormalizer autoNormalizer;
    
    public AutoNormalize(String[] args) throws IOException{
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
        
        String RDB_URL = "jdbc:oracle:thin:@10.0.1.35:1521:orc11";
        String RDB_USERNAME = "orthonormal";
        String RDB_PASSWORD = "---";
        String[] CONNECTION_PARAMETERS = {RDB_URL, RDB_USERNAME, RDB_PASSWORD};
        RDBLexicon lexicon = new RDBLexicon();
        lexicon.read(CONNECTION_PARAMETERS);
        
        autoNormalizer = new AutoNormalizer(lexicon);
        autoNormalizer.addSearchListener(this);
        autoNormalizer.MIN_AUTO_FREQUENCY = 50;
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            AutoNormalize aa = new AutoNormalize(args);
            aa.processFiles();
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(AutoNormalize.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    private void processFiles() throws UnsupportedEncodingException, FileNotFoundException, IOException, JDOMException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, LexiconException {
        for (File inputFile : inputFiles){
            System.out.println("Reading " + inputFile.getName());     
            Document document = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());            
            autoNormalizer.normalize(document);
            FileIO.writeDocumentToLocalFile(makeOutputPath(inputFile), document);            
        }
    }
    
    @Override
    public void processSearchEvent(SearchEvent se) {
        System.out.println("[" + se.getProgress() + "] " + se.getData());
    }
    

}
