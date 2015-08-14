/*
 * SimpleXMLFileListCorpus.java
 *
 * Created on 8. Januar 2007, 16:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

import java.io.*;
import org.jdom.*;
import org.jdom.xpath.*;
import java.net.*;
import java.util.*;
/**
 *
 * @author thomas
 */
public class SimpleXMLFileListCorpus extends AbstractXMLFileListCorpus {
    
    String XPATH_TO_SEARCHABLE_SEGMENT;
    URI baseURI;    
    
    
    /** Creates a new instance of SimpleXMLFileListCorpus */
    public SimpleXMLFileListCorpus(String xp) {
        XPATH_TO_SEARCHABLE_SEGMENT = xp;
    }
    
    public String getXPathToSearchableSegment() {
        return XPATH_TO_SEARCHABLE_SEGMENT;
    }
    
    public void setXPathToSearchableSegment(String xp) {
        XPATH_TO_SEARCHABLE_SEGMENT = xp;
    }

    public void readCorpus(File file) throws JDOMException, IOException {
        baseURI = file.getParentFile().toURI();
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        fireCorpusInit(0, "Started reading " + file.getAbsolutePath() + ".");
        String pathToXMLFile = new String();
        while ((pathToXMLFile = br.readLine()) != null){
            File fullPath = new File(baseURI.resolve(pathToXMLFile));
            getFileList().addElement(fullPath);
        }
        fireCorpusInit(0, file.getAbsolutePath() + " read successfully.");
        index();        
    }
    
    
    
}
