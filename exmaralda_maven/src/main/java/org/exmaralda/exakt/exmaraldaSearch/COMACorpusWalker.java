/*
 * COMACorpusWalker.java
 *
 * Created on 26. Januar 2007, 15:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch;

import java.io.*;
import org.jdom.*;
import org.jdom.xpath.*;
import java.net.*;
import java.util.*;
import org.exmaralda.exakt.search.SearchEvent;

/**
 *
 * @author thomas
 */
public abstract class COMACorpusWalker {
    
    private static String[] XPATH_TO_TRANSCRIPTIONS = 
    {"//Transcription[Description/Key[@Name='segmented']/text()='false']/NSLink",
     "//Transcription[Description/Key[@Name='segmented']/text()='true']/NSLink",     
     "//Transcription[]/NSLink"};

    public static int BASIC_TRANSCRIPTIONS = 0;
    public static int SEGMENTED_TRANSCRIPTIONS = 1;
    public static int ALL_TRANSCRIPTIONS = 2;
    
    URI baseURI;    
    Document comaDocument;
    Element currentNode;
    public String currentFilename;
    public String currentPath;

    Vector<org.exmaralda.exakt.search.SearchListenerInterface> listeners = new Vector<org.exmaralda.exakt.search.SearchListenerInterface>();

    /** Creates a new instance of COMACorpusWalker */
    public COMACorpusWalker() {
    }
    
    public void readCorpus(File file) throws IOException, JDOMException{
        baseURI = file.getParentFile().toURI();
        comaDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromLocalFile(file);        
    }
    
    public void walk(int whichTranscriptions) throws IOException, JDOMException{
        XPath xpath = XPath.newInstance(XPATH_TO_TRANSCRIPTIONS[whichTranscriptions]);
        List transcriptionList = xpath.selectNodes(comaDocument);
        for (int pos=0; pos<transcriptionList.size(); pos++){
            currentNode = (Element)(transcriptionList.get(pos));
            String transcriptionPath = currentNode.getText();
            currentFilename = transcriptionPath;
            System.out.println("processing " + transcriptionPath);
            currentPath = new File(baseURI.resolve(transcriptionPath)).getAbsolutePath();
            File fullPath = new File(baseURI.resolve(transcriptionPath));

            fireCorpusInit((double)pos/(double)transcriptionList.size(), fullPath.getName());

            Document transcriptionDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromLocalFile(fullPath);
            try {
                processTranscription(transcriptionDocument);
            } catch (JDOMException ex) {
                throw(ex);
            }
        }        
    }
    
    public abstract void processTranscription(Document document) throws JDOMException;

    public void addSearchListener(org.exmaralda.exakt.search.SearchListenerInterface listener) {
        listeners.addElement(listener);
    }

    protected void fireCorpusInit(double progress, String message) {
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.size() - 1; i >= 0; i -= 1) {
            SearchEvent se = new SearchEvent(SearchEvent.CORPUS_INIT_PROGRESS, progress, message);
            listeners.elementAt(i).processSearchEvent(se);
        }
    }


    
}
