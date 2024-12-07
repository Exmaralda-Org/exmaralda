/*
 * XSLWalkerTest.java
 *
 * Created on 26. Januar 2007, 15:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch;

import java.io.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.jdom.*;
import org.xml.sax.SAXException;


/**
 *
 * @author thomas
 */
public class Count {
    
    //static String CORPUS_FILE = "S:\\TP-Z2\\DATEN\\K5\\0.5\\";
    //static String STYLESHEET_FILE = "D:\\EXAKT_NetBeans_Project\\EXAKT\\src\\resources\\CountSegments.xsl";
    
    
    /** Creates a new instance of XSLWalkerTest */
    public Count() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String CORPUS_FILE = args[0];
            String STYLESHEET1 = args[1];
            String STYLESHEET2 = args[2];
            String OUT_FILE = args[3];
            String OUT_FILE2 = args[4];
            COMAXSLCorpusWalker johnny_walker = new COMAXSLCorpusWalker("result");
            johnny_walker.readCorpus(new File(CORPUS_FILE));
            johnny_walker.readStylesheet(new File(STYLESHEET1));
            johnny_walker.walk(COMACorpusWalker.SEGMENTED_TRANSCRIPTIONS);
            Document result = johnny_walker.getResultDocument();
            
            Document comaDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromLocalFile(CORPUS_FILE);
            Element corp = comaDocument.getRootElement();
            corp.detach();
            result.getRootElement().addContent(corp);
            
            org.exmaralda.exakt.utilities.FileIO.writeDocumentToLocalFile(OUT_FILE, result);
            try {                
                org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory ssf = 
                        new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory(true);
                String html = ssf.applyExternalStylesheetToExternalXMLFile(STYLESHEET2, OUT_FILE);
                FileOutputStream fos = new FileOutputStream(new File(OUT_FILE2));
                fos.write(html.getBytes("UTF-8"));
                fos.close();
            } catch (TransformerConfigurationException ex) {
                ex.printStackTrace();
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (TransformerException ex) {
                ex.printStackTrace();
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            
            
            
            
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
