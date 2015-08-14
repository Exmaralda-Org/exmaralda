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
import java.io.*;
import org.exmaralda.folker.application.ApplicationControl;
import org.exmaralda.folker.utilities.PreferencesUtilities;


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
        System.out.println("[*** OpenRecentAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        ac.checkLog();
        if (!ac.checkSave()) return;
        PreferencesUtilities.setProperty("workingDirectory", fileToBeOpened.getParent());        
        ac.openTranscriptionFile(fileToBeOpened);
    }
    
}
