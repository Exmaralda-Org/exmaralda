/*
 * PraatControl.java
 *
 * Created on 14. Mai 2004, 11:04
 */

package org.exmaralda.partitureditor.praatPanel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import org.exmaralda.common.ExmaraldaApplication;

/**
 *
 * @author  thomas
 */
public class PraatControl {
    
    static String PRAAT = "Praat";            // the name of the praat program
    static String SENDPRAAT = "sendpraat";    // the name of the sendpraat routine
    static final String OPEN_LONG_SOUND_FILE = "Open long sound file... ";  // command to open long sound file
    static final String VIEW = "View"; // command to open a view for the currently selected sound file

    String PRAATPATH = "";

    String storeResultFilePath = "";
    String tempScriptFilePath= "";
    String NL;
    String soundFilePath;
    String windowName;
    boolean praatStarted = false;
    boolean isWindows;
    boolean isMac;
    
    /** Creates a new instance of PraatControl */
    public PraatControl() {
        storeResultFilePath = System.getProperty("user.home") + System.getProperty("file.separator") + "Praat_res.txt";
        tempScriptFilePath = System.getProperty("user.home") + System.getProperty("file.separator") + "Praat_scr.praat";
        isWindows = System.getProperty("os.name").toLowerCase().startsWith("win");
        isMac = System.getProperty("os.name").toLowerCase().startsWith("mac");
        if (isWindows){
            PRAAT = "Praat.exe";
        }
        if (isMac){
            PRAAT = "Praat";
            SENDPRAAT = "sendpraat-mac";
        }
        NL = System.getProperty("line.separator");
    }
    
    public boolean checkSendpraat(){
        File f = new File(PRAATPATH + SENDPRAAT);
        return (f.exists() && f.canExecute());        
    }
    
    public boolean checkPraat(){
        File f = new File(PRAATPATH + PRAAT);
        return (f.exists() && f.canExecute());                
    }
    
    public String getSendpraatPath(){
        return new File(PRAATPATH + SENDPRAAT).getAbsolutePath();
    }

    public String getPraatPath(){
        return new File(PRAATPATH + PRAAT).getAbsolutePath();
    }

    public void configure(ExmaraldaApplication app){
        Preferences settings = java.util.prefs.Preferences.userRoot().node(app.getPreferencesNode());
        String pd = settings.get("PRAAT-Directory", "");
        if (pd.length()>0){
            PRAATPATH = pd + System.getProperty("file.separator");
            
            // 2023-07-13 : new for #401
            String praatPathToCheck = PRAATPATH + PRAAT;
            File praatFileToCheck =new File(praatPathToCheck);
            if (!praatFileToCheck.exists() || !praatFileToCheck.canExecute()){
                JOptionPane.showMessageDialog(app.getApplicationFrame(), praatPathToCheck + " does not exist or cannot be executed");
                System.out.println(praatPathToCheck + " does not exist or cannot be executed");
            }
            
            String sendPraatPathToCheck = PRAATPATH + SENDPRAAT;
            File sendpraatFileToCheck =new File(sendPraatPathToCheck);
            if (!sendpraatFileToCheck.exists()){
                if (isWindows){
                    SENDPRAAT = "sendpraat-win.exe";
                } else if (isMac){
                    SENDPRAAT = "sendpraat-mac";                    
                } else {
                    SENDPRAAT = "sendpraat-linux";                                        
                }
                sendPraatPathToCheck = PRAATPATH + SENDPRAAT;
                sendpraatFileToCheck =new File(sendPraatPathToCheck);
                if (!sendpraatFileToCheck.exists() || !sendpraatFileToCheck.canExecute()){
                    JOptionPane.showMessageDialog(app.getApplicationFrame(), sendPraatPathToCheck + " does not exist or cannot be executed");                    
                    System.out.println(sendPraatPathToCheck + " does not exist or cannot be executed");
                }
            }
        }
        
    }
    
    public void callPraatProcess(String cmd) throws IOException {
        try {
            ProcessBuilder pb = new ProcessBuilder(PRAATPATH + SENDPRAAT, "Praat", "\"" + cmd + "\"");
            Process p = pb.start();
            p.waitFor();
            System.out.println("Command executed.");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            throw new IOException(ex.getLocalizedMessage());
        }
    }
    
    public void callPraatProcess(String[] cmds) throws IOException {
        try {
            List<String> command = new ArrayList<>();
            command.add(PRAATPATH + SENDPRAAT);
            command.add("Praat");
            for (String c : cmds) {
                command.add("\"" + c + "\"");
            }
            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = pb.start();
            p.waitFor();
            System.out.println("Command executed.");
        } catch (InterruptedException ex) {
            Logger.getLogger(PraatControl.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        }
    }
    
    Process praatProcess = null;
    
    public boolean isPraatRunning(){
        // isAlive() is not available in 1.6, so need to use this defunct
        // version when compiling for the crappy Mac
        //return (praatProcess!=null);
        return ((praatProcess!=null) && (praatProcess.isAlive())); 
    } 
    
    public void startPraat() throws IOException {
        //Runtime.getRuntime().exec(PRAATPATH + PRAAT);
        String command = PRAATPATH + PRAAT;
        ProcessBuilder pb = new ProcessBuilder(command);
        praatProcess = pb.start();
        //p.waitFor();
        praatStarted = true;
    }
    
    public void quitPraat() throws IOException {
        callPraatProcess("Quit");
    }
    
    public void openSoundFile(String path) throws IOException {
        System.out.println("==== PRAAT: OPENING SOUND FILE + " + path);
        callPraatProcess(OPEN_LONG_SOUND_FILE + path);
        String filename = new File(path).getName();
        windowName = filename.substring(0, filename.lastIndexOf('.'));
        System.out.println("Window name set to " + windowName);
        callPraatProcess(VIEW);
    }
    
    public void selectSound(double startTime, double endTime) throws IOException{
        String start = Double.toString(startTime);
        String end = Double.toString(endTime);
        // 2023-07-13 : changed for #401
        String[] cmds = {
            "editor LongSound " + windowName,
            "Zoom... " + start + " " + end,
            "endeditor"
        };
        callPraatProcess(cmds);
        /*callPraatProcess("editor LongSound " + windowName);
        callPraatProcess("Zoom... " + start + " " + end);
        callPraatProcess("endeditor");*/
    }
    
    public double getCursorTime() throws IOException {
        //callPraatProcess("editor LongSound " + windowName);
        // 2023-07-13 : changed for #401
        String[] cmds = {
            "editor LongSound " + windowName,
            "cursorPosition$ = Get cursor", 
            "cursorPosition$ > " + storeResultFilePath,
            "endeditor"
        };
        callPraatProcess(cmds);
        //callPraatProcess("endeditor");
        FileReader fr = new FileReader(storeResultFilePath);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        br.close();
        String time = line.substring(0, line.lastIndexOf(' '));
        try {
            double result = Double.parseDouble(time);
            return result;
        } catch (NumberFormatException nfe){
            throw new IOException("NumberFormatException - " + nfe.getLocalizedMessage());
        }
    }

    
}
