/*
 * MergeAnnette.java
 *
 * Created on 28. Juni 2007, 09:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.utilities;

import java.io.*;
import org.exmaralda.exakt.search.SearchResultList;


/**
     * }
 *
 * @author thomas
 */
public class MergeAnnette {
    
    String[] FILES = {
        "hangi_sortiert.xml","kim_sortiert.xml","nasil_sortiert.xml",
        "ne_kadar_sortiert.xml","ne_sortiert.xml","ne_zaman_sortiert.xml",
        "neden_sortiert.xml","neleri_sortiert.xml","ner_sortiert.xml",
        "nesi_sortiert.xml","neyi_sortiert.xml","neyle_sortiert.xml",
        "nicin_sortiert.xml","niye_sortiert.xml"
    };
    
    String BASE_DIRECTORY = "S:\\TP-E5\\Annette\\Neue_Searches_20070803\\Searches_New_SKOBI_Differenz\\wh\\";
    String OUT = BASE_DIRECTORY + "alle_wh_elemente_ohne_doppelte.xml";

    /** Creates a new instance of MergeAnnette */
    public MergeAnnette() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new MergeAnnette().doIt();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void doIt()throws Exception{
        SearchResultList x = new SearchResultList();
        x.read(new File(BASE_DIRECTORY + FILES[0]));
        for (int pos=1; pos<FILES.length; pos++){
            String path = BASE_DIRECTORY + FILES[pos];
            SearchResultList y = new SearchResultList();
            y.read(new File(path));
            x = x.merge(y, true);
        }                
        x.writeXML(new File(OUT));
    }
    
}
