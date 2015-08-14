/*
 * AutoSave.java
 *
 * Created on 9. Maerz 2004, 09:53
 */

package org.exmaralda.partitureditor.partiture;

import java.text.SimpleDateFormat;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;

/**
 *
 * @author  thomas
 */
public class AutoSave implements Runnable {
    
    public String FILENAME;
    public String PATH;
    public int SAVE_INTERVAL;
    private String timestamp;
    
    private Thread thread;
    private BasicTranscription transcription;
    
    /** Creates a new instance of AutoSave */
    public AutoSave(BasicTranscription bt) {
        transcription = bt;
        PATH = System.getProperty("user.home");
        FILENAME =  "EXMARaLDA_AutoBackup";
        SAVE_INTERVAL = 600000;
        timestamp = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(new java.util.Date());
    }
    
    public void setTranscription(BasicTranscription bt){
        transcription = bt;
    }
    
    public void doSave() {
        try {
            System.out.println("Auto saving " + FILENAME + " at " + new java.util.Date().toString());
            String filename = PATH + System.getProperty("file.separator") + FILENAME + "_" + timestamp + ".exb";
            
            // new 31-08-2012
            // log auto saving in the meta information
            if (!(transcription.getHead().getMetaInformation().getUDMetaInformation().containsAttribute("AutoSave"))){
                transcription.getHead().getMetaInformation().getUDMetaInformation().setAttribute("AutoSave", "");
            }
            transcription.getHead().getMetaInformation().getUDMetaInformation().setAttribute("AutoSave", 
                    transcription.getHead().getMetaInformation().getUDMetaInformation().getValueOfAttribute("AutoSave")
                    + ";" + filename + " at " + new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(new java.util.Date()));
            
            transcription.writeXMLToFile(filename, "none");
        } catch (Exception e) {
            System.out.println (e.getLocalizedMessage());
            try{
                System.out.println("Auto saving to default location.");
                String filename = System.getProperty("user.home") + System.getProperty("file.separator") + "EXMARaLDA_AutoBackup" + ".exb";
                transcription.writeXMLToFile(filename, "none");
            } catch (Exception e2){
                System.out.println(e2.getLocalizedMessage());
            }
        }
    }

    public void start() {
        thread = new Thread(this);
        thread.setName("Autosave thread");
        thread.start();
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }
        thread = null;
    }

    public void run() {
        while (thread != null) {
            // Pause
            try {
                thread.sleep(SAVE_INTERVAL);
                doSave();
            } catch (Exception e) {
                break;
            }
        }
    }

    public void setSaveInterval (int sec) {
        SAVE_INTERVAL = Math.max(sec, 10) * 1000;
    }

    
    
}
