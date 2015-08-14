/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.actions.fileactions;

import org.exmaralda.orthonormal.actions.AbstractApplicationAction;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.io.*;
import org.exmaralda.orthonormal.application.ApplicationControl;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.folker.utilities.PreferencesUtilities;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;

/**
 *
 * @author thomas
 */
public class OutputAction extends AbstractApplicationAction {
    
    ParameterFileFilter htmlContributionListFilter = new ParameterFileFilter("html", "Normalisierte Beitragsliste");
    public static String CONTRIBUTIONS2HTML_STYLESHEET = "/org/exmaralda/folker/data/normalizedFolker2HTMLContributionList.xsl";
    
    /** Creates a new instance of OpenAction */
    public OutputAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** OutputAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(FOLKERInternationalizer.getString("dialog.output"));
        fileChooser.addChoosableFileFilter(htmlContributionListFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(PreferencesUtilities.getProperty("workingDirectory", "")));        

        int retValue = fileChooser.showSaveDialog(ac.getFrame());
        if (retValue==JFileChooser.CANCEL_OPTION) return;
        
        File f = fileChooser.getSelectedFile();
        if (!(f.getName().indexOf(".")>=0)){
            f = new File(f.getAbsolutePath() + "." + ((ParameterFileFilter)(fileChooser.getFileFilter())).getSuffix());
        }
        if (f.exists()){
            int confirm = JOptionPane.showConfirmDialog(ac.getFrame(), FOLKERInternationalizer.getString("option.fileexists"));
            if (confirm!=JOptionPane.OK_OPTION){
                actionPerformed(e);
            }
        } // remotely commented
        try {
            if ((fileChooser.getFileFilter()==htmlContributionListFilter)){
                String STYLESHEET = "";
                if (fileChooser.getFileFilter()==htmlContributionListFilter) {
                    System.out.println("[*** ContributionListOutput ***]");
                    STYLESHEET = CONTRIBUTIONS2HTML_STYLESHEET;
                }
                Document transcriptionDoc = ac.getTranscription().getDocument();
                String docString = org.exmaralda.common.jdomutilities.IOUtilities.documentToString(transcriptionDoc);
                StylesheetFactory sf = new StylesheetFactory(true);
                String resultString = sf.applyInternalStylesheetToString(STYLESHEET, docString);

                System.out.println("started writing document...");
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(resultString.getBytes("UTF-8"));
                fos.close();
                System.out.println("document written.");
            }
        } catch (Exception ex) {
            applicationControl.displayException(ex);
            return;
        }
        
        PreferencesUtilities.setProperty("workingDirectory", f.getParent());        
        ac.status(FOLKERInternationalizer.getString("status.output1") + f.getAbsolutePath() +   FOLKERInternationalizer.getString("status.output2"));

        
    }
    
}
