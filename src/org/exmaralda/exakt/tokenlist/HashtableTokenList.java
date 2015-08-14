/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.tokenlist;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.exmaraldaSearch.COMACorpus;
import org.exmaralda.exakt.exmaraldaSearch.COMACorpusInterface;
import org.exmaralda.exakt.exmaraldaSearch.COMADBCorpus;
import org.exmaralda.exakt.exmaraldaSearch.COMARemoteCorpus;
import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public class HashtableTokenList extends AbstractTokenList {

    Hashtable<String, Integer> theTokens = new Hashtable<String, Integer>();
    private Vector<SearchListenerInterface> listenerList = new Vector<SearchListenerInterface>();


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


    @Override
    public int getNumberOfTokens() {
        return theTokens.size();
    }

    @Override
    public AbstractTokenList getTokensWithPrefix(String prefix) {
        AbstractTokenList result = new HashtableTokenList();
        for (String token : theTokens.keySet()){
            if (token.toLowerCase().startsWith(prefix.toLowerCase())){
                result.addToken(token);
                result.setTokenCount(token, getTokenCount(token));
            }
        }
        return result;
    }

    @Override
    public AbstractTokenList getTokensBetweenPrefixes(String prefix1, String prefix2) {
        //TODO
        return null;
    }

    @Override
    public void read(Object source) throws IOException {
        // TODO
    }

    @Override
    public void write(Object target) throws IOException {
        File file = (File)target;
        Document doc = new Document(new Element("token-list"));
        for (String token : getTokens(AbstractTokenList.ALPHABETICALLY_SORTED)){
            Element tokenElement = new Element("token");
            tokenElement.setText(token);
            tokenElement.setAttribute("count", Integer.toString(getTokenCount(token)));
            doc.getRootElement().addContent(tokenElement);
        }
        IOUtilities.writeDocumentToLocalFile(file.getAbsolutePath(), doc);
    }

    @Override
    public boolean addToken(String token) {
        int newCount = getTokenCount(token)+1;
        theTokens.put(token, newCount);
        return (newCount==1);
    }

    @Override
    public int removeToken(String token) {
        return theTokens.remove(token);
    }

    @Override
    public boolean readWordsFromExmaraldaCorpus(COMACorpusInterface c) throws JDOMException, IOException {
        return readWordsFromExmaraldaCorpus(c, null);
    }
    //public boolean readWordsFromExmaraldaCorpus(AbstractCorpus c) throws JDOMException, IOException {
    public boolean readWordsFromExmaraldaCorpus(COMACorpusInterface c, String[] speakerIdentifiers) throws JDOMException, IOException {
        corpus = c;
        if (c instanceof COMACorpus){
            COMACorpus comacorpus = (COMACorpus)c;
            //HashSet<String> segmentNames = comacorpus.getSegmentNames();
            String nameOfWordSegment = comacorpus.getWordSegmentName();
            if (nameOfWordSegment==null) return false;
            String XPATH_TO_WORDS = "//ts[@n='" + nameOfWordSegment + "']";            
            XPath xp = XPath.newInstance(XPATH_TO_WORDS);
            for (int pos=0; pos<comacorpus.getNumberOfCorpusComponents(); pos++){
                double prog = (double)pos/(double)(corpus.getNumberOfCorpusComponents());
                File xmlFile = comacorpus.getFileList().elementAt(pos);
                fireCorpusInit(prog, "Getting tokens for " + xmlFile.getName());
                //System.out.println("Getting tokens from " + xmlFile.getName());
                Document xmlDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromLocalFile(xmlFile);
                if (speakerIdentifiers!=null){
                    xp = makeSpeakerSpecificXPath(speakerIdentifiers, xmlDocument, c, nameOfWordSegment);
                }
                if (xp!=null){
                    List l = xp.selectNodes(xmlDocument);
                    for (Object o : l){
                        Element e = (Element)o;
                        this.addToken(e.getText());
                    }
                }
            }
            this.setName("Word list for " + comacorpus.getCorpusName());
        } else if (c instanceof COMARemoteCorpus){
            COMARemoteCorpus comacorpus = (COMARemoteCorpus)c;
            HashSet<String> segmentNames = comacorpus.getSegmentNames();
            String nameOfWordSegment = null;
            for (String segmentName : segmentNames){
                if (segmentName.endsWith(":w")){
                    nameOfWordSegment = segmentName;
                    break;
                }
            }
            if (nameOfWordSegment==null) return false;
            String XPATH_TO_WORDS = "//ts[@n='" + nameOfWordSegment + "']";
            XPath xp = XPath.newInstance(XPATH_TO_WORDS);
            for (int pos=0; pos<comacorpus.getNumberOfCorpusComponents(); pos++){
                double prog = (double)pos/(double)(corpus.getNumberOfCorpusComponents());
                URI xmlURI = comacorpus.uriList.elementAt(pos);
                Document xmlDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromURL(xmlURI.toString());
                if (speakerIdentifiers!=null){
                    xp = makeSpeakerSpecificXPath(speakerIdentifiers, xmlDocument, c, nameOfWordSegment);
                }
                //File xmlFile = comacorpus.getFileList().elementAt(pos);
                fireCorpusInit(prog, "Getting tokens for " + xmlURI.getPath());
                if (xp!=null){
                    List l = xp.selectNodes(xmlDocument);
                    for (Object o : l){
                        Element e = (Element)o;
                        this.addToken(e.getText());
                    }
                }
            }
            this.setName("Word list for " + comacorpus.getCorpusName());
        } else if (c instanceof COMADBCorpus){
            // TODO: write code
        }
        return true;
    }
    
    public boolean readWordsFromFolkerFiles(File[] files) throws JDOMException, IOException {
        return readWordsFromFolkerFiles(files, "//w");
    }
    
    public boolean readWordsFromFolkerFiles(File[] files, String xpath) throws JDOMException, IOException {
        corpus = null;
        String XPATH_TO_WORDS = xpath;
        XPath xp = XPath.newInstance(XPATH_TO_WORDS);
        int pos=0;
        for (File xmlFile : files){
            double prog = (double)pos/(double)(files.length);
            fireCorpusInit(prog, "Getting tokens for " + xmlFile.getName());
            //System.out.println(xmlFile.getName());
            Document xmlDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromLocalFile(xmlFile);
            List l = xp.selectNodes(xmlDocument);
            for (Object o : l){
                if (o instanceof Element){
                    Element e = (Element)o; 
                    this.addToken(e.getText());
                } else if (o instanceof Attribute){
                    Attribute a = (Attribute)o;
                    // attention: can be more than one token!
                    String attValue = a.getValue();
                    if (!(attValue.contains(" "))){
                        addToken(attValue);                    
                    } else {
                        String[] tokens = attValue.split(" ");
                        for (String t : tokens){
                            addToken(t);
                        }
                    }
                }
            }
        }
        this.setName("Word list for list of FOLKER files");
        return true;
    }
    

    @Override
    public int getTokenCount(String token) {
        if (!theTokens.containsKey(token)) return 0;
        return theTokens.get(token).intValue();
    }

    @Override
    public void setTokenCount(String token, int count) {
        theTokens.put(token, count);
    }

    @Override
    public List<String> getTokens(int howtosort) {
        Vector<String> result = new Vector<String>();
        result.addAll(theTokens.keySet());
        if (howtosort==AbstractTokenList.ALPHABETICALLY_SORTED){
            Collections.sort(result, String.CASE_INSENSITIVE_ORDER);
        } else if (howtosort==AbstractTokenList.FREQUENCY_SORTED){
            Comparator c = new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return new Integer(getTokenCount(o2)).compareTo(new Integer(getTokenCount(o1)));
                }
            };
            Collections.sort(result, c);
        }
        return result;
    }

    private XPath makeSpeakerSpecificXPath(String[] speakerIdentifiers, Document xmlDocument, COMACorpusInterface c, String wordSegmentName) throws JDOMException {
        HashSet<String> sigles = new HashSet<String>();
        for (String si : speakerIdentifiers){
            sigles.add(si);
        }
        Element speakerTable = xmlDocument.getRootElement().getChild("head").getChild("speakertable");
        String uniqueSpeakerIdentifier = c.getUniqueSpeakerIdentifier();
        //speaker/abbreviation
        String xpath =  "//speaker[";
        int count = 0;
        for (String si: speakerIdentifiers){
           xpath+=uniqueSpeakerIdentifier + "='" + si + "'";
           if (count<(speakerIdentifiers.length-1)){
               xpath+=" or ";
           }
           count++;
        }
        xpath+="]/@id";
        System.out.println(xpath);
        List speakerIDs = XPath.newInstance(xpath).selectNodes(speakerTable);
        
        if (speakerIDs.isEmpty()){
            return null;
        }
        // segmented-tier[@speaker='id0' or @speaker='
        String xpath2 = "//segmented-tier";
        xpath2="//segmented-tier[";
        count = 0;
        for (Object o : speakerIDs){
            String speakerID = ((Attribute)o).getValue();
            xpath2+="@speaker='" + speakerID + "'";
            if (count<speakerIDs.size()-1){
               xpath2+=" or ";                
            }
            count++;
        }
        xpath2+="]";
        xpath2+="/descendant::ts[@n='" + wordSegmentName + "']";
        System.out.println(xpath2);
        return XPath.newInstance(xpath2);
    }


}
