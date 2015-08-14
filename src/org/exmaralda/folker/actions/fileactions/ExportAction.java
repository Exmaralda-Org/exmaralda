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
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.GATParser;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.folker.utilities.PreferencesUtilities;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.convert.AudacityConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.F4Converter;
import org.exmaralda.partitureditor.jexmaralda.convert.SubtitleConverter;
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
    ParameterFileFilter vttFileFilter = new ParameterFileFilter("vtt", "VTT subtitles (*.vtt)");
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
        fileChooser.addChoosableFileFilter(exmaraldaFileFilter);
        fileChooser.addChoosableFileFilter(elanFileFilter);
        fileChooser.addChoosableFileFilter(praatFileFilter);
        fileChooser.addChoosableFileFilter(f4FileFilter);
        fileChooser.addChoosableFileFilter(teiFileFilter);
        fileChooser.addChoosableFileFilter(tcfFileFilter);
        fileChooser.addChoosableFileFilter(audacityLabelFileFilter);
        fileChooser.addChoosableFileFilter(vttFileFilter);
        fileChooser.addChoosableFileFilter(srtFileFilter);
        fileChooser.addChoosableFileFilter(subtitlePlainTextFileFilter);        
        fileChooser.setFileFilter(exmaraldaFileFilter);
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
                    BasicTranscription bt = ac.getBasicTranscription();
                    bt.writeXMLToFile(f.getAbsolutePath(), "none");
            } else if (fileChooser.getFileFilter()==elanFileFilter){
                    BasicTranscription bt = ac.getBasicTranscription();
                    org.exmaralda.partitureditor.jexmaralda.convert.ELANConverter converter 
                            = new org.exmaralda.partitureditor.jexmaralda.convert.ELANConverter();
                    converter.writeELANToFile(bt, f.getAbsolutePath());
            }  else if (fileChooser.getFileFilter()==praatFileFilter){
                    BasicTranscription bt = ac.getBasicTranscription();
                    org.exmaralda.partitureditor.jexmaralda.convert.PraatConverter converter 
                            = new org.exmaralda.partitureditor.jexmaralda.convert.PraatConverter();
                    converter.writePraatToFile(bt, f.getAbsolutePath());
            } else if (fileChooser.getFileFilter()==teiFileFilter){
                    BasicTranscription bt = ac.getBasicTranscription();
                    TEIConverter converter = new TEIConverter();
                    // changed 16-01-2015: ISO!
                    converter.writeFOLKERISOTEIToFile(bt, f.getAbsolutePath());
            } else if (fileChooser.getFileFilter()==tcfFileFilter){
                    Document flnDoc = EventListTranscriptionXMLReaderWriter.toJDOMDocument(ac.getTranscription(), f);
                    
                    GATParser gp = new GATParser();
                    gp.parseDocument(flnDoc, 1);
                    gp.parseDocument(flnDoc, 2);
                    
                    TCFConverter converter = new TCFConverter();
                    converter.writeFOLKERTCFToFile(flnDoc, f.getAbsolutePath()); 
            }  else if (fileChooser.getFileFilter()==f4FileFilter){
                    BasicTranscription bt = ac.getBasicTranscription();
                    F4Converter converter = new F4Converter();
                    converter.writeText(bt, f, F4Converter.SPEAKER_CONTRIBUTIONS, "RTF");
            }  else if (fileChooser.getFileFilter()==audacityLabelFileFilter){
                    BasicTranscription bt = ac.getBasicTranscription();
                    AudacityConverter.writeAudacityToFile(bt, f.getAbsolutePath());
            }  else if (fileChooser.getFileFilter()==srtFileFilter){
                    EventListTranscription elt = ac.getTranscription();
                    SubtitleConverter converter = new SubtitleConverter(elt);
                    converter.writeSRT(f);
            }  else if (fileChooser.getFileFilter()==vttFileFilter){
                    EventListTranscription elt = ac.getTranscription();
                    SubtitleConverter converter = new SubtitleConverter(elt);
                    converter.writeVTT(f);
            }  else if (fileChooser.getFileFilter()==srtFileFilter){
                    EventListTranscription elt = ac.getTranscription();
                    SubtitleConverter converter = new SubtitleConverter(elt);
                    converter.writeSRT(f);
            } else if (fileChooser.getFileFilter()==subtitlePlainTextFileFilter){
                    EventListTranscription elt = ac.getTranscription();
                    SubtitleConverter converter = new SubtitleConverter(elt);
                    converter.writeSRT(f, true, true);                
            }

        } catch (Exception ex) {
            applicationControl.displayException(ex);
            return;
        }
        
        PreferencesUtilities.setProperty("workingDirectory", f.getParent());        
        ac.status(FOLKERInternationalizer.getString("status.export1") + f.getAbsolutePath() + FOLKERInternationalizer.getString("status.export2"));

        
    }
    
}
