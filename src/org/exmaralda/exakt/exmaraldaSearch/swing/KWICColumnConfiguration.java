/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.exakt.exmaraldaSearch.swing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author bernd
 */
public class KWICColumnConfiguration {
    
    List<KWICColumnConfigurationEntry> columns = new ArrayList<>();
        
    public static KWICColumnConfiguration readFromFile(File file) throws JDOMException, IOException{
        Document doc = FileIO.readDocumentFromLocalFile(file);
        KWICColumnConfiguration columnConfiguration = new KWICColumnConfiguration();
        List columns = XPath.selectNodes(doc, "//column");
        for (Object o : columns){
            Element column = (Element)o;
            KWICColumnConfigurationEntry addColumn = columnConfiguration.addColumn(
                    column.getAttributeValue("name"),
                    column.getAttributeValue("class"), 
                    column.getAttributeValue("type")
            );
            List categories = XPath.selectNodes(column, "descendant::category");
            if (categories!=null){
                String[] catArray = new String[categories.size()];
                int i=0;
                for (Object o2 : categories){
                    Element category = (Element)o2;
                    catArray[i] = category.getText();
                    i++;
                }
                addColumn.values = catArray;
            }
        }
        return columnConfiguration;
    }
    
    public List<KWICColumnConfigurationEntry> getEntries(){
        return columns;
    }
    
    public String getXML(){
        return IOUtilities.documentToString(getDocument());
    }
    
    public Document getDocument(){
        Element root = new Element("kwic-column-configuration");
        Document document = new Document(root);
        for (KWICColumnConfigurationEntry column : columns){
            Element entry = new Element("column");
            entry.setAttribute("name", column.name);
            entry.setAttribute("class", column.cl);
            entry.setAttribute("type", column.type);
            root.addContent(entry);
            if (column.values!=null){
                for (String value : column.values){
                    Element cat = new Element("category");
                    cat.setText(value);
                    entry.addContent(cat);
                }
            }
        }
        return document;
    }

    public KWICColumnConfigurationEntry addColumn(String name, String cl, String type) {
        KWICColumnConfigurationEntry entry = new KWICColumnConfigurationEntry(name, cl, type);
        columns.add(entry);
        return entry;
    }
    
    public KWICColumnConfigurationEntry addColumn(String name, String cl, String type, String[] categories) {
        KWICColumnConfigurationEntry entry = new KWICColumnConfigurationEntry(name, cl, type);
        entry.values = categories;
        columns.add(entry);
        return entry;
    }
    
    
}
