/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.sound.AudioProcessor;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class Whisper4EXMARaLDA {

    
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

    public File createWhisperInputFile(BasicTranscription bt, double startTime, double endTime) throws JexmaraldaException, IOException, SAXException, FSMException, JDOMException{
                        
        
        
        // **************************************
        // 3. convert and write the audio file
        // **************************************
        // make sure all timeline items have absolute times
        String wavPath = bt.getHead().getMetaInformation().getReferencedFile("wav");
        File wavFile = new File(wavPath);
        File audioFile = convertAudioFileToMono(cutAudioFile(wavFile, startTime, endTime));
        
        return audioFile;
        
    };
    
    public File cutAudioFile(File inputFile, double start, double end) throws IOException{
       File tempFile = File.createTempFile("EXMARaLDA_MAUS", ".WAV");
       System.out.println("Temp file: " + tempFile.getAbsolutePath());
       tempFile.deleteOnExit();
       audioProcessor.writePart(start, end, inputFile.getAbsolutePath(), tempFile.getAbsolutePath());   
       return tempFile;
    }
    
    public File convertAudioFileToMono(File inputFile) throws IOException{
       File tempFile = File.createTempFile("EXMARaLDA_MAUS", ".WAV");
       //tempFile.deleteOnExit();
       audioProcessor.stereoToMono16kHz(inputFile, tempFile);
       return tempFile;
    }
    
    public static void main(String[] args){
        try {
            BasicTranscription bt = new BasicTranscription("C:\\Users\\bernd\\Dropbox\\EXMARaLDA-Demokorpus\\PaulMcCartney\\PaulMcCartney.exb");
            Whisper4EXMARaLDA m4e = new Whisper4EXMARaLDA();
            File file = m4e.createWhisperInputFile(bt, 0.8, 1.6);
            WhisperConnector whisperConnector = new WhisperConnector("");           
            String result = whisperConnector.callWhisperSimple(file);            
            System.out.println(result);
        } catch (IOException | SAXException | JexmaraldaException | JDOMException | FSMException | URISyntaxException ex) {
            Logger.getLogger(Whisper4EXMARaLDA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    public static String getWhisperKey() {
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor");        
        // retrieve values from preferences
        String apiKey = settings.get("WHISPER-API-KEY", "");
        return apiKey;
    }
    
    
    
}
