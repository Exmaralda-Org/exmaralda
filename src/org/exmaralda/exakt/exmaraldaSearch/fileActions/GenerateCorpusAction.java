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
/**
 *
 * @author thomas
 */
public class GenerateCorpusAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    /** Creates a new instance of OpenCorpusAction */
    public GenerateCorpusAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    public void actionPerformed(ActionEvent e) {
        org.exmaralda.coma.helpers.CCW corpusCreationWizard = new org.exmaralda.coma.helpers.CCW(exaktFrame, true);
        corpusCreationWizard.setLocationRelativeTo(exaktFrame);
        corpusCreationWizard.setVisible(true);
        File file = corpusCreationWizard.resultFile;
        if (file!=null){
            exaktFrame.doOpen(file);
            exaktFrame.setLastCorpusPath(file);
        }
    }
    
}
