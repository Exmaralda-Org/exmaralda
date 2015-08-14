/*
 * PraatControl.java
 *
 * Created on 14. Mai 2004, 11:04
 */

package org.exmaralda.partitureditor.praatPanel;

import java.io.*;
import java.util.Vector;
import java.util.prefs.Preferences;
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
        if (isMac){
            /* /Users/thomasschmidt/Desktop/PRAAT/Praat.app/Contents/MacOS/Praat
            /Users/thomasschmidt/Desktop/PRAAT/sendpraat_intel.dms */
            PRAAT = "Praat.app/Contents/MacOS/Praat";
            SENDPRAAT = "sendpraat_intel";
        }
        NL = System.getProperty("line.separator");
    }

    public void configure(ExmaraldaApplication app){
        Preferences settings = java.util.prefs.Preferences.userRoot().node(app.getPreferencesNode());
        String pd = settings.get("PRAAT-Directory", "");
        if (pd.length()>0){
            PRAATPATH = pd + System.getProperty("file.separator");
        }
    }
    
    public void callPraatProcess(String cmd) throws IOException {
        try {
            //String fullCmd = PRAATPATH + SENDPRAAT + " " + PRAAT + " \"" + cmd + "\"";
            /*String fullCmd = PRAATPATH + SENDPRAAT + " " + "praat" + " \"" + cmd + "\"";
            System.out.println("Executing command " + fullCmd + "...");
            try {
            Process p = Runtime.getRuntime().exec(fullCmd);
            p.waitFor();
            } catch (InterruptedException ie){
            throw new IOException(ie.getLocalizedMessage());
            }
            System.out.println("Command executed.");*/
            ProcessBuilder pb = new ProcessBuilder(PRAATPATH + SENDPRAAT, PRAAT, "\"" + cmd + "\"");
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
            Vector<String> command = new Vector<String>();
            command.add(PRAATPATH + SENDPRAAT);
            command.add(PRAAT);
            for (String c : cmds) {
                command.add("\"" + c + "\"");
            }
            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = pb.start();
            p.waitFor();
            //String fullCmd = PRAATPATH + SENDPRAAT + " " + PRAAT + " ";
            /*String fullCmd = PRAATPATH + SENDPRAAT + " " + "praat" + " ";
            for (int pos=0; pos<cmds.length; pos++){
            String cmd = cmds[pos];
            fullCmd+="\"" + cmd + "\" ";
            }
            System.out.println("Executing command " + fullCmd + "...");
            try {
            Process p = Runtime.getRuntime().exec(fullCmd);
            p.waitFor();
            } catch (InterruptedException ie){
            throw new IOException(ie.getLocalizedMessage());
            }*/
            System.out.println("Command executed.");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public void startPraat() throws IOException {
        Runtime.getRuntime().exec(PRAATPATH + PRAAT);
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
        callPraatProcess("editor LongSound " + windowName);
        callPraatProcess("Zoom... " + start + " " + end);
        callPraatProcess("endeditor");
    }
    
    public double getCursorTime() throws IOException {
        callPraatProcess("editor LongSound " + windowName);
        String[] cmds = {"cursorPosition$ = Get cursor", "cursorPosition$ > " + storeResultFilePath};
        callPraatProcess(cmds);
        callPraatProcess("endeditor");
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
