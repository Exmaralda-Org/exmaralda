/*
 * OpenCorpusAction.java
 *
 * Created on 9. Februar 2007, 11:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.fileActions;

import java.awt.event.ActionEvent;
import java.io.*;
import javax.swing.*;
/**
 *
 * @author thomas
 */
public class OpenCorpusAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    /** Creates a new instance of OpenCorpusAction */
    public OpenCorpusAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(exaktFrame.getLastCorpusPath());
        fileChooser.setDialogTitle("Open COMA Corpus file");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                String name = f.getAbsolutePath();
                return (f.isDirectory() || name.substring(Math.max(0,name.length()-3)).equalsIgnoreCase("XML") || name.substring(Math.max(0,name.length()-4)).equalsIgnoreCase("COMA"));
            }
            public String getDescription() {
                return "XML files, CoMa files (*.xml, *.coma)";
            }
        });        
        int retValue = fileChooser.showOpenDialog(exaktFrame);
        
        if (retValue==JFileChooser.APPROVE_OPTION){
            final File file = fileChooser.getSelectedFile();        
            exaktFrame.doOpen(file);
            exaktFrame.setLastCorpusPath(file);
        }                    
    }
    
}
