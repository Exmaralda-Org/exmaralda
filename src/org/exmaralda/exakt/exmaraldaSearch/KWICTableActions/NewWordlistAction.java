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

/**
 *
 * @author thomas
 */
public class NewWordlistAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    /** Creates a new instance of NewSearchPanelAction */
    public NewWordlistAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    public void actionPerformed(ActionEvent e) {
        COMACorpusInterface corpus = (COMACorpusInterface)(exaktFrame.corpusList.getSelectedValue());
        if (corpus==null) return;

        if (corpus.isWordSegmented()){
                exaktFrame.readWordList(corpus);
        }
    }
    
}
