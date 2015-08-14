/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.annotation;

import java.util.Enumeration;
import java.util.Hashtable;
import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class Category extends javax.swing.tree.DefaultMutableTreeNode {

    String name;
    String tag;
    String description;
    String dependson;
    String keyboardShortcut;

    Hashtable<String,Category> dependencies;

    public Category(Element e){
        if (e.getName().equals("category")){
            name = e.getAttributeValue("name");
            dependson = e.getAttributeValue("depends-on");
            Element tagElement = e.getChild("tag");
            if (tagElement!=null){
                tag = tagElement.getAttributeValue("name");
                keyboardShortcut = tagElement.getAttributeValue("keyboard-shortcut");
            }
            description = e.getChildText("description");
        } else if (e.getName().equals("annotation-set")){
            name = e.getAttributeValue("exmaralda-tier-category");
            dependson = e.getAttributeValue("depends-on");
        }
        for (Object o : e.getChildren("category")){
            Element childCategoryElement = (Element)o;
            Category childCategory = new Category(childCategoryElement);
            add(childCategory);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    Category findMatchingCategory(String parentTag) {
        if (dependencies!=null){
            return dependencies.get(parentTag);
        }
        return null;
    }

    void indexDependencies() {
        dependencies = new Hashtable<String,Category>();
        for (Enumeration e=this.depthFirstEnumeration(); e.hasMoreElements(); ) {
            Category c = (Category)e.nextElement();
            if (c.dependson!=null){
                dependencies.put(c.dependson, c);
            }
        }
    }


}
