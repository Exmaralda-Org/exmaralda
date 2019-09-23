/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.tgdp;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.xml.sax.SAXException;

/**
 *
 * @author Thomas_Schmidt
 */
public class STEP_3_4_FixHeader extends AbstractEAFProcessor {

    File OUT = new File("D:\\Dropbox\\work\\WERKVERTRAEGE\\2019_AUSTIN\\2019_05_03_Pilot_Sample\\4-FIX_HEADER");
        
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            STEP_3_4_FixHeader x = new STEP_3_4_FixHeader();
            x.IN_DIR = "D:\\Dropbox\\work\\WERKVERTRAEGE\\2019_AUSTIN\\2019_05_03_Pilot_Sample\\3-ANNOTATION_REFERENCES";                
            x.doit();
        } catch (IOException ex) {
            Logger.getLogger(STEP_3_4_FixHeader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    StylesheetFactory ssf = new StylesheetFactory(true);
    
    @Override
    public void processFile(File eafFile) throws IOException {
        try {
            Document eafDoc = FileIO.readDocumentFromLocalFile(eafFile);
            
            /*<ANNOTATION_DOCUMENT AUTHOR="alena" DATE="2019-06-07T11:56:48+01:00" FORMAT="3.0" VERSION="3.0"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:noNamespaceSchemaLocation="http://www.mpi.nl/tools/elan/EAFv3.0.xsd">
                <HEADER MEDIA_FILE="" TIME_UNITS="milliseconds">
                    <MEDIA_DESCRIPTOR
                        MEDIA_URL="file:///D:/Dropbox/work/WERKVERTRAEGE/2019_AUSTIN/2019_05_03_Pilot_Sample/AUDIO/1-51-1-1-a.wav"
                        MIME_TYPE="audio/x-wav" RELATIVE_MEDIA_URL="../AUDIO/1-51-1-1-a.wav"/>
                    <PROPERTY NAME="URN">urn:nl-mpi-tools-elan-eaf:293debc2-6be0-48ad-be6f-ef6e25a99d16</PROPERTY>
                    <PROPERTY NAME="lastUsedAnnotationId">92</PROPERTY>
                </HEADER>*/
            
            Namespace xsiNamespace = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            eafDoc.getRootElement().setAttribute("noNamespaceSchemaLocation", "http://www.mpi.nl/tools/elan/EAFv3.0.xsd", xsiNamespace);
            eafDoc.getRootElement().setAttribute("FORMAT", "3.0");
            eafDoc.getRootElement().setAttribute("VERSION", "3.0");
            
            // correct date: 2004-07-15T16:56:58-06:00
            // wrong date: 2004.01.22 15:13 CST
            String oldDate = eafDoc.getRootElement().getAttributeValue("DATE");
            String regex = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[-\\+]\\d{2}:\\d{2}$";
            if (!oldDate.matches(regex)){
                String newDate = oldDate
                        .replaceAll(" C.T", ":00-00:00")
                        .replaceAll("\\.", "-")
                        .replaceAll(" ", "T");
                System.out.println("   Does not match: " + oldDate + " ==> " + newDate);
                eafDoc.getRootElement().setAttribute("DATE", newDate);
            }
            
            
            eafDoc.getRootElement().removeChild("HEADER");
            
            Element newHeader = new Element("HEADER");
            newHeader.setAttribute("MEDIA_FILE", "");
            newHeader.setAttribute("TIME_UNITS", "milliseconds");
            
            Element mediaDescriptor = new Element("MEDIA_DESCRIPTOR");
            String wavFileName = eafFile.getName().replaceAll("\\.eaf", ".wav");
            mediaDescriptor.setAttribute("MEDIA_URL", "file:///D:/Dropbox/work/WERKVERTRAEGE/2019_AUSTIN/2019_05_03_Pilot_Sample/AUDIO/" + wavFileName);
            mediaDescriptor.setAttribute("RELATIVE_MEDIA_URL", "../AUDIO/" + wavFileName);
            mediaDescriptor.setAttribute("MIME_TYPE", "audio/x-wav");
            
            eafDoc.getRootElement().addContent(0, newHeader.addContent(mediaDescriptor));
            
            OUT.mkdir();
            FileIO.writeDocumentToLocalFile(new File(OUT, eafFile.getName()), eafDoc);
            
            
            
            
        } catch (JDOMException ex) {
            Logger.getLogger(STEP_3_4_FixHeader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
