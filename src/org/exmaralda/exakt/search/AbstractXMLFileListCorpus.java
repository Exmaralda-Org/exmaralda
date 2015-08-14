/*
 * AbstractXMLFileListCorpus.java
 *
 * Created on 8. Januar 2007, 15:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

import java.io.*;
import org.jdom.*;
import org.jdom.xpath.*;
import java.util.*;

/**
 *
 * @author thomas
 */
public abstract class AbstractXMLFileListCorpus extends AbstractCorpus implements CorpusInterface {

    private Vector<File> fileList = new Vector<File>();

    /** Creates a new instance of AbstractXMLFileListCorpus */
    public AbstractXMLFileListCorpus() {
    }
    
    public int getNumberOfCorpusComponents() {
        return fileList.size();
    }
    
    public CorpusComponentInterface next() {
        XMLCorpusComponent returnValue = new XMLCorpusComponent(getXPathToSearchableSegment());
        File xmlFile = getFileList().elementAt(counter);
        try {
            returnValue.readCorpusComponent(xmlFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
        counter++;
        return returnValue;
    }
    
    public CorpusComponentInterface getCorpusComponent(SearchableSegmentLocatorInterface id) {
        XMLSearchableSegmentLocator locator = (XMLSearchableSegmentLocator)id;
        String corpusComponentPath = (String)(locator.getCorpusComponentLocator());
        File transcriptionFile = new File(corpusComponentPath);
        XMLCorpusComponent returnValue = new XMLCorpusComponent(getXPathToSearchableSegment());
        try {
            returnValue.readCorpusComponent(transcriptionFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
        return returnValue;
        
    }    

    public Vector<File> getFileList() {
        return fileList;
    }

    public void setFileList(Vector<File> fileList) {
        this.fileList = fileList;
    }
    
    public void index() throws JDOMException, IOException {
        XPath xpath = XPath.newInstance(getXPathToSearchableSegment());
        int countSegments = 0;
        for (int pos=0; pos<getNumberOfCorpusComponents(); pos++){
            File xmlFile = getFileList().elementAt(pos);
            Document xmlDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromLocalFile(xmlFile);
            List segmentList = xpath.selectNodes(xmlDocument);
            countSegments+=segmentList.size();
            fireCorpusInit((double)pos/(double)getNumberOfCorpusComponents()*0.99 + 0.1, xmlFile.getAbsolutePath() +  " indexed.");
        }
        setNumberOfSearchableSegments(countSegments);
    }

    public abstract String getXPathToSearchableSegment();
    
    public abstract void setXPathToSearchableSegment(String xp);
    
    public abstract void readCorpus(File file) throws JDOMException, IOException;    
    
    
    
}
