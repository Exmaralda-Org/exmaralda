/*
 * PlayAction.java
 *
 * Created on 14. Mai 2008, 16:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.actions.playeractions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import org.exmaralda.orthonormal.actions.AbstractApplicationAction;
import org.exmaralda.orthonormal.application.ApplicationControl;
/**
 *
 * @author thomas
 */
public class StopAction extends AbstractApplicationAction {
    
    /** Creates a new instance of PlayAction */
    public StopAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    public void actionPerformed(ActionEvent e) {
        applicationControl.stop();
    }
    
}
