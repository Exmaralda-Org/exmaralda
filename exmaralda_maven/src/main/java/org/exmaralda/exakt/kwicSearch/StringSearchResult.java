/*
 * ExmaraldaSearchResult.java
 *
 * Created on 14. November 2006, 11:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.kwicSearch;

import org.jdom.Element;
/**
 *
 * @author thomas
 */
public class StringSearchResult implements SearchResult, XMLable {
    
    private String leftContext;
    private String matchText;
    private String rightContext;
    private String documentID;
    private String elementID;
    
    /** Creates a new instance of ExmaraldaSearchResult */
    public StringSearchResult(String allText, int matchStart, int matchEnd, String dID, String eID) {
        leftContext = allText.substring(0,matchStart);
        matchText = allText.substring(matchStart, matchEnd);
        rightContext = allText.substring(matchEnd);
        documentID = dID;
        elementID = eID;
    }

    public String getRightContextAsString() {
        return rightContext;
    }

    public String[] getPrecedingAdditionalAttributeValues() {
        return null;
    }

    public String getMatchTextAsString() {
        return matchText;
    }

    public String getLeftContextAsString() {
        return leftContext;
    }

    public String getKWICSearchableComponentID() {
        return elementID;
    }

    public String[] getFollowingAdditionalAttributeValues() {
        return null;
    }

    public String getCorpusComponentID() {
        return documentID;
    }

    public Element toXML() {
        Element returnValue = new Element("search-result");
        returnValue.setAttribute("text-id", getCorpusComponentID());
        returnValue.setAttribute("element-id", getKWICSearchableComponentID());
        Element leftContext = new Element("left-context");
        leftContext.setText(getLeftContextAsString());
        Element matchText = new Element("match-text");
        matchText.setText(getMatchTextAsString());
        Element rightContext = new Element("right-context");
        rightContext.setText(getRightContextAsString());
        returnValue.addContent(leftContext);
        returnValue.addContent(matchText);
        returnValue.addContent(rightContext);
        return returnValue;
    }
    
    
    
}
