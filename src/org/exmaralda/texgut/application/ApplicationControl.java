/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.texgut.application;

import java.util.prefs.Preferences;
import org.exmaralda.texgut.gui.NewEAFDialog;

/**
 *
 * @author bernd
 */
public class ApplicationControl {

    ApplicationFrame applicationFrame;   
    Preferences preferences;
    
    ApplicationControl(ApplicationFrame af) {
        applicationFrame = af;
         preferences = java.util.prefs.Preferences.userRoot().node(applicationFrame.getPreferencesNode());
    }

    public void assignActions() {
        
    }

    public void retrieveSettings() {
        System.out.println("Retrieving settings");
        // window location
        int windowLocationX = Integer.parseInt(preferences.get("window-location-x", "0"));
        int windowLocationY = Integer.parseInt(preferences.get("window-location-y", "0"));
        applicationFrame.setLocation(windowLocationX, windowLocationY);
        
        String audioFolder = preferences.get("audio-folder", System.getProperty("user.dir"));
        applicationFrame.audioFolderTextField.setText(audioFolder);

        String transcriptFolder = preferences.get("transcript-folder", System.getProperty("user.dir"));
        applicationFrame.transcriptFolderTextField.setText(transcriptFolder);

    }
    
    public void storeSettings(){
        System.out.println("Storing settings");
        // window location
        preferences.put("window-location-x", Integer.toString(applicationFrame.getLocationOnScreen().x));        
        preferences.put("window-location-y", Integer.toString(applicationFrame.getLocationOnScreen().y));        
    }

    void newEAF() {
        NewEAFDialog dialog = new NewEAFDialog(applicationFrame, true);
        dialog.setLocationRelativeTo(applicationFrame);
        dialog.setVisible(true);
    }
    
    
}
