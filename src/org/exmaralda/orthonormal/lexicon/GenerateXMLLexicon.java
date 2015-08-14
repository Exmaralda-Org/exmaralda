/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.lexicon;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;

/**
 *
 * @author thomas
 */
public class GenerateXMLLexicon {

    public static String PATH = "C:\\Users\\Schmidt\\Desktop\\DGD-RELEASE\\transcripts\\FOLK";
    public static FilenameFilter FLN_FILTER = new FilenameFilter(){
        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".fln");
        }                
   };
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new GenerateXMLLexicon().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(GenerateXMLLexicon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LexiconException ex) {
            Logger.getLogger(GenerateXMLLexicon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GenerateXMLLexicon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateXMLLexicon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException, SQLException, JDOMException, LexiconException {
        //SimpleXMLFileLexicon lexicon = new SimpleXMLFileLexicon();
        XMLLexicon lexicon = new XMLLexicon();
        File[] flns = new File(PATH).listFiles(FLN_FILTER);
        lexicon.update(flns);
        File f = new File("C:\\Users\\Schmidt\\Dropbox\\IDS\\FOLK\\Normalisierung\\FOLK_Normalization_Lexicon_FEB_2015.xml");
        lexicon.write(f);
        lexicon.read(f);
        
        for (String c : lexicon.getCandidateForms("ma")){
            System.out.println(c);
        }
        System.out.println("====");
        
    }
}
