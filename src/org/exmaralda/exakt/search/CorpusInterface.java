/*
 * CorpusInterface.java
 *
 * Created on 5. Januar 2007, 17:35
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
public interface CorpusInterface {
    
    
    public boolean hasNext();
    
    public CorpusComponentInterface next();
    
    public void reset();
    
    public CorpusComponentInterface getCorpusComponent(SearchableSegmentLocatorInterface id);        
    
    public int getNumberOfCorpusComponents();
    
    public int getNumberOfSearchableSegments();
    
    public void addSearchListener(SearchListenerInterface sli);
}
