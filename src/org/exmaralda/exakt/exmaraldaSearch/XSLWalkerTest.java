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
import org.jdom.*;
import org.jdom.xpath.*;
import java.util.*;


/**
 *
 * @author thomas
 */
public class XSLWalkerTest {
    
    static String CORPUS_FILE = "S:\\TP-E5\\SKOBI-ORDNER AKTUELL\\PBU_CORPUS_22MAI2007.xml";
    static String STYLESHEET_FILE = "D:\\EXAKT_NetBeans_Project\\EXAKT\\src\\resources\\CountSegments.xsl";
    
    
    /** Creates a new instance of XSLWalkerTest */
    public XSLWalkerTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            COMAXSLCorpusWalker johnny_walker = new COMAXSLCorpusWalker("result");
            johnny_walker.readCorpus(new File(CORPUS_FILE));
            johnny_walker.readStylesheet(new File(STYLESHEET_FILE));
            johnny_walker.walk(COMACorpusWalker.SEGMENTED_TRANSCRIPTIONS);
            Document result = johnny_walker.getResultDocument();
            
            Document comaDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromLocalFile(CORPUS_FILE);
            Element corp = comaDocument.getRootElement();
            corp.detach();
            result.getRootElement().addContent(corp);
            /*String xp = "//Speaker";
            XPath xpath = XPath.newInstance(xp);
            List l = xpath.selectNodes(comaDocument);
            for (Object o : l){
                Element s = (Element)o;
                s.detach();
                result.getRootElement().addContent(s);
            }*/
            
            org.exmaralda.exakt.utilities.FileIO.writeDocumentToLocalFile("S:\\TP-E5\\SKOBI-ORDNER AKTUELL\\Count.xml", result);
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
