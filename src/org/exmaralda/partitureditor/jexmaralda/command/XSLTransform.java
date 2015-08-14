/*
 * XSLConvert.java
 *
 * Created on 12. September 2007, 14:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda.command;

import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class XSLTransform {
    
    /** Creates a new instance of XSLConvert */
    public XSLTransform() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String pathToXMLFile = args[0];
            String pathToXSLFile = args[1];
            String pathToOutputFile = args[2];
            boolean useXSL2 = false;
            if (args.length>3) useXSL2 = Boolean.parseBoolean(args[3]);
            String outputType = "XML";
            if (args.length>4) outputType = args[4];
            
            org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory ssf =
                    new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory(useXSL2);
            String result = ssf.applyExternalStylesheetToExternalXMLFile(pathToXSLFile, pathToXMLFile);
            if ("XML".equals(outputType)){
                org.jdom.Document doc = org.exmaralda.common.corpusbuild.FileIO.readDocumentFromString(result);            
                boolean omitXMLDeclaration = pathToOutputFile.toLowerCase().endsWith("html");
                org.exmaralda.common.corpusbuild.FileIO.writeDocumentToLocalFile(pathToOutputFile, doc, omitXMLDeclaration);
            } else {
                FileOutputStream fos = new FileOutputStream(pathToOutputFile);
                fos.write(result.getBytes("UTF-8"));
                fos.close();                
            }
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
    }
    
}
