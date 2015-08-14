/*
 * ExmaraldaCorpusComponent.java
 *
 * Created on 13. November 2006, 17:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.xmlSearch;

import java.io.IOException;
import org.exmaralda.exakt.kwicSearch.KWICSearchableIterator;
import org.jdom.*;
import org.jdom.xpath.XPath;
import java.util.*;

/**
 *
 * @author thomas
 */
public class XMLCorpusComponent implements org.exmaralda.exakt.kwicSearch.CorpusComponent {
    
    private Document xmlDocument;
    private String id;
    private String XPATH_STRING = "/";
    private List<org.jdom.Element> elementList;
    private XMLKWICSearchableIterator searchableIterator;
    private String elementIDAttributeName = "id";
    
    
    
    /** Creates a new instance of ExmaraldaCorpusComponent */
    public XMLCorpusComponent(String xp) {
        XPATH_STRING = xp;
    }

    public void setXPathString(String xp){
        XPATH_STRING = xp;
    }
    
    public String getXPathString(){
        return XPATH_STRING;
    }
    
    public void readFromLocalFile(String pathToFile) throws XMLSearchException {
        try {
            xmlDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromLocalFile(pathToFile);
            XPath xpath = XPath.newInstance(XPATH_STRING);
            elementList = xpath.selectNodes(xmlDocument);      
            searchableIterator = new XMLKWICSearchableIterator(elementList);
            searchableIterator.setDocumentID(this.getID());   
            searchableIterator.setElementIDAttributeName(this.getElementIDAttributeName());
        } catch (JDOMException ex) {
            throw new XMLSearchException(ex);
        } catch (IOException ex) {
            throw new XMLSearchException(ex);
        }
    }
    
    public void setID(String i){
        id = i;
    }
    
    public String getID() {
        return id;
    }

    public KWICSearchableIterator getKWICSearchableIterator() {
        return searchableIterator;
    }

    public String getElementIDAttributeName() {
        return elementIDAttributeName;
    }

    public void setElementIDAttributeName(String elementIDAttributeName) {
        this.elementIDAttributeName = elementIDAttributeName;
    }
    
}
