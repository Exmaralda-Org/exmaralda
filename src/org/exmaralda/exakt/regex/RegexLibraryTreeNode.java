/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.regex;

import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class RegexLibraryTreeNode extends javax.swing.tree.DefaultMutableTreeNode {

    Element element;

    public RegexLibraryTreeNode(Element e){
        element = e;
        if (e.getName().equals("super-library")){
            for (Object o : e.getChildren("regex-library")){
                Element childCategoryElement = (Element)o;
                RegexLibraryTreeNode childCategory = new RegexLibraryTreeNode(childCategoryElement);
                add(childCategory);
            }
        } else if (e.getName().equals("regex-library")){
            if (e.getChildren("folder").size()>0){
                for (Object o : e.getChildren("folder")){
                    Element childCategoryElement = (Element)o;
                    RegexLibraryTreeNode childCategory = new RegexLibraryTreeNode(childCategoryElement);
                    add(childCategory);
                }
            } else {
                for (Object o : e.getChildren("entry")){
                    Element childCategoryElement = (Element)o;
                    RegexLibraryTreeNode childCategory = new RegexLibraryTreeNode(childCategoryElement);
                    add(childCategory);
                }                                            
            }
        } else if (e.getName().equals("folder")){
            if (e.getChildren("folder").size()>0){
                for (Object o : e.getChildren("folder")){
                    Element childCategoryElement = (Element)o;
                    RegexLibraryTreeNode childCategory = new RegexLibraryTreeNode(childCategoryElement);
                    add(childCategory);
                }                            
            } else {
                for (Object o : e.getChildren("entry")){
                    Element childCategoryElement = (Element)o;
                    RegexLibraryTreeNode childCategory = new RegexLibraryTreeNode(childCategoryElement);
                    add(childCategory);
                }                                            
            }
        }
    }

    public String getDescription() {
        return element.getAttributeValue("name");
    }

    public String getName() {
        return element.getName();
    }

    public Element getElement(){
        return element;
    }



}
