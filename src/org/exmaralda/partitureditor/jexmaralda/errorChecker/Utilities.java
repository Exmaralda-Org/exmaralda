/*
 * Utilities.java
 *
 * Created on 15. November 2007, 11:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda.errorChecker;

import org.jdom.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author thomas
 */
public class Utilities {
    
    /** Creates a new instance of Utilities */
    public Utilities() {
    }
    
    public static Document readErrorList(String path) throws JDOMException, URISyntaxException, IOException{
        Document d = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromLocalFile(path);
        String baseURI = new File(path).getParentFile().toURI().toString();
        resolvePathNames(d,baseURI);
        return d;
    }
    
    public static void resolvePathNames(Document d, String baseURI) throws URISyntaxException{
        List l = d.getRootElement().getChild("errors").getChildren("error");
        for (Object o : l){
            Element error = (Element)o;
            URI uri2 = new URI(baseURI);
            String fl = error.getAttributeValue("file");
            URI absoluteURI = uri2.resolve(fl);
            System.out.println("Resolved path is " + new File(absoluteURI).getAbsolutePath());
            String resolvedPath = new File(absoluteURI).getAbsolutePath();
            error.setAttribute("file", resolvedPath);
        }        
    }
    
    public static void relativizePathNames(Document d, String baseURI) throws URISyntaxException {
        List l = d.getRootElement().getChild("errors").getChildren("error");
        for (Object o : l){
            Element error = (Element)o;
            URI uri2 = new URI(baseURI);
            String fl = error.getAttributeValue("file");
            URI relativeURI = uri2.relativize(new File(fl).toURI());
            System.out.println("Relative path is " + relativeURI.toString());
            String relativePath = relativeURI.toString();
            error.setAttribute("file", relativePath);
        }                
    }
    
    public static void writeErrorList(Document d, String path) throws JDOMException, URISyntaxException, IOException{
        String baseURI = new File(path).getParentFile().toURI().toString();
        relativizePathNames(d, baseURI);
        org.exmaralda.common.jdomutilities.IOUtilities.writeDocumentToLocalFile(path, d);
        resolvePathNames(d, baseURI);
    }
    
    public static Document makeEmptyDocument(){
        Document d = new Document(new Element("error-list"));
        d.getRootElement().addContent(new Element("errors"));
        return d;
    }
    
    public static Element makeError(String path, String tier, String start, String text) throws URISyntaxException{
        Element e = new Element("error");
        e.setAttribute("file", path);
        e.setAttribute("tier", tier);
        e.setAttribute("start", start);
        e.setAttribute("done", "no");
        e.setText(text);
        return e;
    }
    
}
