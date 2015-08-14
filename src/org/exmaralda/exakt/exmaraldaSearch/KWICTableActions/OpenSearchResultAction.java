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
import java.util.*;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.*;
import org.jdom.transform.*;
import java.util.prefs.Preferences;
import org.exmaralda.exakt.search.SearchResultList;
import org.exmaralda.exakt.exmaraldaSearch.*;
import org.exmaralda.exakt.exmaraldaSearch.swing.*;


/**
 *
 * @author thomas
 */
public class OpenSearchResultAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    public static final String PATH_TO_INTERNAL_STYLESHEET = "/org/exmaralda/exakt/resources/SearchResult2HTML.xsl";
    
    /** Creates a new instance of SaveSearchResultAction */
    public OpenSearchResultAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(exaktFrame.getLastSearchResultPath());
        fileChooser.setDialogTitle("Open a concordance");
        fileChooser.setFileFilter(new org.exmaralda.exakt.utilities.XMLFileFilter());
        
        int retValue = fileChooser.showOpenDialog(exaktFrame);

        if (retValue==JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            try {
                COMACorpusInterface corpus = (COMACorpusInterface)(exaktFrame.corpusList.getSelectedValue());
                if (corpus==null) return;
                String baseDirectory = corpus.getCorpusPath();
                if (corpus instanceof COMACorpus){
                    baseDirectory = new File(corpus.getCorpusPath()).getParentFile().toURI().toString();
                } else if (corpus instanceof COMARemoteCorpus){
                    // NEED TO GET THE PARENT!
                    // HERE OR SOMEWHERE ELSE?
                    baseDirectory = new URL(new URL(corpus.getCorpusPath()), ".").toString();
                } else if (corpus instanceof COMADBCorpus){
                    // TODO: check if this is OK
                    baseDirectory = new URL(corpus.getCorpusPath()).toString();
                }
                SearchResultList srl = new SearchResultList();
                srl.read(file, baseDirectory);
                COMAKWICSearchPanel newPanel = new COMAKWICSearchPanel(corpus);
                newPanel.setSearchResultList(srl);
                newPanel.setMeta(FileIO.getMetaFromSearchResult(file));
                java.awt.Dimension size = exaktFrame.tabbedPane.getSize();
                newPanel.setPreferredSize(size);
                newPanel.setSize(size);
                newPanel.splitPane.setDividerLocation((int)Math.round(0.8*size.height));
                newPanel.addCOMAKWICSearchPanelListener(exaktFrame);
                newPanel.addKWICTableListener(exaktFrame);
                String panelTitle = corpus.getCorpusName() + " (" + file.getName() + ")";
                exaktFrame.tabbedPane.add(panelTitle,newPanel);        
                exaktFrame.tabbedPane.setSelectedComponent(newPanel);
                exaktFrame.panelIndex.put(newPanel, new Integer(exaktFrame.tabbedPane.getTabCount()-1));    
                exaktFrame.concordanceListModel.addElement(newPanel);
                exaktFrame.concordanceList.setSelectedValue(exaktFrame.getActiveSearchPanel(),true);
                newPanel.setCellEditors();
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
