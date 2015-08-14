/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.jdom.Element;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class GenerateXMLandHTML {

    String HTML_STYLESHEET = "/org/exmaralda/dgd/unparsedFOLKER2DGDHTML.xsl";
    String XML_STYLESHEET = "/org/exmaralda/dgd/parsedFOLKER2DGDXML.xsl";
    
    
    File folkerFile;
    File htmlFile;
    File xmlFile;
    
    StylesheetFactory ssf = new StylesheetFactory(true);

    public GenerateXMLandHTML(String folkerPath, String htmlPath, String xmlPath) {
        folkerFile = new File(folkerPath);
        htmlFile = new File(htmlPath);
        xmlFile = new File(xmlPath);
    }
    
   
    
    public void transformHTML() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException{
        EventListTranscription elt = EventListTranscriptionXMLReaderWriter.readXML(folkerFile);
        Document folkerDocumentParseLevel0 = EventListTranscriptionXMLReaderWriter.toJDOMDocument(elt, folkerFile);
        String htmlString = ssf.applyInternalStylesheetToString(HTML_STYLESHEET, IOUtilities.documentToString(folkerDocumentParseLevel0));                
        write(htmlString, htmlFile);
    }
    
    public void transformXML() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException{
        String xmlString = ssf.applyInternalStylesheetToExternalXMLFile(XML_STYLESHEET, folkerFile.getAbsolutePath());
        write(xmlString, xmlFile);
    }

    private void write(String string, File file) throws FileNotFoundException, IOException {
        System.out.println("started writing document " + file.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(string.getBytes("UTF-8"));
        fos.close();
        System.out.println(file.getAbsolutePath() + " written.");
    }
    
    public static void main(String[] args){
        if (args.length!=1){
            System.out.println("Usage: GenerateXMLandHTML transcriptDirectory");
            System.exit(0);
        }
        try {
            File[] transcriptFiles = new File(args[0]).listFiles(new FilenameFilter(){
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".flk");
                }               
            });
            System.out.println("Found " + transcriptFiles.length + " FOLKER transcripts. ");
            
            
            for (File tf : transcriptFiles){
                String folker = tf.getAbsolutePath();
                String html = folker.replaceAll("\\.[Ff][Ll][Kk]", ".HTML");
                String xml = folker.replaceAll("\\.[Ff][Ll][Kk]", ".xml");
                System.out.println("Processing " + folker);
                GenerateXMLandHTML f = new GenerateXMLandHTML(folker, html, xml);
                f.transformHTML();
                f.transformXML();                
                
            }

        } catch (JDOMException ex) {
            Logger.getLogger(GenerateXMLandHTML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateXMLandHTML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(GenerateXMLandHTML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(GenerateXMLandHTML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(GenerateXMLandHTML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(GenerateXMLandHTML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(GenerateXMLandHTML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
    
}
