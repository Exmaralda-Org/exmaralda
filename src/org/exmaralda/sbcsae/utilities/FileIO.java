package org.exmaralda.sbcsae.utilities;
/*
 * FileReader.java
 *
 * Created on 27. Mai 2005, 11:23
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Content;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.HashMap;
import java.util.*;
/**
 *
 * @author thomas
 */
public class FileIO {
    
    public static Document readDocumentFromLocalFile(String pathToDocument) throws JDOMException, IOException {
        if (pathToDocument.startsWith("http")){
            return readDocumentFromURL(pathToDocument);
        }
        SAXBuilder builder = new SAXBuilder();
        File file = new File(pathToDocument);
        Document document =  builder.build(file);
        return document;
    }
    
    public static Document readDocumentFromLocalFile(File file) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document document =  builder.build(file);
        return document;
    }
    


    public static Document readDocumentFromURL(String url) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document document =  builder.build(new java.net.URL(url));   
        return document;
    }
    
    public static String getDocumentAsString(Document document) throws IOException {
        return getDocumentAsString(document,false);
    }
    
    public static String getDocumentAsString(Document document, boolean omitDeclaration) throws IOException {
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(xmlOutputter.getFormat().setOmitDeclaration(omitDeclaration));
        java.io.StringWriter writer = new java.io.StringWriter();
        xmlOutputter.output(document,writer);
        return writer.toString();        
    }
    
    public static void writeDocumentToLocalFile(String pathToDocument, Document document) throws IOException{
        XMLOutputter xmlOutputter = new XMLOutputter();
        //String docString = xmlOutputter.outputString(document);        
        FileOutputStream fos = new FileOutputStream(new File(pathToDocument));        
        xmlOutputter.output(document,fos);
        //fos.write(docString.getBytes("UTF-8"));
        fos.close();    
    }
    
    
    
    public static void writeDocumentToLocalFile(File file, Document document) throws IOException{
        XMLOutputter xmlOutputter = new XMLOutputter();
        FileOutputStream fos = new FileOutputStream(file);        
        xmlOutputter.output(document,fos);
        fos.close();    
    }

    public static String getPlainTextFromLocalFile(String pathToDocument) throws JDOMException, IOException {
        Document doc = readDocumentFromLocalFile(pathToDocument);
        Element body = doc.getRootElement().getChild("text");
        return getPlainText(body);
    }
    
    public static String getPlainText(Content c){
        if (c instanceof org.jdom.Text){
            return ((org.jdom.Text)c).getText();
        } else if (c instanceof org.jdom.CDATA){
            return (((org.jdom.CDATA)c).getText());                
        } else {
            StringBuffer sb = new StringBuffer();
            Element e = (Element)c;            
            List children = e.getChildren();
            //List children = e.getContent();
            // if this element has element children
            // process just those, else get all the content 
            // - it should be text
            if (children.size()==0){
                children = e.getContent();
            }
            for (int pos=0; pos<children.size(); pos++){
                Object o = children.get(pos);
                Content content = (Content)o;
                sb.append(getPlainText(content));
            }
            return sb.toString();
        }
        
    }
    
    public static void assignStylesheet(String pathToStylesheet, Document doc){
          List l = doc.getContent();
          for (Object o : l){
              if (o instanceof org.jdom.ProcessingInstruction){
                  ((org.jdom.ProcessingInstruction)o).detach();
              }
          }
          HashMap piMap = new HashMap( 2 );
          piMap.put( "type", "text/xsl" );
          piMap.put( "href", pathToStylesheet );
          org.jdom.ProcessingInstruction pi = new org.jdom.ProcessingInstruction( "xml-stylesheet", piMap );
          doc.getContent().add( 0, pi );                
    }
}
