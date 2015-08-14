/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.application;

import org.exmaralda.common.*;
import org.exmaralda.common.helpers.Internationalizer;


/**
 *
 * @author thomas
 */
public class AboutAction extends javax.swing.AbstractAction {

    ExmaraldaApplication application;
    
    public AboutAction(String name, ExmaraldaApplication app) {
        super(Internationalizer.getString(name));
        application = app;
    }

    
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        AboutDialog ad = new AboutDialog(null, true, application);
        ad.setLocationRelativeTo((java.awt.Component)application);
        ad.setVisible(true);
    }

}
