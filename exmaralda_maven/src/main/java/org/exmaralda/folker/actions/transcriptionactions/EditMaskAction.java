/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.actions.transcriptionactions;

import org.exmaralda.folker.actions.AbstractApplicationAction;
import java.awt.event.ActionEvent;
import javax.swing.*;
import org.exmaralda.folker.application.ApplicationControl;


/**
 *
 * @author thomas
 */
public class EditMaskAction extends AbstractApplicationAction {
    
    
    /** Creates a new instance of OpenAction */
    public EditMaskAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** EditMaskAction ***]");
        ((ApplicationControl)(applicationControl)).maskDialog.setLocationRelativeTo(((ApplicationControl)(applicationControl)).getFrame());
        ((ApplicationControl)(applicationControl)).maskDialog.setVisible(true);
        

        
    }
    
}
