/*
 * XMLFileListCorpus.java
 *
 * Created on 14. November 2006, 16:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.xmlSearch;

import org.exmaralda.exakt.kwicSearch.CorpusComponent;
import org.exmaralda.exakt.kwicSearch.CorpusIterator;
import java.util.*;
import java.io.*;
import org.jdom.xpath.*;
import org.jdom.*;
/**
 *
 * @author thomas
 */
public class XMLURLListCorpus implements org.exmaralda.exakt.kwicSearch.Corpus {
    
    private Hashtable<String,String> id2FilepathMapping;
    private XMLFileListCorpusIterator corpusIterator;
    private int size = 0;
    private String searchableXPath;
    private String elementIDAttributeName = "id";
    

    /** Creates a new instance of XMLFileListCorpus */
    public XMLURLListCorpus(String sxp, String ean) {
        id2FilepathMapping = new Hashtable<String,String>();     
        searchableXPath = sxp;
        elementIDAttributeName = ean;
    }

    public void readFromLocalFile(String pathToCorpusFile, String xpathToDocumentID) throws XMLSearchException {
        try {
            System.out.println("Reading " + pathToCorpusFile);
            FileReader fr = new FileReader(pathToCorpusFile);
            BufferedReader br = new BufferedReader(fr);            
            String urlToXMLFile = new String();
            System.out.println("Started processing");
            XPath xpath = XPath.newInstance(xpathToDocumentID);
            while ((urlToXMLFile = br.readLine()) != null){
                size++;
                Document xmlDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromURL(urlToXMLFile);           
                Attribute idAtt = (Attribute)(xpath.selectSingleNode(xmlDocument));
                String identifier = urlToXMLFile;
                if (idAtt!=null){
                    identifier = idAtt.getValue();
                }
                id2FilepathMapping.put(identifier,urlToXMLFile);                
            }
            corpusIterator = new XMLFileListCorpusIterator(id2FilepathMapping);
            corpusIterator.setSearchableXPath(this.searchableXPath);
            corpusIterator.setElementIDAttributeName(this.getElementIDAttributeName());
        } catch (FileNotFoundException ex) {
            throw new XMLSearchException(ex);
        } catch (JDOMException ex) {
            throw new XMLSearchException(ex);
        } catch (IOException ex) {
            throw new XMLSearchException(ex);
        }
    }


    public CorpusComponent getCorpusComponent(String componentID) {
        XMLCorpusComponent xcc = new XMLCorpusComponent(this.searchableXPath);
        String path = id2FilepathMapping.get(componentID);
        xcc.setElementIDAttributeName(this.getElementIDAttributeName());
        try {
            xcc.readFromLocalFile(path);
        } catch (XMLSearchException ex) {
            ex.printStackTrace();
        }
        return xcc;
    }

    public int getSize() {
        return size;
    }

    public CorpusIterator getCorpusIterator() {
        return corpusIterator;
    }

    public String getElementIDAttributeName() {
        return elementIDAttributeName;
    }

    public void setElementIDAttributeName(String elementIDAttributeName) {
        this.elementIDAttributeName = elementIDAttributeName;
    }
    
}
