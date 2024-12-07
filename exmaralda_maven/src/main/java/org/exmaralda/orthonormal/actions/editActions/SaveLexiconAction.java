/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.actions.editActions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import org.exmaralda.orthonormal.actions.AbstractApplicationAction;
import org.exmaralda.orthonormal.application.ApplicationControl;


/**
 *
 * @author thomas
 */
public class SaveLexiconAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction */
    public SaveLexiconAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** SaveLexiconAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl; 
        ac.saveLexicon();
    }
    
}
