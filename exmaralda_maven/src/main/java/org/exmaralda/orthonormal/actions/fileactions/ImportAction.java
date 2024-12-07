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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.orthonormal.application.ApplicationControl;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.folker.utilities.PreferencesUtilities;
import org.exmaralda.orthonormal.data.NormalizedFolkerTranscription;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.jexmaralda.convert.TCFConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class ImportAction extends AbstractApplicationAction {
    
    String[] exmaraldaSuffixes = {"exb", "xml"};
    String[] f4Suffixes = {"rtf", "txt"};
    String[] tcfSuffixes = {"tcf", "xml"};
    ParameterFileFilter exmaraldaFileFilter = new ParameterFileFilter(exmaraldaSuffixes, FOLKERInternationalizer.getString("misc.basicTranscription"));
    ParameterFileFilter elanFileFilter = new ParameterFileFilter("eaf", "ELAN Annotation File (*.eaf)");
    ParameterFileFilter praatFileFilter = new ParameterFileFilter("textGrid", "Praat TextGrid (*.textGrid)");
    ParameterFileFilter teiFileFilter = new ParameterFileFilter("xml", "ISO/TEI File (*.xml)");
    ParameterFileFilter tcfFileFilter = new ParameterFileFilter(tcfSuffixes, "TCF File (*.tcf, *.xml)");
    ParameterFileFilter f4FileFilter = new ParameterFileFilter(f4Suffixes, "F4 transcript (*.rtf, *.txt)");
    ParameterFileFilter audacityLabelFileFilter = new ParameterFileFilter("txt", "Audacity label file (*.txt)");
    ParameterFileFilter srtFileFilter = new ParameterFileFilter("srt", "SRT subtitles (*.srt)");
    ParameterFileFilter subtitlePlainTextFileFilter = new ParameterFileFilter("txt", "Plain text subtitles (*.txt)");
    
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
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(FOLKERInternationalizer.getString("dialog.import"));
        fileChooser.addChoosableFileFilter(teiFileFilter);
        fileChooser.setFileFilter(teiFileFilter);
        fileChooser.setCurrentDirectory(new File(PreferencesUtilities.getProperty("workingDirectory", "")));        
        int retValue = fileChooser.showOpenDialog(ac.getFrame());
        if (retValue==JFileChooser.CANCEL_OPTION) return;
        
        File f = fileChooser.getSelectedFile();

        if (fileChooser.getFileFilter()==teiFileFilter){
            try {
                ac.importISOTEIFile(f);
                /*TEIConverter converter = new TEIConverter();
                converter.setLanguage("de");
                NormalizedFolkerTranscription nft;
                try {
                nft = converter.readFOLKERISOTEIFromFile(f.getAbsolutePath());
                String mediaPath = nft.getMediaPath();    
                
                System.out.println("[ImportAction] mediaPath=" + mediaPath);
                ac.setTranscription(nft);
                System.out.println("[ImportAction] transcription set.");

                boolean mediaSet = false;
                while (!mediaSet){
                try {
                System.out.println("[ImportAction] Setting media to " + mediaPath);
                ac.setMedia(mediaPath);
                mediaSet = true;
                } catch (IOException ex) {
                //ex.printStackTrace();
                System.out.println("[ImportAction] Error setting media " + ex.getMessage());
                mediaPath = ac.displayRecordingNotFoundDialog(mediaPath, ex);
                nft.setMediaPath(mediaPath);
                }
                }
                System.out.println("[ImportAction] media set.");
                
                } catch (IOException ex) {
                Logger.getLogger(ImportAction.class.getName()).log(Level.SEVERE, null, ex);
                applicationControl.displayException(ex);
                return;
                }*/
            } catch (IOException ex) {
                Logger.getLogger(ImportAction.class.getName()).log(Level.SEVERE, null, ex);
                applicationControl.displayException(ex);
                return;
            }
        } 
        
        PreferencesUtilities.setProperty("workingDirectory", f.getParent());        
        ac.status(FOLKERInternationalizer.getString("status.import1") + f.getAbsolutePath() + FOLKERInternationalizer.getString("status.import2"));

        
    }
    
}
