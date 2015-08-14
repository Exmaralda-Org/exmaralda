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
import org.exmaralda.folker.utilities.FolkerFileFilter;
import org.exmaralda.orthonormal.utilities.PreferencesUtilities;
import org.exmaralda.orthonormal.actions.AbstractApplicationAction;
import org.exmaralda.orthonormal.application.ApplicationControl;


/**
 *
 * @author thomas
 */
public class OpenAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction */
    public OpenAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** OpenAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        if (!ac.checkSave()) return;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Normalisierte Transkription öffnen");
        FolkerFileFilter ff = new org.exmaralda.folker.utilities.FolkerFileFilter();
        fileChooser.addChoosableFileFilter(ff);
        fileChooser.addChoosableFileFilter(new org.exmaralda.folker.utilities.NormalizedFolkerFileFilter());
        fileChooser.setFileFilter(ff);
        fileChooser.setCurrentDirectory(new File(PreferencesUtilities.getProperty("workingDirectory", "")));        
        int retValue = fileChooser.showOpenDialog(ac.getFrame());
        if (retValue==JFileChooser.CANCEL_OPTION) return;
        File f = fileChooser.getSelectedFile();
        PreferencesUtilities.setProperty("workingDirectory", f.getParent());        
        ac.openTranscriptionFile(f);
    }
    
}
