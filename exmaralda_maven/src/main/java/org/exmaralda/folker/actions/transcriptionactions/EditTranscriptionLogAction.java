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
import org.exmaralda.folker.application.ApplicationControl;
import org.exmaralda.folker.gui.EditTranscriptionLogPanel;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;


/**
 *
 * @author thomas
 */
public class EditTranscriptionLogAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction */
    public EditTranscriptionLogAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** EditTranscriptionLogAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        JDialog dialog = new JDialog(ac.getFrame(), true);
        EditTranscriptionLogPanel editTranscriptionLogPanel = new EditTranscriptionLogPanel(ac.getTranscriptionHead());
        dialog.setTitle(FOLKERInternationalizer.getString("dialog.log.editlog"));
        dialog.getContentPane().add(editTranscriptionLogPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(ac.getFrame());
        dialog.setVisible(true);
        
        //ac.speakersChanged(); ?? what was this doing here?!?!?!

        
    }
    
}
