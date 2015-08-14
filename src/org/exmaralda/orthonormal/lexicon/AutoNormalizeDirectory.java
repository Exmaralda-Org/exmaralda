/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.lexicon;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class AutoNormalizeDirectory implements SearchListenerInterface {

    String INPUT_DIRECTORY = /*CompareDirectories.MANUAL_DIRECTORY; */ "Y:\\thomas\\BW2FLK\\4";
    String OUTPUT_DIRECTORY = /*CompareDirectories.AUTO_DIRECTORY; */ "Y:\\thomas\\BW2FLK\\4a";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            try {
                try {
                    AutoNormalizeDirectory and = new AutoNormalizeDirectory();
                    if (args.length==2){
                        and.INPUT_DIRECTORY = args[0];
                        and.OUTPUT_DIRECTORY = args[1];
                    }
                    and.doit();
                } catch (LexiconException ex) {
                    Logger.getLogger(AutoNormalizeDirectory.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (JDOMException ex) {
                Logger.getLogger(AutoNormalizeDirectory.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(AutoNormalizeDirectory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException, JDOMException, LexiconException {
        String RDB_URL = "jdbc:oracle:thin:@10.0.1.35:1521:orc11";
        String RDB_USERNAME = "orthonormal";
        String RDB_PASSWORD = "---";
        String[] CONNECTION_PARAMETERS = {RDB_URL, RDB_USERNAME, RDB_PASSWORD};
        RDBLexicon lexicon = new RDBLexicon();
        lexicon.read(CONNECTION_PARAMETERS);
        
        new File(OUTPUT_DIRECTORY).mkdir();
        
        AutoNormalizer autoNormalizer = new AutoNormalizer(lexicon);
        autoNormalizer.addSearchListener(this);
        autoNormalizer.MIN_AUTO_FREQUENCY = 3;
        //File[] flns = new File(INPUT_DIRECTORY).listFiles(ResetDatabaseLexicon.FLN_FILTER);
        File[] flns = new File(INPUT_DIRECTORY).listFiles();
        for (File f : flns){
           System.out.println("Reading " + f.getAbsolutePath());
           Document doc = FileIO.readDocumentFromLocalFile(f);
           List allWords = XPath.newInstance("//w[not(@id)]").selectNodes(doc);
           int count=1;
           for (Object o : allWords){
               ((Element)o).setAttribute("id", "w" + Integer.toString(count));
               count++;        
           }
           
           List normalizedWords = XPath.newInstance("//w[@n]").selectNodes(doc);
           for (Object o : normalizedWords){
               ((Element)o).removeAttribute("n");
           }
           autoNormalizer.normalize(doc);
           File outFile = new File(new File(OUTPUT_DIRECTORY), f.getName().replaceAll("\\.flk", ".fln"));
           FileIO.writeDocumentToLocalFile(outFile, doc);
        }
        
        
        
    }

    @Override
    public void processSearchEvent(SearchEvent se) {
        System.out.println("[" + se.getProgress() + "] " + se.getData());
    }
}
