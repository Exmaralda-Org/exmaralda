/*
 * AbstractXMLCorpusComponent.java
 *
 * Created on 8. Januar 2007, 12:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

import java.io.*;
import java.net.URL;
import org.jdom.*;
import org.jdom.transform.*;
import org.jdom.xpath.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author thomas
 */
public class XMLCorpusComponent implements CorpusComponentInterface {
    
    int numberOfSearchableSegments;
    int counter = 0;
    Vector<Element> searchableElementsList = new Vector<Element>();   
    String url;
    String xpath_to_searchable_segment;
    
    Document corpusComponentDocument;
    
    /** Creates a new instance of XMLCorpusComponent */
    public XMLCorpusComponent(String xp) {
        xpath_to_searchable_segment = xp;
    }
    
    public void readCorpusComponent(File file) throws JDOMException, IOException{
        corpusComponentDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromLocalFile(file);
        url = file.getAbsolutePath();
        XPath xpath = XPath.newInstance(getXPathToSearchableSegment());
        List elementList = xpath.selectNodes(corpusComponentDocument);
        numberOfSearchableSegments = elementList.size();
        for (int pos=0; pos<numberOfSearchableSegments; pos++){
            Element node = (Element)(elementList.get(pos));
            searchableElementsList.addElement(node);
        }
    }

    public void readCorpusComponent(URL url) throws JDOMException, IOException{
        corpusComponentDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromURL(url.toString());
        this.url = url.toString();
        XPath xpath = XPath.newInstance(getXPathToSearchableSegment());
        List elementList = xpath.selectNodes(corpusComponentDocument);
        numberOfSearchableSegments = elementList.size();
        for (int pos=0; pos<numberOfSearchableSegments; pos++){
            Element node = (Element)(elementList.get(pos));
            searchableElementsList.addElement(node);
        }
    }

    @Override
    public SearchableSegmentInterface getSearchableSegment(SearchableSegmentLocatorInterface id) {
        XMLSearchableSegmentLocator locator = (XMLSearchableSegmentLocator)id;
        String segmentXPath = (String)(locator.getSearchableSegmentLocator());
        try {
            XPath xpath = XPath.newInstance(segmentXPath);
            Element e = (Element)(xpath.selectSingleNode(corpusComponentDocument));
            XMLSearchableSegment returnValue = new XMLSearchableSegment(e, locator);
            return returnValue;
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return (counter<numberOfSearchableSegments);
    }

    @Override
    public SearchableSegmentInterface next() {
        XMLSearchableSegmentLocator xssl = new XMLSearchableSegmentLocator(
                url,
                "(" + getXPathToSearchableSegment() + ")" + "[" + Integer.toString(counter+1) + "]"
                );
        XMLSearchableSegment returnValue = new XMLSearchableSegment(searchableElementsList.elementAt(counter), xssl);
        counter++;
        return returnValue;
    }

    @Override
    public void reset() {
        counter=0;
    }

    @Override
    public int getNumberOfSearchableSegments() {
        return numberOfSearchableSegments;
    }
    
    public String getXPathToSearchableSegment(){
        return xpath_to_searchable_segment;
    }

    @Override
    public String getIdentifier() {
        return url;
    }

    @Override
    public SearchResultList search(SearchParametersInterface searchParameters) {
        SearchResultList searchResult = new SearchResultList();
        if (searchParameters.getSearchType() == SearchParametersInterface.DEFAULT_SEARCH){        
            // delegate the search to the searchable segments
            while (hasNext()){
                SearchableSegmentInterface ss = next();
                SearchResultList thisResult = ss.search(searchParameters);
                searchResult.addSearchResults(thisResult);
            }        
        } else if (searchParameters.getSearchType() == SearchParametersInterface.ANNOTATION_SEARCH){
            // NEW 07 AUGUST 2008
            // delegate the search to the searchable segments
            while (hasNext()){
                SearchableSegmentInterface ss = next();
                SearchResultList thisResult = ss.search(searchParameters);                
                searchResult.addSearchResults(thisResult);
                //System.out.println("Found result: " + thisResult.toString());
            }        
            
        } else if (searchParameters instanceof XSLSearchParameters){
            Transformer xslsp = ((XSLSearchParameters)(searchParameters)).getXSLTransformer();
            try {
                //convert Document into Source
                String corpusComponentString = org.exmaralda.exakt.utilities.FileIO.getDocumentAsString(corpusComponentDocument);
                StreamSource corpusComponentSource = new StreamSource(new StringReader(corpusComponentString));
                
                //transform and fetch result as jdom Document
                String result = "";
                StreamSource resultSource = new StreamSource(new StringReader(result));
                StringWriter resultWriter = new StringWriter();
                xslsp.transform(corpusComponentSource, new StreamResult(resultWriter));
                result = resultWriter.toString();
                InputStream resultInputStream = new ByteArrayInputStream(result.getBytes("UTF-8"));
                SAXBuilder sb=new SAXBuilder();
                Document thisResultDocument = sb.build(resultInputStream);
                
                
                // NEW 27-02-2017
                thisResultDocument.getRootElement().getChild("base-directory").setAttribute("url", url);
                searchResult.read(thisResultDocument);
                for (SearchResultInterface sri : searchResult){
                    ((XMLSearchableSegmentLocator)(sri.getSearchableSegmentLocator())).setCorpusComponentLocator(getIdentifier());
                }
            } catch (XSLTransformException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JDOMException ex) {
                ex.printStackTrace();
            } catch (TransformerException ex) {
                Logger.getLogger(XMLCorpusComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return searchResult;
    }

    
}
