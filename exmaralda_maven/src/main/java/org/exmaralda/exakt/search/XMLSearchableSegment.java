/*
 * XMLSearchableSegment.java
 *
 * Created on 8. Januar 2007, 12:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

import java.util.*;
import org.jdom.*;
import org.jdom.filter.*;
import org.jdom.xpath.*;
import java.util.regex.*;

/**
 *
 * @author thomas
 */
public class XMLSearchableSegment implements SearchableSegmentInterface{
    
    private Element searchableElement;
    XMLSearchableSegmentLocator segmentLocator;
    String[][] additionalData;
    
    /** Creates a new instance of XMLSearchableSegment */
    public XMLSearchableSegment(Element e, XMLSearchableSegmentLocator xssl) {
        searchableElement = e;
        segmentLocator = xssl;
    }

    private void initMetaData(SearchParametersInterface sp){
        Object[][] adls = (Object[][])(sp.getAdditionalDataLocators());
        additionalData = new String[adls.length][2];
        int index = 0;
        for (Object[] a : adls){
            String attribute = (String)(a[0]);
            XPath xpath = (XPath)(a[1]);
            String value = null;
            try {
                Object o = xpath.selectSingleNode(searchableElement);
                if (o instanceof Attribute){
                  value = ((Attribute)o).getValue();
                } else if (o instanceof Element){
                  value = ((Element)o).getText();
                } else if (o==null){
                  value = "#undefined";
                } 
            } catch (JDOMException ex) {
                ex.printStackTrace();
            }
            additionalData[index][0] = attribute;
            additionalData[index][1] = value;
            index++;
        }
    }
    
    
    private String[][] getAdditionalData(){
        return additionalData;
    }
    
    public SearchResultList search(SearchParametersInterface sp) {
        if (sp instanceof RegularExpressionSearchParameters){
            RegularExpressionSearchParameters searchParameters = (RegularExpressionSearchParameters)sp;
            SearchResultList returnValue = new SearchResultList();
            String searchString = getSearchString();
            Matcher matcher = searchParameters.getPattern().matcher(searchString);
            matcher.reset();
            while (matcher.find()){
                if (getAdditionalData()==null){
                    initMetaData(sp);
                }
                if (searchParameters.getSearchType()==SearchParametersInterface.DEFAULT_SEARCH){
                    SimpleSearchResult ssr = new SimpleSearchResult(searchString, matcher.start(), matcher.end(), 
                                                        sp.getContextLimit(), segmentLocator, getAdditionalData());
                    returnValue.addSearchResult(ssr);
                } else if (searchParameters.getSearchType()==SearchParametersInterface.ANNOTATION_SEARCH){
                    try {
                        // ***** NEW
                        String annotationStart = searchableElement.getAttributeValue("s");
                        String annotationEnd = searchableElement.getAttributeValue("e");
                        String annotationRefID = searchableElement.getAttributeValue("ref-id");
                        
                        //System.out.println("Trying to find a match for " + annotationStart + " / " + annotationEnd);
                        // change to account for new annotations from tagging, 17-05-2011
                        //String segmentationName = "SpeakerContribution_Event";
                        //String xpstring = "../../segmentation[@name='" + segmentationName + "']/ts/ts[@s='" + annotationStart + "' and @e='" + annotationEnd + "']";
                        String xpstring = "../../segmentation/descendant::ts[@s='" + annotationStart + "' and @e='" + annotationEnd + "']";
                        if (annotationRefID!=null){
                            xpstring = "../../segmentation/descendant::ts[@id='" + annotationRefID + "']";
                        }
                        //System.out.println(xpstring);
                        XPath xp = XPath.newInstance(xpstring);
                        Element annotatedElement = (Element)(xp.selectSingleNode(searchableElement));
                        if (annotatedElement!=null){
                            //System.out.println("Found one: " + annotatedElement.getAttributeValue("id"));
                            AnnotationSearchResult asr = new AnnotationSearchResult(searchString, matcher.start(), matcher.end(),
                                                            sp.getContextLimit(), segmentLocator, getAdditionalData(), annotatedElement);
                            returnValue.addSearchResult(asr);
                        } else {
                            //System.out.println("Found none :-(");

                            // TODO!!!
                            // Here lies a rabbit in the pepper...
                            // if the annotation refers to a sequence of, rather than a single annotated element
                            // nothing is found
                            String xpstring2 = "../../segmentation[@name='SpeakerContribution_Event']/ts/ts[@s='" + annotationStart + "']";
                            //System.out.println(xpstring2);
                            XPath xp2 = XPath.newInstance(xpstring2);
                            List aes = xp2.selectNodes(searchableElement);
                            boolean found = false;
                            Vector<Element> allAnnotated = null;
                            for (Object o : aes){
                                if (!(o instanceof Element)) continue;
                                Element ae = (Element)o;
                                Element parentSC = ae.getParentElement();
                                int indexInParent = parentSC.indexOf(ae);
                                allAnnotated = new Vector<Element>();
                                allAnnotated.add(ae);
                                for (int i=indexInParent+1; i<parentSC.getContentSize(); i++){
                                    if (!(parentSC.getContent(i) instanceof Element)) continue;
                                    Element nextElement = (Element)(parentSC.getContent(i));
                                    allAnnotated.add(nextElement);
                                    Attribute end = nextElement.getAttribute("e");
                                    if ((end!=null) && (end.getValue().equals(annotationEnd))){
                                        //i.e. this the last segment of the annotated chain
                                        found=true;
                                        break;
                                    }
                                }
                                if (found) break;
                            }
                            if (found){
                                Element[] annotatedElements = allAnnotated.toArray(new Element[1]);
                                AnnotationSearchResult asr = new AnnotationSearchResult(searchString, matcher.start(), matcher.end(),
                                                                sp.getContextLimit(), segmentLocator, getAdditionalData(), annotatedElements);
                                returnValue.addSearchResult(asr);
                            }
                        } 
                    } catch (JDOMException ex) {
                        ex.printStackTrace();
                    }
                }
            }                    
            return returnValue;
        } else if (sp instanceof XPathSearchParameters){
            XPathSearchParameters searchParameters = (XPathSearchParameters)sp;
            SearchResultList returnValue = new SearchResultList();
            ElementFilter elementFilter = new ElementFilter();
            try {
                List l = searchParameters.getXPath().selectNodes(searchableElement);
                Iterator i = searchableElement.getDescendants(elementFilter);
                String allText = getSearchString();
                int start = 0;
                while (i.hasNext()){                        
                    if (getAdditionalData()==null){initMetaData(sp);}
                    Element e = (Element)(i.next());
                    String currentText = org.exmaralda.exakt.utilities.FileIO.getPlainText(e);
                    if (l.contains(e)){
                        SimpleSearchResult ssr = new SimpleSearchResult(allText, start, start+currentText.length(), 
                                                sp.getContextLimit(), segmentLocator, getAdditionalData());
                        returnValue.addSearchResult(ssr);
                    }
                    if (e.getChildren().size()==0){
                        start+=currentText.length();
                    }
                }
            } catch (JDOMException ex) {
                ex.printStackTrace();
            }
            return returnValue;
        }
        return null;
    }    
    
    public String getSearchString(){
        return org.exmaralda.exakt.utilities.FileIO.getPlainText(getSearchableElement());
    }

    public Element getSearchableElement() {
        return searchableElement;
    }
    
}

