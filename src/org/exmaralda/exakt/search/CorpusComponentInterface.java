/*
 * CorpusComponentInterface.java
 *
 * Created on 8. Januar 2007, 12:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

/**
 *
 * @author thomas
 */
public interface CorpusComponentInterface {
    
    public boolean hasNext();
    
    public SearchableSegmentInterface next();
    
    public void reset();
    
    public SearchableSegmentInterface getSearchableSegment(SearchableSegmentLocatorInterface id);        
    
    public int getNumberOfSearchableSegments();

    public String getIdentifier();
    
    public SearchResultList search(SearchParametersInterface searchParameters);
        
}
