/*
 * ExmaraldaKWICSearchableIterator.java
 *
 * Created on 13. November 2006, 18:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.xmlSearch;

import org.exmaralda.exakt.kwicSearch.KWICSearchableComponent;
import org.exmaralda.exakt.kwicSearch.KWICSearchableIterator;
import org.jdom.*;
import java.util.*;
/**
 *
 * @author thomas
 */
public class XMLKWICSearchableIterator implements KWICSearchableIterator {
    
    private Iterator<org.jdom.Element> iterator;
    private String documentID;
    private String elementIDAttributeName = "id";
    
    
    /** Creates a new instance of ExmaraldaKWICSearchableIterator */
    public XMLKWICSearchableIterator(List<org.jdom.Element> el) {
        iterator = el.iterator();
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public KWICSearchableComponent next() {
        Element e = iterator.next();
        XMLKWICSearchableComponent component = new XMLKWICSearchableComponent(e);
        component.setDocumentID(getDocumentID());
        component.setElementIDAttributeName(this.getElementIDAttributeName());
        return component;
    }
    
    public void setDocumentID(String dID){
        documentID = dID;
    }
    
    public String getDocumentID(){
        return documentID;
    }

    public String getElementIDAttributeName() {
        return elementIDAttributeName;
    }

    public void setElementIDAttributeName(String elementIDAttributeName) {
        this.elementIDAttributeName = elementIDAttributeName;
    }
    
}
