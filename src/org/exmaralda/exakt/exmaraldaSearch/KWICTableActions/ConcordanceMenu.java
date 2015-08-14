/*
 * FileMenu.java
 *
 * Created on 9. Februar 2007, 11:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import org.exmaralda.exakt.exmaraldaSearch.swing.*;


/**
 *
 * @author thomas
 */
public class ConcordanceMenu extends javax.swing.JMenu {
    
    EXAKT exaktFrame;

    /** Creates a new instance of FileMenu */
    public ConcordanceMenu(EXAKT ef) {
        setText("Concordance");
        exaktFrame = ef;

        add(exaktFrame.newSearchPanelAction);
        add(exaktFrame.openSearchResultAction);
        add(exaktFrame.appendSearchResultAction);
        add(exaktFrame.closeSearchResultAction);
        this.add(new JSeparator());
        add(exaktFrame.saveSearchResultAction);
        add(exaktFrame.saveSearchResultAsAction);

        //browsingModeMenuItem = this.add(exaktFrame.browsingModeAction);
        //this.add(new javax.swing.JSeparator());

        /*this.add(new javax.swing.JSeparator());

        add(exaktFrame.newWordlistAction);*/

        /*selectAllMenuItem = this.add(exaktFrame.s);
        editPartiturParametersMenuItem = this.add(exaktFrame.getPartitur().editPartiturParametersAction);
        this.add(new javax.swing.JSeparator());
        editPreferencesMenuItem = this.add(exaktFrame.editPreferencesAction);*/
        
        this.enableMenuItems(false);
    }
    
    

    public void enableMenuItems(boolean enabled){
    }
    
}
