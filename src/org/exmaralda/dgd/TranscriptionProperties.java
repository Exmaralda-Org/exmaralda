/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.folker.utilities.TimeStringFormatter;
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
public class TranscriptionProperties {

    
    File transcriptDirectory;
    File[] transcriptFiles;
    StringBuffer properties = new StringBuffer();
    double totalLength = 0.0;
    
    Document resultDocument;
    String HTML_XSL = "/org/exmaralda/dgd/transcriptionProperties2HTML.xsl";
    
    

    public TranscriptionProperties(String transcriptPath) {
        transcriptDirectory = new File(transcriptPath);
        transcriptFiles = transcriptDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".flk") || name.toLowerCase().endsWith(".fln"));
            }            
        });
        resultDocument = new Document(new Element("transcript-files"));        
    }
    
       
    public static void main(String[] args){
        try {
            if ((args.length!=2)){
                System.out.println("Usage: TranscriptionProperties transcriptDirectory result.xml");
                System.exit(0);
            }

            TranscriptionProperties f = new TranscriptionProperties(args[0]);
            f.getProperties();
            
            //f.properties.append("-----------------------------------------------\n");
            //f.properties.append("TOTAL\t\t\t\t\t\t"+ Double.toString(f.totalLength)+ "\t" + TimeStringFormatter.formatMiliseconds(f.totalLength*1000.0, 2) + "\n");
            
            f.writeResultList(args[1]);

            /*File recErrFile = new File(args[1]);
            FileOutputStream fos = new FileOutputStream(recErrFile);
            fos.write(f.properties.toString().getBytes("UTF-8"));
            fos.close();*/
            System.exit(0);
        } catch (SAXException ex) {
            Logger.getLogger(TranscriptionProperties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(TranscriptionProperties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(TranscriptionProperties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(TranscriptionProperties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(TranscriptionProperties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TranscriptionProperties.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    void writeResultList(String xmlPath) throws IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        FileIO.writeDocumentToLocalFile(new File(xmlPath), resultDocument);
        String htmlPath = xmlPath.replaceAll("\\.xml", ".html");
        StylesheetFactory sf = new StylesheetFactory(true);
        String html = sf.applyInternalStylesheetToExternalXMLFile(HTML_XSL, xmlPath);
        FileOutputStream fos = new FileOutputStream(new File(htmlPath));
        fos.write(html.getBytes("UTF-8"));
        fos.close();
        System.out.println(htmlPath + " written.");
        
    }
    

    private void getProperties() throws IOException, JDOMException{
        for (File transcriptFile : transcriptFiles){
            System.out.println("Reading " + transcriptFile.getAbsolutePath());
            Document t = FileIO.readDocumentFromLocalFile(transcriptFile);
            List l = XPath.newInstance("//contribution").selectNodes(t);
            Element firstCont = (Element)(l.get(0));
            Element lastCont = (Element)(l.get(l.size()-1));
            String firstTLI = firstCont.getAttributeValue("start-reference");
            String lastTLI = lastCont.getAttributeValue("end-reference");
            Double firstTime = Double.parseDouble(((Element) (XPath.newInstance("//timepoint[@timepoint-id='" + firstTLI + "']").selectSingleNode(t)))
                    .getAttributeValue("absolute-time"));
            Double lastTime = Double.parseDouble(((Element) (XPath.newInstance("//timepoint[@timepoint-id='" + lastTLI + "']").selectSingleNode(t)))
                    .getAttributeValue("absolute-time"));
            double lengthInSeconds = lastTime - firstTime;
            /*totalLength+=lengthInSeconds;
            properties.append(transcriptFile.getName() + "\t");
            properties.append(transcriptFile.getAbsolutePath() + "\t");
            properties.append(Double.toString(firstTime) + "\t");
            properties.append(TimeStringFormatter.formatMiliseconds(firstTime*1000.0, 2) + "\t");            
            properties.append(Double.toString(lastTime) + "\t");
            properties.append(TimeStringFormatter.formatMiliseconds(lastTime*1000.0, 2) + "\t");            
            properties.append(Double.toString(lengthInSeconds) + "\t");
            properties.append(TimeStringFormatter.formatMiliseconds(lengthInSeconds*1000.0, 2) + "\n");            */

            List words = XPath.newInstance("//w").selectNodes(t);
            HashSet<String> types = new HashSet<String>();
            for (Object o : words){
                types.add(((Element)o).getText());
            }
            
            Element transcriptFileElement = new Element("transcript-file");
            resultDocument.getRootElement().addContent(transcriptFileElement);
            transcriptFileElement.setAttribute("name", transcriptFile.getName());
            transcriptFileElement.setAttribute("path", transcriptFile.getAbsolutePath());
            transcriptFileElement.setAttribute("start", TimeStringFormatter.formatMiliseconds(firstTime*1000.0, 2));
            transcriptFileElement.setAttribute("end", TimeStringFormatter.formatMiliseconds(lastTime*1000.0, 2));
            transcriptFileElement.setAttribute("seconds", Double.toString(lengthInSeconds));
            transcriptFileElement.setAttribute("length", TimeStringFormatter.formatMiliseconds(lengthInSeconds*1000.0, 2));
            transcriptFileElement.setAttribute("bytes", Long.toString(transcriptFile.length()));
            transcriptFileElement.setAttribute("tokens", Integer.toString(words.size()));
            transcriptFileElement.setAttribute("types", Integer.toString(types.size()));
        }
    }
    
    
    
}
