/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Vector;

import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public abstract class AbstractCorpusChecker extends AbstractBasicTranscriptionChecker {

    Element currentElement;
    String currentFilename;
    int count = 0;
    private Vector<SearchListenerInterface> listenerList = new Vector<SearchListenerInterface>();
    public Document corpusDocument;


    public AbstractCorpusChecker() {
    }



    public void checkCorpus(Document corpus, String CORPUS_BASEDIRECTORY) throws JDOMException, SAXException, JexmaraldaException, URISyntaxException{
        corpusDocument = corpus;
        XPath xpath = XPath.newInstance("//Transcription[Description/Key[@Name='segmented']/text()='false']/NSLink");
        List transcriptionList = xpath.selectNodes(corpus);
        for (int pos=0; pos<transcriptionList.size(); pos++){
            Element nslink = (Element)(transcriptionList.get(pos));
            currentElement = nslink;
            final String fullTranscriptionName = CORPUS_BASEDIRECTORY + System.getProperty("file.separator", "/") + nslink.getText();
            System.out.println("Reading " + fullTranscriptionName + "...");
            currentFilename = fullTranscriptionName;

            count++;

            fireCorpusInit((double)count/(double)transcriptionList.size(), nslink.getText());

            BasicTranscription bt = new BasicTranscription(currentFilename);
            processTranscription(bt, currentFilename);

        }

    }


    public void addSearchListener(SearchListenerInterface sli) {
        listenerList.addElement(sli);
    }

    protected void fireCorpusInit(double progress, String message) {
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listenerList.size() - 1; i >= 0; i -= 1) {
            SearchEvent se = new SearchEvent(SearchEvent.CORPUS_INIT_PROGRESS, progress, message);
            listenerList.elementAt(i).processSearchEvent(se);
        }
    }




}
