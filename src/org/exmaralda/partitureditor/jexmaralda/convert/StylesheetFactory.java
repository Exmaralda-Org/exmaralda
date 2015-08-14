/*
 * StylesheetFactory.java
 *
 * Created on 23. Juli 2003, 14:11
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import javax.xml.parsers.ParserConfigurationException;
 
import org.xml.sax.SAXException;

// For write operation
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;


import java.io.*;

/**
 *
 * @author  thomas
 */
public class StylesheetFactory {
    
    TransformerFactory tFactory; // = TransformerFactory.newInstance();

    /** Creates a new instance of StylesheetFactory */
    public StylesheetFactory() {
        // Change in version 1.3.4. to add XSLT2 support
        this(false);
    }
    
    public StylesheetFactory(boolean useXSLT2){
        if (useXSLT2){
            //System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");  
            tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);            
        } else {
            //System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");                
            tFactory = TransformerFactory.newInstance("org.apache.xalan.processor.TransformerFactoryImpl", null);            
        }
    }
    
    public String applyInternalStylesheetToString(String pathToInternalStyleSheet, 
                                                  String sourceString,
                                                  String[][] parameters)
                                        throws    SAXException, 
                                                  ParserConfigurationException, 
                                                  IOException, 
                                                  TransformerConfigurationException, 
                                                  TransformerException{

        // set up the transformer         
        java.io.InputStream is2 = getClass().getResourceAsStream(pathToInternalStyleSheet);
        if (is2==null) {throw new IOException("Stylesheet not found!");}
        javax.xml.transform.stream.StreamSource styleSource = new javax.xml.transform.stream.StreamSource(is2);
        javax.xml.transform.Transformer transformer = tFactory.newTransformer(styleSource);
        
        java.io.StringReader sr = new java.io.StringReader(sourceString);
        javax.xml.transform.stream.StreamSource ss = new javax.xml.transform.stream.StreamSource(sr);

        // set up the output stream for the second transformation
        java.io.StringWriter sw2 = new java.io.StringWriter();
        javax.xml.transform.stream.StreamResult resultStream = new StreamResult(sw2);       

        //pass the parameters to the transfomer
        for (String[] p : parameters){
            transformer.setParameter(p[0], p[1]);
        }
        
        //perform the second transformation
        transformer.transform(ss, resultStream);    
        
        // convert the ouput stream to a string and return it
        return sw2.toString();        
    }

    public String applyInternalStylesheetToString(String pathToInternalStyleSheet, String sourceString)
                                        throws    SAXException, 
                                                  ParserConfigurationException, 
                                                  IOException, 
                                                  TransformerConfigurationException, 
                                                  TransformerException{

        // set up the transformer 
        //TransformerFactory tFactory = TransformerFactory.newInstance();
        java.io.InputStream is2 = getClass().getResourceAsStream(pathToInternalStyleSheet);
        if (is2==null) {throw new IOException("Stylesheet not found!");}
        javax.xml.transform.stream.StreamSource styleSource = new javax.xml.transform.stream.StreamSource(is2);
        javax.xml.transform.Transformer transformer = tFactory.newTransformer(styleSource);
        
        java.io.StringReader sr = new java.io.StringReader(sourceString);
        javax.xml.transform.stream.StreamSource ss = new javax.xml.transform.stream.StreamSource(sr);

        // set up the output stream for the second transformation
        java.io.StringWriter sw2 = new java.io.StringWriter();
        javax.xml.transform.stream.StreamResult resultStream = new StreamResult(sw2);       

        //perform the second transformation
        transformer.transform(ss, resultStream);    
        
        // convert the ouput stream to a string and return it
        return sw2.toString();        
    }
    
    public String applyInternalStylesheetToExternalXMLFile(String pathToInternalStyleSheet, String pathToExternalXMLFile)
                                        throws    SAXException, 
                                                  ParserConfigurationException, 
                                                  IOException, 
                                                  TransformerConfigurationException, 
                                                  TransformerException{

        // set up a SAX source as input to the transformer
        java.io.File inputFile  = new java.io.File(pathToExternalXMLFile);        
        java.io.FileInputStream fis = new java.io.FileInputStream(inputFile);
        org.xml.sax.InputSource is = new org.xml.sax.InputSource(fis);
        javax.xml.transform.sax.SAXSource saxSource = new javax.xml.transform.sax.SAXSource(is);
        saxSource.setSystemId(inputFile.toURL().toString());
        
        //TransformerFactory tFactory = TransformerFactory.newInstance();
        java.io.InputStream is2 = getClass().getResourceAsStream(pathToInternalStyleSheet);
        if (is2==null) {throw new IOException("Stylesheet not found!");}
        javax.xml.transform.stream.StreamSource styleSource = new javax.xml.transform.stream.StreamSource(is2);
        javax.xml.transform.Transformer transformer = tFactory.newTransformer(styleSource);
                
        // set up the output stream for the first transformation
        java.io.StringWriter sw = new java.io.StringWriter();
        javax.xml.transform.stream.StreamResult inputStream = new StreamResult(sw);
                
        //perform the first transformation        
        transformer.transform(saxSource, inputStream);    
        
        return sw.toString();
    }    
    
    public String applyExternalStylesheetToString(String pathToExternalStyleSheet, String sourceString)
                                        throws    SAXException, 
                                                  ParserConfigurationException, 
                                                  IOException, 
                                                  TransformerConfigurationException, 
                                                  TransformerException{

        // set up the transformer 
        //TransformerFactory tFactory = TransformerFactory.newInstance();
        java.io.FileInputStream is2 = new FileInputStream(pathToExternalStyleSheet);
        if (is2==null) {throw new IOException("Stylesheet not found!");}
        javax.xml.transform.stream.StreamSource styleSource = new javax.xml.transform.stream.StreamSource(is2);
        javax.xml.transform.Transformer transformer = tFactory.newTransformer(styleSource);
        
        java.io.StringReader sr = new java.io.StringReader(sourceString);
        javax.xml.transform.stream.StreamSource ss = new javax.xml.transform.stream.StreamSource(sr);

        // set up the output stream for the second transformation
        java.io.StringWriter sw2 = new java.io.StringWriter();
        javax.xml.transform.stream.StreamResult resultStream = new StreamResult(sw2);       

        //perform the second transformation
        transformer.transform(ss, resultStream);
        
        // convert the ouput stream to a string and return it
        return sw2.toString();        
    }
    
    public String applyExternalStylesheetToExternalXMLFile(String pathToExternalStyleSheet, String pathToExternalXMLFile)
                                        throws    SAXException, 
                                                  ParserConfigurationException, 
                                                  IOException, 
                                                  TransformerConfigurationException, 
                                                  TransformerException{

        // set up the transformer 
        //TransformerFactory tFactory = TransformerFactory.newInstance();
        java.io.FileInputStream is2 = new FileInputStream(pathToExternalStyleSheet);
        if (is2==null) {throw new IOException("Stylesheet not found!");}
        javax.xml.transform.stream.StreamSource styleSource = new javax.xml.transform.stream.StreamSource(is2);
        javax.xml.transform.Transformer transformer = tFactory.newTransformer(styleSource);

        // set up a SAX source as input to the transformer
        java.io.File inputFile  = new java.io.File(pathToExternalXMLFile);        
        java.io.FileInputStream fis = new java.io.FileInputStream(inputFile);
        org.xml.sax.InputSource is = new org.xml.sax.InputSource(fis);
        javax.xml.transform.sax.SAXSource saxSource = new javax.xml.transform.sax.SAXSource(is);
        saxSource.setSystemId(inputFile.toURL().toString());

        // set up the output stream for the second transformation
        java.io.StringWriter sw2 = new java.io.StringWriter();
        javax.xml.transform.stream.StreamResult resultStream = new StreamResult(sw2);       

        //perform the second transformation
        transformer.transform(saxSource, resultStream);
                
        // convert the ouput stream to a string and return it
        return sw2.toString();        
    }
    
    public String applyExternalStylesheetToExternalXMLFile(String pathToExternalStyleSheet, 
                                                           String pathToExternalXMLFile,
                                                           String[][] parameters)
                                        throws    SAXException, 
                                                  ParserConfigurationException, 
                                                  IOException, 
                                                  TransformerConfigurationException, 
                                                  TransformerException{

        // set up the transformer 
        //TransformerFactory tFactory = TransformerFactory.newInstance();
        java.io.FileInputStream is2 = new FileInputStream(pathToExternalStyleSheet);
        if (is2==null) {throw new IOException("Stylesheet not found!");}
        javax.xml.transform.stream.StreamSource styleSource = new javax.xml.transform.stream.StreamSource(is2);
        javax.xml.transform.Transformer transformer = tFactory.newTransformer(styleSource);

        // set up a SAX source as input to the transformer
        java.io.File inputFile  = new java.io.File(pathToExternalXMLFile);        
        java.io.FileInputStream fis = new java.io.FileInputStream(inputFile);
        org.xml.sax.InputSource is = new org.xml.sax.InputSource(fis);
        javax.xml.transform.sax.SAXSource saxSource = new javax.xml.transform.sax.SAXSource(is);
        saxSource.setSystemId(inputFile.toURL().toString());

        // set up the output stream for the second transformation
        java.io.StringWriter sw2 = new java.io.StringWriter();
        javax.xml.transform.stream.StreamResult resultStream = new StreamResult(sw2);       

      
        //pass the parameters to the transfomer
        for (String[] p : parameters){
            transformer.setParameter(p[0], p[1]);
        }
        
        //perform the second transformation
        transformer.transform(saxSource, resultStream);
                
        // convert the ouput stream to a string and return it
        return sw2.toString();        
    }

}
