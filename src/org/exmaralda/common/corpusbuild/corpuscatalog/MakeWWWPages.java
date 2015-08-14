/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild.corpuscatalog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.common.jdomutilities.IOUtilities;
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
public class MakeWWWPages {

    String CATALOG_FILE = "S:\\Korpora\\Katalog\\EXMARaLDACorpusCatalog.xml";
    String EN_OVERVIEW_XSL = "/org/exmaralda/common/corpusbuild/corpuscatalog/Catalog2OverviewTable.xsl";
    String DE_OVERVIEW_XSL = "/org/exmaralda/common/corpusbuild/corpuscatalog/Catalog2OverviewTable_de.xsl";
    String EN_SINGLE_XSL = "/org/exmaralda/common/corpusbuild/corpuscatalog/Resource2HTML.xsl";
    String DE_SINGLE_XSL = "/org/exmaralda/common/corpusbuild/corpuscatalog/Resource2HTML_de.xsl";
    
    String OUTPUT_PATH = "S:\\Korpus-Distribution\\www";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new MakeWWWPages().doit();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void doit() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        StylesheetFactory ssf = new StylesheetFactory(true);
        
        String en_overview_html = ssf.applyInternalStylesheetToExternalXMLFile(EN_OVERVIEW_XSL, CATALOG_FILE);
        writeHTML(en_overview_html, new File(OUTPUT_PATH, "en_overview.html"));
        
        String de_overview_html = ssf.applyInternalStylesheetToExternalXMLFile(DE_OVERVIEW_XSL, CATALOG_FILE);
        writeHTML(de_overview_html, new File(OUTPUT_PATH, "de_overview.html"));

        
        Document CATALOG_DOCUMENT = FileIO.readDocumentFromLocalFile(CATALOG_FILE);                        
        List l = XPath.newInstance("//resource").selectNodes(CATALOG_DOCUMENT);
        int count=0;
        for (Object o : l){
            String previous = ((Element)(l.get(l.size()-1))).getChildText("filename");
            if (count>0){
                previous = ((Element)(l.get(count-1))).getChildText("filename");
            }
            String next = ((Element)(l.get(0))).getChildText("filename");
            if (count<l.size()-1){
                next = ((Element)(l.get(count+1))).getChildText("filename");               
            }
            Element resourceElement = (Element)o;
            
            Element prevElement = new Element("prev");
            prevElement.setAttribute("filename", previous);
            resourceElement.addContent(0, prevElement);
            Element nextElement = new Element("next");
            nextElement.setAttribute("filename", next);
            resourceElement.addContent(0, nextElement);
            
            String filename = resourceElement.getChildText("filename");
            String reString = IOUtilities.elementToString(resourceElement);
            String re_html = ssf.applyInternalStylesheetToString(EN_SINGLE_XSL, reString);
            writeHTML(re_html, new File(OUTPUT_PATH, "en_" + filename + ".html"));
            
            String re_html_de = ssf.applyInternalStylesheetToString(DE_SINGLE_XSL, reString);
            writeHTML(re_html_de, new File(OUTPUT_PATH, "de_" + filename + ".html"));

            count++;
        }
        
    }

    private void writeHTML(String en_overview_html, File file) throws FileNotFoundException, IOException {
        System.out.println("started writing document...");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(en_overview_html.getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");
        
    }

}
