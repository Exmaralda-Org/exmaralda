/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.exakt.tokenlist.HashtableTokenList;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class TransformPFMetaData extends AbstractSchneiderProcessor {
    
    private String STYLESHEET_PATH = "Y:\\thomas\\PF2FLK\\Meta\\Old2New.xsl";
    File transcriptDirectory;
    
    public TransformPFMetaData(String[] args){
        inputDirectory = new File(args[0]);
        inputSuffix = args[1];
        outputDirectory = new File(args[2]);
        outputDirectory.mkdir();
        for (File f : outputDirectory.listFiles()){
            f.delete();
        }
        outputSuffix = args[3];        
        
        inputFiles = inputDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(inputSuffix.toUpperCase());
            }            
        });
        
        transcriptDirectory = new File(args[4]);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String[] myArgs = {
                "Y:\\thomas\\PF2FLK\\Meta\\pf_TR_DGD_1.0", "xml", 
                "Y:\\thomas\\PF2FLK\\Meta\\pf_TR_DGD_2.0", "xml",
                "Y:\\thomas\\PF2FLK\\10"
            };
            TransformPFMetaData aa = new TransformPFMetaData(myArgs);
            aa.processFiles(); 
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(FixNonAlignedTranscripts.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void processFiles() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException{
        StylesheetFactory ssf = new StylesheetFactory(true);
        for (File inputFile : inputFiles){
            System.out.println("Transforming " + inputFile.getName());                        
            //Document xmlDocument = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());
            String transformed = ssf.applyExternalStylesheetToExternalXMLFile(STYLESHEET_PATH, inputFile.getAbsolutePath());
            Document transformedDocument = FileIO.readDocumentFromString(transformed);
            Object o = XPath.newInstance("//Transkript[1]//Dateiname").selectSingleNode(transformedDocument);
            String fln = ((Element)o).getText();
            File f = new File(transcriptDirectory, fln);
            Element e = (Element) XPath.newInstance("//Transkript[1]//Dateigröße").selectSingleNode(transformedDocument);
            e.setText(Long.toString(f.length())); 
            // Random
            
            HashtableTokenList htl = new HashtableTokenList();
            File[] fs = {f};
            htl.readWordsFromFolkerFiles(fs);
            int types = htl.getNumberOfTokens();
            int tokens = XPath.newInstance("//w").selectNodes(FileIO.readDocumentFromLocalFile(f.getAbsolutePath())).size();
            
            System.out.println("Types: " + types);
            System.out.println("Tokens: " + tokens);
            
            e = (Element) XPath.newInstance("//Transkript[1]//Types").selectSingleNode(transformedDocument);
            e.setText(Integer.toString(types));

            e = (Element) XPath.newInstance("//Transkript[1]//Tokens").selectSingleNode(transformedDocument);
            e.setText(Integer.toString(tokens));

            FileIO.writeDocumentToLocalFile(makeOutputPath(inputFile), transformedDocument);
        }                
    }
    
}
