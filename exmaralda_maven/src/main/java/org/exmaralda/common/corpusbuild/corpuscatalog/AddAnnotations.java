/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild.corpuscatalog;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.exakt.exmaraldaSearch.COMACorpus;
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
public class AddAnnotations {

    String CATALOG_FILE = "S:\\TP-Z2\\DATEN\\EXMARaLDACorpusCatalog.xml";
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
            try {
                new AddAnnotations().doit();
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
        Document CATALOG_DOCUMENT = FileIO.readDocumentFromLocalFile(CATALOG_FILE);
        List l = XPath.newInstance("//resource[contains(@type,'exmaralda') and descendant::exmaralda-coma[@type='local']]").selectNodes(CATALOG_DOCUMENT);
        for (Object o : l){
            Element resourceElement = (Element)o;
            Element comaCorpusElement = (Element) XPath.newInstance("descendant::exmaralda-coma[@type='local']").selectSingleNode(resourceElement);
            String url = comaCorpusElement.getAttributeValue("url").substring(7);
            System.out.println("Processing " + url);
            
            COMACorpus comaCorpus = new COMACorpus();
            comaCorpus.readCorpus(new File(url), true);
            
            //resourceElement.removeChildren("annotation");
            HashSet<String> annotationNames = comaCorpus.getAnnotationNames();
            for (String annotationName : annotationNames){
                if (XPath.newInstance("descendant::annotation[@category='" + annotationName + "']").selectSingleNode(resourceElement)==null){
                    Element annotationElement = new Element("annotation");
                    annotationElement.setAttribute("category", annotationName);                
                    int index = resourceElement.indexOf(
                            (Element) XPath.newInstance("descendant::short-description[last()]").selectSingleNode(resourceElement));
                    resourceElement.addContent(index+1, annotationElement);
                }
            }
            FileIO.writeDocumentToLocalFile(CATALOG_FILE, CATALOG_DOCUMENT);
        }
        
    }

}
