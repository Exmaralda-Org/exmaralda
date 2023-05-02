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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.folker.application.ApplicationControl;
import org.exmaralda.folker.data.*;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.folker.utilities.PreferencesUtilities;
import org.exmaralda.orthonormal.data.NormalizedFolkerTranscription;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.convert.AmberscriptJSONConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.AudacityConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.F4Converter;
import org.exmaralda.partitureditor.jexmaralda.convert.SubtitleConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.WhisperJSONConverter;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class ImportAction extends AbstractApplicationAction {
    
    String[] exmaraldaSuffixes = {"exb"};
    ParameterFileFilter exmaraldaFileFilter = new ParameterFileFilter(exmaraldaSuffixes, FOLKERInternationalizer.getString("misc.basicTranscription"));
    ParameterFileFilter audacityFileFilter = new ParameterFileFilter("txt", FOLKERInternationalizer.getString("misc.audacityLabelFile"));
    ParameterFileFilter teiFileFilter = new ParameterFileFilter("xml", "ISO/TEI File (*.xml)");
    ParameterFileFilter f4FileFilter = new ParameterFileFilter("txt", "F4 Transcript (*.txt)");

    // new 15-01-2023: issues #119, #357 and #358
    ParameterFileFilter WhisperJSONFileFilter = new ParameterFileFilter("json", "Whisper JSON file (*.json)"); // issue #357
    ParameterFileFilter AmberscriptJSONFileFilter = new ParameterFileFilter("json", "Amberscript JSON file (*.json)"); // issue #358
    ParameterFileFilter SRTFileFilter = new ParameterFileFilter("srt", "SubRip Subtitle file (*.srt)");
    ParameterFileFilter VTTFileFilter = new ParameterFileFilter("vtt", "Web Video Text Tracks file (*.vtt)");
    
    
    /** Creates a new instance of OpenAction
     * @param ac
     * @param name
     * @param icon */
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
        fileChooser.addChoosableFileFilter(teiFileFilter);
        fileChooser.addChoosableFileFilter(audacityFileFilter);
        fileChooser.addChoosableFileFilter(f4FileFilter);

        // new 15-01-2023: issues #119, #357 and #358
        fileChooser.addChoosableFileFilter(WhisperJSONFileFilter);
        fileChooser.addChoosableFileFilter(AmberscriptJSONFileFilter);
        fileChooser.addChoosableFileFilter(SRTFileFilter);
        fileChooser.addChoosableFileFilter(VTTFileFilter);

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
            } catch (SAXException | JexmaraldaException ex) {
                applicationControl.displayException(ex);
                return;
            }
        } else if (fileChooser.getFileFilter()==audacityFileFilter){
            try {
                AudacityConverter converter = new AudacityConverter();
                BasicTranscription bt = converter.readAudacityFromFile(f, AudacityConverter.GAT_PAUSE_DESCRIPTOR);     
                //System.out.println(bt.toXML());
                importedTranscription = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt);
            } catch (IOException | JexmaraldaException ex) {
                applicationControl.displayException(ex);
                return;
            }
        } else if (fileChooser.getFileFilter()==teiFileFilter){
            try {
                TEIConverter converter = new TEIConverter();
                NormalizedFolkerTranscription nft = converter.readFOLKERISOTEIFromFile(f.getAbsolutePath());
                mediaPath = nft.getMediaPath();
                importedTranscription = org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.readXML(nft.getDocument(), 0);
            } catch (IOException | JexmaraldaException | JDOMException | SAXException | ParserConfigurationException | TransformerException | IllegalArgumentException ex) {
                applicationControl.displayException(ex);
                return;
            }
        } else if (fileChooser.getFileFilter()==f4FileFilter){
            try {
                F4Converter converter = new F4Converter();
                converter.readText(f, "UTF-8");
                BasicTranscription bt = converter.importF4(true);
                bt.getBody().getCommonTimeline().completeTimes(false, bt);
                importedTranscription = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt, true);
            } catch (IOException | IllegalArgumentException ex) {
                applicationControl.displayException(ex);
                return;
            }
        } else if (fileChooser.getFileFilter()==VTTFileFilter){
            try {
                BasicTranscription bt = SubtitleConverter.readVTT(f);
                importedTranscription = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt, true);
            } catch (IOException | IllegalArgumentException | JexmaraldaException ex) {
                applicationControl.displayException(ex);
                return;
            }
        } else if (fileChooser.getFileFilter()==SRTFileFilter){
            try {
                BasicTranscription bt = SubtitleConverter.readSRT(f);
                importedTranscription = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt, true);
            } catch (IOException | IllegalArgumentException | JexmaraldaException ex) {
                applicationControl.displayException(ex);
                return;
            }
        } else if (fileChooser.getFileFilter()==WhisperJSONFileFilter){
            try {
                BasicTranscription bt = WhisperJSONConverter.readWhisperJSON(f, false);
                importedTranscription = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt, true);
            } catch (IOException | IllegalArgumentException | JexmaraldaException ex) {
                applicationControl.displayException(ex);
                return;
            }
        } else if (fileChooser.getFileFilter()==AmberscriptJSONFileFilter){
            try {
                BasicTranscription bt = AmberscriptJSONConverter.readAmberscriptJSON(f);
                importedTranscription = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt, true);
            } catch (IOException | IllegalArgumentException | JexmaraldaException ex) {
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
                mediaFileChooser.setDialogTitle(FOLKERInternationalizer.getString("dialog.recording") + ": " + f.getName());
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
