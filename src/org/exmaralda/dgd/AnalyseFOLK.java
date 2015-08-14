/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class AnalyseFOLK {

    static String IN = "C:\\Users\\Schmidt\\Desktop\\FOLK_Testset";
    static String OUT = "C:\\Users\\Schmidt\\Desktop\\FOLK_Testset\\Analysis.xml";
    
    File transcriptDirectory;
    Document result;
            
    public AnalyseFOLK(String transcriptPath) {
        transcriptDirectory = new File(transcriptPath);
        Element re = new Element("analysisResults");
        result = new Document(re);
    }
    
    void analyse() throws JDOMException, IOException{
        File[] transcriptFiles = transcriptDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".flk");
            }               
        });
        for (File transcriptFile : transcriptFiles){
            System.out.println("Analysing " + transcriptFile.getName());
            Document transcriptDoc = FileIO.readDocumentFromLocalFile(transcriptFile);
            Element cre = new Element("check");
            cre.setAttribute("filename", transcriptFile.getName());
            result.getRootElement().addContent(cre);
            
            //            
            cre.setAttribute("words", Integer.toString(XPath.newInstance("//w").selectNodes(transcriptDoc).size()));
            cre.setAttribute("unintelligible-words", Integer.toString(XPath.newInstance("//w[starts-with(text(),'+')]").selectNodes(transcriptDoc).size()));
            cre.setAttribute("uncertain", Integer.toString(XPath.newInstance("//uncertain").selectNodes(transcriptDoc).size()));
            cre.setAttribute("words-in-uncertain", Integer.toString(XPath.newInstance("//uncertain/descendant::w").selectNodes(transcriptDoc).size()));
            cre.setAttribute("alternative", Integer.toString(XPath.newInstance("//alternative").selectNodes(transcriptDoc).size()));
            cre.setAttribute("words-in-alternative", Integer.toString(XPath.newInstance("//alternative/descendant::w").selectNodes(transcriptDoc).size()));                        
            
        }
    }
    
    void writeResultList(String xmlPath) throws IOException {
        FileIO.writeDocumentToLocalFile(new File(xmlPath), result);
    }

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            if (args.length==0){
                AnalyseFOLK analyser = new AnalyseFOLK(IN);
                analyser.analyse();
                analyser.writeResultList(OUT);                
                System.exit(0);
            }
            if (args.length!=2){
                System.out.println("Usage: AnalyseFOLK transcriptDirectory output.xml");
                System.exit(0);
            }
            AnalyseFOLK analyser = new AnalyseFOLK(args[0]);
            analyser.analyse();
            analyser.writeResultList(args[1]);
        } catch (JDOMException ex) {
            Logger.getLogger(AnalyseFOLK.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AnalyseFOLK.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }
}
