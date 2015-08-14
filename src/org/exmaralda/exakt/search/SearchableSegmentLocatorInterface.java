/*
 * SearchableSegmentLocatorInterface.java
 *
 * Created on 5. Januar 2007, 17:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

/**
 *
 * @author thomas
 */
public interface SearchableSegmentLocatorInterface {
    
    public Object getCorpusComponentLocator();
    public Object getSearchableSegmentLocator();
    public String getCorpusComponentFilename();
    public org.jdom.Element toXML();

}
