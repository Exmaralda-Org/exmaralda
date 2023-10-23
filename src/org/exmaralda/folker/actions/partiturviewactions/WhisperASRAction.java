/*
 * NewAction.java
 *
 * Created on 16. Juni 2003, 16:23
 */

package org.exmaralda.folker.actions.partiturviewactions;

import java.awt.event.ActionEvent;
import javax.swing.Icon;
import org.exmaralda.folker.actions.AbstractApplicationAction;
import org.exmaralda.folker.application.AbstractTimeviewPartiturPlayerControl;



/**
 * Creates a new transcription
 * Menu: File --> New
 * @author  thomas
 */
public class WhisperASRAction extends AbstractApplicationAction {
    
    
    /** Creates a new instance of NewAction
     * @param ac
     * @param name
     * @param icon */
    public WhisperASRAction(AbstractTimeviewPartiturPlayerControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** WhisperASRAction ***]");
        applicationControl.whisperASR();
    }
    
        
}



