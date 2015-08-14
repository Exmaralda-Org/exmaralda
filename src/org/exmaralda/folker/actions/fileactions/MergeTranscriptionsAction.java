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
public class MergeTranscriptionsAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction */
    public MergeTranscriptionsAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** MergeTranscriptionsAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        //if (!ac.checkSave()) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(FOLKERInternationalizer.getString("dialog.merge"));
        fileChooser.setFileFilter(new org.exmaralda.folker.utilities.FolkerFileFilter());
        fileChooser.setCurrentDirectory(new File(PreferencesUtilities.getProperty("workingDirectory", "")));
        int retValue = fileChooser.showOpenDialog(ac.getFrame());
        if (retValue==JFileChooser.CANCEL_OPTION) return;
        File f = fileChooser.getSelectedFile();
        PreferencesUtilities.setProperty("workingDirectory", f.getParent());

        ac.mergeTranscriptions(f);

    }
    
}
