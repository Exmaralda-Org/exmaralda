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
 * @author thomas
 */
public class SextantIntegrator {

    Document segmentedTranscription;
    Hashtable<String, Element> ids2elements = new Hashtable<String, Element>();
    Hashtable<String, String> ids2speakers = new Hashtable<String, String>();
    List tierList;
    Namespace xlinkNS;

    public SextantIntegrator(String st) throws JDOMException, IOException {
        xlinkNS = Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");
        segmentedTranscription = FileIO.readDocumentFromLocalFile(new File(st));
        List l = XPath.newInstance("//*[@id]").selectNodes(segmentedTranscription);
        for (Object o : l){
            Element e = (Element)o;
            String id = e.getAttributeValue("id");
            ids2elements.put(id, e);
        }
        tierList = XPath.newInstance("//segmented-tier[@speaker and @type='t']").selectNodes(segmentedTranscription);
        for (Object o : tierList){
            Element tier = (Element)o;
            String speaker = tier.getAttributeValue("speaker");
            List l2 = XPath.newInstance("descendant::*[@id]").selectNodes(tier);
            for (Object o2 : l2){
                Element e = (Element)o2;
                ids2speakers.put(e.getAttributeValue("id"), speaker);
            }
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
        // make an annotation tier for each speaker and each annotation category
        Hashtable<String, Element> speakersAndCategories2Annotations = new Hashtable<String, Element>();
        for (Object o : tierList){
            Element tier = (Element)o;
            String speaker = tier.getAttributeValue("speaker");
            for (String an : annotationNames){
                Element thisAnnotation = new Element("annotation");
                thisAnnotation.setAttribute("name", an);
                tier.addContent(thisAnnotation);
                speakersAndCategories2Annotations.put(speaker + "*****" + an, thisAnnotation);
            }
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
        int count=0;
        for (Object o : standoffAnnotations){
            Element ann = (Element)o;
            String id = ann.getAttributeValue("href", xlinkNS).substring(1);
            String speaker = ids2speakers.get(id);
            List fs = XPath.newInstance("descendant::f").selectNodes(ann);
            for (Object o2 : fs){
                Element f = (Element)o2;
                String category = f.getAttributeValue("name");
                Element annotation = speakersAndCategories2Annotations.get(speaker + "*****" + category);
                Element referencedElement = ids2elements.get(id);
                if (referencedElement==null) continue;
                String start = referencedElement.getAttributeValue("s");
                String end = referencedElement.getAttributeValue("e");
                if ((start==null) || (end==null)) continue;

                /* <ta id="Seg_32" s="T0" e="T1">eine annotation</ta>  */
                count++;
                Element ta = new Element("ta");
                ta.setAttribute("id", "SA_" + Integer.toString(count));
                ta.setAttribute("s", start);
                ta.setAttribute("e", end);
                ta.setAttribute("ref-id", id);
                ta.setText(f.getChild("symbol").getAttributeValue("value"));
                annotation.addContent(ta);

            }
        }

    }

    public void writeDocument(String filename) throws IOException{
        IOUtilities.writeDocumentToLocalFile(filename, segmentedTranscription);
    }




}
