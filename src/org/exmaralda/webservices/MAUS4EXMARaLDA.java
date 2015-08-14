/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.fsm.FSMSaxReader;
import org.exmaralda.partitureditor.fsm.FiniteStateMachine;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.SegmentList;
import org.exmaralda.partitureditor.jexmaralda.Segmentation;
import org.exmaralda.partitureditor.jexmaralda.TimedSegment;
import org.exmaralda.partitureditor.jexmaralda.convert.PraatConverter;
import org.exmaralda.partitureditor.jexmaralda.segment.GenericSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation;
import org.exmaralda.partitureditor.sound.AudioProcessor;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class MAUS4EXMARaLDA {
    
    AudioProcessor audioProcessor = new AudioProcessor();
        
    HashMap<String, Object> parameters;
    
    
    public void setParameters(HashMap<String, Object> parameters){
        this.parameters = parameters;
    }
    
    
     /* LANGUAGE", languageComboBox.getSelectedItem());
        SEGMENT-CHAIN-SELECTION", segmentChainSelectionRadioButton.isSelected());
        USE-SEGMENTATION", segmentRadioButton.isSelected());
        SEGMENTATION-ALGORITHM", segmentationComboBox.getSelectedItem());
        WORDS-ORTHOGRAPHIC", wordsOrthoCheckBox.isSelected());
        WORDS-SAMPA", wordsSAMPACheckBox.isSelected());
        PHONEMES", phonemesCheckBox.isSelected());
        MERGE", mergeWithExistingRadioButton.isSelected()); */
    
    public String allText = "";        
    public String modifiedStartID;
    public String modifiedEndID;

    public File[] createMAUSInputFiles(BasicTranscription bt, String tierID, String startID, String endID) throws JexmaraldaException, IOException, SAXException, FSMException, JDOMException{
                        
        
        // **************************************
        // 0. check the selection
        // **************************************
        int selectionStartCol = bt.getBody().getCommonTimeline().lookupID(startID);
        int selectionStartRow = bt.getBody().lookupID(tierID);
        if (selectionStartRow<0 || selectionStartCol<0){
            throw new JexmaraldaException(112, "Invalid selection");
        } else if (!(bt.getBody().getTierWithID(tierID).containsEventAtStartPoint(startID))){
            throw new JexmaraldaException(112, "Selection is on empty event.");
        }

        // **************************************
        // 1. get the text from the transcription
        // **************************************
        Boolean segmentChain = (Boolean) parameters.get("SEGMENT-CHAIN-SELECTION");
        allText = ""; 
        modifiedStartID = startID;
        modifiedEndID = endID;

        
        if (segmentChain){
            if (!(bt.getBody().getTierAt(selectionStartRow).getType().equals("t"))){
                throw new JexmaraldaException(112, "Selection is not in tier of type 't'");
            } else {
                Segmentation s = bt.toSegmentedTranscription().getBody().getSegmentedTierWithID(tierID).getSegmentationWithName("SpeakerContribution_Event");
                SegmentList sl = s.getAllSegmentsWithName("sc");
                for (Object o : sl){
                    TimedSegment ts = (TimedSegment)o;
                    int thisStart = bt.getBody().getCommonTimeline().lookupID(ts.getStart());
                    int thisEnd = bt.getBody().getCommonTimeline().lookupID(ts.getEnd());
                    if (selectionStartCol>=thisStart && selectionStartCol<=thisEnd){
                        //we found the right segment chain!
                        modifiedStartID = ts.getStart();
                        modifiedEndID = ts.getEnd();
                        allText = ts.getDescription();
                        break;
                    }
                }                
            }            
        } else {
            Vector<Event> events = bt.getBody().getTierWithID(tierID).getEventsBetween(bt.getBody().getCommonTimeline(), startID, endID);        
            for (Event e : events){
                allText+=e.getDescription();
            }
        }
        
        // *********************************************
        // 2. use a segmentation algorithm if so desired
        // *********************************************
        Boolean useSegmentation = (Boolean) parameters.get("USE-SEGMENTATION");

        if (useSegmentation){            
            String whichSegmentation = (String) parameters.get("SEGMENTATION-ALGORITHM");
            
            FiniteStateMachine fsm = null;
            String segmentName = "";
            if ("HIAT".equals(whichSegmentation)){
                fsm = new FSMSaxReader().readFromStream(getClass().getResourceAsStream(HIATSegmentation.utteranceWordFSM));
                segmentName = "HIAT:w";
            } else if ("GENERIC".equals(whichSegmentation)){
                fsm = new FSMSaxReader().readFromStream(getClass().getResourceAsStream(GenericSegmentation.wordFSM));
                segmentName = "GEN:w";                
            }
            
            String output = fsm.process(allText);
            Document doc = FileIO.readDocumentFromString(output);
            List l = XPath.selectNodes(doc, "//ts[@n='" + segmentName + "']");
            String segmentedText = "";
            for (Object o : l){
                Element e = (Element)o;
                segmentedText+= e.getText() + " ";
            }
            allText = segmentedText;
        }        
        
        System.out.println("MAUS TEXT: " + allText);
        
        // **************************************
        // 3. write the text file
        // **************************************
        File textFile = File.createTempFile("EXMARaLDA_MAUS", ".TXT");
        textFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(textFile);
        fos.write(allText.getBytes("UTF-8"));
        fos.close();                    
        
        
        // **************************************
        // 3. convert and write the audio file
        // **************************************
        // make sure all timeline items have absolute times
        bt.getBody().getCommonTimeline().completeTimes(false, bt, true);                
        double startTime = bt.getBody().getCommonTimeline().getTimelineItemWithID(modifiedStartID).getTime();
        double endTime = bt.getBody().getCommonTimeline().getTimelineItemWithID(modifiedEndID).getTime();
        
        String wavPath = bt.getHead().getMetaInformation().getReferencedFile("wav");
        File wavFile = new File(wavPath);
        File audioFile = convertAudioFileToMono(cutAudioFile(wavFile, startTime, endTime));
        
        File[] result = {textFile, audioFile};
        
        return result;
    };
    
    File cutAudioFile(File inputFile, double start, double end) throws IOException{
       File tempFile = File.createTempFile("EXMARaLDA_MAUS", ".WAV");
       tempFile.deleteOnExit();
       audioProcessor.writePart(start, end, inputFile.getAbsolutePath(), tempFile.getAbsolutePath());   
       return tempFile;
    }
    
    File convertAudioFileToMono(File inputFile) throws IOException{
       File tempFile = File.createTempFile("EXMARaLDA_MAUS", ".WAV");
       //tempFile.deleteOnExit();
       audioProcessor.stereoToMono16kHz(inputFile, tempFile);
       return tempFile;
    }
    
    public static void main(String[] args){
        try {
            BasicTranscription bt = new BasicTranscription("S:\\Korpora\\EXMARaLDA-Demokorpus\\Rudi\\Rudi_Voeller_Wutausbruch.exb");
            MAUS4EXMARaLDA m4e = new MAUS4EXMARaLDA();
            File[] files = m4e.createMAUSInputFiles(bt, "TIE6", "T12", "T15");
            MAUSConnector mc = new MAUSConnector();
            String[][] parameters = {
                {"LANGUAGE","deu"}
            };

            
            String result = mc.callMAUS(files[0], files[1], null);
            File temp = File.createTempFile("MAUSRESULT", ".textGrid");
            temp.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(temp);
            fos.write(result.getBytes("UTF-8"));
            fos.close();                    


            PraatConverter pc = new PraatConverter();
            BasicTranscription bt2 = pc.readPraatFromFile(temp.getAbsolutePath(), "UTF-8");
            bt2.getHead().getMetaInformation().setReferencedFile(files[1].getAbsolutePath());
            bt2.writeXMLToFile("C:\\Users\\Schmidt\\Desktop\\TEST\\MausOut2.exb", "none");
        } catch (IOException ex) {
            Logger.getLogger(MAUS4EXMARaLDA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(MAUS4EXMARaLDA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(MAUS4EXMARaLDA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(MAUS4EXMARaLDA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FSMException ex) {
            Logger.getLogger(MAUS4EXMARaLDA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    
    
    
}
