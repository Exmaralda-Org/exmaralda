/*
 * EditPreferencesAction.java
 *
 * Created on 17. Juni 2003, 14:07
 */

package org.exmaralda.partitureditor.partiture.editActions;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.JOptionPane;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.partitureditor.jexmaraldaswing.EditPreferencesDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;

/**
 *
 * @author  thomas
 */
public class EditPreferencesAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditPreferencesAction */
    public EditPreferencesAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Preferences...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("editPreferencesAction!");
        table.commitEdit(true);
        editPreferences(actionEvent);
    }
    
    private void editPreferences(java.awt.event.ActionEvent evt){
        String mediaPlayer = 
                java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor").get("PlayerType", "JMF-Player");

        org.exmaralda.common.ExmaraldaApplication app = null;
        if (table.parent instanceof org.exmaralda.common.ExmaraldaApplication){
            app = (org.exmaralda.common.ExmaraldaApplication)table.parent;
        }
        Boolean showSFB538Menu = new Boolean(false);
        Boolean showSinMenu = new Boolean(false);
        Boolean showODTSTDMenu = new Boolean(false);
        if (app instanceof PartiturEditor){
            PartiturEditor pe = (PartiturEditor)app;
            showSFB538Menu = new Boolean(pe.menuBar.sfb538Menu.isShowing());
            showSinMenu = new Boolean(pe.menuBar.sinMenu.isShowing());
            showODTSTDMenu = new Boolean(pe.menuBar.odtstdMenu.isShowing());

        }
        
        String oldMediaPlayer = mediaPlayer;

        String[] values = { table.defaultFontName,
                            table.generalPurposeFontName,
                            table.head2HTMLStylesheet,
                            table.speakertable2TranscriptionStylesheet,
                            table.transcription2FormattableStylesheet,
                            table.freeStylesheetVisualisationStylesheet,
                            table.HIATUtteranceList2HTMLStylesheet,
                            new Boolean(table.autoSave).toString(),
                            table.autoSaveThread.FILENAME,
                            table.autoSaveThread.PATH,
                            Integer.toString(table.autoSaveThread.SAVE_INTERVAL/60000),
                            table.hiatFSM,
                            table.didaFSM,
                            table.gatFSM,
                            table.chatFSM,
                            table.language, 
                            mediaPlayer,
                            new Boolean(table.underlineWithDiacritics).toString(),
                            table.underlineCategory,
                            table.preferredSegmentation,
                            showSFB538Menu.toString(),
                            showSinMenu.toString(),
                            showODTSTDMenu.toString(),
                            new Boolean(table.AUTO_ANCHOR).toString(),
                            new Boolean(table.AUTO_REMOVE_UNUSED_TLI).toString(),
                            // pause notation
                            table.pausePrefix,
                            table.pauseSuffix,
                            Integer.toString(table.pauseDigits),
                            Boolean.toString(table.pauseDecimalComma),
                            Boolean.toString(table.undoEnabled),
                            new Boolean(table.getModel().INTERPOLATE_WHEN_SPLITTING).toString(),
        };
        EditPreferencesDialog dialog = new EditPreferencesDialog(table.parent, true, app);
        if ((evt!=null) && ("ChangeSegmentation".equals(evt.getActionCommand()))){
            dialog.changeToSegmentationTab();
        }
        boolean ok = dialog.editPreferences(values);
        if (dialog.reset){
            ExmaraldaApplication ea = (ExmaraldaApplication) table.getTopLevelAncestor();            
            ea.resetSettings(); 
        } else if (ok){
            String[] newValues = dialog.getValues();
            
            table.defaultFontName = newValues[0];
            table.getModel().defaultFontName = table.defaultFontName;            
            table.generalPurposeFontName = newValues[1];
            table.keyboardDialog.getKeyboardPanel().setGeneralPurposeFontName(newValues[1]);     
            table.largeTextField.setFont(new java.awt.Font(newValues[1], java.awt.Font.PLAIN, 10));
            
            table.head2HTMLStylesheet = newValues[2];
            table.speakertable2TranscriptionStylesheet = newValues[3];
            table.transcription2FormattableStylesheet = newValues[4];   
            table.freeStylesheetVisualisationStylesheet = newValues[5];
            table.HIATUtteranceList2HTMLStylesheet = newValues[6];
            
            table.stopAutoSaveThread();
            table.autoSave = new Boolean(newValues[7]).booleanValue();
            table.autoSaveThread.FILENAME = newValues[8];
            table.autoSaveThread.PATH = newValues[9];
            table.autoSaveThread.setSaveInterval(Integer.parseInt(newValues[10])*60);
            if (table.autoSave){
                table.startAutoSaveThread();
            }            
            
            table.hiatFSM = newValues[11];
            table.didaFSM = newValues[12];
            table.gatFSM = newValues[13];
            table.chatFSM = newValues[14];
         
            table.language = newValues[15];
            
            java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor")
                        .put("PlayerType", newValues[16]);
            
            table.underlineWithDiacritics = new Boolean(newValues[17]).booleanValue();
            table.underlineCategory = newValues[18];

            table.preferredSegmentation = newValues[19];
            if (table.getTopLevelAncestor() instanceof PartiturEditor){
                PartiturEditor pe = ((PartiturEditor)(table.getTopLevelAncestor()));
                   pe.infoPanel.segmentationLabel.setText(table.preferredSegmentation);
                
                   pe.menuBar.transcriptionMenu.segmentationLabel.setText(" Segmentation (" + table.preferredSegmentation + ")");
                   
                   pe.menuBar.transcriptionMenu.insertHIATUtteranceNumbersMenuItem.setVisible(table.preferredSegmentation.equals("HIAT"));

                   pe.menuBar.sfb538Menu.setVisible(new Boolean(newValues[20]).booleanValue());
                   pe.menuBar.sinMenu.setVisible(new Boolean(newValues[21]).booleanValue());
                   pe.menuBar.odtstdMenu.setVisible(new Boolean(newValues[22]).booleanValue());
            }

            table.AUTO_ANCHOR = new Boolean(newValues[23]).booleanValue();
            table.AUTO_REMOVE_UNUSED_TLI = new Boolean(newValues[24]).booleanValue();

            // pause notation
            table.pausePrefix = newValues[25];
            table.pauseSuffix = newValues[26];
            table.pauseDigits = Integer.parseInt(newValues[27]);
            table.pauseDecimalComma = Boolean.parseBoolean(newValues[28]);
            table.undoEnabled = Boolean.parseBoolean(newValues[29]);
            table.undoAction.setEnabled(table.undoAction.isEnabled() && table.undoEnabled);
            table.getModel().INTERPOLATE_WHEN_SPLITTING = new Boolean(newValues[30]).booleanValue();
            
            table.status("Preferences changed");

            if (!newValues[16].equals(oldMediaPlayer)){
                String message = "You have changed the player from \n"
                        + oldMediaPlayer + " to " + newValues[16] + ".\n"
                        + "You must restart the Partitur-Editor \nfor this change to take effect.";
                JOptionPane.showMessageDialog(table, message);
            }

        }
    }
    
    
}
