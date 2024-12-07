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
import java.io.*;
import org.exmaralda.orthonormal.utilities.PreferencesUtilities;
import org.exmaralda.orthonormal.actions.AbstractApplicationAction;
import org.exmaralda.orthonormal.application.ApplicationControl;


/**
 *
 * @author thomas
 */
public class OpenRecentAction extends AbstractApplicationAction {
    
    File fileToBeOpened;
    
    /** Creates a new instance of OpenAction */
    public OpenRecentAction(ApplicationControl ac, File file) {
        //super(ac, file.getAbsolutePath().substring(Math.max(0, file.getAbsolutePath().length()-30)), null);
        super(ac, file.getName(), null);
        fileToBeOpened = file;
        
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** OpenRecenttAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        if (!ac.checkSave()) return;
        PreferencesUtilities.setProperty("workingDirectory", fileToBeOpened.getParent());        
        ac.openTranscriptionFile(fileToBeOpened);
    }
    
}
