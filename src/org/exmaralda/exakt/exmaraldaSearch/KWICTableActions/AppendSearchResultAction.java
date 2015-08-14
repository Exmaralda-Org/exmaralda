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
import java.util.*;
import org.jdom.*;
import org.jdom.transform.*;
import java.util.prefs.Preferences;
import org.exmaralda.exakt.search.SearchResultList;

/**
 *
 * @author thomas
 */
public class AppendSearchResultAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    public static final String PATH_TO_INTERNAL_STYLESHEET = "/org/exmaralda/exakt/resources/SearchResult2HTML.xsl";
    
    /** Creates a new instance of SaveSearchResultAction */
    public AppendSearchResultAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(exaktFrame.getLastSearchResultPath());
        fileChooser.setDialogTitle("Append a concordance");
        fileChooser.setFileFilter(new org.exmaralda.exakt.utilities.XMLFileFilter());
        
        int retValue = fileChooser.showOpenDialog(exaktFrame);

        if (retValue==fileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            try {
                SearchResultList appendToList = exaktFrame.getActiveSearchPanel().getSearchResultList();
                SearchResultList appendedList = new SearchResultList();
                appendedList.read(file);
                SearchResultList newList = appendToList.merge(appendedList, true);
                
                exaktFrame.getActiveSearchPanel().setSearchResultList(newList);
                exaktFrame.getActiveSearchPanel().setCellEditors();                

            } catch (JDOMException ex) {
                String message = "JDOM Exception:";
                message += ex.getMessage() + "\n";
                javax.swing.JOptionPane.showMessageDialog(exaktFrame, message);
                ex.printStackTrace();
                return;
            } catch (IOException ex) {
                String message = "IOException:";
                message += ex.getMessage() + "\n";
                javax.swing.JOptionPane.showMessageDialog(exaktFrame, message);
                ex.printStackTrace();
                return;
            }
        }
        
    }
    
}
