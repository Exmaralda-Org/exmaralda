/*
 * SearchResultComparator.java
 *
 * Created on 28. Juni 2007, 09:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

/**
 *
 * @author thomas
 */
public class SearchResultComparator implements java.util.Comparator<SearchResultInterface> {
    
    /** Creates a new instance of SearchResultComparator */
    public SearchResultComparator() {
    }

    public int compare(SearchResultInterface o1, SearchResultInterface o2) {
        for (int pos=0; pos<Math.min(o1.getAdditionalData().length, o2.getAdditionalData().length); pos++){
            int c = o1.getAdditionalData()[pos].compareTo(o2.getAdditionalData()[pos]);
            if (c!=0) return c;
        }
        int c1 = o1.getMatchTextAsString().compareTo(o2.getMatchTextAsString());
        if (c1!=0) return c1;
        int c2 = o1.getLeftContextAsString().compareTo(o2.getLeftContextAsString());
        if (c2!=0) return c2;
        int c3 = o1.getRightContextAsString().compareTo(o2.getRightContextAsString());
        return c3;
    }

    
}
