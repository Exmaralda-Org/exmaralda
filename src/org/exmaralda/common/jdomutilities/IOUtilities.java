/*
 * IOUtilities.java
 *
 * Created on 12. Oktober 2006, 12:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.jdomutilities;

import org.jdom.*;
import org.jdom.output.*;
import org.jdom.input.*;
import java.io.*;
import java.net.URL;

/**
 *
 * @author thomas
 */
public class IOUtilities {

    
    /** Creates a new instance of IOUtilities */
    public IOUtilities() {
    }
    
    public static Document readDocumentFromLocalFile(String path) throws JDOMException, IOException {
        File file = new File(path);
        SAXBuilder saxBuilder = new SAXBuilder();
        Document doc = saxBuilder.build(file);        
        return doc;
    }

    public static Document readDocumentFromURL(URL url) throws JDOMException, IOException{
        SAXBuilder saxBuilder = new SAXBuilder();
        Document doc = saxBuilder.build(url);
        return doc;
    }
    
    public static Document readDocumentFromString(String docString) throws JDOMException, IOException{
        SAXBuilder saxBuilder = new SAXBuilder();
        java.io.StringReader sr = new java.io.StringReader(docString);
        Document doc = saxBuilder.build(sr);        
        return doc;        
    }
    
    public static Element readElementFromString(String elementString) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        java.io.StringReader sr = new java.io.StringReader(elementString);
        Document doc = saxBuilder.build(sr);        
        return doc.detachRootElement();        
    }
    
    
    public Document readDocumentFromResource(String pathToResource) throws JDOMException, IOException{
        SAXBuilder saxBuilder = new SAXBuilder();
        java.io.InputStream is = getClass().getResourceAsStream(pathToResource);
        Document doc = saxBuilder.build(is);
        return doc;
    }

    public static void writeDocumentToLocalFile(String pathToDocument, Document document) throws IOException{
        XMLOutputter xmlOutputter = new XMLOutputter();
        //String docString = xmlOutputter.outputString(document);        
        FileOutputStream fos = new FileOutputStream(new File(pathToDocument));        
        xmlOutputter.output(document,fos);
        //fos.write(docString.getBytes("UTF-8"));
        fos.close();    
    }

    public static void writeDocumentToLocalFile(String pathToDocument, Document document, boolean omitXMLDeclaration) throws IOException{
        XMLOutputter xmlOutputter = new XMLOutputter();
        FileOutputStream fos = new FileOutputStream(new File(pathToDocument));
        xmlOutputter.setFormat(xmlOutputter.getFormat().setOmitDeclaration(omitXMLDeclaration));

        xmlOutputter.output(document,fos);
        fos.close();
    }


    public static String documentToString(Document document){
        XMLOutputter xmlOutputter = new XMLOutputter();
        return xmlOutputter.outputString(document);
    }

    public static String documentToString(Document document, boolean omitXMLDeclaration){
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(xmlOutputter.getFormat().setOmitDeclaration(omitXMLDeclaration));
        return xmlOutputter.outputString(document);
    }

    public static String elementToString(Element element){
        return elementToString(element, false);
    }
    public static String elementToString(Element element, boolean prettyPrint){
        XMLOutputter xmlOutputter = new XMLOutputter();
        if (prettyPrint) {
            xmlOutputter.setFormat(Format.getPrettyFormat());
        }
        return xmlOutputter.outputString(element);
    }
    
    public static void schemaValidateLocalFile(File file, String schemaLocation) throws JDOMException, IOException{
        SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser", true);
        String schemaLocString = new File(schemaLocation).toURI().toURL().toString();
        saxBuilder.setFeature("http://apache.org/xml/features/validation/schema", true);
        saxBuilder.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation",
                schemaLocString);
        saxBuilder.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
                schemaLocString);
        Document doc = saxBuilder.build(file);                
    }


    
    
}
