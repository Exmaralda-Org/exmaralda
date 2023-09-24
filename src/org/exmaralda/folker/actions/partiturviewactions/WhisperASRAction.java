/*
 * NewAction.java
 *
 * Created on 16. Juni 2003, 16:23
 */

package org.exmaralda.folker.actions.partiturviewactions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.exmaralda.folker.actions.AbstractApplicationAction;
import org.exmaralda.folker.application.AbstractTimeviewPartiturPlayerControl;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;
import org.exmaralda.webservices.Whisper4EXMARaLDA;
import org.exmaralda.webservices.WhisperConnector;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;



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



