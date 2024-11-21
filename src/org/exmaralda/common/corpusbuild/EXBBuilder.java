/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.common.corpusbuild;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.Speaker;
import org.exmaralda.partitureditor.jexmaralda.Speakertable;
import org.exmaralda.partitureditor.jexmaralda.UDInformationHashtable;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class EXBBuilder {
    
    String corpusName;
    File topDirectory;
    String uniqueSpeakerDistinction = "descendant::abbreviation";
    String segmentation = "default";
    
    
    public EXBBuilder(String corpusName, File topDirectory, String uniqueSpeakerDistinction, String segmentation){
        this.corpusName = corpusName;
        this.topDirectory = topDirectory;
        this.uniqueSpeakerDistinction = uniqueSpeakerDistinction;
        this.segmentation = segmentation;
    }
    
    public void build() throws IOException, SAXException, JexmaraldaException, JDOMException{
        List<File> exbFiles = collectEXBFiles();
        segmentEXBFiles(exbFiles);
        constructComa(exbFiles);
    }

    private List<File> collectEXBFiles() throws IOException {
        List<File> result = new ArrayList<>();
        try (Stream<Path> walkStream = Files.walk(topDirectory.toPath())) {
            walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                if (f.toString().endsWith(".exb")) {
                    result.add(f.toFile());
                }
            });
        }    
        return result;
    }

    private void segmentEXBFiles(List<File> exbFiles) throws SAXException, JexmaraldaException, IOException {
        for (File exbFile : exbFiles){
            BasicTranscription exb = new BasicTranscription(exbFile.getAbsolutePath());
            SegmentedTranscription exs = exb.toSegmentedTranscription();
            File exsOut = new File(exbFile.getParentFile(), exbFile.getName().replaceAll("\\.exb", "_s.exs"));
            exs.writeXMLToFile(exsOut.getAbsolutePath(), "none");
            System.out.println("[Segmented]: " + exbFile.getAbsolutePath() + " --> " + exsOut.getName());
        }
    }

    private void constructComa(List<File> exbFiles) throws SAXException, JexmaraldaException, IOException, JDOMException {
        Map<String, Element> speakerElements = new HashMap<>();
        Document comaDocument = new Document(new Element("Corpus"));
        comaDocument.getRootElement().setAttribute("uniqueSpeakerDistinction", "//speaker/" + this.uniqueSpeakerDistinction);
        comaDocument.getRootElement().setAttribute("Name", corpusName);
        comaDocument.getRootElement().setAttribute("Id", corpusName);
        
        for (File exbFile : exbFiles){
            BasicTranscription exb = new BasicTranscription(exbFile.getAbsolutePath());            
            Element communicationElement = new Element("Communication")
                    .setAttribute("Name", exb.getHead().getMetaInformation().getTranscriptionName())
                    .setAttribute("Id", exb.getHead().getMetaInformation().getTranscriptionName());
            comaDocument.getRootElement().addContent(communicationElement);
            
            // Metadata
            Element communicationDescriptionElement = new Element("Description");
            communicationElement.addContent(communicationDescriptionElement);
            UDInformationHashtable udMetaInformation = exb.getHead().getMetaInformation().getUDMetaInformation();
            for (String attribute : udMetaInformation.getAllAttributes()){
                String value = udMetaInformation.getValueOfAttribute(attribute);
                Element keyElement = new Element("Key")
                        .setAttribute("Name", attribute)
                        .setText(value);
                communicationDescriptionElement.addContent(keyElement);
            }
            communicationDescriptionElement.addContent(new Element("Key")
                            .setAttribute("Name", "Project name").setText(exb.getHead().getMetaInformation().getProjectName()));
            communicationDescriptionElement.addContent(new Element("Key")
                            .setAttribute("Name", "Transcription convention").setText(exb.getHead().getMetaInformation().getTranscriptionConvention()));
            communicationDescriptionElement.addContent(new Element("Key")
                            .setAttribute("Name", "Comment").setText(exb.getHead().getMetaInformation().getComment()));
            
            // Speakers
            Element settingElement = new Element("Setting");
            communicationElement.addContent(settingElement);
            Speakertable st = exb.getHead().getSpeakertable();
            for (int i=0; i<st.getNumberOfSpeakers(); i++){
                Speaker s = st.getSpeakerAt(i);
                Element exbSpeakerElement = IOUtilities.readElementFromString(s.toXML());
                Element uElement = (Element) XPath.selectSingleNode(exbSpeakerElement, this.uniqueSpeakerDistinction);
                String sID = uElement.getText();
                if (!(speakerElements.containsKey(sID))){
                    Element speakerElement = makeSpeakerElement(sID, s);
                    speakerElements.put(sID, speakerElement);
                }
                Element personElement = new Element("Person");
                personElement.setText(sID);
                settingElement.addContent(personElement);
            }
            
            // Transcripts
            /*
                <Transcription Id="CIDID85A4C1B1-F356-1E48-7116-003EC186E84A">
                  <Name>So_K6_EZ_G2a_prep</Name>
                  <Filename>So_K6_EZ_G2a_prep.exb</Filename>
                  <NSLink>So_K6_EZ_G2a_prep.exb</NSLink>
                  <Description>
                    <Key Name="segmented">false</Key>
                  </Description>
                  <Availability>
                    <Available>false</Available>
                    <ObtainingInformation/>
                  </Availability>
                </Transcription>            
            */
            
            Path relativePath = topDirectory.toPath().relativize(exbFile.toPath());
            
            Element transcriptionElement1 = new Element("Transcription")
                    .setAttribute("Id", "EXB_" + exb.getHead().getMetaInformation().getTranscriptionName());
            transcriptionElement1.addContent(new Element("Name").setText(exb.getHead().getMetaInformation().getTranscriptionName()));
            transcriptionElement1.addContent(new Element("Filename").setText(exbFile.getName()));
            transcriptionElement1.addContent(new Element("NSLink").setText(relativePath.toString().replace(File.separatorChar, '/')));
            communicationElement.addContent(transcriptionElement1);
            Element transcriptionDescriptionElement = new Element("Description");
            transcriptionElement1.addContent(transcriptionDescriptionElement);
            transcriptionDescriptionElement.addContent(new Element("Key").setAttribute("Name", "segmented").setText("false"));
            
            Element transcriptionElement2 = new Element("Transcription")
                    .setAttribute("Id", "EXS_" + exb.getHead().getMetaInformation().getTranscriptionName());
            transcriptionElement2.addContent(new Element("Name").setText(exb.getHead().getMetaInformation().getTranscriptionName()));
            transcriptionElement2.addContent(new Element("Filename").setText(exbFile.getName().replaceAll("\\.exb", "_s.exs")));
            transcriptionElement2.addContent(new Element("NSLink").setText(relativePath.toString().replace(File.separatorChar, '/').replaceAll("\\.exb", "_s.exs")));
            communicationElement.addContent(transcriptionElement2);
            Element transcriptionDescriptionElement2 = new Element("Description");
            transcriptionElement2.addContent(transcriptionDescriptionElement2);
            transcriptionDescriptionElement2.addContent(new Element("Key").setAttribute("Name", "segmented").setText("true"));
            
            
            
        }
        
        for (String id : speakerElements.keySet()){
           comaDocument.getRootElement().addContent(speakerElements.get(id));
        }
        
        File comaOutFile = new File(topDirectory, corpusName + ".coma");
        FileIO.writeDocumentToLocalFile(comaOutFile.getAbsolutePath(), comaDocument);
        
    }

    
    /*
        <Speaker Id="KOMP_S_00469">
          <Sigle>Sm_K6b_BaO_G2a.Daniela</Sigle>
          <Pseudo>DAN</Pseudo>
          <Sex>female</Sex>
          <Description>
            <Key Name="UniqueID">Sm_K6b_BaO_G2a.Daniela</Key>
            <Key Name="Role">Proband/in</Key>
          </Description>
          <Language Type="L1">
            <LanguageCode>deu</LanguageCode>
            <Description/>
          </Language>
        </Speaker>    
    */
    private Element makeSpeakerElement(String sID, Speaker s) {
        Element speakerElement = new Element("Speaker");
        speakerElement.setAttribute("Id", sID);
        speakerElement.addContent(new Element("Sigle").setText(sID));
        speakerElement.addContent(new Element("Pseudo").setText(s.getAbbreviation()));
        String sex = "male";
        if (s.getSex()=='f') sex = "female";
        speakerElement.addContent(new Element("Sex").setText(sex));
        Element speakerDescriptionElement = new Element("Description");
        speakerElement.addContent(speakerDescriptionElement);
        UDInformationHashtable udMetaInformation = s.getUDSpeakerInformation();
        for (String attribute : udMetaInformation.getAllAttributes()){
            String value = udMetaInformation.getValueOfAttribute(attribute);
            Element keyElement = new Element("Key")
                    .setAttribute("Name", attribute)
                    .setText(value);
            speakerDescriptionElement.addContent(keyElement);
        }
        
        return speakerElement;
    }
    
}
