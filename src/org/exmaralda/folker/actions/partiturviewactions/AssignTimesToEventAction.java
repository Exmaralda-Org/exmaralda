/*
 * PlayAction.java
 *
 * Created on 14. Mai 2008, 16:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.actions.partiturviewactions;

import java.awt.event.ActionEvent;
import org.exmaralda.folker.actions.AbstractApplicationAction;
import javax.swing.*;
import org.exmaralda.folker.application.AbstractTimeviewPartiturPlayerControl;
/**
 *
 * @author thomas
 */
public class AssignTimesToEventAction extends AbstractApplicationAction {
    
    /** Creates a new instance of PlayAction
     * @param ac
     * @param name
     * @param icon */
    public AssignTimesToEventAction(AbstractTimeviewPartiturPlayerControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** AssignTimesToEventAction ***]");
        applicationControl.assignTimesToEvent();
    }
    
}
