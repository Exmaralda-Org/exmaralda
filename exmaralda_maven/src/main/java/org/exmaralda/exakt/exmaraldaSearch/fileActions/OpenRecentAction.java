/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.fileActions;

import java.awt.event.ActionEvent;
import java.io.File;
import org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT;

/**
 *
 * @author thomas
 */
public class OpenRecentAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {

    File filetoopen;
    
    public OpenRecentAction(EXAKT exaktFrame, String filename) {
        super(exaktFrame, new File(filename).getName());
        filetoopen = new File(filename);
    }

    public void actionPerformed(ActionEvent e) {
        exaktFrame.doOpen(filetoopen);
    }

}
