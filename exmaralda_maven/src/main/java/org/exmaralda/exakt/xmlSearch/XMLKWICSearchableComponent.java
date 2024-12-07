/*
 * ExmaraldaKWICSearchableComponent.java
 *
 * Created on 13. November 2006, 18:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.xmlSearch;

import org.exmaralda.exakt.kwicSearch.KWICSearchableComponent;
import org.exmaralda.exakt.kwicSearch.RegularSearchExpression;
import org.exmaralda.exakt.kwicSearch.SearchExpression;
import org.exmaralda.exakt.kwicSearch.SearchResult;
import org.exmaralda.exakt.kwicSearch.StringSearchResult;
import org.jdom.*;
import org.jdom.xpath.*;
import java.util.regex.*;
import java.util.*;

/**
 *
 * @author thomas
 */
public class XMLKWICSearchableComponent implements KWICSearchableComponent {
    
    Element element;
    String documentID;
    private String elementIDAttributeName = "id";
    XPath xpathToID;
    
    /** Creates a new instance of ExmaraldaKWICSearchableComponent */
    public XMLKWICSearchableComponent(Element e) {
        element = e;
    }

    public SearchResult[] search(SearchExpression se) {
        Vector<StringSearchResult> returnVector = new Vector<StringSearchResult>();
        String eText = org.exmaralda.exakt.utilities.FileIO.getPlainText(element);
        if (se instanceof RegularSearchExpression){
            RegularSearchExpression searchExpression = (RegularSearchExpression)se;
            Pattern pattern = (Pattern)(searchExpression.getSearchExpressionObject());
            Matcher matcher = pattern.matcher(eText);
            matcher.reset();
            while (matcher.find()){
                int start = matcher.start();
                int end = matcher.end();
                StringSearchResult esr = new StringSearchResult(eText, start, end, getDocumentID(), getID());
                returnVector.addElement(esr);
            }
            StringSearchResult[] returnArray = new StringSearchResult[returnVector.size()];
            returnArray = returnVector.toArray(returnArray);
            /*for (int pos=0; pos<returnVector.size(); pos++){
                returnArray[pos] = returnVector.elementAt(pos);
            }*/
            return returnArray;            
        } else {
            return null;
        }
    }
    
    public String getDocumentID(){
        return documentID;
    }

    public void setDocumentID(String dID){
        documentID = dID;
    }
    
    public String getID() {                
        try {
            Attribute idAttribute = (Attribute)(xpathToID.selectSingleNode(element));     
            if (idAttribute!=null){
                return idAttribute.getValue();
            } else {
                return element.getName();
            }
        } catch (JDOMException ex) {
            ex.printStackTrace();
            return "#NO ID";
        }
    }


    public String getElementIDAttributeName() {
        return elementIDAttributeName;
    }

    public void setElementIDAttributeName(String elementIDAttributeName) {
        this.elementIDAttributeName = elementIDAttributeName;
        try {
            xpathToID = XPath.newInstance(elementIDAttributeName);
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
    }
    
}
