/*
 * AudioProcessor.java
 *
 * Created on 19. April 2005, 09:31
 */

package org.exmaralda.partitureditor.sound;


import java.io.*;
import javax.sound.sampled.*;
import org.exmaralda.partitureditor.jexmaralda.*;
/**
 *
 * @author  thomas
 */
public class AudioProcessor {
    
    private AudioInputStream audioInputStream;
    private AudioFormat audioFormat = null;
    private SourceDataLine line = null;
    private File soundFile = null;

    /** Creates a new instance of AudioProcessor */
    public AudioProcessor() {
    }
    
    public static boolean isCuttable(String pathToSoundFile){
        File sf = new File(pathToSoundFile);
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(sf);    
            AudioFormat af = ais.getFormat();        
            if (af.getFrameSize() == AudioSystem.NOT_SPECIFIED){
                return false;
            }
            ais.close();
            return true;
        } catch (UnsupportedAudioFileException uafe){
            return false;
        } catch (IOException ioe){
            return false;
        }
    }
    
    public void writePart(double startTime, double endTime, String pathToSoundFile, String output) throws IOException {            

        System.out.println("Cutting " + pathToSoundFile);
        soundFile = new File(pathToSoundFile);
        try {
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);    
        } catch (UnsupportedAudioFileException uafe){
            IOException wrappedException = new IOException("Unsupported audio file:" + uafe.getLocalizedMessage());
            throw wrappedException;
        }
        audioFormat = audioInputStream.getFormat();        

        if (audioFormat.getFrameSize() == AudioSystem.NOT_SPECIFIED){
            IOException wrappedException = new IOException("Audio format does not support this operation.");
            throw wrappedException;
        }

        long start = (int)Math.round(startTime * audioFormat.getFrameRate()); // in frames
        long length = (int)Math.round((endTime - startTime) * audioFormat.getFrameRate());; // in frames
        // changed 30-11-2011 for audio cutting servlet
        long streamLength = audioInputStream.getFrameLength();
        if ((start + length) > streamLength){
            length = streamLength - start;
        }
        if ((start<0) || ((start+length)> audioInputStream.getFrameLength())){
            String message = "At least one time value is illegal for this audio file: " + Double.toString(startTime) + "/" + Double.toString(endTime);
            IOException wrappedException = new IOException(message);
            throw wrappedException;                
        }

        int frameSize = audioFormat.getFrameSize();
        audioInputStream.skip(start * frameSize);
        AudioInputStream derivedAIS = new AudioInputStream(audioInputStream,
                                             audioFormat, length);        
        File outputFile = new File(output);
        AudioSystem.write(derivedAIS, AudioFileFormat.Type.WAVE, outputFile);

        derivedAIS.close();
        audioInputStream.close();
    }
    
    public void stereoToMono(File stereoFile, File monoFile) throws IOException{
        try {
            AudioInputStream stereoInputStream = AudioSystem.getAudioInputStream(stereoFile);    
            AudioInputStream monoInputStream = convertChannels(1, stereoInputStream);
            AudioSystem.write(monoInputStream, AudioFileFormat.Type.WAVE, monoFile);
        } catch (UnsupportedAudioFileException uafe){
            IOException wrappedException = new IOException("Unsupported audio file:" + uafe.getLocalizedMessage());
            throw wrappedException;
        }        
    }
    
    // convencience method for MAUS
    public void stereoToMono16kHz(File stereoFile, File monoFile) throws IOException{
        try {
            AudioInputStream stereoInputStream = AudioSystem.getAudioInputStream(stereoFile);    

            AudioFormat sourceFormat = stereoInputStream.getFormat();
            AudioFormat targetFormat = new AudioFormat(
                    sourceFormat.getEncoding(),
                    16000,
                    sourceFormat.getSampleSizeInBits(),
                    1,
                    calculateFrameSize(1,  sourceFormat.getSampleSizeInBits()),
                    sourceFormat.getFrameRate(),
                    sourceFormat.isBigEndian());
            AudioInputStream monoInputStream =  AudioSystem.getAudioInputStream(targetFormat,stereoInputStream);
            AudioSystem.write(monoInputStream, AudioFileFormat.Type.WAVE, monoFile);
        } catch (UnsupportedAudioFileException uafe){
            IOException wrappedException = new IOException("Unsupported audio file:" + uafe.getLocalizedMessage());
            throw wrappedException;
        }        
    }
    
    
    private static AudioInputStream convertChannels(int nChannels, AudioInputStream sourceStream) {
            AudioFormat sourceFormat = sourceStream.getFormat();
            AudioFormat targetFormat = new AudioFormat(
                    sourceFormat.getEncoding(),
                    sourceFormat.getSampleRate(),
                    sourceFormat.getSampleSizeInBits(),
                    nChannels,
                    calculateFrameSize(nChannels,  sourceFormat.getSampleSizeInBits()),
                    sourceFormat.getFrameRate(),
                    sourceFormat.isBigEndian());
            return AudioSystem.getAudioInputStream(targetFormat,sourceStream);
    }

private static int calculateFrameSize(int nChannels, int nSampleSizeInBits) {
		return ((nSampleSizeInBits + 7) / 8) * nChannels;
	}

public static void main(String[] args){
        try{
            String INPUT = "D:\\UEFA_2\\NDR2\\BL_0904_NDR2.wav";
            String TRANNS = "D:\\UEFA_2\\NDR2\\BL_0904_NDR2_parts.xml";
            String OUTPUT = "D:\\UEFA_2\\NDR2\\Snippets";
            String BASE = "BL_0904_";

            double START = 10.0;
            double END = 15.0;
            AudioProcessor ap = new AudioProcessor();
            
            BasicTranscription bt = new BasicTranscription(TRANNS);
            //ap.writeParts(bt.getBody().getCommonTimeline(), INPUT, OUTPUT, BASE);
            
            /*ap.writePart(START, END, INPUT, OUTPUT);
            OUTPUT = "D:\\Edinburgh\\D\\AAA_Beispiele\\PaulMcCartney\\PaulMcCartney_out2.wav";
            START = 5.0;
            END = 8.0;
            ap.writePart(START, END, INPUT, OUTPUT);*/
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    
}
