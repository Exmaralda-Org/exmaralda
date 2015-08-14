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
import org.exmaralda.partitureditor.jexmaralda.convert.TCFConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
import org.jdom.Document;

/**
 *
 * @author thomas
 */
public class ExportAction extends AbstractApplicationAction {
    
    String[] exmaraldaSuffixes = {"exb", "xml"};
    String[] f4Suffixes = {"rtf", "txt"};
    String[] tcfSuffixes = {"tcf", "txt"};
    ParameterFileFilter exmaraldaFileFilter = new ParameterFileFilter(exmaraldaSuffixes, FOLKERInternationalizer.getString("misc.basicTranscription"));
    ParameterFileFilter elanFileFilter = new ParameterFileFilter("eaf", "ELAN Annotation File (*.eaf)");
    ParameterFileFilter praatFileFilter = new ParameterFileFilter("textGrid", "Praat TextGrid (*.textGrid)");
    ParameterFileFilter teiFileFilter = new ParameterFileFilter("xml", "TEI File (*.xml)");
    ParameterFileFilter tcfFileFilter = new ParameterFileFilter(tcfSuffixes, "TCF File (*.tcf, *.xml)");
    ParameterFileFilter f4FileFilter = new ParameterFileFilter(f4Suffixes, "F4 transcript (*.rtf, *.txt)");
    ParameterFileFilter audacityLabelFileFilter = new ParameterFileFilter("txt", "Audacity label file (*.txt)");
    ParameterFileFilter srtFileFilter = new ParameterFileFilter("srt", "SRT subtitles (*.srt)");
    ParameterFileFilter subtitlePlainTextFileFilter = new ParameterFileFilter("txt", "Plain text subtitles (*.txt)");
    
    /** Creates a new instance of OpenAction */
    public ExportAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** ExportAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(FOLKERInternationalizer.getString("dialog.export"));
        //fileChooser.addChoosableFileFilter(exmaraldaFileFilter);
        //fileChooser.addChoosableFileFilter(elanFileFilter);
        //fileChooser.addChoosableFileFilter(praatFileFilter);
        //fileChooser.addChoosableFileFilter(f4FileFilter);
        fileChooser.addChoosableFileFilter(teiFileFilter);
        fileChooser.addChoosableFileFilter(tcfFileFilter);
        //fileChooser.addChoosableFileFilter(audacityLabelFileFilter);
        //fileChooser.addChoosableFileFilter(srtFileFilter);
        //fileChooser.addChoosableFileFilter(subtitlePlainTextFileFilter);        
        fileChooser.setFileFilter(teiFileFilter);
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
            if (fileChooser.getFileFilter()==exmaraldaFileFilter){
            } else if (fileChooser.getFileFilter()==elanFileFilter){
            }  else if (fileChooser.getFileFilter()==praatFileFilter){
            } else if (fileChooser.getFileFilter()==teiFileFilter){
                Document flnDoc = ac.getTranscription().getDocument();
                TEIConverter converter = new TEIConverter();
                converter.writeFOLKERISOTEIToFile(flnDoc, f.getAbsolutePath()); 
            } else if (fileChooser.getFileFilter()==tcfFileFilter){
                Document flnDoc = ac.getTranscription().getDocument();
                TCFConverter converter = new TCFConverter();
                converter.writeFOLKERTCFToFile(flnDoc, f.getAbsolutePath()); 
            } else if (fileChooser.getFileFilter()==f4FileFilter){
            } else if (fileChooser.getFileFilter()==audacityLabelFileFilter){
            } else if (fileChooser.getFileFilter()==srtFileFilter){
            } else if (fileChooser.getFileFilter()==srtFileFilter){
            } else if (fileChooser.getFileFilter()==subtitlePlainTextFileFilter){
            }

        } catch (Exception ex) {
            applicationControl.displayException(ex);
            return;
        }
        
        PreferencesUtilities.setProperty("workingDirectory", f.getParent());        
        ac.status(FOLKERInternationalizer.getString("status.export1") + f.getAbsolutePath() + FOLKERInternationalizer.getString("status.export2"));

        
    }
    
}
