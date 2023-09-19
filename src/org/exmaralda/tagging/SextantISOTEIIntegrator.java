/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class SextantISOTEIIntegrator {
    
    Document isoteiTranscription;
    Namespace xlinkNS;
    Map<String, Element> ids2elements = new HashMap<>();
    
    Map<String, String> annotationCategoryMappings = new HashMap<>();
    
    public SextantISOTEIIntegrator(String fln) throws JDOMException, IOException {
        xlinkNS = Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");
        isoteiTranscription = FileIO.readDocumentFromLocalFile(new File(fln));
        List l = XPath.newInstance("//*[@xml:id]").selectNodes(isoteiTranscription);
        for (Object o : l){
            Element e = (Element)o;
            //String id = e.getAttributeValue("xml:id");
            String id = e.getAttributeValue("id", Namespace.XML_NAMESPACE);
            //System.out.println("ID=" + id);
            ids2elements.put(id, e);
        }
    }
    
    public void addAnnotationCategoryMapping(String value, String replaceValue){
        annotationCategoryMappings.put(value, replaceValue);
    }
    
    public void integrate(String sd) throws JDOMException, IOException{
        Document sextantAnnotation = FileIO.readDocumentFromLocalFile(new File(sd));
        HashSet<String> annotationNames = new HashSet<>();
        List l = XPath.newInstance("//f//@name").selectNodes(sextantAnnotation);
        for (Object o : l){
            Attribute a = (Attribute)o;
            annotationNames.add(a.getValue());
        }

        /* <ann id="ann_0" xlink:href="#Seg_4">
            <fs>
                <f name="pos">
                    <symbol value="VVIMP"/>
                </f>
                <f name="lemma">
                    <symbol value="Segmentier"/>
                </f>
            </fs>
        </ann> */


        List standoffAnnotations = XPath.newInstance("//ann").selectNodes(sextantAnnotation);
        for (Object o : standoffAnnotations){
            Element ann = (Element)o;
            String id = ann.getAttributeValue("href", xlinkNS).substring(1);
            List fs = XPath.newInstance("descendant::f").selectNodes(ann);
            for (Object o2 : fs){
                Element f = (Element)o2;
                String annotationCategory = f.getAttributeValue("name");
                // new 20-08-2023
                annotationCategory = annotationCategoryMappings.getOrDefault(annotationCategory, annotationCategory);
                if ("p-pos".equals(annotationCategory)) continue;
                String annotationValue = f.getChild("symbol").getAttributeValue("value");
                Element referencedElement = ids2elements.get(id);
                if (referencedElement==null) {
                    System.out.println("No reference!");
                    continue;
                }
                String newValue = annotationValue;
                if (referencedElement.getAttribute(annotationCategory)!=null){
                    newValue = referencedElement.getAttributeValue(annotationCategory) + " " + annotationValue;
                }
                referencedElement.setAttribute(annotationCategory, newValue);
                //System.out.println(annotationCategory + " - " + newValue);
            }
        }

    }

    public void writeDocument(String filename) throws IOException{
        IOUtilities.writeDocumentToLocalFile(filename, isoteiTranscription);
    }

    
    
    
    
}
