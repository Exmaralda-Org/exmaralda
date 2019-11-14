/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author fsnv625
 */
public class FlexTextConverter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SAXException, ParserConfigurationException, IOException, TransformerException {
    }

    public FlexTextConverter() {

    }
    public static String FLEXTEXT2EX_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/flextext2exb.xsl";

    //how to give the settings.xml?
    public static String DEFAULT_SETTINGS = "";

    //(File inputFile)
    public BasicTranscription readFlexTextFromTextFile(File inputFile, File parameterFile) throws SAXException, ParserConfigurationException, IOException, TransformerException, JDOMException, JexmaraldaException {
        //initialise the Stylesheet Factory
        StylesheetFactory ssf = new StylesheetFactory(true);
        //get the whole path of the flextext file
        String filepath = inputFile.getAbsolutePath();
        String parametervalue = "";
        if(parameterFile.getPath().startsWith("file:")){// if inel predefined settings file 
            parametervalue = parameterFile.getPath().replace("\\", "/");
        }else{
            parametervalue = parameterFile.toURI().toString();
        }
        String[][] parameters = {{"SETTINGS-FILE", parametervalue}};
        //how will the settings be put in here?
        String out = ssf.applyInternalStylesheetToExternalXMLFile(FLEXTEXT2EX_STYLESHEET, filepath, parameters);
        System.out.println(out);  
        //out is only a conversion-settings document - but it contains the location of a created file
        //there could be more than one exb as output because of the structure of flextext, but only the first will be opened in PE
        //first make a document of the xslt output
        SAXBuilder builder = new SAXBuilder();
        StringReader in = new StringReader(out);
        Document xmlout = builder.build(in);       
        //create xpath for getting the information where exb is stored
        XPath xpath = XPath.newInstance("/conversion-report[1]/@output-location");
        xpath.selectSingleNode(xmlout);
        Object newBT = xpath.selectSingleNode(xmlout);
        //System.out.println(newBT.toString()); 
        //sorry this is so ugly
        //there is probably a much easier way
        String btPath = newBT.toString().replace("[Attribute: output-location=\"file:/", "").replace("\"]","");
        //System.out.println(btPath); 
        //finally make a basic transcription of new exb filepath
        BasicTranscription bt = new BasicTranscription(btPath);
        return bt;

    }
    
    public BasicTranscription readFlexTextFromTextFilePredefined(File inputFile, String parameterPath) throws SAXException, ParserConfigurationException, IOException, TransformerException, JDOMException, JexmaraldaException {
        //initialise the Stylesheet Factory
        StylesheetFactory ssf = new StylesheetFactory(true);
        //get the whole path of the flextext file
        String filepath = inputFile.getAbsolutePath();
        String parametervalue = "";
        parametervalue = getClass().getResource(parameterPath).toExternalForm();
        String[][] parameters = {{"SETTINGS-FILE", parametervalue}};
        //how will the settings be put in here?
        String out = ssf.applyInternalStylesheetToExternalXMLFile(FLEXTEXT2EX_STYLESHEET, filepath, parameters);
        System.out.println(out);  
        //out is only a conversion-settings document - but it contains the location of a created file
        //there could be more than one exb as output because of the structure of flextext, but only the first will be opened in PE
        //first make a document of the xslt output
        SAXBuilder builder = new SAXBuilder();
        StringReader in = new StringReader(out);
        Document xmlout = builder.build(in);       
        //create xpath for getting the information where exb is stored
        XPath xpath = XPath.newInstance("/conversion-report[1]/@output-location");
        xpath.selectSingleNode(xmlout);
        Object newBT = xpath.selectSingleNode(xmlout);
        //System.out.println(newBT.toString()); 
        //sorry this is so ugly
        //there is probably a much easier way
        String btPath = newBT.toString().replace("[Attribute: output-location=\"file:/", "").replace("\"]","");
        //System.out.println(btPath); 
        //finally make a basic transcription of new exb filepath
        BasicTranscription bt = new BasicTranscription(btPath);
        return bt;

    }

}
