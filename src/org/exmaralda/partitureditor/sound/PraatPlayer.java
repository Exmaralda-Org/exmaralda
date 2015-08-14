/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.sound;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author thomas
 */
@Deprecated
public class PraatPlayer extends AbstractPlayer implements PlayableListener {

    boolean IS_WACKELIG = true;
    
    File praatconPath;
    File openFileScript;
    File playSoundScript;
    
    String pathToSoundFile;
    String longsoundname;
    double totalLength;
    
    Process praatProcess;
    Thread playThread;    
    long playerStartedTime;
    public int UPDATE_INTERVAL = 1;
    
                
    String[] PLAY_SOUND_SCRIPT = {  "form pls",
                                    "\tsentence Filename",    
                                    "\tpositive Starttime",
                                    "\tpositive Endtime",
                                    "endform",
                                    "Open long sound file... 'filename$'"
                                };
   
    public PraatPlayer(File pcp) throws IOException{
        praatconPath = pcp;    
        addPlayableListener(this);
    }
    
    void writePlaySoundScript(String longsoundname) throws IOException{
        playSoundScript = File.createTempFile("playsound", ".praatscript");
        playSoundScript.deleteOnExit();
        java.io.FileOutputStream fos = new java.io.FileOutputStream(playSoundScript);
        for (String line : PLAY_SOUND_SCRIPT){
            fos.write((line + "\n").getBytes());
        }
        String playLine1 = "select LongSound " + longsoundname;
        String playLine2 = "Play part... starttime endtime";
        fos.write((playLine1 + "\n").getBytes());
        fos.write((playLine2 + "\n").getBytes());
        fos.close();                
    }
    
    void executePraatScript(File scriptPath, String[] arguments) throws IOException, InterruptedException{
        String fullCommand = praatconPath.getAbsolutePath();
        fullCommand+=" " + scriptPath.getAbsolutePath();
        for (String arg : arguments){
            fullCommand+=" " + arg;
        }
        System.out.println("Praat command: " + fullCommand);
        praatProcess = Runtime.getRuntime().exec(fullCommand);        
    }
    
    public void setSoundFile(String ptsf) throws IOException {
        if (!(ptsf.toLowerCase().endsWith(".wav"))){
            throw new IOException("Praat Player can only handle wav files");
        }
        
        // use a JMFPlayer to determine the total duration
        JMFPlayer jmfp = new JMFPlayer();
        jmfp.setSoundFile(ptsf);
        totalLength = jmfp.getTotalLength();
        
        pathToSoundFile = ptsf;
        soundFilePath = ptsf;
        
        String fn = new File(pathToSoundFile).getName();
        longsoundname = fn.substring(0, fn.lastIndexOf("."));
        writePlaySoundScript(longsoundname);
    }

    public double getTotalLength() {
        return totalLength;
    }

    public void startPlayback() {
        if (IS_WACKELIG){
            play(startTime, startTime+0.1);
        }
        play(startTime, endTime);
    }
    
    void play(double st, double et){
        String[] args = {pathToSoundFile, Double.toString(st), Double.toString(et)};
        try {
            executePraatScript(playSoundScript, args);
            playerStartedTime = System.currentTimeMillis();
            playThread = new Thread(new Runnable(){
                public void run(){
                    while (playThread != null) {
                        if (playThread.isInterrupted()) break;
                        firePosition();
                        try{
                            playThread.sleep(UPDATE_INTERVAL);
                        } catch (InterruptedException ie){
                            if (playThread!=null) playThread.interrupt();
                        }
                    }
                }
            });
            playThread.start();
            firePlaybackStarted();
        } catch (IOException ex) {
            Logger.getLogger(PraatPlayer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(PraatPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    public void haltPlayback() {
        // todo
    }

    public void resumePlayback() {
        // todo
    }

    public void stopPlayback() {
        playThread.interrupt();
        playThread = null;
        praatProcess.destroy();
        this.firePlaybackStopped();
    }

    public double getCurrentPosition() {
        long now = System.currentTimeMillis();
        long diff = now - playerStartedTime;
        return (this.startTime + (diff/1000.0));
    }

    public void decreaseCurrentPosition(double time) {
        // todo
    }

    public void increaseCurrentPosition(double time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    public void processPlayableEvent(PlayableEvent e) {
        if (e.getType()==PlayableEvent.POSITION_UPDATE){
            if (e.getPosition()>=this.endTime){
                stopPlayback();
            }
        }
    };
    
    public static void main(String[] args){
        try {
            PraatPlayer pp = new PraatPlayer(new File("c:\\programme\\praat\\praatcon.exe"));
            pp.setSoundFile("T:\\TP-Z2\\GAT\\Demokorpus\\HART_ABER_FAIR\\HART_ABER_FAIR_02_APRIL_2008.wav");
            pp.setStartTime(4.95);
            pp.setEndTime(8.63);
            pp.startPlayback();
        } catch (IOException ex) {
            Logger.getLogger(PraatPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
