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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author Schmidt
 */
public class OrtsdatenMarburg {

    Document result;    
    String directory;
    
    String HTML_XSL = "/org/exmaralda/dgd/metadataOverview2HTML.xsl";
            
    public OrtsdatenMarburg(String directory) {
        Element re = new Element("metadata");
        result = new Document(re);
        this.directory = directory;
    }
    
    void doit() throws JDOMException, IOException{
        File dir = new File(directory);
        File[] files = dir.listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name) {
                //ZW--_E_05654.xml
                return name.matches("[A-Z][A-Z]([A-Z]|-){2}_E_\\d{5}\\.xml");
            }
            
        });
        
        for (File f : files){
            System.out.println("Reading " + f.getAbsolutePath());
            try {
                Document xmlDoc = FileIO.readDocumentFromLocalFile(f);
                if (!(xmlDoc.getRootElement().getName().equals("Ereignis"))) {
                    continue;
                }
                Element cre = new Element("Ereignis");
                cre.setAttribute("kennung", xmlDoc.getRootElement().getAttributeValue("Kennung"));
                result.getRootElement().addContent(cre);

                /*<Land>Deutschland</Land>
                <Region>Nicht dokumentiert</Region>
                <Kreis>Bludenz</Kreis>
                <Ortsname>St. Gallenkirch</Ortsname>
                <Einwohnerzahl>0</Einwohnerzahl>
                <Koordinaten>
                    <Planquadrat>5416</Planquadrat>
                    <Anmerkungen/>
                </Koordinaten>
                <Ortsteil>Nicht dokumentiert</Ortsteil>*/
                addElement("//Basisdaten/Ort/Land", xmlDoc, cre);
                addElement("//Basisdaten/Ort/Region", xmlDoc, cre);
                addElement("//Basisdaten/Ort/Kreis", xmlDoc, cre);
                addElement("//Basisdaten/Ort/Ortsname", xmlDoc, cre);
                addElement("//Basisdaten/Ort/Koordinaten/Planquadrat", xmlDoc, cre);
                addElement("//Basisdaten/Ort/Ortsteil", xmlDoc, cre);

                
            } catch (FileNotFoundException fnfe){
                System.out.println("File does not exist");
            }
            
        }
    }

    
    void writeResultList(String xmlPath) throws IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        FileIO.writeDocumentToLocalFile(new File(xmlPath), result);
        /*String htmlPath = xmlPath.replaceAll("\\.xml", ".html");
        StylesheetFactory sf = new StylesheetFactory(true);
        String html = sf.applyInternalStylesheetToExternalXMLFile(HTML_XSL, xmlPath);
        FileOutputStream fos = new FileOutputStream(new File(htmlPath));
        fos.write(html.getBytes("UTF-8"));
        fos.close();
        System.out.println(htmlPath + " written.");*/
        
    }

    
    
    static String INPUT_DIRECTORY = "O:\\xml\\events\\final\\ZW_HE";
    static String OUTPUT_FILE = "C:\\Users\\Schmidt\\Desktop\\Marburg\\ZW.xml";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            OrtsdatenMarburg tabulator = new OrtsdatenMarburg(INPUT_DIRECTORY);
            tabulator.doit();
            tabulator.writeResultList(OUTPUT_FILE);
        } catch (SAXException ex) {
            Logger.getLogger(OrtsdatenMarburg.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(OrtsdatenMarburg.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(OrtsdatenMarburg.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(OrtsdatenMarburg.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(OrtsdatenMarburg.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OrtsdatenMarburg.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }

    private void addElement(String xpString, Document xmlDoc, Element cre) throws JDOMException {
        XPath xp = XPath.newInstance(xpString);
        Element sb = (Element)(xp.selectSingleNode(xmlDoc));            
        if (sb!=null){
            sb.detach();
            cre.addContent(sb);
        } else {
            Element e = new Element(xpString.substring(xpString.lastIndexOf("/")+1));
            e.setText("Element nicht vorhanden");
            cre.addContent(e);
        }
    }
}
