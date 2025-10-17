/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.partitureditor.annotation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.exmaralda.common.corpusbuild.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author bernd
 */
public class UDPOSMapping extends HashMap<String, String> {

    
    public enum TagSet {PENN_1_0, FRENCH_1_0, ITALIAN_1_0, STTS_2_0};
    
    
    public UDPOSMapping(TagSet tagSet) throws JDOMException, IOException{
        this(UDPOSMapping.getInternalPathForTagSet(tagSet));
    }
    
    public UDPOSMapping(File xmlFile) throws JDOMException, IOException{
        Document mappingDocument = FileIO.readDocumentFromLocalFile(xmlFile.getAbsolutePath());
        init(mappingDocument);        
    }
    
    public UDPOSMapping(String internalPath) throws JDOMException, IOException{
        java.io.InputStream is = getClass().getResourceAsStream(internalPath);
        Document mappingDocument = FileIO.readDocumentFromInputStream(is, false);
        init(mappingDocument);
    }
    
    private void init(Document mappingDocument) throws JDOMException {
        // <tag name="N" ud-mapping="NOUN"/>
        List tags = XPath.selectNodes(mappingDocument, "//tag[@name and @ud-mapping]");
        for (Object o : tags){
            Element tag = (Element)o;
            put(tag.getAttributeValue("name"), tag.getAttributeValue("ud-mapping"));
        }

    }

    public static String getInternalPathForTagSet(TagSet tagSet){
        switch (tagSet){
            case PENN_1_0 : 
                return "/org/exmaralda/partitureditor/annotation/PENN_1_0.xml";
            case FRENCH_1_0 : 
                return "/org/exmaralda/partitureditor/annotation/FRENCH_1_0.xml";
            case ITALIAN_1_0 : 
                return "/org/exmaralda/partitureditor/annotation/ITALIAN_1_0.xml";
            case STTS_2_0 : 
                return "/org/exmaralda/partitureditor/annotation/stts_2_0-annotation-panel.xml";
        }
        return "";
    }
}
