/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.actions.transcriptionactions;

import org.exmaralda.folker.actions.AbstractApplicationAction;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.io.*;
import org.exmaralda.folker.application.ApplicationControl;
import org.exmaralda.folker.gui.EditSpeakerPanel;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;


/**
 *
 * @author thomas
 */
public class EditSpeakersAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction */
    public EditSpeakersAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** EditSpeakersAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        JDialog dialog = new JDialog(ac.getFrame(), true);
        EditSpeakerPanel editSpeakerPanel = new EditSpeakerPanel(ac.getTranscription());
        dialog.setTitle(FOLKERInternationalizer.getString("dialog.speaker.editspeakers"));
        dialog.getContentPane().add(editSpeakerPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(ac.getFrame());
        dialog.setVisible(true);
        
        ac.speakersChanged();

        
    }
    
}
