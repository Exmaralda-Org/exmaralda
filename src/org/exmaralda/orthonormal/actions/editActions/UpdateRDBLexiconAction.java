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
import java.sql.SQLException;
import javax.swing.*;
import org.exmaralda.orthonormal.actions.AbstractApplicationAction;
import org.exmaralda.orthonormal.application.ApplicationControl;
import org.exmaralda.orthonormal.lexicon.LexiconException;
import org.jdom.JDOMException;


/**
 *
 * @author thomas
 */
public class UpdateRDBLexiconAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction */
    public UpdateRDBLexiconAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** UpdateRDBLexiconAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        try {
            ac.updateRDBLexicon();
        } catch (SQLException ex) {
            ex.printStackTrace();
            ac.displayException(ex);
        } catch (JDOMException ex) {
            ex.printStackTrace();
            ac.displayException(ex);
        } catch (LexiconException ex) {
            ex.printStackTrace();
            ac.displayException(ex);
        }
    }
    
}
