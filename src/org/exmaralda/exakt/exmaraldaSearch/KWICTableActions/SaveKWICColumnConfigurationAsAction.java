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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICTableSorter;
import org.exmaralda.exakt.exmaraldaSearch.swing.KWICColumnConfiguration;
import org.exmaralda.exakt.search.swing.SearchResultListTableModel;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;

/**
 *
 * @author thomas
 */
public class SaveKWICColumnConfigurationAsAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    
    /** Creates a new instance of SaveSearchResultAction
     * @param ef
     * @param title
     * @param icon */
    public SaveKWICColumnConfigurationAsAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("*** SAVE KWIC COLUMN CONFIGURATION AS ACTION");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(exaktFrame.getLastSearchResultPath());
        fileChooser.setDialogTitle("Save the column configuration of the KWIC");
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
                    file = new File(file.getAbsolutePath() + ".xml");
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
        KWICColumnConfiguration kwicColumnConfiguration = ((SearchResultListTableModel)((COMAKWICTableSorter)exaktFrame.getActiveSearchPanel().getKWICTable().getModel()).getTableModel()).getKWICColumnConfiguration();
        Document document = kwicColumnConfiguration.getDocument();
        try {
            FileIO.writeDocumentToLocalFile(file, document);
            System.out.println("KWIC column configration written to " + file.getAbsolutePath());
        } catch (IOException ex) {
            Logger.getLogger(SaveKWICColumnConfigurationAsAction.class.getName()).log(Level.SEVERE, null, ex);
            String message = "Error writing KWIC column configuration to\n" 
                    + file.getAbsolutePath()
                    + ":\n"
                    + ex.getLocalizedMessage();
            JOptionPane.showMessageDialog(exaktFrame, message);
        }
    }
    
}
