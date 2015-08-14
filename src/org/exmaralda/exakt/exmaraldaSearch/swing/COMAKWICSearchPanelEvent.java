/*
 * KWICSearchPanelEvent.java
 *
 * Created on 6. Februar 2007, 09:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.swing;

import java.awt.Component;
import org.exmaralda.exakt.search.SearchResultList;

/**
 *
 * @author thomas
 */
public class COMAKWICSearchPanelEvent {
    
    public static final int NEW_SEARCH_RESULT = 0;
    
    private int type;
    private Component parentComponent;
    private SearchResultList searchResultList;
    
    
    /** Creates a new instance of KWICSearchPanelEvent */
    public COMAKWICSearchPanelEvent(int t, Component c, SearchResultList l) {
        type = t;
        parentComponent = c;
        searchResultList = l;
    }

    public int getType() {
        return type;
    }

    public Component getParentComponent() {
        return parentComponent;
    }

    public SearchResultList getSearchResultList() {
        return searchResultList;
    }
    
}
