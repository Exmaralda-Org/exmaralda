/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.annotation;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
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

    Hashtable<String,Category> annotationSets = new Hashtable<String,Category>();
    Vector<String> exmaraldaTierCategories = new Vector<String>();
    Hashtable<String,String> dependencies = new Hashtable<String,String>();
    Hashtable<String,String> keyboardShortcuts = new Hashtable<String, String>();

    public Hashtable<String, String> getKeyboardShortcuts() {
        return keyboardShortcuts;
    }
    
    public void read(File f) throws JDOMException, IOException{
        Document doc = FileIO.readDocumentFromLocalFile(f);
        annotationSets.clear();
        exmaraldaTierCategories.clear();
        for (Object o : doc.getRootElement().getChildren("annotation-set")){
            Element annotationSetElement = (Element)o;
            String name = annotationSetElement.getAttributeValue("exmaralda-tier-category");
            Category c = new Category(annotationSetElement);
            annotationSets.put(name, c);
            exmaraldaTierCategories.addElement(name);

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

    public Category getAnnotationSet(String name) {
        return annotationSets.get(name);
    }

}
