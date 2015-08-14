/*
 * KWICTableEvent.java
 *
 * Created on 15. Januar 2007, 13:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search.swing;

import org.exmaralda.exakt.search.*;
import org.exmaralda.exakt.search.*;


/**
 *
 * @author thomas
 */
public class KWICTableEvent {
    
    public static final int SELECTION = 0;
    public static final int DOUBLE_CLICK = 1;    
    
    private int type;
    private SearchResultInterface selectedSearchResult;
    
    /** Creates a new instance of KWICTableEvent */
    public KWICTableEvent(int t, SearchResultInterface ssr) {
        type = t;
        selectedSearchResult = ssr;
    }

    public int getType() {
        return type;
    }

    public SearchResultInterface getSelectedSearchResult() {
        return selectedSearchResult;
    }

    
}
