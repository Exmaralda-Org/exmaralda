/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class MetadataSelection {

    String SELECTION_XML = "y:\\thomas\\metadata\\Selection.xml";
    String XSL = "y:\\thomas\\metadata\\selections\\selectionValues2HTML.xsl";
    String XSL2 = "y:\\thomas\\metadata\\selections\\selectionValues2HTML_Speakers.xsl";
    String XSL3 = "y:\\thomas\\metadata\\selections\\eventProfile.xsl";
    String XSL4 = "y:\\thomas\\metadata\\selections\\speakerProfile.xsl";
    String EVENT_PATH = "X:\\xml\\events";
    String SPEAKER_PATH = "X:\\xml\\speakers";
    String OUT_PATH = "y:\\thomas\\metadata\\selections";
    
    FilenameFilter EVENT_FILENAME_FILTER = new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                //ZW--_E_05654.xml
                return name.matches("[A-Z][A-Z]([A-Z]|-){2}_E_\\d{5}\\.xml");
            }            
        };    
    FilenameFilter SPEAKER_FILENAME_FILTER = new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                //ZW--_E_05654.xml
                return name.matches("[A-Z][A-Z]([A-Z]|-){2}_S_\\d{5}\\.xml");
            }            
        };    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            try {
                new MetadataSelection().doit();
            } catch (SAXException ex) {
                Logger.getLogger(MetadataSelection.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(MetadataSelection.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerConfigurationException ex) {
                Logger.getLogger(MetadataSelection.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerException ex) {
                Logger.getLogger(MetadataSelection.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (JDOMException ex) {
            Logger.getLogger(MetadataSelection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MetadataSelection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        Document selDoc = FileIO.readDocumentFromLocalFile(new File(SELECTION_XML));
        for (Object o : selDoc.getRootElement().getChildren("corpus")){
            Element c = (Element)o;
            String corpusName = c.getAttributeValue("name");
            
            Document eventOutDoc = new Document(new Element("corpus-event-metadata"));
            eventOutDoc.getRootElement().setAttribute("corpus", corpusName);
            
            File[] eventFiles = new File(EVENT_PATH + "\\" + corpusName.replaceAll("-", "")).listFiles(EVENT_FILENAME_FILTER);
            for (File f : eventFiles){
                System.out.println("Reading " + f.getAbsolutePath());
                Document eDoc = FileIO.readDocumentFromLocalFile(f);
                
                Element thisOutElement = new Element("event-metadata");
                thisOutElement.setAttribute("event-id", eDoc.getRootElement().getAttributeValue("Kennung"));
                eventOutDoc.getRootElement().addContent(thisOutElement);
                
                
                getValues(c, thisOutElement, eDoc, "event-metadata", "/Ereignis/");
                getValues(c, thisOutElement, eDoc, "speech-event-metadata", "/Ereignis/Sprechereignis/");
                getValues(c, thisOutElement, eDoc, "speech-event-speaker-metadata", "/Ereignis/Sprechereignis/Sprecher/");
                
            }
            
            File OUT_FILE = new File(OUT_PATH + "\\" + corpusName.replaceAll("-", "") + "_Ereignisse.xml");
            FileIO.writeDocumentToLocalFile(OUT_FILE, eventOutDoc);
            
            StylesheetFactory ssf = new StylesheetFactory(true);
            String html = ssf.applyExternalStylesheetToExternalXMLFile(XSL, OUT_FILE.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(new File(OUT_PATH + "\\" + corpusName.replaceAll("-", "") + "_Ereignisse.html"));
            fos.write(html.getBytes("UTF-8"));
            fos.close();
            
            html = ssf.applyExternalStylesheetToExternalXMLFile(XSL3, OUT_FILE.getAbsolutePath());
            fos = new FileOutputStream(new File(OUT_PATH + "\\" + corpusName.replaceAll("-", "") + "_E_Profil.html"));
            fos.write(html.getBytes("UTF-8"));
            fos.close();
            
            /*********************************************/
            
            Document speakerOutDoc = new Document(new Element("corpus-speaker-metadata"));
            eventOutDoc.getRootElement().setAttribute("corpus", corpusName);

            
            File[] speakerFiles = new File(SPEAKER_PATH + "\\" + corpusName.replaceAll("-", "")).listFiles(SPEAKER_FILENAME_FILTER);
            if (speakerFiles!=null){
                for (File f : speakerFiles){
                    System.out.println("Reading " + f.getAbsolutePath());
                    Document sDoc = FileIO.readDocumentFromLocalFile(f);

                    Element thisOutElement = new Element("speaker-metadata");
                    thisOutElement.setAttribute("speaker-id", sDoc.getRootElement().getAttributeValue("Kennung"));
                    speakerOutDoc.getRootElement().addContent(thisOutElement);


                    getValues(c, thisOutElement, sDoc, "speaker-metadata", "/Sprecher/");

                }

                File OUT_FILE2 = new File(OUT_PATH + "\\" + corpusName.replaceAll("-", "") + "_Sprecher.xml");
                FileIO.writeDocumentToLocalFile(OUT_FILE2, speakerOutDoc);

                html = ssf.applyExternalStylesheetToExternalXMLFile(XSL2, OUT_FILE2.getAbsolutePath());
                fos = new FileOutputStream(new File(OUT_PATH + "\\" + corpusName.replaceAll("-", "") + "_Sprecher.html"));
                fos.write(html.getBytes("UTF-8"));
                fos.close();
                
                html = ssf.applyExternalStylesheetToExternalXMLFile(XSL4, OUT_FILE2.getAbsolutePath());
                fos = new FileOutputStream(new File(OUT_PATH + "\\" + corpusName.replaceAll("-", "") + "_S_Profil.html"));
                fos.write(html.getBytes("UTF-8"));
                fos.close();
                
            }
            
        }
    }

    private void getValues(Element c, Element thisOutElement, Document eDoc, String type, String xpathPrefix) throws JDOMException {

        List mis = XPath.newInstance(type + "/metadata-item").selectNodes(c);
        for (Object o2 : mis){
            /*<metadata-item>
                <label>Beschreibung</label>
                <xpath>Basisdaten/Beschreibung</xpath>
            </metadata-item>*/
            Element mi = (Element)o2;
            List values = XPath.newInstance(xpathPrefix + mi.getChildText("xpath")).selectNodes(eDoc);
            for (Object o3 : values){
                String kennung = ((Attribute)(XPath.newInstance("ancestor::*[@Kennung][1]/@Kennung").selectSingleNode(o3))).getValue();
                Element e = new Element("metadata");
                e.setAttribute("Kennung", kennung);
                e.setAttribute("label", mi.getChildText("label"));
                e.setAttribute("level", type);
                e.setText(((Element)o3).getText());
                thisOutElement.addContent(e);

            }
        }        
    }
}
