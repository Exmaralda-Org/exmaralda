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
import java.net.MalformedURLException;
import java.net.URL;
import org.exmaralda.exakt.exmaraldaSearch.swing.OpenRemoteCorpusDialog;
/**
 *
 * @author thomas
 */
public class OpenRemoteCorpusAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    /** Creates a new instance of OpenCorpusAction */
    public OpenRemoteCorpusAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    public void actionPerformed(ActionEvent e) {
        //String url = "http://www1.uni-hamburg.de/exmaralda/files/demokorpus/EXMARaLDA_DemoKorpus.coma";
        //String url = "http://www1.uni-hamburg.de/exmaralda/files/k2-korpus/K2_Corpus.xml";
        OpenRemoteCorpusDialog dialog = new OpenRemoteCorpusDialog(exaktFrame, true);
        dialog.setLocationRelativeTo(exaktFrame);
        dialog.setVisible(true);
        if (!dialog.approved) return;
        String url = dialog.getURL();
        if (!dialog.getAnonymous()){
            String un = dialog.getUsername();
            String pw = "";
            for (char c : dialog.getPassword()){
                pw+=c;
            }
            java.net.Authenticator.setDefault(new org.exmaralda.exakt.utilities.ConfigurableAuthenticator(un, pw));
        }
        try {
            exaktFrame.doOpen(new URL(url));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }
    
}
