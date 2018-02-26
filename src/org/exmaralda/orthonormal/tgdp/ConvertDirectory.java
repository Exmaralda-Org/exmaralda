/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.tgdp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Thomas_Schmidt
 */
public class ConvertDirectory {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new ConvertDirectory().doit();
        } catch (SAXException ex) {
            Logger.getLogger(ConvertDirectory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ConvertDirectory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConvertDirectory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(ConvertDirectory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(ConvertDirectory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    String IN_DIR = "F:\\Dropbox\\IDS\\AGD\\Sprachinseln\\GOLD_STANDARD\\TGDP";
    String EAF2TEI1 = "/org/exmaralda/orthonormal/tgdp/EAF2TEI_tgdp.xsl";
    String EAF2TEI2 = "/org/exmaralda/orthonormal/tgdp/EAF2TEI_tgdp2.xsl";
    String TEI2HTML = "/org/exmaralda/orthonormal/tgdp/TEI2HTML_tgdp.xsl";

    private void doit() throws SAXException, ParserConfigurationException, IOException, TransformerException, JDOMException {
        File[] eafFiles = new File(IN_DIR).listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(".EAF");
            }            
        });
        StylesheetFactory ssf = new StylesheetFactory(true);
        for (File eafFile : eafFiles){
            System.out.println("Processing " + eafFile.getAbsolutePath());
            String isoTei1 = ssf.applyInternalStylesheetToExternalXMLFile(EAF2TEI1, eafFile.getAbsolutePath());
            String isoTei2 = ssf.applyInternalStylesheetToString(EAF2TEI2, isoTei1);
            System.out.println(isoTei2);
            Document doc = FileIO.readDocumentFromString(isoTei2);
            File outFile = new File(new File(IN_DIR), eafFile.getName().replaceAll("\\.eaf", ".xml"));
            FileIO.writeDocumentToLocalFile(outFile, doc);
            
            String html = ssf.applyInternalStylesheetToString(TEI2HTML, isoTei2);
            File htmlFile = new File(new File(IN_DIR), eafFile.getName().replaceAll("\\.eaf", ".html"));
            FileOutputStream fos = new FileOutputStream(htmlFile);
            fos.write(html.getBytes("UTF-8"));
            fos.close();

        }
    }
    
}
