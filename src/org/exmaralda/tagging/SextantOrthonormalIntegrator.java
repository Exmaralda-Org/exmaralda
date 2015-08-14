/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
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
public class SextantOrthonormalIntegrator {
    
    Document orthonormalTranscription;
    Namespace xlinkNS;
    Hashtable<String, Element> ids2elements = new Hashtable<String, Element>();
    
    public SextantOrthonormalIntegrator(String fln) throws JDOMException, IOException {
        xlinkNS = Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");
        orthonormalTranscription = FileIO.readDocumentFromLocalFile(new File(fln));
        List l = XPath.newInstance("//*[@id]").selectNodes(orthonormalTranscription);
        for (Object o : l){
            Element e = (Element)o;
            String id = e.getAttributeValue("id");
            ids2elements.put(id, e);
        }
    }
    
    public void integrate(String sd) throws JDOMException, IOException{
        Document sextantAnnotation = FileIO.readDocumentFromLocalFile(new File(sd));
        HashSet<String> annotationNames = new HashSet<String>();
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
                String annotationValue = f.getChild("symbol").getAttributeValue("value");
                Element referencedElement = ids2elements.get(id);
                if (referencedElement==null) continue;
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
        IOUtilities.writeDocumentToLocalFile(filename, orthonormalTranscription);
    }

    
    
    
    
}
