/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.corpusbuild.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class AnalyseZWSiglen extends AbstractSchneiderProcessor {
    
    File metaDirectory;
    
    
    public AnalyseZWSiglen(String[] args){
        inputDirectory = new File(args[0]);
        inputSuffix = args[1];        
        inputFiles = inputDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(inputSuffix.toUpperCase());
            }            
        });
        
        metaDirectory = new File(args[2]);

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String[] myArgs = {
                "Y:\\thomas\\ZW_HE", "fln", 
                "Y:\\thomas\\ZW_HE\\Meta\\zw+he_3"
            };
            AnalyseZWSiglen aa = new AnalyseZWSiglen(myArgs);
            aa.processFiles();
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(FixNonAlignedTranscripts.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void processFiles() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException{
        Element root = new Element("root");
        for (File inputFile : inputFiles){
            System.out.println("Processing " + inputFile.getName());                        
            Element thisFile = new Element("Transkript");
            thisFile.setAttribute("file", inputFile.getName());
            root.addContent(thisFile);
            Document xmlDocument = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());
            List l = XPath.newInstance("//speaker").selectNodes(xmlDocument);
            HashSet<String> speakers = new HashSet<String>();
            for (Object o : l){
                Element s = (Element)o;
                speakers.add(s.getAttributeValue("speaker-id"));
            }
            
            for (String sID : speakers){
                String xp = "//w[ancestor::contribution[@speaker-reference='" +  sID + "']]";
                int countWords = XPath.newInstance(xp).selectNodes(xmlDocument).size();
                System.out.println(sID + " " + countWords);
                Element thisSigle = new Element("Sigle");
                thisSigle.setAttribute("sigle", sID);
                thisSigle.setAttribute("anzahl-wörter", Integer.toString(countWords));
                thisFile.addContent(thisSigle);
            }
            
            Document eDoc = FileIO.readDocumentFromLocalFile(metaDirectory + "\\" + inputFile.getName().substring(0, 12) + ".xml");
            l = XPath.newInstance("//Sprecher").selectNodes(eDoc);
            for (Object o : l){
                Element s = (Element)o;
                s.detach();
                //s.removeContent();
                thisFile.addContent(s);                
            }
            
            
        }                
        Document out = new Document(root);
        FileIO.writeDocumentToLocalFile("Y:\\thomas\\ZW_HE\\Meta\\Siglen.xml", out);
    }
    
}
