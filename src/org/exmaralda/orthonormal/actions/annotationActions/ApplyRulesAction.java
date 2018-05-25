/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.actions.annotationActions;

import org.exmaralda.orthonormal.actions.AbstractApplicationAction;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.orthonormal.application.ApplicationControl;
import org.exmaralda.orthonormal.application.ApplicationFrame;
import org.exmaralda.orthonormal.gui.RulesFileDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.OKCancelDialog;
import org.exmaralda.tagging.PostProcessingRules;
import org.exmaralda.tagging.SextantOrthonormalIntegrator;
import org.exmaralda.tagging.TaggingProfiles;
import org.exmaralda.tagging.TreeTaggableOrthonormalTranscription;
import org.exmaralda.tagging.TreeTagger;
import org.exmaralda.tagging.swing.TreeTaggerParametersPanel;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public class ApplyRulesAction extends AbstractApplicationAction {
    
    ProgressBarDialog pbd;
     
     /** Creates a new instance of TreeTaggerAction
     * @param ac
     * @param name
     * @param icon */
    public ApplyRulesAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** ApplyRulesAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;

        RulesFileDialog rfd = new RulesFileDialog(ac.getFrame(), true);
        String rulesFilePath = java.util.prefs.Preferences.userRoot().node(((ApplicationFrame)ac.getFrame()).getPreferencesNode())
                .get("rules-file", "");        
        rfd.setRulesFile(rulesFilePath);
        rfd.setLocationRelativeTo(ac.getFrame());
        rfd.setVisible(true);
        if (rfd.approved){
            String rulesPath = rfd.getRulesFile();
            java.util.prefs.Preferences.userRoot().node(((ApplicationFrame)ac.getFrame()).getPreferencesNode())
                .put("rules-file", rulesPath); 
            ac.applyRules(rulesPath);
        }
        
        ac.status("Rules applied");
    }
    

        
    
}
