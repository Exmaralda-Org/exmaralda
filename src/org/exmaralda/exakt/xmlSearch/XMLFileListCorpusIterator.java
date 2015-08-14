/*
 * ExmaraldaCorpusIterator.java
 *
 * Created on 13. November 2006, 17:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.xmlSearch;


import java.util.*;
import org.exmaralda.exakt.kwicSearch.CorpusComponent;
import org.exmaralda.exakt.kwicSearch.CorpusIterator;

/**
 *
 * @author thomas
 */
public class XMLFileListCorpusIterator implements CorpusIterator {
    
    java.util.Enumeration<String> iterator;
    java.util.Hashtable<String,String> mapping;
    private String searchableXPath = "/";
    private String elementIDAttributeName = "id";
    
    
    
    /** Creates a new instance of ExmaraldaCorpusIterator */
    public XMLFileListCorpusIterator(Hashtable<String,String> id2FilepathMapping) {
        mapping = id2FilepathMapping;
        iterator = mapping.keys();
    }

    public boolean hasNext() {
        return iterator.hasMoreElements();
    }

    public CorpusComponent next() {
        String id = iterator.nextElement();
        String filePath = mapping.get(id);
        XMLCorpusComponent component = new XMLCorpusComponent(getSearchableXPath());
        component.setID(id);            
        component.setElementIDAttributeName(this.getElementIDAttributeName());
        try {
            component.readFromLocalFile(filePath);
            return component;
        } catch (XMLSearchException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String getSearchableXPath() {
        return searchableXPath;
    }

    public void setSearchableXPath(String searchableXPath) {
        this.searchableXPath = searchableXPath;
    }

    public String getElementIDAttributeName() {
        return elementIDAttributeName;
    }

    public void setElementIDAttributeName(String elementIDAttributeName) {
        this.elementIDAttributeName = elementIDAttributeName;
    }
    
    
}
