/*
 * MergeAnnette2.java
 *
 * Created on 9. August 2007, 17:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.utilities;

import java.io.*;
import org.exmaralda.exakt.search.SearchResultList;

/**
 *
 * @author thomas
 */
public class MergeAnnette2 {
    
    String FILE1 = "S:\\TP-E5\\Annette\\Neue_Searches_20070803\\Searches_Goeteborg_PBU_CORPUS_20JUNI2007\\DIK_sortiert_morph_synt_Goeteborg.xml"; 
    String FILE2 = "S:\\TP-E5\\Annette\\Neue_Searches_20070803\\Searches_New_SKOBI_Differenz\\DIK_sortiert.xml"; 
    String OUT = "S:\\TP-E5\\Annette\\Neue_Searches_20070803\\Alle_DIK_A_Z.xml";
            
    /** Creates a new instance of MergeAnnette2 */
    public MergeAnnette2() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new MergeAnnette2().doit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void doit() throws Exception {
        SearchResultList x = new SearchResultList();
        x.read(new File(FILE1));
        SearchResultList y = new SearchResultList();
        y.read(new File(FILE2));
        x = x.merge(y, false);
        x.writeXML(new File(OUT));        
    }
    
}
