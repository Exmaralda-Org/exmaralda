/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.actions.fileactions;

import java.io.IOException;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.*;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.orthonormal.actions.AbstractApplicationAction;
import org.exmaralda.orthonormal.application.ApplicationControl;


/**
 *
 * @author thomas
 */
public class EditRecordingAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction */
    public EditRecordingAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** EditRecordingAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(FOLKERInternationalizer.getString("dialog.recording"));
        fileChooser.setFileFilter(new org.exmaralda.folker.utilities.WaveFileFilter());
        if (ac.currentMediaPath!=null){
            fileChooser.setSelectedFile(new File(ac.currentMediaPath));
        }
        int retValue = fileChooser.showOpenDialog(ac.getFrame());
        if (retValue==JFileChooser.CANCEL_OPTION) return;
        File f = fileChooser.getSelectedFile();
        try {
            ac.setMedia(f.getAbsolutePath());
            ac.getTranscription().setMediaPath(f.getAbsolutePath());
            ac.DOCUMENT_CHANGED = true;
            ac.status("Aufnahme neu zugeordnet: " + f.getAbsolutePath());
        } catch (IOException ex) {
            ac.displayException(ex);
        }

        
    }
    
}
