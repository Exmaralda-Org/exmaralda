/*
 * NewSearchPanelAction.java
 *
 * Created on 9. Februar 2007, 11:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.event.ActionEvent;
import org.exmaralda.exakt.exmaraldaSearch.*;
import org.exmaralda.exakt.exmaraldaSearch.swing.*;

/**
 *
 * @author thomas
 */
public class NewSearchPanelAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    /** Creates a new instance of NewSearchPanelAction */
    public NewSearchPanelAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    public void actionPerformed(ActionEvent e) {
        COMACorpusInterface corpus = (COMACorpusInterface)(exaktFrame.corpusList.getSelectedValue());
        if (corpus==null) return;
        COMAKWICSearchPanel newPanel = new COMAKWICSearchPanel(corpus);
        java.awt.Dimension size = exaktFrame.tabbedPane.getSize();
        newPanel.setPreferredSize(size);
        newPanel.setSize(size);        
        newPanel.splitPane.setDividerLocation((int)Math.round(0.8*size.height));

        newPanel.addCOMAKWICSearchPanelListener(exaktFrame);
        newPanel.addKWICTableListener(exaktFrame);
        exaktFrame.tabbedPane.add(corpus.getCorpusName(),newPanel);        
        exaktFrame.tabbedPane.setSelectedComponent(newPanel);
        exaktFrame.panelIndex.put(newPanel, new Integer(exaktFrame.tabbedPane.getTabCount()-1));    
        exaktFrame.concordanceListModel.addElement(newPanel);
        exaktFrame.concordanceList.setSelectedValue(exaktFrame.getActiveSearchPanel(),true);        
    }
    
}
