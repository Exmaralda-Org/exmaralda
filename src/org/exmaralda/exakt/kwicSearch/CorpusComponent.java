/*
 * CorpusComponent.java
 *
 * Created on 13. November 2006, 17:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.kwicSearch;

/**
 *
 * @author thomas
 */
public interface CorpusComponent {
    
    public String getID();
    public KWICSearchableIterator getKWICSearchableIterator();
    
}
