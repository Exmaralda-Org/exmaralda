/*
 * EditPreferencesAction.java
 *
 * Created on 17. Juni 2003, 14:07
 */

package org.exmaralda.partitureditor.partiture.editActions;

import javax.swing.JOptionPane;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.partitureditor.jexmaraldaswing.EditPreferencesDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.tagging.TaggingProfiles;

/**
 *
 * @author  thomas
 */
public class EditPreferencesAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditPreferencesAction
     * @param t
     * @param icon */
    public EditPreferencesAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Preferences...", icon, t);
    }
    
    @Override
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
        Boolean showSFB538Menu = false;
        Boolean showSinMenu = false;
        Boolean showODTSTDMenu = false;
        Boolean showInelMenu = false;
        Boolean showTransformationDropdown = false;
        if (app instanceof PartiturEditor){
            PartiturEditor pe = (PartiturEditor)app;
            showSFB538Menu = pe.menuBar.sfb538Menu.isShowing();
            showSinMenu = pe.menuBar.sinMenu.isShowing();
            showODTSTDMenu = pe.menuBar.odtstdMenu.isShowing();
            showInelMenu = pe.menuBar.inelMenu.isShowing();
            showTransformationDropdown = pe.getTransformationComboBox().isShowing();
        }
        
        String oldMediaPlayer = mediaPlayer;

        String[] values = { table.defaultFontName,
                            table.generalPurposeFontName,
                            table.head2HTMLStylesheet,
                            table.speakertable2TranscriptionStylesheet,
                            table.transcription2FormattableStylesheet,
                            table.freeStylesheetVisualisationStylesheet,
                            table.HIATUtteranceList2HTMLStylesheet,
                            Boolean.toString(table.autoSave),
                            table.autoSaveThread.FILENAME,
                            table.autoSaveThread.PATH,
                            Integer.toString(table.autoSaveThread.SAVE_INTERVAL/60000),
                            table.hiatFSM,
                            table.didaFSM,
                            table.gatFSM,
                            table.chatFSM,
                            table.language, 
                            mediaPlayer,
                            Boolean.toString(table.underlineWithDiacritics),
                            table.underlineCategory,
                            table.preferredSegmentation,
                            showSFB538Menu.toString(),
                            showSinMenu.toString(),
                            showODTSTDMenu.toString(),
                            showInelMenu.toString(),
                            Boolean.toString(table.AUTO_ANCHOR),
                            Boolean.toString(table.AUTO_REMOVE_UNUSED_TLI),
                            // pause notation
                            table.pausePrefix,
                            table.pauseSuffix,
                            Integer.toString(table.pauseDigits),
                            Boolean.toString(table.pauseDecimalComma),
                            Boolean.toString(table.undoEnabled),
                            Boolean.toString(table.getModel().INTERPOLATE_WHEN_SPLITTING),
                            // TreeTagger options need not be set because the panel sets them
                            "",
                            "",
                            "",
                            "",
                            "",
                            // show transformation dropdown, issue #230
                            Boolean.toString(showTransformationDropdown),
                            table.genericFSM,
                            table.cGATMinimalFSM
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
            table.autoSave = Boolean.valueOf(newValues[7]);
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
            table.genericFSM = newValues[38];
            table.cGATMinimalFSM = newValues[39];
         
            table.language = newValues[15];
            
            java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor")
                        .put("PlayerType", newValues[16]);
            
            System.out.println("Changed player type settings to " + newValues[16]);
            
            table.underlineWithDiacritics = Boolean.parseBoolean(newValues[17]);
            table.underlineCategory = newValues[18];

            table.preferredSegmentation = newValues[19];
            if (table.getTopLevelAncestor() instanceof PartiturEditor){
                PartiturEditor pe = ((PartiturEditor)(table.getTopLevelAncestor()));
                   pe.infoPanel.segmentationLabel.setText(table.preferredSegmentation);
                
                   pe.menuBar.transcriptionMenu.segmentationLabel.setText(" Segmentation (" + table.preferredSegmentation + ")");
                   
                   pe.menuBar.transcriptionMenu.insertHIATUtteranceNumbersMenuItem.setVisible(table.preferredSegmentation.equals("HIAT"));
                   pe.menuBar.transcriptionMenu.addTokenLayerMenuItem.setVisible(
                            table.preferredSegmentation.equals("GENERIC") ||
                            table.preferredSegmentation.equals("cGAT_MINIMAL") ||
                            table.preferredSegmentation.equals("HIAT")
                    );
                   

                   pe.menuBar.sfb538Menu.setVisible(Boolean.parseBoolean(newValues[20]));
                   pe.menuBar.sinMenu.setVisible(Boolean.parseBoolean(newValues[21]));
                   pe.menuBar.odtstdMenu.setVisible(Boolean.parseBoolean(newValues[22]));
                   pe.menuBar.inelMenu.setVisible(Boolean.parseBoolean(newValues[23]));
                   
                   pe.getTransformationComboBox().setVisible(Boolean.parseBoolean(newValues[37]));
            }

            table.AUTO_ANCHOR = Boolean.parseBoolean(newValues[24]);
            table.AUTO_REMOVE_UNUSED_TLI = Boolean.parseBoolean(newValues[25]);

            // pause notation
            table.pausePrefix = newValues[26];
            table.pauseSuffix = newValues[27];
            table.pauseDigits = Integer.parseInt(newValues[28]);
            table.pauseDecimalComma = Boolean.parseBoolean(newValues[29]);
            table.undoEnabled = Boolean.parseBoolean(newValues[30]);
            table.undoAction.setEnabled(table.undoAction.isEnabled() && table.undoEnabled);
            table.getModel().INTERPOLATE_WHEN_SPLITTING = Boolean.parseBoolean(newValues[31]);
            
            // new 02-12-2020, issue #228, changed 08-12-2020, issue #228
            //TaggingProfiles.writePreferences(newValues[32], newValues[33], newValues[34], null);
            TaggingProfiles.writePreferences(newValues[32], newValues[33], newValues[34], newValues[35], newValues[36], dialog.getTaggingOptions());
            
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
