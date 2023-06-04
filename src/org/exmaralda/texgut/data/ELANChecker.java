/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.texgut.data;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.texgut.data.ELANMessage.ELANMessageType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class ELANChecker {

    public static final String CORRECT_SCHEMA = "http://www.mpi.nl/tools/elan/EAFv3.0.xsd";
    // 1-21-1-3-a.eaf
    public static final String EAF_NAME_REGEX = "\\d{1,4}-\\d{1,4}-\\d{1,4}-\\d{1,4}-[a-z]\\.eaf";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String PATH = "C:\\Users\\bernd\\Dropbox\\work\\2021_MARGO_TEXAS_GERMAN\\TGDP-Cleanup\\5\\1-1-1\\1-1-1-3-a.eaf";
        //System.out.println(ELANChecker.EAF_NAME_REGEX);
        List<ELANMessage> messages = new ELANChecker().checkFile(new File(PATH));
        if (messages.isEmpty()){
            System.out.println("No messages!");
        } else {
            for (ELANMessage m : messages){
                System.out.println(m.description);
            }
        }
    }

    //////////////////////////////////////////////////////////////////

    public List<ELANMessage> checkFile(File eafFile){
        List<ELANMessage> result = new ArrayList<>();
        try {
            Document eafDoc = FileIO.readDocumentFromLocalFile(eafFile);

            result.addAll(checkFilename(eafFile));
            result.addAll(checkAudio(eafFile, eafDoc));
            result.addAll(checkSchema(eafFile, eafDoc));
            result.addAll(validate(eafFile));
            result.addAll(checkLinguisticTypes(eafFile, eafDoc));
            
            
        } catch (JDOMException ex) {
            //Logger.getLogger(ELANChecker.class.getName()).log(Level.SEVERE, null, ex);
            result.add(new ELANMessage(ELANMessageType.ERROR, "JDOMException: " + ex.getMessage(), eafFile));
        } catch (IOException ex) {
            //Logger.getLogger(ELANChecker.class.getName()).log(Level.SEVERE, null, ex);
            result.add(new ELANMessage(ELANMessageType.ERROR, "IOException: " + ex.getMessage(), eafFile));
        } catch (URISyntaxException ex) {
            //Logger.getLogger(ELANChecker.class.getName()).log(Level.SEVERE, null, ex);
            result.add(new ELANMessage(ELANMessageType.ERROR, "URISyntaxException: " + ex.getMessage(), eafFile));
        }
        return result;
    }

    //////////////////////////////////////////////////////////////////
    
    private List<ELANMessage> checkFilename(File eafFile) {
        List<ELANMessage> result = new ArrayList<>();
        if (!(eafFile.getName().matches(EAF_NAME_REGEX))){
            result.add(new ELANMessage(ELANMessageType.WARNING, "Filename " + eafFile.getName() + " does not match pattern.", eafFile));            
        }
        return result;        
    }

    //////////////////////////////////////////////////////////////////
    
    private List<ELANMessage> checkAudio(File eafFile, Document eafDoc) throws JDOMException, MalformedURLException, URISyntaxException {
        List<ELANMessage> result = new ArrayList<>();
        /*
        <MEDIA_DESCRIPTOR MEDIA_URL="file:///D:/tgdp_20230210/prod/sound_files/1-1-1/1-1-1-3-a.wav"
            RELATIVE_MEDIA_URL="../AUDIO/1-1-1-3-a.wav" MIME_TYPE="audio/x-wav"/>        
        */
        Element mediaDescriptor = (Element) XPath.selectSingleNode(eafDoc, "//MEDIA_DESCRIPTOR");
        if (mediaDescriptor==null){
            result.add(new ELANMessage(ELANMessageType.ERROR, "No MEDIA_DESCRIPTOR found.", eafFile));                                    
        } else {
            String mediaURL = mediaDescriptor.getAttributeValue("MEDIA_URL");
            try {
                File mediaFile = Paths.get(new URL(mediaURL).toURI()).toFile();
                if (!(mediaFile.exists())){
                    result.add(new ELANMessage(ELANMessageType.WARNING, "Audio with MEDIA_URL " + mediaURL + " does not exist.", eafFile));                        
                }
                String name = mediaFile.getName();
                if (!(name.endsWith(".wav"))){
                    result.add(new ELANMessage(ELANMessageType.WARNING, "MEDIA_URL " + mediaURL + " does not end in '.wav'.", eafFile));                                    
                }
                String nakedName = name.substring(0, name.indexOf("."));
                String nakedFilename = eafFile.getName().substring(0, eafFile.getName().indexOf("."));
                if (!(nakedName.equals(nakedFilename))){
                    result.add(new ELANMessage(ELANMessageType.WARNING, "Filename " + eafFile.getName() + " of ELAN file does not match audio filename " + name, eafFile));                                                
                }
            } catch (java.lang.IllegalArgumentException ex){
                result.add(new ELANMessage(ELANMessageType.ERROR, "IllegalArgumentException: " + ex.getMessage(), eafFile));                                                
            }
        }
        return result;        
    }

    //////////////////////////////////////////////////////////////////
    
    private List<ELANMessage> validate(File eafFile) {
        List<ELANMessage> result = new ArrayList<>();
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = factory.newSchema(new URL(ELANChecker.CORRECT_SCHEMA));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(eafFile));
        } catch (SAXException ex) {
            Logger.getLogger(ELANChecker.class.getName()).log(Level.SEVERE, null, ex);
            result.add(new ELANMessage(ELANMessageType.ERROR, "SAXException: " + ex.getMessage(), eafFile));
        } catch (IOException ex) {
            Logger.getLogger(ELANChecker.class.getName()).log(Level.SEVERE, null, ex);
            result.add(new ELANMessage(ELANMessageType.ERROR, "IOException: " + ex.getMessage(), eafFile));
        }
        return result;
    }
    
    //////////////////////////////////////////////////////////////////

    private List<ELANMessage> checkLinguisticTypes(File eafFile, Document eafDoc) throws JDOMException {
        /*<LINGUISTIC_TYPE GRAPHIC_REFERENCES="false" LINGUISTIC_TYPE_ID="TRANSCRIPTION" TIME_ALIGNABLE="true"/>
        <LINGUISTIC_TYPE GRAPHIC_REFERENCES="false" LINGUISTIC_TYPE_ID="TRANSLATION" CONSTRAINTS="Symbolic_Association" TIME_ALIGNABLE="true"/>*/
        List<ELANMessage> result = new ArrayList<>();
        
        Element lingTypeTranscription = (Element)(XPath.selectSingleNode(eafDoc, "//LINGUISTIC_TYPE[@LINGUISTIC_TYPE_ID='TRANSCRIPTION']"));
        if (lingTypeTranscription==null){
            String message = "No LINGUISTIC_TYPE 'TRANSCRIPTION' defined.";
            result.add(new ELANMessage(ELANMessageType.ERROR, message, eafFile));            
        } else {
            String timeAlignable = lingTypeTranscription.getAttributeValue("TIME_ALIGNABLE");
            if (!("true").equals(timeAlignable)){
                String message = "LINGUISTIC_TYPE 'TRANSCRIPTION' not defined as time-alignable='true'.";
                result.add(new ELANMessage(ELANMessageType.ERROR, message, eafFile));            
                
            }
        }
        
        
        Element lingTypeTranslation = (Element)(XPath.selectSingleNode(eafDoc, "//LINGUISTIC_TYPE[@LINGUISTIC_TYPE_ID='TRANSLATION']"));
        if (lingTypeTranslation==null){
            String message = "No LINGUISTIC_TYPE 'TRANSLATION' defined.";
            result.add(new ELANMessage(ELANMessageType.ERROR, message, eafFile));            
        } else {
            String timeAlignable = lingTypeTranslation.getAttributeValue("TIME_ALIGNABLE");
            if (!("true").equals(timeAlignable)){
                String message = "LINGUISTIC_TYPE 'TRANSLATION' not defined as time-alignable='true'.";
                result.add(new ELANMessage(ELANMessageType.ERROR, message, eafFile));                            
            }
        }
        
        List allLinguisticTypes = XPath.selectNodes(eafDoc, "//LINGUISTIC_TYPE[not(@LINGUISTIC_TYPE_ID='TRANSCRIPTION' or @LINGUISTIC_TYPE_ID='TRANSLATION')]");
        if (!(allLinguisticTypes.isEmpty())){
            String message = "There are linguistic types other than 'TRANSCRIPTION' or 'TRANSLATION'.";
            result.add(new ELANMessage(ELANMessageType.WARNING, message, eafFile));                        
        }
        
        return result;
    }


    private List<ELANMessage> checkSchema(File eafFile, Document eafDoc) {
        // <ANNOTATION_DOCUMENT xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
        // AUTHOR="X" DATE="1971-11-14T19:30:00-00:00" FORMAT="3.0" VERSION="3.0" 
        // xsi:noNamespaceSchemaLocation="http://www.mpi.nl/tools/elan/EAFv3.0.xsd">

        List<ELANMessage> result = new ArrayList<>();
        Namespace xsiNamespace = Namespace.getNamespace("http://www.w3.org/2001/XMLSchema-instance");
        String schemaURL = eafDoc.getRootElement().getAttributeValue("noNamespaceSchemaLocation", xsiNamespace);
        if (schemaURL==null){
            String message = "No schema reference present.";
            result.add(new ELANMessage(ELANMessageType.ERROR, message, eafFile));            
        } else {
            if (!schemaURL.equals(ELANChecker.CORRECT_SCHEMA)){
                String message = "Schema reference is " + schemaURL + ", should be " + ELANChecker.CORRECT_SCHEMA + ".";
                result.add(new ELANMessage(ELANMessageType.ERROR, message, eafFile));
            }
        }
        String format = eafDoc.getRootElement().getAttributeValue("FORMAT");
        if (!(format.equals("3.0"))){
            String message = "FORMAT attribute is " + format + ", should be 3.0.";
            result.add(new ELANMessage(ELANMessageType.ERROR, message, eafFile));            
        }
        String version = eafDoc.getRootElement().getAttributeValue("VERSION");
        if (!(version.equals("3.0"))){
            String message = "VERSION attribute is " + version + ", should be 3.0.";
            result.add(new ELANMessage(ELANMessageType.ERROR, message, eafFile));            
        }
        return result;
        
    }

    
}
