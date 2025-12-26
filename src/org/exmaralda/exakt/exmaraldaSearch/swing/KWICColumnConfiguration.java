/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.exakt.exmaraldaSearch.swing;

import org.exmaralda.common.jdomutilities.IOUtilities;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author bernd
 */
public class KWICColumnConfiguration {
    
    public String getXML(){
        return IOUtilities.documentToString(getDocument());
    }
    
    public Document getDocument(){
     Element root = new Element("kwic-column-configuration");
     Document document = new Document(root);
     return document;
    }
    
}
