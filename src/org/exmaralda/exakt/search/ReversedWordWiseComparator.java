/*
 * ReversedWordWiseComparator.java
 *
 * Created on 5. Februar 2007, 11:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

/**
 *
 * @author thomas
 */
public class ReversedWordWiseComparator implements java.util.Comparator {
    
    /** Creates a new instance of ReversedWordWiseComparator */
    public ReversedWordWiseComparator() {
    }

    public int compare(Object o1, Object o2) {
        String s1 = (String)o1;
        String s2 = (String)o2;
        
        String[] s1_tokens = s1.split("[^\\p{javaLowerCase}\\p{javaUpperCase}]");
        String[] s2_tokens = s2.split("[^\\p{javaLowerCase}\\p{javaUpperCase}]");
          	
        int pos1 = s1_tokens.length-1;
        int pos2 = s2_tokens.length-1;
        
        while ((pos1>=0) && (pos2>=0)){
            int c = s1_tokens[pos1].compareToIgnoreCase(s2_tokens[pos2]);
            if (c!=0) return c;
            pos1--;
            pos2--;
        }
        return 0;
    }
    
}
