/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.actions.waveformactions;

import org.exmaralda.folker.actions.AbstractApplicationAction;
import java.awt.event.ActionEvent;
import javax.swing.*;
import org.exmaralda.folker.application.AbstractTimeviewPartiturPlayerControl;
import org.exmaralda.partitureditor.partiture.PartiturEditorTimeviewPlayerControl;


/**
 *
 * @author thomas
 */
public class ScrollToTimeAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction */
    public ScrollToTimeAction(AbstractTimeviewPartiturPlayerControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** ScrollToTimeAction ***]");
        if (!(this.applicationControl instanceof PartiturEditorTimeviewPlayerControl)) return;
        PartiturEditorTimeviewPlayerControl petpc = (PartiturEditorTimeviewPlayerControl)applicationControl;
        petpc.partitur.commitEdit(true);
        double selectionStart = petpc.selectionStart;
        System.out.println("Selection start: " + selectionStart);
        petpc.scrollToTime(selectionStart / 1000.0);
    }
    
}
