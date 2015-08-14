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
import org.exmaralda.folker.utilities.FolkerFileFilter;
import org.exmaralda.folker.utilities.NormalizedFolkerFileFilter;


/**
 *
 * @author thomas
 */
public class OpenAction extends javax.swing.AbstractAction {
    
    ApplicationFrame applicationFrame;
    
    /** Creates a new instance of OpenAction */
    public OpenAction(ApplicationFrame af, String name, Icon icon) {
        super(name, icon);
        applicationFrame = af;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** OpenAction ***]");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Normalisierte Transkription öffnen");
        fileChooser.setCurrentDirectory(new File("Y:\\thomas\\DS2FLK\\13_Manual_Alignment"));
        NormalizedFolkerFileFilter nff = new org.exmaralda.folker.utilities.NormalizedFolkerFileFilter();
        fileChooser.addChoosableFileFilter(nff);
        fileChooser.setFileFilter(nff);
        int retValue = fileChooser.showOpenDialog(applicationFrame);
        if (retValue==JFileChooser.CANCEL_OPTION) return;
        File f = fileChooser.getSelectedFile();        
        applicationFrame.openTranscriptionFile(f);
    }
    
}
