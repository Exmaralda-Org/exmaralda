/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.actions.transcriptionactions;

import java.io.IOException;
import org.exmaralda.folker.actions.AbstractApplicationAction;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.*;
import org.exmaralda.folker.application.ApplicationControl;
import org.exmaralda.folker.utilities.Constants;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;


/**
 *
 * @author thomas
 */
public class EditRecordingAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction */
    public EditRecordingAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** EditRecordingAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(FOLKERInternationalizer.getString("dialog.recording"));
        fileChooser.setFileFilter(new org.exmaralda.folker.utilities.WaveFileFilter());
        JLabel imageLabel = new JLabel("");
        imageLabel.setIcon(new Constants().getIcon(Constants.BIG_RECORDING_ICON));
        fileChooser.setAccessory(imageLabel);        
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
