/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.actions.fileactions;

import org.exmaralda.folker.actions.AbstractApplicationAction;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.io.*;
import org.exmaralda.folker.application.ApplicationControl;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.folker.utilities.PreferencesUtilities;


/**
 *
 * @author thomas
 */
public class SplitTranscriptionAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction */
    public SplitTranscriptionAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** SplitTranscriptionAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        //if (!ac.checkSave()) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(FOLKERInternationalizer.getString("dialog.split"));
        fileChooser.setFileFilter(new org.exmaralda.folker.utilities.FolkerFileFilter());
        fileChooser.setCurrentDirectory(new File(PreferencesUtilities.getProperty("workingDirectory", "")));
        if (ac.currentFilePath==null){
            int index = Math.max(0, ac.currentMediaPath.lastIndexOf(".wav"));
            File f = new File(ac.currentMediaPath.substring(0, index) + ".flk");
            fileChooser.setSelectedFile(f);
        }
        boolean goon = false;
        File f = null;
        while (!goon){
            int retValue = fileChooser.showSaveDialog(ac.getFrame());
            if (retValue==JFileChooser.CANCEL_OPTION) return;
            f = fileChooser.getSelectedFile();
            if (!(f.getName().contains("."))){
                f = new File(f.getAbsolutePath()+".flk");
            }
            if (f.exists()){
                int userChoice = JOptionPane.showConfirmDialog(ac.getFrame(),
                                                            FOLKERInternationalizer.getString("option.fileexists"),
                                                            FOLKERInternationalizer.getString("option.confirmation"),
                                                            JOptionPane.YES_NO_OPTION);

                goon = (userChoice==JOptionPane.YES_OPTION);
            } else {
                goon = true;
            }
        }
        PreferencesUtilities.setProperty("workingDirectory", f.getParent());

        ac.splitTranscription(f);

    }
    
}
