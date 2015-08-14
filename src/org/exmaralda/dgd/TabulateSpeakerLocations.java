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
public class TabulateSpeakerLocations {

    Document result;    
    String directory;
    
    String HTML_XSL = "/org/exmaralda/dgd/metadataOverview2HTML.xsl";
            
    public TabulateSpeakerLocations(String directory) {
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

                /* <Ortsdaten Typ="Mundartort">
                    <Land>Nicht dokumentiert</Land>
                    <Region>Nicht dokumentiert</Region>
                    <Kreis>Emmendingen</Kreis>
                    <Ortsname>Maleck</Ortsname>
                    <Koordinaten>
                        <Planquadrat>4808</Planquadrat>
                        <Anmerkungen/>
                    </Koordinaten>*/
                
                addElement("//Ortsdaten[@Typ='Mundartort']/Kreis", xmlDoc, cre);
                addElement("//Ortsdaten[@Typ='Mundartort']/Ortsname", xmlDoc, cre);
                addElement("//Ortsdaten[@Typ='Mundartort']/Koordinaten/Planquadrat", xmlDoc, cre);
                
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
            TabulateSpeakerLocations tabulator = new TabulateSpeakerLocations(args[0]);
            tabulator.doit();
            tabulator.writeResultList(args[1]);
        } catch (SAXException ex) {
            Logger.getLogger(TabulateSpeakerLocations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(TabulateSpeakerLocations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(TabulateSpeakerLocations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(TabulateSpeakerLocations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(TabulateSpeakerLocations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TabulateSpeakerLocations.class.getName()).log(Level.SEVERE, null, ex);
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
