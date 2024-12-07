/*
 * AbstractApplicationAction.java
 *
 * Created on 9. Mai 2008, 15:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.actions;

import javax.swing.*;
import org.exmaralda.orthonormal.application.ApplicationControl;

/**
 *
 * @author thomas
 */
public abstract class AbstractApplicationAction extends javax.swing.AbstractAction {
    
    public ApplicationControl applicationControl;
    
    /** Creates a new instance of AbstractApplicationAction */
    public AbstractApplicationAction(ApplicationControl ac, String name, Icon icon) {
        super(name, icon);
        applicationControl = ac;
    }
    
}
