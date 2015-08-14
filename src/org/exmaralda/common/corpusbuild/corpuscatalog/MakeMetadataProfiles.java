/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild.corpuscatalog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class MakeMetadataProfiles {

    String CATALOG_FILE = "S:\\TP-Z2\\DATEN\\EXMARaLDACorpusCatalog.xml";
    String XSL_STYLESHEET = "/org/exmaralda/coma/resources/outputxsl/output_descriptions_overview.xsl";
    String OUTPUT_FOLDER = "S:\\TP-Z2\\DATEN\\Metadaten-Profile";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            try {
                new MakeMetadataProfiles().doit();
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            } catch (TransformerConfigurationException ex) {
                ex.printStackTrace();
            } catch (TransformerException ex) {
                ex.printStackTrace();
            }
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void doit() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        StylesheetFactory ssf = new StylesheetFactory(true);
        Document CATALOG_DOCUMENT = FileIO.readDocumentFromLocalFile(CATALOG_FILE);
        List l = XPath.newInstance("//exmaralda-coma[@type='local']").selectNodes(CATALOG_DOCUMENT);
        for (Object o : l){
            Element e = (Element)o;
            String url = e.getAttributeValue("url").substring(7);
            System.out.println("Processing " + url);
            // file://S:\TP-Z2\DATEN\MAPTASK\0.2\MAPTASK.coma
            String out = ssf.applyInternalStylesheetToExternalXMLFile(XSL_STYLESHEET, url);
            File outFile = new File(new File(OUTPUT_FOLDER), new File(url).getName().replaceAll("\\.coma", ".html"));
            System.out.println("Writing " + outFile.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(outFile);
            fos.write(out.getBytes("UTF-8"));
            fos.close();
        }
    }

}
