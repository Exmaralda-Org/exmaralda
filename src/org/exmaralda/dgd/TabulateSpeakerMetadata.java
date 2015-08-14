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

/**
 *
 * @author Schmidt
 */
public class TabulateSpeakerMetadata {

    Document result;    
    String directory;
    
    String HTML_XSL = "/org/exmaralda/dgd/metadataOverview2HTML.xsl";
            
    public TabulateSpeakerMetadata(String directory) {
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
                return name.matches("[A-Z][A-Z]([A-Z]|-){2}_S_\\d{5}\\.xml");
            }
            
        });
        /*for (int i=1; i<1000; i++){
            String number = "0" + Integer.toString(i);
            if (i<10) number = "0" + number;
            if (i<100) number = "0" + number;
            if (i<1000) number = "0" + number;
            String name = corpus + "_S_" + number + ".xml";
            String filename = directory + "/" + name;
            System.out.println("Reading " + filename);
            try {
                Document xmlDoc = FileIO.readDocumentFromURL(filename);*/
       for (File f : files){
            System.out.println("Reading " + f.getAbsolutePath());
            try {
                Document xmlDoc = FileIO.readDocumentFromLocalFile(f);

                if (!(xmlDoc.getRootElement().getName().equals("Sprecher"))) {
                    continue;
                }
                Element cre = new Element("speaker");
                String kennung = xmlDoc.getRootElement().getAttributeValue("Kennung");
                cre.setAttribute("speaker-id", kennung);
                cre.setAttribute("file", f.getName());
                result.getRootElement().addContent(cre);

                addElement("//In_Sprechereignis/SE-Kennung", xmlDoc, cre);
                addElement("//Basisdaten/Sonstige_Bezeichnungen", xmlDoc, cre);
                addElement("//Basisdaten/Sigle_in_Transkripten", xmlDoc, cre);
                if (kennung.startsWith("FOLK")){
                    addElement("//Basisdaten/Pseudonym", xmlDoc, cre);
                } else if (kennung.startsWith("IS")){
                    addElement("//Basisdaten/Name", xmlDoc, cre);
                    addElement("//Basisdaten/Früherer_Name", xmlDoc, cre);                    
                }
                addElement("//Basisdaten/Geschlecht", xmlDoc, cre);
                addElement("//Basisdaten/Geburtsdatum/YYYY-MM-DD", xmlDoc, cre);
                
            } catch (FileNotFoundException fnfe){
                System.out.println("File does not exist");
            }
        }
    }
    
    void writeResultList(String xmlPath) throws IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        FileIO.writeDocumentToLocalFile(new File(xmlPath), result);
        String htmlPath = xmlPath.replaceAll("\\.xml", ".html");
        StylesheetFactory sf = new StylesheetFactory(true);
        String html = sf.applyInternalStylesheetToExternalXMLFile(HTML_XSL, xmlPath);
        FileOutputStream fos = new FileOutputStream(new File(htmlPath));
        fos.write(html.getBytes("UTF-8"));
        fos.close();
        System.out.println(htmlPath + " written.");
        
    }

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            if (args.length!=2){
                System.out.println("Usage: TabulateEventMetadata directory output.xml");
                System.exit(0);
            }
            TabulateSpeakerMetadata tabulator = new TabulateSpeakerMetadata(args[0]);
            tabulator.doit();
            tabulator.writeResultList(args[1]);
        } catch (SAXException ex) {
            Logger.getLogger(TabulateSpeakerMetadata.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(TabulateSpeakerMetadata.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(TabulateSpeakerMetadata.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(TabulateSpeakerMetadata.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(TabulateSpeakerMetadata.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TabulateSpeakerMetadata.class.getName()).log(Level.SEVERE, null, ex);
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
