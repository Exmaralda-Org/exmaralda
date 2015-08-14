/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.actions.fileactions;

import org.exmaralda.folker.actions.AbstractApplicationAction;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.io.*;
import org.exmaralda.folker.application.ApplicationControl;
import org.exmaralda.folker.data.*;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.folker.utilities.PreferencesUtilities;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.convert.AudacityConverter;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class ImportAction extends AbstractApplicationAction {
    
    String[] exmaraldaSuffixes = {"exb", "xml"};
    ParameterFileFilter exmaraldaFileFilter = new ParameterFileFilter(exmaraldaSuffixes, FOLKERInternationalizer.getString("misc.basicTranscription"));
    ParameterFileFilter audacityFileFilter = new ParameterFileFilter("txt", FOLKERInternationalizer.getString("misc.audacityLabelFile"));
    
    /** Creates a new instance of OpenAction */
    public ImportAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** ImportAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        if (!ac.checkSave()) return;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(FOLKERInternationalizer.getString("dialog.import"));
        fileChooser.addChoosableFileFilter(exmaraldaFileFilter);
        fileChooser.addChoosableFileFilter(audacityFileFilter);
        fileChooser.setFileFilter(exmaraldaFileFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(PreferencesUtilities.getProperty("workingDirectory", "")));        
        int retValue = fileChooser.showOpenDialog(ac.getFrame());
        if (retValue==JFileChooser.CANCEL_OPTION) return;
        
        File f = fileChooser.getSelectedFile();
        ac.setView(0);
        
        EventListTranscription importedTranscription = null;
        String mediaPath = null;
        
        if (fileChooser.getFileFilter()==exmaraldaFileFilter){
            try {
                BasicTranscription bt = new BasicTranscription(f.getAbsolutePath());
                mediaPath = bt.getHead().getMetaInformation().getReferencedFile("wav");
                importedTranscription = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt, true);
            } catch (SAXException ex) {
                applicationControl.displayException(ex);
                return;
            } catch (JexmaraldaException ex) {
                applicationControl.displayException(ex);
                return;
            }
        } else if (fileChooser.getFileFilter()==audacityFileFilter){
            try {
                AudacityConverter converter = new AudacityConverter();
                BasicTranscription bt = converter.readAudacityFromFile(f, AudacityConverter.GAT_PAUSE_DESCRIPTOR);     
                //System.out.println(bt.toXML());
                importedTranscription = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt);
            } catch (IOException ex) {
                applicationControl.displayException(ex);
                return;
            } catch (JexmaraldaException ex) {
                applicationControl.displayException(ex);
                return;
            }
        }
        
        
        ac.transcriptionHead = new TranscriptionHead();
        
        ac.setTranscription(importedTranscription);
        ac.setGeneralDocumentActionsEnabled(true);

        // changed 23-12-2011
        //ac.setMedia(mediaPath);
        boolean mediaSet = false;
        while (!mediaSet){
            try {
                ac.setMedia(mediaPath);
                mediaSet = true;
            } catch (IOException ex) {
                int optionChosen = ac.displayRecordingNotFoundDialog(mediaPath, ex);
                if (optionChosen==JOptionPane.NO_OPTION) return;
                JFileChooser mediaFileChooser = new JFileChooser();
                mediaFileChooser.setDialogTitle(FOLKERInternationalizer.getString("dialog.recording"));
                mediaFileChooser.setFileFilter(new org.exmaralda.folker.utilities.WaveFileFilter());
                mediaFileChooser.setCurrentDirectory(new File(PreferencesUtilities.getProperty("workingDirectory", "")));
                int retValue2 = mediaFileChooser.showOpenDialog(ac.getFrame());
                if (retValue2==JFileChooser.CANCEL_OPTION) return;
                mediaPath = mediaFileChooser.getSelectedFile().getAbsolutePath();
                // added 05-10-2009
                importedTranscription.setMediaPath(mediaPath);
                // added 28-09-2010
                ac.DOCUMENT_CHANGED = true;
            }
        }
        
        PreferencesUtilities.setProperty("workingDirectory", f.getParent());        
        ac.setCurrentFilePath(null);
        ac.reset();
        ac.status(FOLKERInternationalizer.getString("status.import1") + f.getAbsolutePath() +   FOLKERInternationalizer.getString("status.import2"));
        
    }
    
}
