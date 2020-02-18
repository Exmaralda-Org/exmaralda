/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common;

import javax.swing.JFrame;
import org.exmaraldapro.support.ReminderDialog;

/**
 *
 * @author thomas
 */
public interface ExmaraldaApplication {
    
	// would it hurt to have a getFrame()-Method for the main application frame? (Kai)
    
    default java.awt.Frame getApplicationFrame(){
        // is that really possible?
        return (JFrame)this;
    }
	
    /* returns the version string */
    public String getVersion();
    
    /** returns the application name */
    public String getApplicationName();
    
    /** returns the top level node for writing preferences */
    public String getPreferencesNode();
    
    /** returns the path to the welcome screen */
    public javax.swing.ImageIcon getWelcomeScreen();

    public void resetSettings();
    
    //public void checkRegistration();
    
    default void checkRegistration(){
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(getPreferencesNode());
        String key = settings.get("EXMARaLDA-Registration-Key", null);
        if (key!=null){
            //check the key, if it's okay, return
        }
        int countRuns = settings.getInt("EXMARaLDA-Count-Runs", 0);
        countRuns++;
        settings.putInt("EXMARaLDA-Count-Runs", countRuns);
        if (countRuns % 3 == 0){
            ReminderDialog reminderDialog = new ReminderDialog(getApplicationFrame(),true);
            reminderDialog.setLocationRelativeTo(getApplicationFrame());
            reminderDialog.setVisible(true);
        }        
    }

}
