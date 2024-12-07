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
import org.exmaralda.folker.utilities.Constants;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.folker.utilities.PreferencesUtilities;


/**
 *
 * @author thomas
 */
public class OpenAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction
     * @param ac
     * @param name
     * @param icon */
    public OpenAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** OpenAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        ac.checkLog();
        if (!ac.checkSave()) return;
        
        // bring up the file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(FOLKERInternationalizer.getString("dialog.open"));
        fileChooser.setFileFilter(new org.exmaralda.folker.utilities.FolkerFileFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(PreferencesUtilities.getProperty("workingDirectory", "")));      
        JLabel imageLabel = new JLabel("");
        imageLabel.setIcon(new Constants().getIcon(Constants.SMALL_FOLKER_ICON));
        fileChooser.setAccessory(imageLabel);        
        int retValue = fileChooser.showOpenDialog(ac.getFrame());
        if (retValue==JFileChooser.CANCEL_OPTION) return;
        
        
        File f = fileChooser.getSelectedFile();
        PreferencesUtilities.setProperty("workingDirectory", f.getParent());        
        ac.openTranscriptionFile(f);
    }
    
}
