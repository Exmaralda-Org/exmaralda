/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.partitureditor.sound.ELANDSPlayer;
import org.exmaralda.partitureditor.sound.JMFPlayer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class InsertZWAudioMetadata extends AbstractSchneiderProcessor {
    
    File transcriptDirectory;
    
    
    public InsertZWAudioMetadata(String[] args){
        inputDirectory = new File(args[0]);
        inputSuffix = args[1];
        outputDirectory = new File(args[2]);
        outputDirectory.mkdir();
        for (File f : outputDirectory.listFiles()){
            f.delete();
        }
        outputSuffix = args[3];        
        
        inputFiles = inputDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(inputSuffix.toUpperCase());
            }            
        });
        
        transcriptDirectory = new File(args[4]);

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String[] myArgs = {
                "Y:\\thomas\\ZW_HE\\Meta\\zw+he_3", "xml", 
                "Y:\\thomas\\ZW_HE\\Meta\\zw+he_4", "xml",
                "Y:\\thomas\\ZW_HE"
            };
            InsertZWAudioMetadata aa = new InsertZWAudioMetadata(myArgs);
            aa.processFiles();
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(FixNonAlignedTranscripts.class.getName()).log(Level.SEVERE, null, ex);
        }        
    } 
    
    private void processFiles() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException{
        for (File inputFile : inputFiles){
            ELANDSPlayer audioPlayer = new ELANDSPlayer();
            System.out.println("Processing " + inputFile.getName());                        
            Document xmlDocument = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());
            // <SE-Aufnahme Kennung="ZW--_E_00114_SE_01_A_02">
            Element seAufnahme = (Element) XPath.newInstance("//SE-Aufnahme[ends-with(@Kennung,'_A_02')]").selectSingleNode(xmlDocument);
            if (seAufnahme !=null){
                  System.out.println("Changing...");
                  Element e1 = seAufnahme.getChild("Basisdaten").getChild("Relation_zu_Quellaufnahme").getChild("Vollständigkeit");
                  e1.setText("Vollständig");
                  Element e2 = seAufnahme.getChild("Basisdaten").getChild("Relation_zu_Quellaufnahme").getChild("Zeitabschnitt");
                  e2.setText("Vollständig");

                  Element e3 = seAufnahme.getChild("Basisdaten").getChild("Relation_zu_SE").getChild("Vollständigkeit");
                  e3.setText("Vollständig");
                  Element e4 = seAufnahme.getChild("Basisdaten").getChild("Relation_zu_SE").getChild("Zeitabschnitt");
                  e4.setText("Vollständig");
                  
                  String aufnahmeKennung = seAufnahme.getChild("Digitale_Fassung").getAttributeValue("Kennung");
                  File audioFile = new File("Y:\\media\\audio\\ZW_2\\" + aufnahmeKennung + ".WAV");
                  System.out.print(audioFile.getAbsolutePath() + " ");
                  if (audioFile.exists()){
                      System.out.println("exists.");
                  } else {
                      System.out.println("is missing!!!!!");
                  }
                  audioPlayer.setSoundFile(audioFile.getAbsolutePath());
                  double lengthInSeconds = audioPlayer.getTotalLength();
                  String dauer = "00:" + org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(lengthInSeconds * 1000.0,0);
                  System.out.println("Dauer: " + dauer);
                  seAufnahme.getChild("Basisdaten").getChild("Dauer").setText(dauer);
                  
                  String fileSize = Long.toString(audioFile.length());
                  seAufnahme.getChild("Digitale_Fassung").getChild("Basisdaten").getChild("Dateigröße").setText(fileSize);
                  
                  //ELANDSPlayer audioPlayer2 = new ELANDSPlayer();
                  Element seAufnahme1 = (Element) XPath.newInstance("//SE-Aufnahme[ends-with(@Kennung,'_A_01')]").selectSingleNode(xmlDocument);
                  String aufnahmeKennung1 = seAufnahme1.getChild("Digitale_Fassung").getAttributeValue("Kennung");
                  File audioFile1 = new File("Y:\\media\\audio\\ZW_2\\" + aufnahmeKennung1 + ".WAV");
                  audioPlayer.setSoundFile(audioFile1.getAbsolutePath());
                  double lengthInSeconds1 = audioPlayer.getTotalLength();
                  String dauer1 = "00:" + org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(lengthInSeconds1 * 1000.0,0);
                  System.out.println("Dauer: " + dauer1);
                  seAufnahme1.getChild("Basisdaten").getChild("Dauer").setText(dauer1);

                  FileIO.writeDocumentToLocalFile(makeOutputPath(inputFile), xmlDocument);
                  
                  audioPlayer = null;
                  
                  System.gc();
            
            }

/*            Document transcript = FileIO.readDocumentFromLocalFile(tryTranscript.getAbsolutePath());
            double first = Double.parseDouble(((Attribute) (XPath.newInstance("//timepoint[1]/@absolute-time").selectSingleNode(transcript))).getValue());
            double last = Double.parseDouble(((Attribute) (XPath.newInstance("//timepoint[last()]/@absolute-time").selectSingleNode(transcript))).getValue());
            double duration = (last - first) * 1000.0;
            String dauer = "00:" + org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(duration,0);
            String zeitabschnitt = "00:" + org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(1000.0 * first ,0)
                                    + " - "
                                    + "00:" + org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(1000.0 * last ,0);
            
            String transformed = ssf.applyExternalStylesheetToExternalXMLFile(STYLESHEET_PATH, inputFile.getAbsolutePath());
            Document transformedDocument = FileIO.readDocumentFromString(transformed);
            Object o = XPath.newInstance("//Transkript[1]//Dateiname").selectSingleNode(transformedDocument);
            String fln = ((Element)o).getText();
            File f = new File(transcriptDirectory, fln);
            Element e = (Element) XPath.newInstance("//Transkript[1]//Dateigröße").selectSingleNode(transformedDocument);
            e.setText(Long.toString(f.length()));
            
            HashtableTokenList htl = new HashtableTokenList();
            File[] fs = {f};
            htl.readWordsFromFolkerFiles(fs);
            int types = htl.getNumberOfTokens();
            int tokens = XPath.newInstance("//w").selectNodes(FileIO.readDocumentFromLocalFile(f.getAbsolutePath())).size();
            
            System.out.println("Types: " + types);
            System.out.println("Tokens: " + tokens);
            System.out.println("Dauer: " + dauer);
            System.out.println("Zeitabschnitt: " + zeitabschnitt);
            
            e = (Element) XPath.newInstance("//Transkript[1]//Types").selectSingleNode(transformedDocument);
            e.setText(Integer.toString(types));

            e = (Element) XPath.newInstance("//Transkript[1]//Tokens").selectSingleNode(transformedDocument);
            e.setText(Integer.toString(tokens));*/

            /*e = (Element) XPath.newInstance("//Transkript[1]//Dauer").selectSingleNode(transformedDocument);
            e.setText(dauer);

            e = (Element) XPath.newInstance("//Transkript[1]//Zeitabschnitt").selectSingleNode(transformedDocument);
            e.setText(zeitabschnitt);*/

            /*List l = XPath.newInstance("//text()")
                    .selectNodes(transformedDocument);
            for (Object t : l){
                Text text = (Text)t;
                if (text.getTextNormalize().length()==0){
                    text.detach();
                }
            }
            FileIO.writeDocumentToLocalFile(makeOutputPath(inputFile), transformedDocument);*/
        }                
    }
    
}
