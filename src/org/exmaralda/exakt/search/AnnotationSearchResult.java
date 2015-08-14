/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.exakt.search;

import org.jdom.*;

/**
 *
 * @author thomas
 */
public class AnnotationSearchResult extends SimpleSearchResult {

    public AnnotationSearchResult(){
    }
    
    public AnnotationSearchResult(Element element, String baseURI){
        super(element, baseURI);
    }
    

    public AnnotationSearchResult(String t, int ms, int me,
            int contextLimit,
            SearchableSegmentLocatorInterface ssli, String[][] ad,
            Element annotatedElement) {
        Element parentElement = annotatedElement.getParentElement();
        //System.out.println("*** Parent element " + parentElement.getAttributeValue("n") + " " + parentElement.toString());

        int index = parentElement.indexOf(annotatedElement);
        String matchText = org.exmaralda.exakt.utilities.FileIO.getPlainText(annotatedElement);
        String leftContext = "";
        for (int pos = 0; pos < index; pos++) {
            Element e = (Element) (parentElement.getContent(pos));
            leftContext += org.exmaralda.exakt.utilities.FileIO.getPlainText(e);
        }
        String rightContext = "";
        for (int pos = index + 1; pos < parentElement.getChildren().size(); pos++) {
            Element e = (Element) (parentElement.getContent(pos));
            rightContext += org.exmaralda.exakt.utilities.FileIO.getPlainText(e);
        }

        String text = leftContext + matchText + rightContext;
        String[][] extendedAD = new String[ad.length + 1][2];
        for (int pos = 0; pos < ad.length; pos++) {
            extendedAD[pos] = ad[pos];
        }
        String[] newAD = {"", t.substring(ms, me)};
        extendedAD[ad.length] = newAD;
        super.init(text, leftContext.length(), leftContext.length() + matchText.length(), contextLimit, ssli, extendedAD);
    }

    public AnnotationSearchResult(String t, int ms, int me,
            int contextLimit,
            SearchableSegmentLocatorInterface ssli, String[][] ad,
            Element[] annotatedElements) {
        Element firstAnnotatedElement = annotatedElements[0];
        Element lastAnnotatedElement = annotatedElements[annotatedElements.length - 1];

        Element parentElement = firstAnnotatedElement.getParentElement();
        //System.out.println("Parent element " + parentElement.getAttributeValue("n") + " " + parentElement.toString());
        int index1 = parentElement.indexOf(firstAnnotatedElement);
        int index2 = parentElement.indexOf(lastAnnotatedElement);

        String matchText = "";
        for (Element ae : annotatedElements) {
            matchText += org.exmaralda.exakt.utilities.FileIO.getPlainText(ae);
        }

        String leftContext = "";
        for (int pos = 0; pos < index1; pos++) {
            if (!(parentElement.getContent(pos) instanceof Element)) {
                continue;
            }
            Element e = (Element) (parentElement.getContent(pos));
            leftContext += org.exmaralda.exakt.utilities.FileIO.getPlainText(e);
        }
        String rightContext = "";
        for (int pos = index2 + 1; pos < parentElement.getChildren().size(); pos++) {
            if (!(parentElement.getContent(pos) instanceof Element)) {
                continue;
            }
            Element e = (Element) (parentElement.getContent(pos));
            rightContext += org.exmaralda.exakt.utilities.FileIO.getPlainText(e);
        }

        String text = leftContext + matchText + rightContext;
        String[][] extendedAD = new String[ad.length + 1][2];
        for (int pos = 0; pos < ad.length; pos++) {
            extendedAD[pos] = ad[pos];
        }
        String[] newAD = {"", t.substring(ms, me)};
        extendedAD[ad.length] = newAD;
        super.init(text, leftContext.length(), leftContext.length() + matchText.length(), contextLimit, ssli, extendedAD);
    }
}
