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
import org.exmaralda.exakt.exmaraldaSearch.swing.OpenDBCorpusDialog;
/**
 *
 * @author thomas
 */
public class OpenDBCorpusAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    /** Creates a new instance of OpenCorpusAction */
    public OpenDBCorpusAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    public void actionPerformed(ActionEvent e) {
        OpenDBCorpusDialog dialog = new OpenDBCorpusDialog(exaktFrame, true);
        dialog.setLocationRelativeTo(exaktFrame);
        dialog.setVisible(true);
        if (!dialog.approved) return;
        String url = dialog.getURL();
        String cn = dialog.getCorpusName();
        String un = dialog.getUsername();
        String pw = "";
        for (char c : dialog.getPassword()){
            pw+=c;
        }
        // set the authenticator for http file access
        java.net.Authenticator.setDefault(new org.exmaralda.exakt.utilities.ConfigurableAuthenticator(un, pw));
        // open the database
        exaktFrame.doOpenDB(cn, url, un, pw);
    }
    
}
