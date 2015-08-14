/*
 * RemoveIdenticalAnnette.java
 *
 * Created on 21. September 2007, 12:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.utilities;

import java.io.*;
import java.util.*;
import org.exmaralda.exakt.search.SearchResultComparator;
import org.exmaralda.exakt.search.SearchResultInterface;
import org.exmaralda.exakt.search.SearchResultList;

/**
 *
 * @author thomas
 */
public class RemoveIdenticalAnnette {
    
    /** Creates a new instance of RemoveIdenticalAnnette */
    public RemoveIdenticalAnnette() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new RemoveIdenticalAnnette().doIt();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void doIt() throws Exception {
        String FILENAME = "S:\\TP-E5\\Annette\\Neue_Searches_20070803\\Alle_DIK_20070907.xml";
        SearchResultList srl = new SearchResultList();
        srl.read(new File(FILENAME));
        SearchResultComparator src = new SearchResultComparator();
        Collections.sort(srl, src);
        int count=0;
        for (int pos=0; pos<srl.size()-1; pos++){
            SearchResultInterface sr1 = srl.elementAt(pos);
            SearchResultInterface sr2 = srl.elementAt(pos+1);   
            if (compare(sr1,sr2)){
                srl.remove(sr2);
                pos--;
                count++;
                System.out.println("Found one!");
            }
        }
        System.out.println("Found " + count + " duplicates in " + srl.size() + " results.");
        srl.writeXML(new File(FILENAME));
    }
    
    public boolean compare(SearchResultInterface sr1, SearchResultInterface sr2){
        if (!sr1.getLeftContextAsString().equals(sr2.getLeftContextAsString())) return false;
        if (!sr1.getRightContextAsString().equals(sr2.getRightContextAsString())) return false;
        if (!sr1.getMatchTextAsString().equals(sr2.getMatchTextAsString())) return false;  
        if (!sr1.getAdditionalData()[0].equals(sr2.getAdditionalData()[0])) return false;  
        if (!sr1.getAdditionalData()[1].equals(sr2.getAdditionalData()[1])) return false;  
        if (!sr1.getAdditionalData()[2].equals(sr2.getAdditionalData()[2])) return false;  
        
        return true;
    }
    
    
    
}
