/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.tgdp;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Thomas_Schmidt
 */
public class STEP_2_3_TranslationTimeToAnnotationReferences extends AbstractEAFProcessor {

    File OUT = new File("D:\\Dropbox\\work\\WERKVERTRAEGE\\2019_AUSTIN\\2019_05_03_Pilot_Sample\\3-ANNOTATION_REFERENCES");
    
    String XSL = "/org/exmaralda/orthonormal/tgdp/CONVERT_ALIGNABLE_TRANSLATIONS_TO_REF.xsl";
    String XSL2 = "/org/exmaralda/orthonormal/tgdp/REMOVE_UNUSED_TIMESLOTS.xsl";
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            STEP_2_3_TranslationTimeToAnnotationReferences x = new STEP_2_3_TranslationTimeToAnnotationReferences();
            x.IN_DIR = "D:\\Dropbox\\work\\WERKVERTRAEGE\\2019_AUSTIN\\2019_05_03_Pilot_Sample\\2-CHANGE_TIER_ATTRIBUTES";                
            x.doit();
        } catch (IOException ex) {
            Logger.getLogger(STEP_2_3_TranslationTimeToAnnotationReferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    StylesheetFactory ssf = new StylesheetFactory(true);
    
    @Override
    public void processFile(File eafFile) throws IOException {
        try {
            Document eafDoc = FileIO.readDocumentFromLocalFile(eafFile);
            List l = XPath.selectNodes(eafDoc, "//ALIGNABLE_ANNOTATION[ancestor::TIER[@LINGUISTIC_TYPE_REF='TRANSLATION']]");
            if (!(l.isEmpty())){
                System.out.println("    Found one!");
            }
            
            int timeslotsBefore = XPath.selectNodes(eafDoc, "//TIME_SLOT").size();
            
            
            String xml = ssf.applyInternalStylesheetToExternalXMLFile(XSL, eafFile.getAbsolutePath());
            String xml2 = ssf.applyInternalStylesheetToString(XSL2, xml);
            Document doc = FileIO.readDocumentFromString(xml2);
            int timeslotsAfter = XPath.selectNodes(doc, "//TIME_SLOT").size();
            
            if (timeslotsBefore!=timeslotsAfter){
                 System.out.println("    Number of time slots reduced");
            }
            
            OUT.mkdir();
            FileIO.writeDocumentToLocalFile(new File(OUT, eafFile.getName()), doc);
        } catch (SAXException ex) {
            Logger.getLogger(STEP_2_3_TranslationTimeToAnnotationReferences.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(STEP_2_3_TranslationTimeToAnnotationReferences.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(STEP_2_3_TranslationTimeToAnnotationReferences.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(STEP_2_3_TranslationTimeToAnnotationReferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
