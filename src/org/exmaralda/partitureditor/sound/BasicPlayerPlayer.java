/*
 * BasicPlayerPlayer.java
 *
 * Created on 17. Oktober 2006, 14:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.sound;

import java.io.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jlgui.basicplayer.*;
//import org.tritonus.share.sampled.TAudioFormat;
//import org.tritonus.share.sampled.file.TAudioFileFormat;
import javax.sound.sampled.*;


/**
 *
 * @author thomas
 */
@Deprecated
public class BasicPlayerPlayer extends AbstractPlayer implements BasicPlayerListener {

    public BasicPlayer player;
    BasicController control;

    // Changed 13-12-2006
    // The length for a JLayer MP3 file seems to be systematically +0.23% off
    // as compared to the length for a JMF MPO3 file, which is the correct one
    static double CORRECTION_FACTOR = 1.0023;
    // Changed 13-12-2006
    // Seeking in a JLayer MP3 file seems to be systematically +1% off
    static double SEEK_CORRECTION = 0.99;


    long timeInMicroSeconds = 0;
    int bitrate;
    public int byteLength;
    double totalLength;
    private double haltTime = 0;


    /** Creates a new instance of BasicPlayerPlayer */
    public BasicPlayerPlayer() {
        player = new BasicPlayer();
        control = (BasicController)player;
        player.addBasicPlayerListener(this);
    }

    public void decreaseCurrentPosition(double time) {
        haltTime = Math.max(startTime, haltTime-time);
        this.firePosition();
    }

    public void increaseCurrentPosition(double time) {
        haltTime = Math.min(endTime, haltTime+time);
        this.firePosition();
    }

    public void setSoundFile(String pathToSoundFile) throws IOException {
        if ((pathToSoundFile==null) && (control!=null)){
            try {
                control.stop();
            } catch (BasicPlayerException ex) {
                ex.printStackTrace();
            }
            return;
        }
        try {
            control.open(new File(pathToSoundFile));

            File file = new File(pathToSoundFile);
            AudioFileFormat baseFileFormat = null;
            AudioFormat baseFormat = null;

            baseFileFormat = AudioSystem.getAudioFileFormat(file);
            //System.out.println("BaseFileFormat: " + baseFileFormat.properties().toString());
            baseFormat = baseFileFormat.getFormat();
            //System.out.println("BaseFormat: " + baseFormat.properties().toString());
            //System.out.println("BaseFormat is " + baseFormat.getClass().getCanonicalName());

            // TAudioFileFormat properties
            /*if (baseFileFormat instanceof TAudioFileFormat) {
                Map properties = ((TAudioFileFormat)baseFileFormat).properties();
                System.out.println("PROPS1: " + properties.toString());
            }*/
            // TAudioFormat properties
            /*if (baseFormat instanceof TAudioFormat) {
                Map properties = ((TAudioFormat)baseFormat).properties();
                System.out.println("PROPS2: " + properties.toString());
            }*/
            this.fireSoundfileSet();
        } catch (BasicPlayerException ex) {
            throw new IOException("BasicPlayerException: " + ex.getMessage());
        } catch (UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public double getCurrentPosition() {
        return (timeInMicroSeconds/1000000.0);
    }

    public double getTotalLength() {
        return totalLength / CORRECTION_FACTOR;
    }

    public void haltPlayback() {
        try {
            control.pause();
            haltTime = getCurrentPosition();
            this.firePlaybackHalted();
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
        }
    }

    public void resumePlayback() {
        try {
            control.resume();
            this.firePlaybackResumed();
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
        }
    }

    public void startPlayback() {
        try {
            //control.seek();
            double rate = startTime / getTotalLength();
            long bytesToSkip = Math.round(rate*byteLength*this.SEEK_CORRECTION);
            //long skipBytes = Math.round(byteLength * rate);

            System.out.println("Skipping " + bytesToSkip + " bytes.");
            control.seek(bytesToSkip);
            System.out.println("Done seeking");
            control.play();
            this.firePlaybackStarted();
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
        }
    }

    public void stopPlayback() {
        try {
            control.stop();
            this.firePlaybackStopped();
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Open callback, stream is ready to play.
     *
     * properties map includes audio format dependant features such as
     * bitrate, duration, frequency, channels, number of frames, vbr flag,
     * id3v2/id3v1 (for MP3 only), comments (for Ogg Vorbis), ...
     *
     * @param stream could be File, URL or InputStream
     * @param properties audio stream properties.
     */
    public void opened(Object stream, Map properties) {
            // Pay attention to properties. It's useful to get duration,
            // bitrate, channels, even tag such as ID3v2.
            display("opened : "+properties.toString());
            Long dur = (Long)(properties.get("duration"));
            System.out.println("DURATION: " + dur);
            if (dur!=null){
                totalLength = (double)dur.longValue() / 1000000.0;
            } else {
                long _dur = getTimeLengthEstimation(properties);
                totalLength = (double)_dur/1000.0;
            }

            Integer br = (Integer)(properties.get("bitrate"));
            System.out.println("BITRATE: " + br);
            if (br!=null){
                bitrate = br.intValue();
            }

            Integer bl = (Integer)(properties.get("mp3.length.bytes"));
            System.out.println("BYTELENGTH: " + bl);
            if (bl!=null){
                byteLength = bl.intValue();
            }

            Integer bl2 = (Integer)(properties.get("audio.length.bytes"));
            System.out.println("BYTELENGTH2: " + bl2);
            if (bl2!=null){
                byteLength = bl2.intValue();
            }

    }

    /**
     * Progress callback while playing.
     *
     * This method is called severals time per seconds while playing.
     * properties map includes audio format features such as
     * instant bitrate, microseconds position, current frame number, ...
     *
     * @param bytesread from encoded stream.
     * @param microseconds elapsed (<b>reseted after a seek !</b>).
     * @param pcmdata PCM samples.
     * @param properties audio stream parameters.
     */
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
            // Pay attention to properties. It depends on underlying JavaSound SPI
            // MP3SPI provides mp3.equalizer.
        Long pos = (Long)(properties.get("mp3.position.microseconds"));
        if (pos!=null){
            this.timeInMicroSeconds =  pos.longValue();
        } else {
            timeInMicroSeconds++;
        }
        display("progress : "+ properties.toString() + " " + timeInMicroSeconds);
        this.firePosition();
        if (timeInMicroSeconds>=this.endTime*1000000){
            stopPlayback();
        }
    }

    /**
     * Notification callback for basicplayer events such as opened, eom ...
     *
     * @param event
     */
    public void stateUpdated(BasicPlayerEvent event)  {
            // Notification of BasicPlayer states (opened, playing, end of media, ...)
            //display("POSITION: " + event.getPosition());
            //display("stateUpdated : "+event.toString());
            if (event.getCode()==BasicPlayerEvent.SEEKED){
                    //actuallySeekedTo = event.getPosition();
            }
            if (event.getCode()==BasicPlayerEvent.STOPPED){
                    //System.exit(0);
            }
    }

    /**
     * A handle to the BasicPlayer, plugins may control the player through
     * the controller (play, stop, ...)
     * @param controller : a handle to the player
     */
    public void setController(BasicController controller)  {
            //display("setController : "+controller);
    }

    public void display(String msg)  {
        System.out.println(msg);
    }

    /**
     * Try to compute time length in milliseconds.
     * @param properties
     * @return
     */
    public long getTimeLengthEstimation(Map properties)
    {
        long milliseconds = -1;
        int byteslength = -1;
        if (properties != null)
        {
            if (properties.containsKey("audio.length.bytes"))
            {
                byteslength = ((Integer) properties.get("audio.length.bytes")).intValue();
            }
            if (properties.containsKey("duration"))
            {
                milliseconds = (int) (((Long) properties.get("duration")).longValue()) / 1000;
            }
            else
            {
                // Try to compute duration
                int bitspersample = -1;
                int channels = -1;
                float samplerate = -1.0f;
                int framesize = -1;
                if (properties.containsKey("audio.samplesize.bits"))
                {
                    bitspersample = ((Integer) properties.get("audio.samplesize.bits")).intValue();
                }
                if (properties.containsKey("audio.channels"))
                {
                    channels = ((Integer) properties.get("audio.channels")).intValue();
                }
                if (properties.containsKey("audio.samplerate.hz"))
                {
                    samplerate = ((Float) properties.get("audio.samplerate.hz")).floatValue();
                }
                if (properties.containsKey("audio.framesize.bytes"))
                {
                    framesize = ((Integer) properties.get("audio.framesize.bytes")).intValue();
                }
                if (bitspersample > 0)
                {
                    milliseconds = (int) (1000.0f * byteslength / (samplerate * channels * (bitspersample / 8)));
                }
                else
                {
                    milliseconds = (int) (1000.0f * byteslength / (samplerate * framesize));
                }
            }
        }
        return milliseconds;
    }



}
