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
public class CheckFOLK {

    File transcriptDirectory;
    Document result;
            
    public CheckFOLK(String transcriptPath) {
        transcriptDirectory = new File(transcriptPath);
        Element re = new Element("checkResults");
        result = new Document(re);
    }
    
    void check() throws JDOMException, IOException{
        File[] transcriptFiles = transcriptDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".flk");
            }               
        });
        for (File transcriptFile : transcriptFiles){
            System.out.println("Checking " + transcriptFile.getName());
            Document transcriptDoc = FileIO.readDocumentFromLocalFile(transcriptFile);
            Element cre = new Element("check");
            cre.setAttribute("filename", transcriptFile.getName());
            result.getRootElement().addContent(cre);
            
            // check for parse errors
            XPath xp = XPath.newInstance("//contribution[not(@parse-level='2')]");
            List parseErrors = xp.selectNodes(transcriptDoc);
            cre.setAttribute("parse-errors", Integer.toString(parseErrors.size()));
            
            // validate against schema
            
            
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
            if (args.length!=2){
                System.out.println("Usage: CheckFOLK transcriptDirectory output.xml");
                System.exit(0);
            }
            CheckFOLK checker = new CheckFOLK(args[0]);
            checker.check();
            checker.writeResultList(args[1]);
        } catch (JDOMException ex) {
            Logger.getLogger(CheckFOLK.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CheckFOLK.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }
}
