/*
 * SimpleSearchResult.java
 *
 * Created on 8. Januar 2007, 13:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.Attribute;
import java.util.*;
import java.net.*;
import java.io.*;

/**
 *
 * @author thomas
 */
public class SimpleSearchResult implements SearchResultInterface {
    
    // in case the context limit leads to a modification of the string, it may be necessary
    // to refer to the original match start, e.g. in order to find out in which child element
    // the match was found
    int originalMatchStart;
    int matchStart;
    int matchEnd;
    String text;
    SearchableSegmentLocatorInterface segmentLocator;
    String[] additionalData;
    boolean selected = true;
    
    /** Creates a new instance of SimpleSearchResult */
    public SimpleSearchResult(){
    }
    
    /** Creates a new instance of SimpleSearchResult */
    public SimpleSearchResult(  String t, int ms, int me, 
                                int contextLimit, 
                                SearchableSegmentLocatorInterface ssli, String[][] ad) {
        init(t,ms,me,contextLimit,ssli,ad);
    }
    
    public void init(String t, int ms, int me, 
                                int contextLimit, 
                                SearchableSegmentLocatorInterface ssli, String[][] ad) {
        setOriginalMatchStart(ms);
        matchStart = ms;
        matchEnd = me;
        text = t;
        if (contextLimit>=0){
            if (getLeftContextAsString().length() > contextLimit){                
                text = text.substring(matchStart-contextLimit);
                matchEnd-=(matchStart-contextLimit);
                matchStart = contextLimit;
            }
            if (getRightContextAsString().length() > contextLimit){
                text = text.substring(0,matchEnd+contextLimit);
            }
        }
        segmentLocator = ssli;
        int index = 0;
        additionalData = new String[ad.length];
        for (String[] a : ad){
            additionalData[index] = a[1];
            index++;
        }
    }

    public SimpleSearchResult(Element element, String baseURI){
        this(element);
        //System.out.println("BaseURI is " + baseURI);
        try {
            URI uri2 = new URI(baseURI);
            Element locator = element.getChild("locator");
            String fl = locator.getAttributeValue("file");
            URI absoluteURI = uri2.resolve(fl);
            //System.out.println("Resolved path is " + new File(absoluteURI).getAbsolutePath());
            String resolvedPath = "";
            if (baseURI.startsWith("http")){
                resolvedPath = absoluteURI.toString();
            } else {
                resolvedPath = new File(absoluteURI).getAbsolutePath();
            }
            ((XMLSearchableSegmentLocator)getSearchableSegmentLocator()).setCorpusComponentLocator(resolvedPath);
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }        
    }

    public SimpleSearchResult(Element element){
        Attribute sel = element.getAttribute("selected");
        if (sel!=null){
            try {
                setSelected(sel.getBooleanValue());
            } catch (DataConversionException ex) {
                ex.printStackTrace();
            }
        }
        
        Element locator = element.getChild("locator");
        String fl = locator.getAttributeValue("file");
        String sl = locator.getAttributeValue("xpath");
        segmentLocator = new XMLSearchableSegmentLocator(fl, sl);
        
        String leftContext = element.getChild("left-context").getText();
        String matchText = element.getChild("match").getText();
        String rightContext = element.getChild("right-context").getText();
        
        text = leftContext + matchText + rightContext;
        matchStart = leftContext.length();
        matchEnd = matchStart + matchText.length();
        
        Attribute oms = element.getChild("match").getAttribute("original-match-start");        
        originalMatchStart = leftContext.length();
        try {
            if (oms!=null){originalMatchStart = oms.getIntValue();}
        } catch (DataConversionException ex) {
            ex.printStackTrace();
        }
        
        List l = element.getChildren("data");
        additionalData = new String[l.size()];
        int count = 0;
        for (Object o : l){
            Element d = (Element)o;
            additionalData[count] = d.getText();
            count++;
        }
    }

    @Override
    public String[] getAdditionalData(){
        return additionalData;
    }
    
    @Override
    public SearchableSegmentLocatorInterface getSearchableSegmentLocator() {
        return segmentLocator;
    }

    @Override
    public String getLeftContextAsString() {
        return text.substring(0,matchStart);
    }

    @Override
    public String getMatchTextAsString() {
        return text.substring(matchStart, matchEnd);
    }

    @Override
    public String getRightContextAsString() {
        return text.substring(matchEnd);
    }

        
    @Override
    public Element toXML() {
        Element returnValue = new Element("search-result");
        returnValue.setAttribute("selected", Boolean.toString(isSelected()));
        Element lt = new Element("left-context");
        lt.setText(getLeftContextAsString());
        Element mt = new Element("match");
        mt.setAttribute("original-match-start", Integer.toString(originalMatchStart));
        mt.setText(getMatchTextAsString());
        Element rt = new Element("right-context");
        rt.setText(getRightContextAsString());
        returnValue.addContent(segmentLocator.toXML());
        returnValue.addContent(lt);
        returnValue.addContent(mt);
        returnValue.addContent(rt);
        for(String a : additionalData){
            Element ad = new Element("data");
            ad.setText(a);
            returnValue.addContent(ad);
        }
        return returnValue;
    }
    
    @Override
    public Element toXML(String baseDirectory) {
        Element returnValue = toXML();
        Element l = returnValue.getChild("locator");
        String fullPath = l.getAttributeValue("file");
        //System.out.println("Base directory is " + baseDirectory);
        try {
            URI uri1 = null;
            if (!(fullPath.startsWith("http"))){
                uri1 = new File(fullPath).toURI();
            } else {
                 uri1 = new URI(fullPath);
            }
            URI uri2 = null;
            try {
                uri2 = new URI(baseDirectory);
            } catch (URISyntaxException use){
                uri2 = new File(baseDirectory).toURI();
            }
            //System.out.println("relativizing" + uri1.toString() + " wrt " + uri2.toString());
            URI relativeURI = uri2.relativize(uri1);        
            String relativePath = relativeURI.toString();
            //System.out.println("resulting path: " + relativePath);
            l.setAttribute("file", relativePath);
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        return returnValue;
    }
    
    
    @Override
    public boolean isSelected() {
        return selected;
    }
    
    @Override
    public void setSelected(boolean isSelected){
        selected = isSelected;
    }

    public int getOriginalMatchStart() {
        return originalMatchStart;
    }

    public void setOriginalMatchStart(int originalMatchStart) {
        this.originalMatchStart = originalMatchStart;
    }

    @Override
    public String toHTML() {
        StringBuilder text = new StringBuilder();
        text.append(getLeftContextAsString());
        text.append("<b>" + getMatchTextAsString() + "</b>");
        text.append(getRightContextAsString() + "<br/>");                
        return text.toString();
    }

    @Override
    public void setAdditionalData(int index, String value) {
        if (index>=additionalData.length){
            String[] newAdditionalData = new String[index+1];
            System.arraycopy(additionalData,0,newAdditionalData,0,additionalData.length);            
            additionalData = newAdditionalData;
        } 
        additionalData[index] = value;
    }
    
    @Override
    public void setAdditionalData(String[] newAdditionalData) {
        additionalData = newAdditionalData;
    }
    

    @Override
    public String getKWICAsHTML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<span style=\"color:rgb(100,100,100)\">");
        sb.append(getLeftContextAsString());
        sb.append("</span>");
        sb.append("<span style=\"color:red;font-weight:bold\">");
        sb.append(getMatchTextAsString());
        sb.append("</span>");
        sb.append("<span style=\"color:rgb(100,100,100)\">");
        sb.append(getRightContextAsString());
        sb.append("</span>");
        return sb.toString();
    }


    
    
    
    
}
