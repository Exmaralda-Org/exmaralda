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
import java.net.URL;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.*;
import org.exmaralda.exakt.search.SearchResultList;
import org.exmaralda.exakt.exmaraldaSearch.*;
import org.exmaralda.exakt.exmaraldaSearch.swing.*;


/**
 *
 * @author thomas
 */
public class OpenKWICColumnConfigurationAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    
    /** Creates a new instance of SaveSearchResultAction
     * @param ef
     * @param title
     * @param icon */
    public OpenKWICColumnConfigurationAction(EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("*** OPEN KWIC COLUMN CONFIGURATION ACTION");
        
        
        SearchResultList searchResultList = exaktFrame.getActiveSearchPanel().getSearchResultList();
        if (!searchResultList.getAnalyses().isEmpty()){
            String message = "You have " + searchResultList.getAnalyses().size() + " analysis columns for this concordance.\n";
            message+="These will be deleted.\n";
            message+="Do you want to continue?";
            int ret = javax.swing.JOptionPane.showConfirmDialog(exaktFrame, message, "Warning", javax.swing.JOptionPane.YES_NO_OPTION);
            //JOptionPane.showOptionDialog(praatButton, message, message, WIDTH, HEIGHT, icon, ADDITIONAL_DATA_LOCATORS, kwicTable)
            if (ret==javax.swing.JOptionPane.NO_OPTION) return;
        }
        
        
        
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(exaktFrame.getLastSearchResultPath());
        fileChooser.setDialogTitle("Open a KWIC column configuration");
        fileChooser.setFileFilter(new org.exmaralda.exakt.utilities.XMLFileFilter());
        
        int retValue = fileChooser.showOpenDialog(exaktFrame);

        if (retValue==JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            try {
                KWICColumnConfiguration columnConfiguration = KWICColumnConfiguration.readFromFile(file);
                exaktFrame.getActiveSearchPanel().setColumnConfiguration(columnConfiguration);
                exaktFrame.status("KWIC column configuration " + file.getName() + " opened. ");
            } catch (JDOMException ex) {
                String message = "JDOM Exception:";
                message += ex.getMessage() + "\n";
                javax.swing.JOptionPane.showMessageDialog(exaktFrame, message);
                System.out.println(ex.getLocalizedMessage());
            } catch (IOException ex) {
                String message = "IOException:";
                message += ex.getMessage() + "\n";
                javax.swing.JOptionPane.showMessageDialog(exaktFrame, message);
                System.out.println(ex.getLocalizedMessage());
            }
        }
        
    }
    
}
