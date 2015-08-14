/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.folker.utilities.TimeStringFormatter;
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
public class SchemaValidation {

    
    File transcriptDirectory;
    File[] transcriptFiles;
    String schemaString;
    
    Document resultDocument;
    
    

    public SchemaValidation(String transcriptPath, final String suffix) {
        transcriptDirectory = new File(transcriptPath);
        transcriptFiles = transcriptDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(suffix);
            }            
        });
        resultDocument = new Document(new Element("transcript-files"));        
    }
    
       
    public static void main(String[] args){
        try {
            if ((args.length!=4)){
                System.out.println("Usage: TranscriptionProperties transcriptDirectory suffix schemaString result.xml");
                System.exit(0);
            }

            SchemaValidation f = new SchemaValidation(args[0], args[1]);
            f.schemaString = args[2];
            f.validate();
            f.writeResultList(args[3]);
            System.exit(0);
        } catch (SAXException ex) {
            Logger.getLogger(SchemaValidation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SchemaValidation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(SchemaValidation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(SchemaValidation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SchemaValidation.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    void writeResultList(String xmlPath) throws IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        FileIO.writeDocumentToLocalFile(new File(xmlPath), resultDocument);
        
    }
    

    private void validate(){
        for (File transcriptFile : transcriptFiles){
            System.out.println("Validating " + transcriptFile.getAbsolutePath());
            Element transcriptFileElement = new Element("transcript-file");
            try {
                IOUtilities.schemaValidateLocalFile(transcriptFile, schemaString);
                transcriptFileElement.setAttribute("validation", "ok");
            } catch (JDOMException ex) {
                transcriptFileElement.setAttribute("validation", "jdomexception");
                transcriptFileElement.setText(ex.getMessage());
            } catch (IOException ex) {
                transcriptFileElement.setAttribute("validation", "ioexception");
                transcriptFileElement.setText(ex.getMessage());
            }
            resultDocument.getRootElement().addContent(transcriptFileElement);
        }
    }
    
    
    
}
