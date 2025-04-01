/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.annotation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;

/**
 *
 * @author thomas
 */
public class AnnotationSpecification {
    
    public static String[][] BUILT_IN_ANNOTATION_SPECIFICATIONS = {
        {"HaMaTac : Disfluency", "/org/exmaralda/partitureditor/annotation/disfluency-annotation-panel.xml"},
        {"Dulko : Annotation", "/org/exmaralda/partitureditor/annotation/dulko-annotation-panel.xml"},
        {"FOLK : STTS 2.0 POS", "/org/exmaralda/partitureditor/annotation/folk_stts_2_0-annotation-panel.xml"},
        {"SegCor : Macro syntax", "/org/exmaralda/partitureditor/annotation/segcor-macrosyntax-annotation-panel.xml"},
        {"English : Penn POS Tags", "/org/exmaralda/partitureditor/annotation/PENN_1_0.xml"},
        {"French : POS Tags", "/org/exmaralda/partitureditor/annotation/FRENCH_1_0.xml"},
        {"Italian : POS Tags", "/org/exmaralda/partitureditor/annotation/ITALIAN_1_0.xml"},
        {"Universal Dependencies : POS Tags", "/org/exmaralda/partitureditor/annotation/UD_POS_1_0.xml"}
    };

    Map<String,Category> annotationSets = new HashMap<>();
    List<String> exmaraldaTierCategories = new ArrayList<>();
    Map<String,String> dependencies = new HashMap<>();
    Map<String,String> keyboardShortcuts = new HashMap<>();
    
    Document annotationSpecificationDocument;

    public Document getAnnotationSpecificationDocument() {
        return annotationSpecificationDocument;
    }

    public void setAnnotationSpecificationDocument(Document annotationSpecificationDocument) {
        init(annotationSpecificationDocument);
    }

    
    
    public Map<String, String> getKeyboardShortcuts() {
        return keyboardShortcuts;
    }
    
    public void read(File f) throws JDOMException, IOException{
        Document doc = FileIO.readDocumentFromLocalFile(f);
        init(doc);
    }

    public void read(String pathToResource) throws JDOMException, IOException{
        Document doc = new IOUtilities().readDocumentFromResource(pathToResource);
        init(doc);
    }
    
    /*
    <annotation-specification>
      <annotation-set exmaralda-tier-category="POS">
            <category name="Apellativa">
                <tag name="NN"/>
                <description>Tisch, Herr, [das] Reisen</description>
            </category>
            <category name="Eigennamen">
                <tag name="NE"/>
                <description>Hans, Hamburg, HSV</description>
            </category>
      </annotation-set>
    </annotation-specification>
    
    */
    // issue #295
    public void build(List<String> tagList, String tierCategory){
        Document doc = new Document(new Element("annotation-specification"));
        Element annotationSetElement = new Element("annotation-set");
        annotationSetElement.setAttribute("exmaralda-tier-category", tierCategory);
        doc.getRootElement().addContent(annotationSetElement);
        for (String tag : tagList){
            Element categoryElement = new Element("category");
            annotationSetElement.addContent(categoryElement);
            categoryElement.setAttribute("name", tag);
            Element tagElement = new Element("tag");
            categoryElement.addContent(tagElement);
            tagElement.setAttribute("name", tag);
            categoryElement.addContent(new Element("description").setText("to do"));
        }
        init(doc);
    }


    public Category getAnnotationSet(String name) {
        return annotationSets.get(name);
    }

    private void init(Document doc) {
        annotationSpecificationDocument = doc;
        annotationSets.clear();
        exmaraldaTierCategories.clear();
        for (Object o : doc.getRootElement().getChildren("annotation-set")){
            Element annotationSetElement = (Element)o;
            String name = annotationSetElement.getAttributeValue("exmaralda-tier-category");
            Category c = new Category(annotationSetElement);
            annotationSets.put(name, c);
            exmaraldaTierCategories.add(name);

            if (c.dependson!=null){
                dependencies.put(name, c.dependson);
                c.indexDependencies();
            }
        }
        Iterator i = doc.getRootElement().getDescendants(new ElementFilter("tag"));
        while (i.hasNext()){
            Element tag = (Element)(i.next());
            String keyboardShortcut = tag.getAttributeValue("keyboard-shortcut");
            if (keyboardShortcut!=null){
                keyboardShortcuts.put(keyboardShortcut, tag.getAttributeValue("name"));
            }
        }
    }

}
