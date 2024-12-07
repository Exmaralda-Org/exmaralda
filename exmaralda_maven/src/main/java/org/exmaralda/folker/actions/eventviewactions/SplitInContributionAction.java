/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.actions.eventviewactions;

import org.exmaralda.folker.actions.AbstractApplicationAction;
import java.awt.event.ActionEvent;
import javax.swing.*;
import org.exmaralda.folker.application.ApplicationControl;


/**
 *
 * @author thomas
 */
public class SplitInContributionAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction */
    public SplitInContributionAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** SplitInContributionAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        ac.splitInContribution();
    }
    
}
