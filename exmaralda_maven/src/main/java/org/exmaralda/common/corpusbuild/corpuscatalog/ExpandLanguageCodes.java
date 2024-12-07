/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild.corpuscatalog;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class ExpandLanguageCodes {

    String CATALOG_FILE = "S:\\TP-Z2\\DATEN\\EXMARaLDACorpusCatalog.xml";
    String LANG_CODES_FILE = "C:\\DATEN\\CODE\\EXMARaLDA\\src\\org\\exmaralda\\partitureditor\\jexmaralda\\Languages.xml";
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
            try {
                new ExpandLanguageCodes().doit();
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
        Document LANG_CODES_DOCUMENT = FileIO.readDocumentFromLocalFile(LANG_CODES_FILE);
        Hashtable<String,String> code2Name = new Hashtable<String,String>();
        for (Object o : LANG_CODES_DOCUMENT.getRootElement().getChildren("language")){
            Element e = (Element)o;
            code2Name.put(e.getAttributeValue("lang"), e.getAttributeValue("desc"));
        }
        Document CATALOG_DOCUMENT = FileIO.readDocumentFromLocalFile(CATALOG_FILE);
        List l = XPath.newInstance("//language").selectNodes(CATALOG_DOCUMENT);
        for (Object o : l){
            /* <language languageID="deu" /> */
            Element languageElement = (Element)o;
            String langID = languageElement.getAttributeValue("languageID");
            String langName = code2Name.get(langID);
            if (langName!=null){
                System.out.println(langID + " --> " + langName);
                languageElement.setAttribute("languageName", langName);
            }
            
        }
        
        FileIO.writeDocumentToLocalFile(CATALOG_FILE, CATALOG_DOCUMENT);
    }

}
