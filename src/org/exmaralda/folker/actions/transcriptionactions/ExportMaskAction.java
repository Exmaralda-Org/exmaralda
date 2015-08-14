/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.actions.transcriptionactions;

import org.exmaralda.folker.actions.AbstractApplicationAction;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.io.*;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.folker.application.ApplicationControl;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.folker.utilities.PreferencesUtilities;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class ExportMaskAction extends AbstractApplicationAction {
    
    ParameterFileFilter htmlFileFilter = new ParameterFileFilter("html", "HTML (*.html)");
    private static String MASK2HTML_STYLESHEET = "/org/exmaralda/folker/data/mask2html.xsl";
    
    /** Creates a new instance of OpenAction */
    public ExportMaskAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** ExportMaskAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(FOLKERInternationalizer.getString("dialog.export"));
        fileChooser.addChoosableFileFilter(htmlFileFilter);
        fileChooser.setFileFilter(htmlFileFilter);
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
        }
        try {
            if (fileChooser.getFileFilter()==htmlFileFilter){
                Element mask = ac.getTranscriptionHead().getHeadElement();
                String maskString = IOUtilities.elementToString(mask);
                StylesheetFactory ssf = new StylesheetFactory(true);
                String html = ssf.applyInternalStylesheetToString(MASK2HTML_STYLESHEET, maskString);
                System.out.println("started writing document " + f.getAbsolutePath() + "...");
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(html.getBytes("UTF-8"));
                fos.close();
                System.out.println("document written.");        
                
            } 
        } catch (Exception ex) {
            applicationControl.displayException(ex);
            return;
        }
        
        PreferencesUtilities.setProperty("workingDirectory", f.getParent());        
        ac.status("Mask exported to " + f.getAbsolutePath());

        
    }
    
}
