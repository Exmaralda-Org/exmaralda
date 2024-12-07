/*
 * SearchHistory.java
 *
 * Created on 16. Januar 2007, 10:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

import java.util.*;

/**
 *
 * @author thomas
 */
public class SearchHistory extends Vector<String[]>{
    
    int MAX = 10;
    int MAX_LENGTH = 60;
    HashSet<String> searchExpressions = new HashSet<String>();
    
    /** Creates a new instance of SearchHistory */
    public SearchHistory() {
    }
    
    public boolean addHistory(String searchExpression, Set<String> types){
        if (searchExpressions.contains(searchExpression)){return false;}
        searchExpressions.add(searchExpression);
        int count = 0;
        String typeString = "";
        for (String t : types){
            typeString+=t;
            if ((count<8) && (count<types.size()-1)) typeString+=" | ";
            if (count==8) break;
            count++;
        }
        if (typeString.length()>MAX_LENGTH) typeString = typeString.substring(0, MAX_LENGTH) + "...";
        String[] toBeAdded = {searchExpression, typeString};
        this.add(0, toBeAdded);
        if (size()>MAX){
            this.removeRange(MAX, size()-1);
        }
        return true;
    }
    
}
