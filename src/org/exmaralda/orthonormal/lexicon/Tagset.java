/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.lexicon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.orthonormal.utilities.PreferencesUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public class Tagset {

    static String DEFAULT_STTS = "/org/exmaralda/orthonormal/lexicon/STTS_Gespraech.xml";
    Document annotationSpecificationDocument;
    DefaultComboBoxModel comboBoxModel;
    HashMap<String,String> tags2categories = new HashMap<String,String>();
    HashMap<String,String> tags2descriptions = new HashMap<String,String>();
    HashMap<String,String> tags2parentTags = new HashMap<String,String>();
            
    public Tagset() throws JDOMException, IOException {
        String type = PreferencesUtilities.getProperty("tagset-type", "xml");
        String xmlPath = PreferencesUtilities.getProperty("tagset-path", null);
        if (("xml".equals(type)) || (xmlPath==null)){
            java.io.InputStream is = getClass().getResourceAsStream(DEFAULT_STTS);
            annotationSpecificationDocument = FileIO.readDocumentFromInputStream(is, false);
        } else {
            annotationSpecificationDocument = FileIO.readDocumentFromLocalFile(xmlPath);
        }        
        comboBoxModel = new DefaultComboBoxModel();
        List l = XPath.newInstance("//category[tag]").selectNodes(annotationSpecificationDocument);
        for (Object o : l){
            comboBoxModel.addElement(o);
            Element e = (Element)o;
            String tag = e.getChild("tag").getAttributeValue("name");
            //comboBoxModel.addElement(tag);
            String category = e.getAttributeValue("name");            
            String description =  e.getChildText("description");  
            if (e.getParentElement()!=null && e.getParentElement().getChild("tag")!=null){
                String parentTag = e.getParentElement().getChild("tag").getAttributeValue("name");
                tags2parentTags.put(tag, parentTag);
                //System.out.println(tag +" --> "+ parentTag);
            }
            tags2categories.put(tag, category);
            tags2descriptions.put(tag, description);
        }        
    }
    
    public String getCategory(String tag){
        if (tags2categories.containsKey(tag)){
            return tags2categories.get(tag);
        }
        return "---";
    }
    
    public String getDescription(String tag){
        if (tags2descriptions.containsKey(tag)){
            return tags2descriptions.get(tag);
        }
        return "---";
    }
    
    public String getParentTag(String tag){
        if (tags2parentTags.containsKey(tag)){
            return tags2parentTags.get(tag);
        }
        return tag;        
    }

    public ComboBoxModel getComboBoxModel(){
        return comboBoxModel;
    }
    
    public ArrayList<String> getTagList(){
        ArrayList<String> result = new ArrayList<String>();
        result.addAll(tags2categories.keySet());
        return result;
    }
    
    
}
