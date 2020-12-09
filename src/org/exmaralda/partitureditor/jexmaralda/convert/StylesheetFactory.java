/*
 * StylesheetFactory.java
 *
 * Created on 23. Juli 2003, 14:11
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
 
import org.xml.sax.SAXException;

// For write operation
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathException;



/**
 *
 * @author  thomas
 */
public class StylesheetFactory implements javax.xml.transform.ErrorListener {
    
    TransformerFactory tFactory; // = TransformerFactory.newInstance();
    // New 09-12-2020, issue #233
    List<String> warnings = new ArrayList<>();

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
    
    public List<String> getWarnings(){
        return warnings;
    }
    
    public String applyInternalStylesheetToString(final String pathToInternalStyleSheet, 
                                                  String sourceString,
                                                  String[][] parameters)
                                        throws    SAXException, 
                                                  ParserConfigurationException, 
                                                  IOException, 
                                                  TransformerConfigurationException, 
                                                  TransformerException{

        // this does not work, but also no harm...
        // well, maybe it DOES do harm, see issue #243
        /*tFactory.setURIResolver(new URIResolver(){
            @Override
            public Source resolve(String href, String base) throws TransformerException {
                InputStream is = getClass().getResourceAsStream("/" + href);
                return new javax.xml.transform.stream.StreamSource(is,href);
            }            
        });*/

        // set up the transformer         
        java.io.InputStream is2 = getClass().getResourceAsStream(pathToInternalStyleSheet);
        if (is2==null) {
            throw new IOException("Stylesheet " + pathToInternalStyleSheet + " not found!");
        }
        java.net.URL styleURL = getClass().getResource(pathToInternalStyleSheet);
        javax.xml.transform.stream.StreamSource styleSource = new javax.xml.transform.stream.StreamSource(is2);
        styleSource.setSystemId(styleURL.toExternalForm());
        
        javax.xml.transform.Transformer transformer = tFactory.newTransformer(styleSource);

        // NEW 09-12-2020: issue #233
        setupMessageHandler(transformer);
        
        java.io.StringReader sr = new java.io.StringReader(sourceString);
        javax.xml.transform.stream.StreamSource ss = new javax.xml.transform.stream.StreamSource(sr);
        

        // set up the output stream for the second transformation
        java.io.StringWriter sw2 = new java.io.StringWriter();
        javax.xml.transform.stream.StreamResult resultStream = new StreamResult(sw2);       

        //pass the parameters to the transfomer
        if (parameters!=null){
            for (String[] p : parameters){
                transformer.setParameter(p[0], p[1]);
            }
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
        if (is2==null) {
            throw new IOException("Stylesheet " + pathToInternalStyleSheet + " not found!");
        }
        java.net.URL styleURL = getClass().getResource(pathToInternalStyleSheet);
        javax.xml.transform.stream.StreamSource styleSource = new javax.xml.transform.stream.StreamSource(is2);
        styleSource.setSystemId(styleURL.toExternalForm());
        javax.xml.transform.Transformer transformer = tFactory.newTransformer(styleSource);
        
        // NEW 09-12-2020: issue #233
        setupMessageHandler(transformer);       
        
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
        if (is2==null) {
            throw new IOException("Stylesheet " + pathToInternalStyleSheet + " not found!");
        }
        java.net.URL styleURL = getClass().getResource(pathToInternalStyleSheet);
        javax.xml.transform.stream.StreamSource styleSource = new javax.xml.transform.stream.StreamSource(is2);
        styleSource.setSystemId(styleURL.toExternalForm());
        javax.xml.transform.Transformer transformer = tFactory.newTransformer(styleSource);

        // NEW 09-12-2020: issue #233
        setupMessageHandler(transformer);
                
        // set up the output stream for the first transformation
        java.io.StringWriter sw = new java.io.StringWriter();
        javax.xml.transform.stream.StreamResult inputStream = new StreamResult(sw);
                
        //perform the first transformation        
        transformer.transform(saxSource, inputStream);    
        
        return sw.toString();
    }    
    
    /**
     *
     * @param pathToInternalStyleSheet
     * @param pathToExternalXMLFile
     * @param parameters
     * @return
     */
    public String applyInternalStylesheetToExternalXMLFile(String pathToInternalStyleSheet, String pathToExternalXMLFile, String[][] parameters)
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
        if (is2==null) {
            throw new IOException("Stylesheet " + pathToInternalStyleSheet + " not found!");
        }
        java.net.URL styleURL = getClass().getResource(pathToInternalStyleSheet);
        javax.xml.transform.stream.StreamSource styleSource = new javax.xml.transform.stream.StreamSource(is2);
        styleSource.setSystemId(styleURL.toExternalForm());
        javax.xml.transform.Transformer transformer = tFactory.newTransformer(styleSource);
                
        // NEW 09-12-2020: issue #233
        setupMessageHandler(transformer);

        // set up the output stream for the first transformation
        java.io.StringWriter sw = new java.io.StringWriter();
        javax.xml.transform.stream.StreamResult inputStream = new StreamResult(sw);
           
        //pass the parameters to the transfomer
         for (String[] p : parameters){
            transformer.setParameter(p[0], p[1]);
        } 
        
        //perform the first transformation        
        transformer.transform(saxSource, inputStream);    
                
        // convert the ouput stream to a string and return it
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
        java.io.File inputFile2  = new java.io.File(pathToExternalStyleSheet);
        java.io.FileInputStream is2 = new java.io.FileInputStream(inputFile2);
        if (is2==null) {
            throw new IOException("Stylesheet " + pathToExternalStyleSheet + " not found!");
        }
        javax.xml.transform.stream.StreamSource styleSource = new javax.xml.transform.stream.StreamSource(is2);
        styleSource.setSystemId(inputFile2.toURL().toString());
        javax.xml.transform.Transformer transformer = tFactory.newTransformer(styleSource);
        
        // NEW 09-12-2020: issue #233
        setupMessageHandler(transformer);
        
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
    

    public String applyExternalStylesheetToString(String pathToExternalStyleSheet, String sourceString, String[][] parameters)
                                        throws    SAXException, 
                                                  ParserConfigurationException, 
                                                  IOException, 
                                                  TransformerConfigurationException, 
                                                  TransformerException{

        // set up the transformer 
        //TransformerFactory tFactory = TransformerFactory.newInstance();
        java.io.File inputFile2  = new java.io.File(pathToExternalStyleSheet);
        java.io.FileInputStream is2 = new java.io.FileInputStream(inputFile2);
        if (is2==null) {
            throw new IOException("Stylesheet " + pathToExternalStyleSheet + " not found!");
        }
        javax.xml.transform.stream.StreamSource styleSource = new javax.xml.transform.stream.StreamSource(is2);
        styleSource.setSystemId(inputFile2.toURL().toString());
        javax.xml.transform.Transformer transformer = tFactory.newTransformer(styleSource);
        
        // NEW 09-12-2020: issue #233
        setupMessageHandler(transformer);
        
        java.io.StringReader sr = new java.io.StringReader(sourceString);
        javax.xml.transform.stream.StreamSource ss = new javax.xml.transform.stream.StreamSource(sr);

        // set up the output stream for the second transformation
        java.io.StringWriter sw2 = new java.io.StringWriter();
        javax.xml.transform.stream.StreamResult resultStream = new StreamResult(sw2);   
        
        //pass the parameters to the transfomer
        if (parameters!=null){
            for (String[] p : parameters){
                transformer.setParameter(p[0], p[1]);
            }
        }
        

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
        java.io.File inputFile2  = new java.io.File(pathToExternalStyleSheet);
        java.io.FileInputStream is2 = new java.io.FileInputStream(inputFile2);
        if (is2==null) {
            throw new IOException("Stylesheet " + pathToExternalStyleSheet + " not found!");
        }
        javax.xml.transform.stream.StreamSource styleSource = new javax.xml.transform.stream.StreamSource(is2);
        styleSource.setSystemId(inputFile2.toURL().toString());
        javax.xml.transform.Transformer transformer = tFactory.newTransformer(styleSource);
        
        
        
        // NEW 09-12-2020: issue #233
        setupMessageHandler(transformer);
        

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
        java.io.File inputFile2  = new java.io.File(pathToExternalStyleSheet);
        java.io.FileInputStream is2 = new java.io.FileInputStream(inputFile2);
        if (is2==null) {
            throw new IOException("Stylesheet " + pathToExternalStyleSheet + " not found!");
        }
        javax.xml.transform.stream.StreamSource styleSource = new javax.xml.transform.stream.StreamSource(is2);
        styleSource.setSystemId(inputFile2.toURL().toString());
        javax.xml.transform.Transformer transformer = tFactory.newTransformer(styleSource);
        
        // NEW 09-12-2020: issue #233
        setupMessageHandler(transformer);
       

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
        if (parameters!=null){
            for (String[] p : parameters){
                transformer.setParameter(p[0], p[1]);
            }
        }
        
        //perform the second transformation
        transformer.transform(saxSource, resultStream);
                
        // convert the ouput stream to a string and return it
        return sw2.toString();        
    }

    @Override
    public void warning(TransformerException exception) throws TransformerException {
        warnings.add(exception.getLocalizedMessage());
    }

    @Override
    public void error(TransformerException exception) throws TransformerException {
        warnings.add(exception.getLocalizedMessage());
    }

    @Override
    public void fatalError(TransformerException exception) throws TransformerException {
        warnings.add(exception.getLocalizedMessage());
    }

    // NEW 09-12-2020: issue #233
    private void setupMessageHandler(Transformer transformer) {
        try {
            transformer.setErrorListener(this);
            net.sf.saxon.event.MessageWarner saxonWarner = new net.sf.saxon.event.MessageWarner();
            Properties props = new Properties();
            props.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            saxonWarner.setOutputProperties(props);
            saxonWarner.setOutputStream(System.err);
            ((net.sf.saxon.Controller)transformer).setMessageEmitter(saxonWarner);
        } catch (net.sf.saxon.trans.XPathException ex) {
            Logger.getLogger(StylesheetFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
