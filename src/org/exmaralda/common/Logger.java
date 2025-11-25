/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common;

import java.io.*;
import org.exmaralda.common.application.AboutDialog;

/**
 *
 * @author thomas
 */
public class Logger {
    
    /** the maximum size of the log file in characters */
    public static int MAX_LOG_LENGTH = 30000;
    
    public static void initialiseLogger(ExmaraldaApplication app){
        
        java.io.File file = new java.io.File(getLogfileName(app));
        if (!(file.exists())){
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                resetLogfileName(app);
                file = new java.io.File(getLogfileName(app));
                try {
                    file.createNewFile();
                } catch (IOException ex1) {
                    System.out.println("Could like totally not create logfile. Giving up now.");
                    ex.printStackTrace();
                }
            }
        }
        
        // check the length of the logfile and cut it if necessary
        checkLogLength(app);
        
        // redirect standard out and error out to the log file
        java.io.PrintStream logfile= null;        
        try {
            logfile = new java.io.PrintStream(new java.io.FileOutputStream(getLogfileName(app), true));
            System.setOut(logfile);
            System.setErr(logfile);
        } catch (Exception e) {
            System.out.println("Redirect:  Unable to open log file! ");
        }
        
        /*java.version	Java Runtime Environment version
        java.vendor	Java Runtime Environment vendor
        java.vendor.url	Java vendor URL
        java.home	Java installation directory
        java.vm.specification.version	Java Virtual Machine specification version
        java.vm.specification.vendor	Java Virtual Machine specification vendor
        java.vm.specification.name	Java Virtual Machine specification name
        java.vm.version	Java Virtual Machine implementation version
        java.vm.vendor	Java Virtual Machine implementation vendor
        java.vm.name	Java Virtual Machine implementation name
        java.specification.version	Java Runtime Environment specification version
        java.specification.vendor	Java Runtime Environment specification vendor
        java.specification.name	Java Runtime Environment specification name
        java.class.version	Java class format version number
        java.class.path	Java class path
        os.name	Operating system name
        os.arch	Operating system architecture*/

        // print information to the standard output
        System.out.println("************* INFO **************");
        System.out.println("Date : " + new java.util.Date().toString());
        
        System.out.println("Operating system : " + System.getProperty("os.name"));
        System.out.println("Operating system architecture : " + System.getProperty("os.arch"));
        System.out.println("OS version : " + System.getProperty("os.version"));
        
        System.out.println("JRE version : " + System.getProperty("java.version"));
        System.out.println("JRE vendor : " + System.getProperty("java.vendor"));
        System.out.println("JavaFX version : " + AboutDialog.getJavaFXVersion());
        
        System.out.println("JVM version (specification/implementation) : " 
                + System.getProperty("java.vm.specification.version") + " / " + System.getProperty("java.vm.version"));
        
        System.out.println("org.sfb538.exmaralda.PartiturEditor.PlayerType: " 
                + java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor").get("PlayerType", "Not set - using default player"));
        
        System.out.println("Java Class Path : " + System.getProperty("java.class.path"));
        System.out.println("Java Library Path: " + System.getProperty("java.library.path"));        
        
        System.out.println("Application name :" + app.getApplicationName());
        System.out.println("Application version :" + app.getVersion());
        System.out.println("Build time :" + org.exmaralda.common.EXMARaLDAConstants.BUILD_TIME);
        System.out.println("http://www.exmaralda.org");
        System.out.println("*********************************");
        
    }
    
    public static void checkLogLength(ExmaraldaApplication app){
        File logFile = new File(getLogfileName(app));
        //System.out.println("File length:" + logFile.length());

        long l = logFile.length();
        if (l>MAX_LOG_LENGTH){
            FileOutputStream fos = null;
            try {
                String log = getLog(app);
                logFile.delete();
                logFile.createNewFile();
                int cutPosition = log.length() - MAX_LOG_LENGTH * 2 / 3;
                System.out.println("Cutting log at " + cutPosition);
                String bitsToKeep = log.substring(Math.max(0, cutPosition));
                fos = new FileOutputStream(logFile);
                fos.write(("========= LOG FILE CUT " +  new java.util.Date().toString() + "===============\n").getBytes());
                fos.write(bitsToKeep.getBytes());
                fos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (fos!=null){
                        fos.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            
        }
    }
    
    public static String getLogfileName(ExmaraldaApplication app){        
        // determine logfile file names
        String defaultDirectory = System.getProperty("user.home");
        java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userRoot().node(app.getPreferencesNode());        
        String logfiledirectory = preferences.get("LOG-Directory", defaultDirectory);
        String logfilename = logfiledirectory + System.getProperty("file.separator") + "EXMARaLDA_" + app.getApplicationName() + ".log";    
        return logfilename;
    }
    
    public static void resetLogfileName(ExmaraldaApplication app){
        String defaultDirectory = System.getProperty("user.home");
        java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userRoot().node(app.getPreferencesNode());        
        preferences.put("LOG-Directory", defaultDirectory);
    }
    
    public static String getLog(ExmaraldaApplication app){
        FileReader fr = null;
        try {
            StringBuffer result = new StringBuffer(MAX_LOG_LENGTH + 1000);
            fr = new FileReader(getLogfileName(app));
            BufferedReader br = new BufferedReader(fr);
            String nextLine = new String();
            while ((nextLine = br.readLine()) != null) {
                result.append(nextLine + "\n");
                //System.out.println("Length" + result.length());
                if (result.length()>MAX_LOG_LENGTH){
                    result = result.delete(0, MAX_LOG_LENGTH/5);
                    result.insert(0, "========= LOG FILE CUT "+  new java.util.Date().toString() +  " ===============\n");
                }
            }
            br.close();
            return result.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "COULD NOT READ LOGFILE :" + ex.getMessage();
        } finally {
            try {
                if (fr!=null){
                    fr.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return "COULD NOT READ LOGFILE :" + ex.getMessage();
            }
        }
    }

}
