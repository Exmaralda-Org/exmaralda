/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
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
public class CompareSGML extends AbstractSchneiderProcessor {
    
    Document compareDocument;
    Document logDocument;
    Element logRoot;
    File compareDirectory;
    
    public CompareSGML(String[] args) throws JDOMException, IOException{
        inputDirectory = new File(args[0]);
        inputSuffix = args[1];
        compareDirectory = new File(args[2]);        
        logFile = new File(args[3]);
        
        logRoot = new Element("compare-sgml");
        logDocument = new Document(logRoot);
        
        inputFiles = inputDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(inputSuffix.toUpperCase());
            }            
        });
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            CompareSGML aa = new CompareSGML(args);
            aa.processFiles();
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(CompareSGML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processFiles() throws FileNotFoundException, IOException, JDOMException, SAXException, ParserConfigurationException {
        for (File inputFile : inputFiles){
            Element compareElement = new Element("compare");
            logRoot.addContent(compareElement);
            
            System.out.println("Reading " + inputFile.getName());                        
            Document xmlDocument = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());
            compareElement.setAttribute("file1", inputFile.getName());
            
            File compareFile = new File(compareDirectory, inputFile.getName().substring(0,inputFile.getName().indexOf("TRA")) + ".SGM.xml");
            if (!compareFile.exists()){
                compareElement.setAttribute("file2", "not_found");
                continue;
            }
            System.out.println("Comparing to " + compareFile.getName());                        
            Document compareDocument = null;
            try {
                compareDocument = FileIO.readDocumentFromLocalFile(compareFile.getAbsolutePath());
            } catch (JDOMException jde){
                compareElement.setAttribute("file2", "error");
                Element error = new Element("error");
                error.setText(jde.getMessage());
                compareElement.addContent(error);
                continue;                
            }

            compareElement.setAttribute("file2", compareFile.getName());

            XPath xp1 = XPath.newInstance("//w");
            XPath xp2 = XPath.newInstance("//AW");
            
            List w1 = xp1.selectNodes(xmlDocument);
            List w2 = xp2.selectNodes(compareDocument);
            
            compareElement.setAttribute("count1", Integer.toString(w1.size()));
            compareElement.setAttribute("count2", Integer.toString(w2.size()));

            HashSet<String> s1 = new HashSet<String>();
            HashSet<String> s2 = new HashSet<String>();
            
            for (Object o : w1) s1.add(((Element)o).getText());
            // SGM version has question marks and some other puncutation inside word tags
            for (Object o : w2) s2.add(((Element)o).getText().replaceAll("[\\?\\;\\,\\!\\\"\\:]", ""));
            
            s1.removeAll(s2);
            Element only1 = new Element("only_in_file1");
            for (String w : s1){
                Element wEl = new Element("w");
                wEl.setText(w);
                only1.addContent(wEl);
            }
            compareElement.addContent(only1);
            
            for (Object o : w1) s1.add(((Element)o).getText());
            
            s2.removeAll(s1);
            Element only2 = new Element("only_in_file2");
            for (String w : s2){
                Element wEl = new Element("w");
                wEl.setText(w);
                only2.addContent(wEl);
            }
            compareElement.addContent(only2);
            
            
        }
        
        writeLogToXMLFile(logDocument);
        
        
    }
}
