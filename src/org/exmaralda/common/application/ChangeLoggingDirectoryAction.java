/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.application;

import org.exmaralda.common.*;
import java.awt.event.ActionEvent;
import javax.swing.Icon;

/**
 *
 * @author thomas
 */
public class ChangeLoggingDirectoryAction extends javax.swing.AbstractAction {

    ExmaraldaApplication application;
    
    public ChangeLoggingDirectoryAction(String name, ExmaraldaApplication app) {
        super(name);
        application = app;
    }

    
    
    public void actionPerformed(ActionEvent e) {
        java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userRoot().node(application.getPreferencesNode());        
        String defaultDirectory = System.getProperty("user.home");
        String logfiledirectory = preferences.get("LOG-Directory", defaultDirectory);

        
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser(logfiledirectory);
        fc.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Choose a new log file directory (current: " + logfiledirectory + ")");
        
        if (fc.showOpenDialog(null)!=javax.swing.JFileChooser.APPROVE_OPTION) return;        
        
        preferences.put("LOG-Directory", fc.getSelectedFile().getAbsolutePath());
        
        String message = "Logging directory set to \n"
                            + fc.getSelectedFile().getAbsolutePath() + ".\n"
                            + "Change will take effect after restart.";
        
        javax.swing.JOptionPane.showMessageDialog(null, message);
        
    }

}
