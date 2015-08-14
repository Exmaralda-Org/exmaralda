/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.actions.fileactions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import java.io.*;
import org.exmaralda.orthonormal.utilities.PreferencesUtilities;
import org.exmaralda.orthonormal.actions.AbstractApplicationAction;
import org.exmaralda.orthonormal.application.ApplicationControl;


/**
 *
 * @author thomas
 */
public class SaveAsAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction */
    public SaveAsAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** SaveAsAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Transkription speichern");
        fileChooser.setFileFilter(new org.exmaralda.folker.utilities.NormalizedFolkerFileFilter());
        fileChooser.setCurrentDirectory(new File(PreferencesUtilities.getProperty("workingDirectory", "")));
        if (ac.currentFilePath==null){
            int index = Math.max(0, ac.currentMediaPath.toLowerCase().lastIndexOf(".wav"));
            File f = new File(ac.currentMediaPath.substring(0, index) + ".fln");
            fileChooser.setSelectedFile(f);
        }
        int retValue = fileChooser.showSaveDialog(ac.getFrame());
        if (retValue==JFileChooser.CANCEL_OPTION) return;
        File f = fileChooser.getSelectedFile();
        if (!(f.getName().contains("."))){
            f = new File(f.getAbsolutePath()+".fln");
        }
        PreferencesUtilities.setProperty("workingDirectory", f.getParent());
        ac.saveTranscriptionFileAs(f, true);
    }
    
}
