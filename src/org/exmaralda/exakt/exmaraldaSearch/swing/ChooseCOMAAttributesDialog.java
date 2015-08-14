/*
 * ChooseCOMAAttributesDialog.java
 *
 * Created on 10. Januar 2007, 18:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.swing;

import java.util.*;

/**
 *
 * @author thomas
 */
public class ChooseCOMAAttributesDialog extends org.exmaralda.exakt.search.swing.AbstractOKCancelDialog {
    
    ChooseCOMAAttributesPanel panel;
    
    /** Creates a new instance of ChooseCOMAAttributesDialog */
    public ChooseCOMAAttributesDialog(java.awt.Frame parent, boolean modal, 
                                        Set<String> speakerAttributes, 
                                        Set<String> communicationAttributes,
                                        Set<String> transcriptionAttributes, 
                                        Vector<String[]> selectedAttributes){
        super(parent, modal);
        setTitle("Choose COMA Attributes");
        panel = new ChooseCOMAAttributesPanel(speakerAttributes, communicationAttributes, transcriptionAttributes, selectedAttributes);
        getContentPane().add(panel);
        pack();
    }
    
    public Vector<String[]> getSelectedAttributes(){
        return panel.getSelectedAttributes();
    }
    
    
}
