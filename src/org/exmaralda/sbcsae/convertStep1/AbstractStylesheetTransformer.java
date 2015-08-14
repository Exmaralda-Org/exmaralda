/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.convertStep1;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.jdom.*;
import org.exmaralda.partitureditor.jexmaralda.convert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public abstract class AbstractStylesheetTransformer {

    public abstract String getStylesheetPath();
    public abstract String getSourceDirectoryPath();
    public abstract String getTargetDirectoryPath();
    
    public void transform(boolean useXSLT2) throws SAXException, ParserConfigurationException, IOException, TransformerConfigurationException, TransformerException, JDOMException{
        StylesheetFactory sf = new StylesheetFactory(useXSLT2);
        for (int i=1; i<=60; i++){
            String filename = "SBC0";
            if (i<10) filename+="0";
            filename+=Integer.toString(i);            
            String inputPath = getSourceDirectoryPath() + System.getProperty("file.separator") + filename + ".xml";
            System.out.println("Processing " + inputPath);
            String result = sf.applyExternalStylesheetToExternalXMLFile(getStylesheetPath(), inputPath);
            Document d = org.exmaralda.exakt.utilities.FileIO.readDocumentFromString(result);
            String outputPath = getTargetDirectoryPath() + System.getProperty("file.separator") + filename + ".xml";
            System.out.println("Writing " + outputPath);
            org.exmaralda.exakt.utilities.FileIO.writeDocumentToLocalFile(outputPath, d);
        }
    }
    



}
