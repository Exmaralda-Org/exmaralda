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
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;


/**
 *
 * @author thomas
 */
public class OpenBBAWAction extends javax.swing.AbstractAction {
    
    ApplicationFrame applicationFrame;
    
    /** Creates a new instance of OpenAction */
    public OpenBBAWAction(ApplicationFrame af, String name, Icon icon) {
        super(name, icon);
        applicationFrame = af;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** OpenBBAWAction ***]");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("BBAW-TEI-Transkription öffnen");
        fileChooser.setCurrentDirectory(new File("Y:\\thomas\\BW2FLK\\3-BBAW-SPLIT"));
        fileChooser.addChoosableFileFilter(new ParameterFileFilter("xml", "XML files"));
        int retValue = fileChooser.showOpenDialog(applicationFrame);
        if (retValue==JFileChooser.CANCEL_OPTION) return;
        File f = fileChooser.getSelectedFile();        
        applicationFrame.openBBAWTranscriptionFile(f);
    }
    
}
