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

    //public static String PATH = "C:\\Users\\Schmidt\\Desktop\\DGD-RELEASE\\transcripts\\FOLK";
    //public static String PATH = "D:\\AGD-DATA\\dgd2_data\\transcripts\\FOLK";
    //public static String PATH = "C:\\Users\\bernd\\Dropbox\\work\\ZUMULT-COMA\\ESLO-DEMO\\ESLO_ENT_1001";
    public static String PATH = "C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2025-07-13_SWISS_GERMAN";
    //public static String PATH = "D:\\Dropbox\\IDS\\AGD\\MEND-Mennonitendeutsch-Goez\\Transkripte\\Normalisierungs_Stichprobe";
    //String OUT = "D:\\Dropbox\\IDS\\FOLK\\Normalisierung\\FOLK_Normalization_Lexicon_MAY_2020.xml";
    ////String OUT = "F:\\Dropbox\\IDS\\AGD\\UNSD-Maitz\\20180517_Workshop_IDS\\UNSD_Normalization_Lexicon_MAY_2018.xml";
    //String OUT = "D:\\Dropbox\\IDS\\FOLK\\Normalisierung\\FOLK_Normalization_Lexicon_JUNE_2021.xml";
    //String OUT = "C:\\exmaralda\\exmaralda\\src\\org\\exmaralda\\orthonormal\\lexicon\\ESLO_Normalization_Lexicon_SEED_December_2022.xml";
    
    String OUT = "C:\\exmaralda\\src\\org\\exmaralda\\orthonormal\\lexicon\\KOMPAS_Normalization_Lexicon_July_2025.xml";
    
    //String OUT = "D:\\Dropbox\\IDS\\AGD\\MEND-Mennonitendeutsch-Goez\\Transkripte\\MEND_Normalization_Lexicon_SEP_2019.xml";
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
        } catch (JDOMException | LexiconException | SQLException | IOException ex) {
            Logger.getLogger(GenerateXMLLexicon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException, SQLException, JDOMException, LexiconException {
        //SimpleXMLFileLexicon lexicon = new SimpleXMLFileLexicon();
        XMLLexicon lexicon = new XMLLexicon();
        File[] flns = new File(PATH).listFiles(FLN_FILTER);
        lexicon.update(flns);
        File f = new File(OUT);
        lexicon.write(f);
        lexicon.read(f);
        
        for (String c : lexicon.getCandidateForms("ma")){
            System.out.println(c);
        }
        System.out.println("====");
        
    }
}
