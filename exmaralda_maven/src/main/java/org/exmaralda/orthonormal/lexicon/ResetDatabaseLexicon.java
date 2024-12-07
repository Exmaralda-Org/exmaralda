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
public class ResetDatabaseLexicon {

    public static String PATH = "D:\\AGD-DATA\\dgd2_data\\transcripts\\FOLK";
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
            new ResetDatabaseLexicon().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(ResetDatabaseLexicon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LexiconException ex) {
            Logger.getLogger(ResetDatabaseLexicon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ResetDatabaseLexicon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ResetDatabaseLexicon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException, SQLException, JDOMException, LexiconException {
        //String RDB_URL = "jdbc:oracle:thin:@193.196.8.35:1521:orc11";
        String RDB_URL = "jdbc:oracle:thin:@10.0.1.35:1521:orc11";
        String RDB_USERNAME = "orthonormal";
        String RDB_PASSWORD = "-----";
        String[] CONNECTION_PARAMETERS = {RDB_URL, RDB_USERNAME, RDB_PASSWORD};
        RDBLexicon lexicon = new RDBLexicon();

        lexicon.read(CONNECTION_PARAMETERS);
        lexicon.clear();
        File[] flns = new File(PATH).listFiles(FLN_FILTER);
        lexicon.update(flns);
    }
}
