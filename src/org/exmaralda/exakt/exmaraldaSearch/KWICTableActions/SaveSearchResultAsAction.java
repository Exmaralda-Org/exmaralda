/*
 * SaveSearchResultAction.java
 *
 * Created on 9. Februar 2007, 11:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import java.io.*;

/**
 *
 * @author thomas
 */
public class SaveSearchResultAsAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    public static final String PATH_TO_INTERNAL_STYLESHEET = "/org/exmaralda/exakt/resources/SearchResult2HTML.xsl";
    
    /** Creates a new instance of SaveSearchResultAction */
    public SaveSearchResultAsAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(exaktFrame.getLastSearchResultPath());
        fileChooser.setDialogTitle("Save a concordance");
        fileChooser.setFileFilter(new org.exmaralda.exakt.utilities.HTMLFileFilter());
        fileChooser.setFileFilter(new org.exmaralda.exakt.utilities.XMLFileFilter());
        
        boolean goAhead = false;
        File file = null;
        while (!goAhead){
            int retValue = fileChooser.showSaveDialog(exaktFrame);

            if (retValue==JFileChooser.APPROVE_OPTION){
                file = fileChooser.getSelectedFile();        
                String name = file.getName();

                // check suffix
                if (name.indexOf('.')<0){
                    // add appropriate suffix
                    if (fileChooser.getFileFilter().getDescription().startsWith("XML")){
                        file = new File(file.getAbsolutePath() + ".xml");
                    } else {
                        file = new File(file.getAbsolutePath() + ".html");                    
                    }
                }

                // check overwrite
                if (file.exists()){
                    String message = file.getAbsolutePath() + " exists. Overwrite?";
                    int returnValue = 
                        JOptionPane.showConfirmDialog(exaktFrame, message);
                    if (returnValue==JOptionPane.OK_OPTION){goAhead = true;}
                } else {goAhead = true;}

            }  else {goAhead = true;}                   
        }
        exaktFrame.saveSearchResultAction.save(file, fileChooser.getFileFilter().getDescription());
        
    }
    
}
