/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.aligner.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import java.io.*;
import org.exmaralda.aligner.application.ApplicationFrame;
import org.exmaralda.folker.utilities.NormalizedFolkerFileFilter;


/**
 *
 * @author thomas
 */
public class SaveAction extends javax.swing.AbstractAction {
    
    ApplicationFrame applicationFrame;
    
    /** Creates a new instance of OpenAction */
    public SaveAction(ApplicationFrame af, String name, Icon icon) {
        super(name, icon);
        applicationFrame = af;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** SaveAction ***]");
        applicationFrame.saveTranscriptionFile();
    }
    
}
